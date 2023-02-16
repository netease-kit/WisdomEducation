// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NEEduLiveRoomVC.h"
#import "NEEduNavigationViewController.h"
//#import "NSString+NE.h"
#import "NSArray+NEUIExtension.h"
#import "NEEduLiveRoomVC+Seat.h"
#import "NEEduLiveRoomVC+UI.h"
#import "NEEduLiveRoomVC+Logic.h"
#import "UIView+NE.h"
static NSString * kLastRtcCid = @"lastRtcCid";

@interface NEEduLiveRoomVC () <NEEduRoomViewMaskViewDelegate,NEEduVideoServiceDelegate,NEEduIMChatDelegate,NEEduRoomServiceDelegate>

@property (nonatomic, assign) BOOL netReachable;
@property (nonatomic, assign) BOOL isPushStream;
@end

@implementation NEEduLiveRoomVC
- (void)dealloc {
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];
    [NEEduManager.shared.seatService removeSeatListener:self];
    [self.shareHost stopBroadcaster];
}

- (void)viewDidLoad {
    [super viewDidLoad];
//    self.netReachable = YES;
//    [self.navigationController setNavigationBarHidden:NO];
    self.whiteboardWritable = NO;
    self.userIsShareScreen = false;
    self.handsupState = NEEduHandsupStateIdle;
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    [NEEduManager shared].imService.chatDelegate = self;
    [NEEduManager.shared.seatService addSeatListener:self];
    [self initData];
    //get seat request lise
    [self getSeatRequestList];
    self.leaveState = NEEduLeaveSeatActive;//默认值
    // get seat info
    [self getSeatInfo];
    [self setupDefaultSubviews];
    [self getLiveRoomSnapshot];
}

- (void)initData {
    NEEduMenuItem *membersItem = [[NEEduMenuItem alloc] initWithTitle:@"课堂成员" image:[UIImage ne_imageNamed:@"menu_members"]];
    membersItem.type = NEEduMenuItemTypeMembers;
    self.menuItems = @[membersItem];
}

- (void)addChatroom:(NEEduHttpRoom *)room {
    __weak typeof(self) weakSelf = self;
    NEEduChatRoomParam *chatparam = [[NEEduChatRoomParam alloc] init];
    chatparam.chatRoomID = room.properties.chatRoom.chatRoomId;
    chatparam.nickname = [NSString stringWithFormat:@"%@(学生)",[NEEduManager shared].localUser.userName];
    [[NEEduManager shared].imService enterChatRoomWithParam:chatparam success:^(NEEduChatRoomResponse * _Nonnull response) {
        __strong typeof(self)strongSelf = weakSelf;
        [strongSelf addChatMenue];
    } failed:^(NSError * _Nonnull error) {
        __strong typeof(self)strongSelf = weakSelf;
        [strongSelf.view makeToast:error.localizedDescription];
    }];
}

- (void)classOver {
//    if (self.room.states.step.value == NEEduLessonStateClassOver) {
//        return;
//    }
    //展示课堂结束页面
    self.classOverView.hidden = NO;
    long minutes = self.maskView.navView.timeCount / 60;
    long seconds = self.maskView.navView.timeCount % 60;
    self.classOverView.timeLabel.text = [NSString stringWithFormat:@"课堂结束(%.2ld:%.2ld)",minutes,seconds];
    if (self.chatVC) {
        [self.chatVC dismissViewControllerAnimated:YES completion:nil];
    }
    if (self.membersVC) {
        [self.membersVC dismissViewControllerAnimated:YES completion:nil];
    }
    if (self.isSharing) {
        [self stopAllScreenShare];
    }
    if (self.player){
        [self.player shutdown];
        [self.player.view removeFromSuperview];
    }
    [[NEEduManager shared] leaveClassroom];
    [[NMCWhiteboardManager sharedManager] callWebLogoutIM];
    [[NMCWhiteboardManager sharedManager] clearWebViewCache];
    [[NEEduManager shared] destoryClassroom];
    [self.view addSubview:self.classOverView];
}

- (void)classOverBack {
    [self backEvent];
}

- (void)showChatViewWithItem:(NEEduMenuItem *)item {
    self.chatVC = [[NEEduChatViewController alloc] init];
    self.chatVC.messages = self.messages;
    self.chatVC.muteChat = self.muteChat;
    NEEduNavigationViewController *chatNav = [[NEEduNavigationViewController alloc] initWithRootViewController:self.chatVC];
    chatNav.modalPresentationStyle = UIModalPresentationFullScreen;
    [self presentViewController:chatNav animated:YES completion:nil];
    //更新菜单底部聊天室红点
    self.chatItem.badgeLabel.hidden = YES;
}
- (void)showMembersViewWithItem:(NEEduMenuItem *)item {
    if (!self.membersVC) {
        self.membersVC = [[NEEduLiveMembersVC alloc] init];
        self.membersVC.room = self.room;
        self.membersVC.modalPresentationStyle = UIModalPresentationFullScreen;
    }
    [self presentViewController:self.membersVC animated:YES completion:nil];
}

- (void)offStageResult:(void(^)(NSError *error))result {
    [self.view showAlertViewOnVC:self withTitle:@"下讲台" subTitle:@"下讲台后，你的视频画面将不再显示在屏幕上，不能继续与老师语音交流" confirm:^{
        [[NEEduManager shared].userService handsupStateChange:NEEduHandsupStateIdle userID:[NEEduManager shared].localUser.userUuid result:^(NSError * _Nonnull error) {
            if (error) {
                if (result) {
                    result(error);
                }
                return;
            }
            [[NEEduManager shared].rtcService enableLocalAudio:NO];
            [[NEEduManager shared].rtcService enableLocalVideo:NO];
            if (result) {
                result(error);
            }
        }];
    }];
}

- (void)updateUIWithMembers:(NSArray *)members {
//    self.totalMembers = members.mutableCopy;
    [self subscribeVideoForOnlineUser];
    [self updateHandsupStateWithMembers:members];
}
- (void)subscribeVideoForOnlineUser {
    for (NEEduHttpUser *user in self.totalMembers) {
        if([user.userUuid isEqualToString:NEEduManager.shared.localUser.userUuid]){
            continue;
        }
        if (user.streams.video.value) {
            [[NEEduManager shared].rtcService subscribeVideo:YES forUserID:user.rtcUid];
        }
        if (user.streams.audio.value) {
            [[NEEduManager shared].rtcService subscribeAudio:YES forUserID:user.rtcUid];
        }
    }
}

- (NSArray <NEEduHttpUser *> *)showMembersWithJoinedMembers:(NSArray <NEEduHttpUser *> *)members {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"properties.avHandsUp.value = %d",NEEduHandsupStateTeaAccept];
    NSArray *onlineMembers = [members filteredArrayUsingPredicate:predicate];
    NSMutableArray *showMembers = onlineMembers.mutableCopy;
    NEEduHttpUser *user = members.firstObject;
    if (![user isTeacher]) {
        [showMembers insertObject:[NEEduHttpUser teacher] atIndex:0];
    }else{
        [showMembers insertObject:user atIndex:0];
    }
    NSLog(@"onlineMembers:%@",onlineMembers);
    return showMembers;
}

#pragma mark- NEEduVideoServiceDelegate
- (void)onRtcDisconnectWithReason:(NERtcError *)reason {
    [self.view makeToast:@"音视频断开连接"];
    [self leaveClass];
}

- (void)onSubStreamDidStop:(UInt64)userID {
    NERtcVideoCanvasExtention *canvas = [[NERtcVideoCanvasExtention alloc] init];
    canvas.uid = userID;
    canvas.container = nil;
    [[NEEduManager shared].rtcService setupSubStreamVideo:canvas];
    self.shareScreenView.hidden = YES;
}
- (void)onNetQuality:(NERtcNetworkQualityStats *)quality {
    if (!self.netReachable) {
        self.maskView.navView.netStateView.image = [UIImage ne_imageNamed:@"net_0"];
        return;
    }
    if (quality.userId == [NEEduManager shared].localUser.rtcUid) {
        int level = 0;
        NERtcNetworkQuality max = MAX(quality.txQuality, quality.rxQuality);
        switch (max) {
            case kNERtcNetworkQualityExcellent:
            case kNERtcNetworkQualityGood:
                level = 3;
                break;
            case kNERtcNetworkQualityPoor:
            case kNERtcNetworkQualityBad:
                level = 2;
                break;
            case kNERtcNetworkQualityVeryBad:
            case kNERtcNetworkQualityDown:
                level = 1;
                break;
            default:
                break;
        }
        NSString *string = [NSString stringWithFormat:@"net_%d",level];
        self.maskView.navView.netStateView.image = [UIImage ne_imageNamed:string];
    }
}
- (void)showAlertViewWithMember:(NEEduHttpUser *)member cell:(NEEduVideoCell *)cell {
    if (![[NEEduManager shared].localUser isTeacher] && ![member.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid] ) {
        //只有老师角色有操作权限
        return;
    }
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:@"更多操作" preferredStyle:UIAlertControllerStyleActionSheet];
    NSString *audioTitle = member.streams.audio.value ? @"静音":@"解除静音";
    NSString *videoTitle = member.streams.video.value ? @"关闭视频":@"开启视频";
    UIAlertAction *audioAction = [UIAlertAction actionWithTitle:audioTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        __weak typeof(self)weakSelf = self;
        if ([member.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
            [[NEEduManager shared].userService localUserAudioEnable:!member.streams.audio.value result:^(NSError * _Nonnull error) {
                if (error) {
                    [weakSelf.view makeToast:error.description];
                }else {
                    [weakSelf.view makeToast:@"操作成功"];
                }
            }];
        }else {
            [[NEEduManager shared].userService remoteUserAudioEnable:!member.streams.audio.value userID:member.userUuid result:^(NSError * _Nonnull error) {
                if (error) {
                    [weakSelf.view makeToast:error.description];
                }else {
                    [weakSelf.view makeToast:@"操作成功"];
                }
            }];
        }
    }];
    UIAlertAction *videoAction = [UIAlertAction actionWithTitle:videoTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        __weak typeof(self)weakSelf = self;
        if ([member.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
            [[NEEduManager shared].userService localUserVideoEnable:!member.streams.video.value result:^(NSError * _Nonnull error) {
                if (error) {
                    [weakSelf.view makeToast:error.description];
                }else {
                    [weakSelf.view makeToast:@"操作成功"];
                }
            }];
        }else {
            [[NEEduManager shared].userService remoteUserVideoEnable:!member.streams.video.value userID:member.userUuid result:^(NSError * _Nonnull error) {
                if (error) {
                    [weakSelf.view makeToast:error.description];
                }else {
                    [weakSelf.view makeToast:@"操作成功"];
                }
            }];
        }
    }];
    [alert addAction:audioAction];
    [alert addAction:videoAction];
    
    if ([[NEEduManager shared].localUser isTeacher] && ![member.role isEqualToString:NEEduRoleHost]) {
        //老师点击学生视频窗口
        NSString *whiteboardTitle = member.properties.whiteboard.drawable ? @"取消白板权限":@"授予白板权限";
        NSString *shareTitle = member.properties.screenShare.value ? @"取消共享权限":@"授予共享权限";
        UIAlertAction *whiteboardAction = [UIAlertAction actionWithTitle:whiteboardTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            if ([NEEduManager shared].profile.snapshot.room.states.step.value != NEEduLessonStateClassIn) {
                [self.view makeToast:@"请先开始上课"];
                return;
            }
            //授予/取消白板权限
            __weak typeof(self)weakSelf = self;
            [[NEEduManager shared].userService whiteboardDrawable:!member.properties.whiteboard.drawable userID:member.userUuid result:^(NSError * _Nonnull error) {
                if (!error) {
                    [weakSelf.view makeToast:@"授权白板权限成功"];
                }else {
                    [weakSelf.view makeToast:error.description];
                }
            }];
        }];
        UIAlertAction *shareAction = [UIAlertAction actionWithTitle:shareTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            if ([NEEduManager shared].profile.snapshot.room.states.step.value != NEEduLessonStateClassIn) {
                [self.view makeToast:@"请先开始上课"];
                return;
            }
            //授予/取消屏幕共享
            __weak typeof(self)weakSelf = self;
            [[NEEduManager shared].userService screenShareAuthorization:!member.properties.screenShare.value userID:member.userUuid result:^(NSError * _Nonnull error) {
                if (!error) {
                    [weakSelf.view makeToast:@"授权屏幕共享权限成功"];
                }else {
                    [weakSelf.view makeToast:error.description];
                }
            }];
        }];
        [alert addAction:whiteboardAction];
        [alert addAction:shareAction];
        
        if (member.properties.avHandsUp.value == NEEduHandsupStateTeaAccept) {
            UIAlertAction *offStage = [UIAlertAction actionWithTitle:@"请他下台" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                __weak typeof(self)weakSelf = self;
                [[NEEduManager shared].userService handsupStateChange:NEEduHandsupStateTeaOffStage userID:member.userUuid result:^(NSError * _Nonnull error) {
                    if (!error) {
                        [weakSelf.view makeToast:@"操作成功"];
                    }else {
                        [weakSelf.view makeToast:error.localizedDescription];
                    }
                }];
            }];
            [alert addAction:offStage];
        }
    }
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    }];
    
    [alert addAction:cancelAction];
    UIPopoverPresentationController *popover = alert.popoverPresentationController;
    if (popover) {
        popover.sourceView = cell;
        popover.permittedArrowDirections = UIPopoverArrowDirectionAny;
    }
    [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - NEEduVideoCellDelegate
- (void)didTapCell:(NEEduVideoCell *)cell {
    if (cell.member.userUuid) {
        [self showAlertViewWithMember:cell.member cell:cell];
    }
}
#pragma mark - NEEduRoomViewMaskViewDelegate
///返回按钮
- (void)backEvent {
    [self.view showAlertViewOnVC:self withTitle:@"确认离开课堂" subTitle:@"离开教室后将暂停学习，需要等待您再次进入课堂后方可继续上课？" confirm:^{
        [self leaveClass];
    }];
}

- (void)leaveClass {
    [self dismissVC];
    //老师学生离开音视频、聊天室、白板
    [[NEEduManager shared] leaveClassroom];
    //结束共享
    if (self.isSharing) {
        [self stopAllScreenShare];
    }
    [self.player shutdown];
    [[NMCWhiteboardManager sharedManager] callWebLogoutIM];
    [[NMCWhiteboardManager sharedManager] clearWebViewCache];
    [[NEEduManager shared] destoryClassroom];
}

- (void)dismissVC {
    if (self.chatVC) {
        [self.chatVC dismissViewControllerAnimated:YES completion:nil];
    }
    if (self.membersVC) {
        [self.membersVC dismissViewControllerAnimated:YES completion:nil];
    }
    [self removeNotification];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)onSectionStateChangeAtIndex:(NSInteger)index item:(NEEduMenuItem *)item {
    switch (item.type) {
        case NEEduMenuItemTypeMembers:
            [self showMembersViewWithItem:item];
            break;
        case NEEduMenuItemTypeChat:
            [self showChatViewWithItem:item];
            break;
        case NEEduMenuItemTypeHandsup:
            [self handsupItem:item];
            break;
        case NEEduMenuItemTypeAudio:
            [self turnOnAudio:!item.isSelected];
            break;
        case NEEduMenuItemTypeVideo:
            [self turnOnVideo:!item.isSelected];
            break;
        case NEEduMenuItemTypeShareScreen:
            [self turnOnShareScreen:!item.isSelected item:item];
            break;
        default:
            break;
    }
}

- (void)handsupItem:(NEEduMenuItem *)item {
    if ([NEEduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateNone) {
        [self.view makeToast:@"请先开始上课"];
        return;
    }
    if (self.handsupState == NEEduHandsupStateApply) {
        //老师还没同意 可取消
        __weak typeof(self)weakSelf = self;
        [self.view showAlertViewOnVC:self withTitle:@"取消举手" subTitle:@"是否确认取消举手" confirm:^{
            if (weakSelf.handsupState == NEEduHandsupStateApply) {
                [[NEEduManager shared].seatService cancelApplySeat:weakSelf.roomUuid
                 userName:[NEEduManager shared].localUser.userName
                  success:^{
                    weakSelf.seatAction.action = NEEduSeatUserActionTypeCancelApply;
                }failure:^(NSError * _Nullable error, NSInteger statusCode) {
                    if (!error) return;
                    [weakSelf.view makeToast:[NSString stringWithFormat:@"取消举手失败:  %@", error.localizedDescription]];
                }];
            }
        }];
        return;
    }
    if(self.handsupState == NEEduHandsupStateTeaAccept) {
        //老师已同意 下讲台
        __weak typeof(self) weakSelf = self;
        [self.view showAlertViewOnVC:self withTitle:@"下讲台" subTitle:@"下讲台后，你的视频画面将不再显示在屏幕上，不能继续与老师语音交流" confirm:^{
            self.leaveState = NEEduLeaveSeatActive;//主动离开麦位
            [[NEEduManager shared].seatService leaveSeat:weakSelf.roomUuid
             userName:[NEEduManager shared].localUser.userName success:^{
            }failure:^(NSError * _Nullable error, NSInteger statusCode) {
                if (!error) return;
                [weakSelf.view makeToast:[NSString stringWithFormat:@"主动下麦失败:  %@", error.localizedDescription]];
            }];
        }];
        return;
    } else {
        __weak typeof(self) weakSelf = self;
        [self.view showAlertViewOnVC:self withTitle:@"举手申请" subTitle:@"申请上台与老师沟通，通过后你的视频画面将出现在屏幕上并能与老师语音" confirm:^{
            
            [[NEEduManager shared].seatService applySeat:weakSelf.roomUuid
                                                 userName: [NEEduManager shared].localUser.userName
                                                 success:^{}
                                                 failure:^(NSError * _Nullable error, NSInteger statusCode) {
                if (!error) return;
                [weakSelf.view makeToast:[NSString stringWithFormat:@"举手申请失败:  %@", error.localizedDescription]];
            }];
        }];
    }
}

- (void)updateScreenShare {
    NEEduHttpUser *user = [[NEEduManager shared].userService userIsShareScreen];
    if (user) {
        //共享按钮状态改变为选择
        [self onSubVideoStreamEnable:YES user:user];
    }
}
#pragma mark - whiteboard
- (WKWebView *)boardView {
    if (!_boardView) {
        _boardView = [[NMCWhiteboardManager sharedManager] createWebViewFrame:CGRectZero];
//        [NMCWhiteboardManager sharedManager].delegate = self;
        _boardView.translatesAutoresizingMaskIntoConstraints = NO;
        NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:NMCWhiteboardURL]];
        [_boardView loadRequest:request];
        if([[[UIDevice currentDevice] systemVersion] floatValue] < 12.0) {
            if (@available(iOS 11.0, *)) {
                _boardView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
            }
        }
        if (@available(iOS 12.0, *)) {
            _boardView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentAutomatic;
        }
    }
    return _boardView;
}

#pragma mark - 举手操作
- (void)updateHandsupStateWithMembers:(NSArray<NEEduHttpUser *> *)members {
    //判断自己是否在举手中
    for (NEEduHttpUser *user in members) {
        if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
            self.handsupState = user.properties.avHandsUp.value;
            [self onHandsupStateChange:self.handsupState user:user];
            break;
        }
    }
}
- (void)onHandsupStateChange:(NEEduHandsupState)state user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        self.handsupState = state;
    }
    switch (state) {
        case NEEduHandsupStateIdle:
            //学生关闭
            [self handleHandsupClose:user];
            break;
        case NEEduHandsupStateTeaReject:
            [self handleHandsupReject:user];
            break;
        case NEEduHandsupStateTeaAccept:
            [self handleHandsupAccept:user];
            break;
        case NEEduHandsupStateTeaOffStage:
            [self handleHandsupTeacherOffStage:user];
            break;
        default:
            break;
    }
}
- (void)handleHandsupAccept:(NEEduHttpUser *)user {
    BOOL exist = NO;
    NSInteger existIndex =  0;
    NEEduHttpUser *existUser;
    for (int i = 0; i < self.totalMembers.count; i++) {
        existUser = self.totalMembers[i];
        if ([existUser.userUuid isEqualToString:user.userUuid]) {
            exist = YES;
            existIndex = i;
            break;
        }
    }
    if (exist) {
        [self.totalMembers replaceObjectAtIndex:existIndex withObject:user];
    }else {
        [self.totalMembers addObject:user];
    }
    
    //区分是否是自己
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        [self.view makeToast:@"举手通过，您已上台"];
        [NEEduManager shared].localUser.isOnStage = YES;
        self.handsupItem.selectTitle = @"下讲台";
        self.handsupItem.isSelected = YES;
        
        NEEduMenuItem *videoItem = [[NEEduMenuItem alloc] initWithTitle:@"关闭视频" image:[UIImage ne_imageNamed:@"menu_video"]];
        videoItem.selectTitle = @"开启视频";
        videoItem.type = NEEduMenuItemTypeVideo;
        [videoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_video_off"]];
        [self.maskView insertItem:videoItem atIndex:0];
        
        NEEduMenuItem *audoItem = [[NEEduMenuItem alloc] initWithTitle:@"静音" image:[UIImage ne_imageNamed:@"menu_audio"]];
        audoItem.selectTitle = @"解除静音";
        audoItem.type = NEEduMenuItemTypeAudio;
        [audoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_audio_off"]];
        [self.maskView insertItem:audoItem atIndex:0];
        //RTC推流
        [[NEEduManager shared].userService localUserVideoEnable:YES result:^(NSError * _Nonnull error) {
            [[NEEduManager shared].userService localUserAudioEnable:YES result:^(NSError * _Nonnull error) {
            }];
            NSLog(@"打开本地音视频");
        }];
        [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    }else {
        [[NEEduManager shared].rtcService subscribeAudio:YES forUserID:user.rtcUid];
        [[NEEduManager shared].rtcService subscribeVideo:YES forUserID:user.rtcUid];
    }
}
- (void)handleHandsupClose:(NEEduHttpUser *)user {
    NEEduHttpUser *removeUser;
    for (NEEduHttpUser *userExist in self.totalMembers) {
        if ([user.userUuid isEqualToString:userExist.userUuid]) {
            removeUser = userExist;
            break;
        }
    }
    if (removeUser) {
        [self.totalMembers removeObject:removeUser];
    }
//    [self.collectionView reloadData];
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        //是自己
        if (self.isSharing) {
            self.isSharing = NO;
            [self stopRecord];
        }
        [NEEduManager shared].localUser.isOnStage = NO;
        [self.maskView removeItemType:NEEduMenuItemTypeAudio];
        [self.maskView removeItemType:NEEduMenuItemTypeVideo];
        [self.maskView removeItemType:NEEduMenuItemTypeShareScreen];
        self.handsupItem.isSelected = NO;
        self.handsupState = NEEduHandsupStateIdle;
    }else {
        [[NEEduManager shared].rtcService subscribeAudio:NO forUserID:user.rtcUid];
        [[NEEduManager shared].rtcService subscribeVideo:NO forUserID:user.rtcUid];
    }
}

-(void)handleSeatRequestApproved:(NSString *)userUuid {
    if ([userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]){
        NCKLogInfo(@"enterRTCRoom");
        NEEduRoom *room = [[NEEduRoom alloc] init];
        room.roomName = [NSString stringWithFormat:@"%@的课堂",self.room.roomName];
        room.sceneType = NEEduSceneTypeLive;
        BOOL showChatroom = YES;
        //[[[NSUserDefaults standardUserDefaults] objectForKey:showChatroomKey] boolValue];
        NERoomConfig *config = [[NERoomConfig alloc] init];
        config.resource.chatroom = showChatroom;
        // 推流需要开启直播
        //    BOOL isPushStream = NO;
        //    config.resource.live = isPushStream;
        room.configId = 20;
        room.config = config;
        
        NEEduCreateRoomRequest *request = [[NEEduCreateRoomRequest alloc]init];
        request.roomName = room.roomName;
        request.configId = 20;
        request.roomUuid = self.roomUuid;
        request.config = room.config;
        [self.view hideToastActivity];
        self.view.userInteractionEnabled = YES;
        [NEEduManager shared].rtcService.delegate = self;
        [self enterRoom:request];
    }
}
- (void)enterRoom:(NEEduCreateRoomRequest *)resRoom {
    NEEduEnterRoomParam *param = [[NEEduEnterRoomParam alloc] init];
    param.roomUuid = resRoom.roomUuid;
    param.roomName = resRoom.roomName;
    param.sceneType = NEEduSceneTypeLive;
    param.userName = self.userName;
    param.role = 0;
    if (param.sceneType == NEEduSceneTypeBig) {
        param.autoSubscribeVideo = NO;
        param.autoSubscribeAudio = NO;
        if (param.role == NEEduRoleTypeStudent) {
            param.autoPublish = NO;
        }else {
            param.autoPublish = YES;
        }
    }else {
        param.autoPublish = YES;
        param.autoSubscribeVideo = YES;
        param.autoSubscribeAudio = YES;
    }
    param.isLiveClass = YES;
    __weak typeof(self)weakSelf = self;
    [[NEEduManager shared] enterClassroom:param completion:^(NSError * _Nonnull error, NEEduEnterRoomResponse * _Nonnull response) {
        [weakSelf.view hideToastActivity];
        weakSelf.view.userInteractionEnabled = YES;
        if (error) {
            [weakSelf.view makeToast:error.localizedDescription];
        }else {
            if (response.room.rtcCid.length) {
                [[NSUserDefaults standardUserDefaults] setObject:response.room.rtcCid forKey:kLastRtcCid];
            }
            int intEnable = 2;
            NSDictionary *param = @{@"value":@(intEnable)};
            [HttpManager updateMemberPropertyWithRoomUuid:self.room.roomUuid userUuid:[NEEduManager shared].localUser.userUuid param:param classType:[NEEduPropertyItem class] property:@"avHandsUp" success:^(NEEduPropertyItem *objModel) {
                for (NEEduHttpUser *user in self.profile.snapshot.members) {
                    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
                        if (user.properties.avHandsUp) {
                            user.properties.avHandsUp.value = objModel.value;
                        }else {
                            NEEduHandsupProperty *item = [[NEEduHandsupProperty alloc] init];
                            item.value = objModel.value;
                            user.properties.avHandsUp = item;
                        }
                    }
                }
                
            } failure:^(NSError * _Nullable error, NSInteger statusCode) {
                
            }];
            
            [weakSelf pushViewController];
        }
    }];
}
-(void)pushViewController{
    [self.player shutdown];
    [self.player.view removeFromSuperview];
    [self.contentView removeFromSuperview];
    self.whiteboardWritable = NO;
    [self setupNewSubview];
//    [self addWhiteboardView];
    [self getRTCRoomSnapshot];
    
}

- (void)handleHandsupReject:(NEEduHttpUser *)user {
    //区分是否是自己
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        [self.view makeToast:@"举手申请被拒绝"];
    }
    self.handsupItem.isSelected = NO;
}
-(void)handleSeatLeave{
    ///学生主动离开房间
    [[NEEduManager shared].rtcService leaveChannel];
    [self.collectionView removeFromSuperview];
    [self.contentView removeFromSuperview];
    //    [self leaveRoom];
    [HttpManager leaveRoomWithRoomUuid:self.roomUuid userUuid:NEEduManager.shared.localUser.userUuid success:^{
        NSLog(@"主动离开房间");
        [self handleHandsupClose:NEEduManager.shared.localUser];
        [self setupDefaultContentView];
        [self getLiveRoomSnapshot];
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        
    }];
    [self.collectionView reloadData];
}
- (void)handleSeatKicked{
    [[NEEduManager shared].rtcService leaveChannel];
    [self.collectionView removeFromSuperview];
    [self.contentView removeFromSuperview];
    [self handleHandsupTeacherOffStage:NEEduManager.shared.localUser];
    NSLog(@"老师请他下台");
    [self setupDefaultContentView];
    [self getLiveRoomSnapshot];
    [self.collectionView reloadData];
}
- (void)handleHandsupTeacherOffStage:(NEEduHttpUser *)user {
    NEEduHttpUser *removeUser;
    for (NEEduHttpUser *userExist in self.totalMembers) {
        if ([user.userUuid isEqualToString:userExist.userUuid]) {
            removeUser = userExist;
            break;
        }
    }
    if (removeUser) {
        [self.totalMembers removeObject:removeUser];
        [self.collectionView reloadData];
    }
    //区分是否是自己
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        [self.view makeToast:@"老师结束了你的上台操作"];
        [NEEduManager shared].localUser.isOnStage = NO;
        if (self.isSharing) {
            self.isSharing = NO;
            [self stopRecord];
            [[NEEduManager shared].userService handsupStateChange:NEEduHandsupStateIdle userID:[NEEduManager shared].localUser.userUuid result:^(NSError * _Nonnull error) {
                if (error) {
                    return;
                }
                NSLog(@"-------------------");
                
            }];
        }
        [self.maskView removeItemType:NEEduMenuItemTypeAudio];
        [self.maskView removeItemType:NEEduMenuItemTypeVideo];
        [self.maskView removeItemType:NEEduMenuItemTypeShareScreen];
        self.handsupItem.isSelected = NO;
        [[NEEduManager shared].rtcService enableLocalVideo:NO];
        [[NEEduManager shared].rtcService enableLocalAudio:NO];
        
    }else {
        [[NEEduManager shared].rtcService subscribeAudio:NO forUserID:user.rtcUid];
        [[NEEduManager shared].rtcService subscribeVideo:NO forUserID:user.rtcUid];
    }
}
#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.totalMembers.count;
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NEEduVideoCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:cellID forIndexPath:indexPath];
    NEEduHttpUser *user = self.totalMembers[indexPath.row];
    cell.delegate = self;
    cell.member = user;
    return cell;
}
#pragma mark - get
- (NSMutableArray<NIMChatroomMember *> *)members {
    if (!_members) {
        _members = @[].mutableCopy;
    }
    return _members;
}
- (NSMutableArray<NEEduHttpUser *> *)totalMembers {
    if (!_totalMembers) {
        _totalMembers = @[].mutableCopy;
    }
    return _totalMembers;
}
- (NEEduRoomViewMaskView *)maskView {
    if (!_maskView) {
        _maskView = [[NEEduRoomViewMaskView alloc] initWithMenuItems:self.menuItems];
        _maskView.delegate = self;
    }
    return _maskView;
}

- (NEEduLessonStateView *)lessonStateView {
    if (!_lessonStateView) {
        _lessonStateView = [[NEEduLessonStateView alloc] init];
        _lessonStateView.hidden = YES;
    }
    return _lessonStateView;
}

- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
        layout.itemSize = CGSizeMake(120, 90);
        layout.minimumLineSpacing = 10;
        layout.footerReferenceSize = CGSizeMake(120, 48);
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:layout];
        _collectionView.translatesAutoresizingMaskIntoConstraints = NO;
        _collectionView.backgroundColor = [UIColor whiteColor];
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        [_collectionView registerClass:[NEEduVideoCell class] forCellWithReuseIdentifier:cellID];
        _collectionView.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    }
    return _collectionView;
}

- (NSMutableArray<NEEduChatMessage *> *)messages {
    if (!_messages) {
        _messages = [NSMutableArray array];
    }
    return _messages;
}

- (UIView *)contentView {
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
        _contentView.backgroundColor = [UIColor whiteColor];
        _contentView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _contentView;
}
- (NEEduLessonOverView *)classOverView {
    if (!_classOverView) {
        _classOverView = [[NEEduLessonOverView alloc] init];
        [_classOverView.backButton addTarget:self action:@selector(classOverBack) forControlEvents:UIControlEventTouchUpInside];
        _classOverView.hidden = YES;
    }
    return _classOverView;
}
- (NEEduLessonInfoView *)infoView {
    if (!_infoView) {
        _infoView = [[NEEduLessonInfoView alloc] init];
        self.infoView.lessonItem.titleLabel.text = self.room.roomUuid;
        self.infoView.lessonName.text = self.room.roomName;
        self.infoView.teacherName.text = self.members.firstObject.roomNickname;
#ifdef DEBUG
        self.infoView.cid.text = self.room.rtcCid;
#else
#endif
        [self.view addSubview:_infoView];
        NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:_infoView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
        NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:_infoView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:-0];
        NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:_infoView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeWidth multiplier:1.0 constant:0];
        NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:_infoView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeHeight multiplier:1.0 constant:0];
        [self.view addConstraints:@[top,right,width,height]];
    }
    return _infoView;
}
- (NEEduMenuItem *)handsupItem {
    if (!_handsupItem) {
        _handsupItem = [[NEEduMenuItem alloc] initWithTitle:@"举手" image:[UIImage ne_imageNamed:@"menu_handsup"]];
        _handsupItem.selectTitle = @"举手中";
        _handsupItem.type = NEEduMenuItemTypeHandsup;
        [_handsupItem setSelctedImage:[UIImage ne_imageNamed:@"menu_handsup_select"]];
        [_handsupItem setSelctedTextColor:[UIColor colorWithRed:55/255.0 green:114/255.0 blue:255/255.0 alpha:1.0]];
    }
    return _handsupItem;
}
- (UIView *)shareScreenView {
    if (!_shareScreenView) {
        _shareScreenView = [[UIView alloc] init];
        _shareScreenView.hidden = YES;
        _shareScreenView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _shareScreenView;
}
- (UILabel *)shareScreenMask {
    if (!_shareScreenMask) {
        _shareScreenMask = [[UILabel alloc] init];
        _shareScreenMask.translatesAutoresizingMaskIntoConstraints = NO;
        _shareScreenMask.backgroundColor = [UIColor blackColor];
        _shareScreenMask.font = [UIFont systemFontOfSize:17];
        _shareScreenMask.textColor = [UIColor whiteColor];
        _shareScreenMask.text = @"您正在进行共享屏幕";
        _shareScreenMask.hidden = YES;
        _shareScreenMask.textAlignment = NSTextAlignmentCenter;
    }
    return _shareScreenMask;
}
- (NSMutableIndexSet *)subscribeSet {
    if (!_subscribeSet) {
        _subscribeSet = [NSMutableIndexSet indexSet];
    }
    return _subscribeSet;
}
- (NEEduMenuItem *)chatItem {
    if (!_chatItem) {
       _chatItem = [[NEEduMenuItem alloc] initWithTitle:@"聊天室" image:[UIImage ne_imageNamed:@"menu_chat"]];
        _chatItem.type = NEEduMenuItemTypeChat;
    }
    return _chatItem;
}

#pragma mark - Orientations
-(BOOL)shouldAutorotate {
    return NO;
}
- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskLandscapeRight;
}
- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return UIInterfaceOrientationLandscapeRight;
}
@end
