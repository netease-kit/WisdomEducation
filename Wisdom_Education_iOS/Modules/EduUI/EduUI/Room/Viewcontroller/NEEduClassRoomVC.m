//
//  NEEduClassRoomVC.m
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduClassRoomVC.h"
#import "UIImage+NE.h"
#import "NEEduChatViewController.h"
#import "UIView+Toast.h"

#import <NEWhiteBoard/WhiteBoardStateModel.h>
#import <NEWhiteBoard/NMCTool.h>
#import "NEEduLessonOverView.h"
#import "NEEduLessonInfoView.h"
#import "UIView+NE.h"
#import "NSString+NE.h"
#import "UIImage+NE.h"
#import "NEEduNavigationViewController.h"
#import <SDWebImage/SDWebImage.h>

@interface NEEduClassRoomVC ()<NEEduRoomViewMaskViewDelegate,NMCWhiteboardManagerDelegate,NEScreenShareHostDelegate,NEEduVideoServiceDelegate,NEEduIMChatDelegate,NEEduMessageServiceDelegate,NEEduRoomServiceDelegate>

/// 自己是否在共享
@property (nonatomic, assign) BOOL isSharing;
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) NEEduLessonOverView *classOverView;
@property (nonatomic, strong) NEEduChatViewController *chatVC;
@property (nonatomic, strong) NEEduLessonInfoView *infoView;
//用于chatVC新创建时同步上一次数据
@property (nonatomic, strong) NSMutableArray<NEEduChatMessage *> *messages;
@property (nonatomic, assign) BOOL netReachable;
@property (nonatomic, strong) UILabel *shareScreenMask;


@end

static NSString *kAppGroup = @"group.com.netease.yunxin.app.wisdom.education";

@implementation NEEduClassRoomVC
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger:UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self addNotification];
    self.netReachable = YES;
    [self.navigationController setNavigationBarHidden:NO];
    self.view.backgroundColor = [UIColor whiteColor];
    self.whiteboardWritable = YES;
    [self initMenuItems];
    [self setupSubviews];
    [self.maskView.navView.infoButton addTarget:self action:@selector(infoButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];

    //占位数据
    self.members = [NSMutableArray arrayWithArray:[self placeholderMembers]];
    //请求
    __weak typeof(self)weakSelf = self;
    NCKLogInfo(@"ClassRoom viewDidLoad");
    [[NEEduManager shared] joinRtcAndGetProfileCompletion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
        if (error) {
            NCKLogInfo(@"profileError%@",error);
            [self.view makeToast:[NSString stringWithFormat:@"加入房间失败，请退出重进 %@",error.localizedDescription]];
            return;
        }
        __strong typeof(self)strongSelf = weakSelf;
        [NEEduManager shared].messageService.delegate = weakSelf;
        [NEEduManager shared].rtcService.delegate = weakSelf;
        [NEEduManager shared].roomService.delegate = weakSelf;
        [NEEduManager shared].imService.chatDelegate = weakSelf;
        
        // update room info
        strongSelf.room = profile.snapshot.room;
        [strongSelf updateUIWithRoom:strongSelf.room];
        
        // update members list
        strongSelf.members = [strongSelf showMembersWithJoinedMembers:profile.snapshot.members].mutableCopy;
        [strongSelf.collectionView reloadData];
        [strongSelf updateScreenShare];
        //subclass update UI
        [strongSelf updateUIWithMembers:profile.snapshot.members];
        //load whiteboard view
        [strongSelf addWhiteboardView];
        //load chatroom function
        [strongSelf addChatroom];
        // update menue
        [strongSelf updateMenueItemWithProfile:profile];
        // 推流
        if (strongSelf.pushStreamIfNeed) {
            [strongSelf setupPushStream];
        }
    }];
}
#pragma mark -----------------------------  推流业务  -----------------------------
- (BOOL)pushStreamIfNeed {
    if (![[NEEduManager shared].localUser.role isEqualToString:NEEduRoleHost] ||
        !self.isPushStream) {
        return NO;
    }
    return YES;
}
- (void)setupPushStream {
    // 加入房间后创建推流任务
    //先初始化推流任务
    NERtcLiveStreamTaskInfo *info = [[NERtcLiveStreamTaskInfo alloc] init];
    //taskID 可选字母、数字，下划线，不超过64位
    info.taskID = [NSString stringWithFormat:@"%ld", NEEduManager.shared.localUser.rtcUid];
    // 设置推互动直播推流地址，一个推流任务对应一个推流房间
    info.streamURL = self.room.properties.live.pushUrl;
    // 设置是否进行互动直播录制，请注意与音视频通话录制区分。
    info.serverRecordEnabled = NO;
    // 设置推音视频流还是纯音频流
    
    info.lsMode = kNERtcLsModeVideo;
    
    CGFloat width = UIScreen.mainScreen.bounds.size.width;
    CGFloat heihgt = UIScreen.mainScreen.bounds.size.height;
    //设置整体布局
    NERtcLiveStreamLayout *layout = [[NERtcLiveStreamLayout alloc] init];
    layout.width = width;      //整体布局宽度
    layout.height = heihgt;  //整体布局高度
    layout.backgroundColor = (0 & 0xff) << 16 | (0 & 0xff) << 8 | (0 & 0xff);
    info.layout = layout;
    // 设置直播成员布局
    NERtcLiveStreamUserTranscoding *user1 = [[NERtcLiveStreamUserTranscoding alloc] init];
    user1.uid = [NEEduManager shared].localUser.rtcUid;
    user1.audioPush = true;        // 推流是否发布user1的音频
    user1.videoPush = true; // 推流是否发布user1的视频
    if (user1.videoPush) {
        // 如果发布视频，需要设置一下视频布局参数
        user1.x = width - 40 - 120; // user1 的视频布局x偏移，相对整体布局的左上角
        if (@available(iOS 11.0, *)) {
            user1.y = self.view.safeAreaLayoutGuide.layoutFrame.origin.y + 50;
        } else {
            // Fallback on earlier versions
            user1.y = 50;
        } // user1 的视频布局y偏移，相对整体布局的左上角
        user1.width = 120; // user1 的视频布局宽度
        user1.height = 90; //user1 的视频布局高度
        user1.adaption = kNERtcLsModeVideoScaleCropFill;
    }
    layout.users = @[user1];
    //配置背景占位图片，可以不配置。
    //        NERtcLiveStreamImageInfo *imageInfo = [[NERtcLiveStreamImageInfo alloc] init];
    //        imageInfo.url = url;
    //        imageInfo.x = 0;
    //        imageInfo.y = 0;
    //        imageInfo.width = 720;
    //        imageInfo.height = 1280;
    //        layout.bgImage = imageInfo;
    //调用 addLiveStreamTask 接口添加推流任务
    int ret = [[NERtcEngine sharedEngine] addLiveStreamTask:info
                                                 compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
        
        NSString *toast = !errorCode ? @"添加成功" : [NSString stringWithFormat:@"添加失败 errorcode = %d",errorCode];
        NSLog(@"%@", toast);
    }];
    
    if (ret != 0) {
        NSLog(@"调用添加推流任务失败");
    } else {
        NSLog(@"推流任务成功");
    }
}


- (void)addChatroom {
    if (self.room.properties.chatRoom && [NEEduManager shared].roomService.room.sceneType != NEEduSceneType1V1) {
        __weak typeof(self) weakSelf = self;
        [[NEEduManager shared] joinChatRoomSuccess:^(NEEduChatRoomResponse * _Nonnull response) {
            __strong typeof(self)strongSelf = weakSelf;
            [strongSelf addChatMenue];
        } failed:^(NSError * _Nonnull error) {
            __strong typeof(self)strongSelf = weakSelf;
            [strongSelf.view makeToast:error.localizedDescription];
        }];
    }
}

- (void)addChatMenue {
    NEEduMenuItem *chatItem = [[NEEduMenuItem alloc] initWithTitle:@"聊天室" image:[UIImage ne_imageNamed:@"menu_chat"]];
    chatItem.type = NEEduMenuItemTypeChat;
    self.chatItem = chatItem;
    [self.maskView addItem:self.chatItem];
}

- (void)updateMenueItemWithProfile:(NEEduRoomProfile *)profile {

}

- (NSArray <NEEduHttpUser *> *)placeholderMembers {
    return @[[NEEduHttpUser teacher]];
}

- (NSArray <NEEduHttpUser *> *)showMembersWithJoinedMembers:(NSArray <NEEduHttpUser *> *)members {
    NSMutableArray *muteMembers = [members mutableCopy];
    NEEduHttpUser *member = muteMembers.firstObject;
    if (!member.isTeacher) {
        [muteMembers insertObject:[NEEduHttpUser teacher] atIndex:0];
    }
    return muteMembers.copy;
}
- (void)updateUIWithMembers:(NSArray *)members {
    
}

- (void)setupSubviews {
    self.view.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    [self.view addSubview:self.maskView];
    if (@available(iOS 11.0, *)) {
        [NSLayoutConstraint activateConstraints:@[
            [self.maskView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor],
            [self.maskView.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor],
            [self.maskView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor],
            [self.maskView.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor],
        ]];
    }else {
        [NSLayoutConstraint activateConstraints:@[
            [self.maskView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor],
            [self.maskView.topAnchor constraintEqualToAnchor:self.view.topAnchor],
            [self.maskView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor],
            [self.maskView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor],
        ]];
    }
    
    [self.view addSubview:self.contentView];
    if (@available(iOS 11.0, *)) {
        [NSLayoutConstraint activateConstraints:@[
            [self.contentView.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor constant:40],
            [self.contentView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:0],
            [self.contentView.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-60],
        ]];
    }else {
        [NSLayoutConstraint activateConstraints:@[
            [self.contentView.topAnchor constraintEqualToAnchor:self.view.topAnchor constant:40],
            [self.contentView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:0],
            [self.contentView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor constant:-60],
        ]];
    }
    
    [self.view addSubview:self.collectionView];
    if (@available(iOS 11.0, *)) {
        [NSLayoutConstraint activateConstraints:@[
            [self.collectionView.leftAnchor constraintEqualToAnchor:self.contentView.rightAnchor constant:8],
            [self.collectionView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-40],
            [self.collectionView.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor constant:50],
            [self.collectionView.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-60],
            [self.collectionView.widthAnchor constraintEqualToConstant:120]
        ]];
    }else {
        [NSLayoutConstraint activateConstraints:@[
            [self.collectionView.leftAnchor constraintEqualToAnchor:self.contentView.rightAnchor constant:8],
            [self.collectionView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-40],
            [self.collectionView.topAnchor constraintEqualToAnchor:self.view.topAnchor constant:50],
            [self.collectionView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor constant:-60],
            [self.collectionView.widthAnchor constraintEqualToConstant:120]
        ]];
    }
    
    [self.contentView addSubview:self.shareScreenView];
    [NSLayoutConstraint activateConstraints:@[
        [self.shareScreenView.topAnchor constraintEqualToAnchor:self.contentView.topAnchor],
        [self.shareScreenView.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor],
        [self.shareScreenView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor],
        [self.shareScreenView.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor]
    ]];

    [self.view addSubview:self.lessonStateView];
    [NSLayoutConstraint activateConstraints:@[
        [self.lessonStateView.topAnchor constraintEqualToAnchor:self.contentView.topAnchor],
        [self.lessonStateView.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor],
        [self.lessonStateView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor],
        [self.lessonStateView.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor]
    ]];

    [self.view addSubview:self.shareScreenMask];
    [NSLayoutConstraint activateConstraints:@[
        [self.shareScreenMask.topAnchor constraintEqualToAnchor:self.shareScreenView.topAnchor],
        [self.shareScreenMask.leftAnchor constraintEqualToAnchor:self.shareScreenView.leftAnchor],
        [self.shareScreenMask.rightAnchor constraintEqualToAnchor:self.view.rightAnchor],
        [self.shareScreenMask.bottomAnchor constraintEqualToAnchor:self.shareScreenView.bottomAnchor]
    ]];
    
    [self.view addSubview:self.classOverView];
    [NSLayoutConstraint activateConstraints:@[
        [self.classOverView.topAnchor constraintEqualToAnchor:self.view.topAnchor],
        [self.classOverView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor],
        [self.classOverView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor],
        [self.classOverView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor]
    ]];
}
- (void)addWhiteboardView {
    // 读取配置开关
//    NMCWhiteboardManager.sharedManager.configRead = YES;
    [self.contentView insertSubview:self.boardView belowSubview:self.shareScreenView];
    [NSLayoutConstraint activateConstraints:@[
        [self.boardView.topAnchor constraintEqualToAnchor:self.contentView.topAnchor],
        [self.boardView.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor],
        [self.boardView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor],
        [self.boardView.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor]
    ]];
}
- (void)initMenuItems {
    NEEduMenuItem *audoItem = [[NEEduMenuItem alloc] initWithTitle:@"静音" image:[UIImage ne_imageNamed:@"menu_audio"]];
    audoItem.selectTitle = @"解除静音";
    audoItem.type = NEEduMenuItemTypeAudio;
    [audoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_audio_off"]];
    
    NEEduMenuItem *videoItem = [[NEEduMenuItem alloc] initWithTitle:@"关闭视频" image:[UIImage ne_imageNamed:@"menu_video"]];
    videoItem.selectTitle = @"开启视频";
    videoItem.type = NEEduMenuItemTypeVideo;
    [videoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_video_off"]];

    NEEduMenuItem *shareItem = [[NEEduMenuItem alloc] initWithTitle:@"共享屏幕" image:[UIImage ne_imageNamed:@"menu_share_screen"]];
    shareItem.type = NEEduMenuItemTypeShareScreen;
    shareItem.selectTitle = @"停止共享";
    [shareItem setSelctedImage:[UIImage ne_imageNamed:@"menu_share_screen_stop"]];
    
    NEEduMenuItem *membersItem = [[NEEduMenuItem alloc] initWithTitle:@"课堂成员" image:[UIImage ne_imageNamed:@"menu_members"]];
    membersItem.type = NEEduMenuItemTypeMembers;
    
    self.menuItems = @[audoItem,videoItem,shareItem,membersItem];
}

- (void)showViewWithItem:(NEEduMenuItem *)item {
    switch (item.type) {
        case NEEduMenuItemTypeAudio:
            [self turnOnAudio:!item.isSelected];
            break;
        case NEEduMenuItemTypeVideo:
            [self turnOnVideo:!item.isSelected];
            break;
        case NEEduMenuItemTypeShareScreen:
            [self turnOnShareScreen:!item.isSelected item:item];
            break;
        case NEEduMenuItemTypeMembers:
            [self showMembersViewWithitem:item];
            break;
        case NEEduMenuItemTypeChat:
            [self showChatViewWithitem:item];
            break;
        case NEEduMenuItemTypeHandsup:
            [self handsupItem:item];
            break;
        default:
            break;
    }
}

- (void)turnOnAudio:(BOOL)isOn {
    [[NEEduManager shared].userService localUserAudioEnable:isOn result:^(NSError * _Nonnull error) {
        if (error) {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)turnOnVideo:(BOOL)isOn {
    [[NEEduManager shared].userService localUserVideoEnable:isOn result:^(NSError * _Nonnull error) {
        if (error) {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)turnOnShareScreen:(BOOL)isOn item:(NEEduMenuItem *)item {
    if ([NEEduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateNone) {
        [self.view makeToast:@"请先开始上课"];
        return;
    }
    if (@available(iOS 12.0, *)) {
        if (isOn) {
            if (!self.shareScreenView.hidden) {
                [self.view makeToast:@"正在共享中，请稍后再试"];
                return;
            }
            if (self.isSharing) {
                [self.view makeToast:@"共享还未结束，请稍后再试"];
                return;
            }
            __weak typeof(self)weakSelf = self;
            [self.view showAlertViewOnVC:self withTitle:@"屏幕共享" subTitle:@"确认开启屏幕共享" confirm:^{
                [weakSelf startRecording];
            }];
        }else {
            [self stopAllScreenShare];
        }
    }else {
        [self.view makeToast:@"屏幕共享仅支持iOS12.0及以上版本"];
    }
}
- (void)startRecording {
    if (self.isSharing) {
        return;
    }
    [self setupShareKit];
    [self.shareHost launchBroadcaster];
}
- (void)startScreenShare {
    // RTC开启共享辅流
    NERtcVideoSubStreamEncodeConfiguration *config = [[NERtcVideoSubStreamEncodeConfiguration alloc] init];
    int result = [NERtcEngine.sharedEngine startScreenCapture:config];
    if (result != 0) {
        [self.view makeToast:[NSString stringWithFormat:@"Rtc开启屏幕共享失败code:%d",result]];
        [self.shareHost stopBroadcaster];
        return;
    }
    __weak typeof(self)weakSelf = self;
    [[NEEduManager shared].userService localShareScreenEnable:YES result:^(NSError * _Nonnull error) {
        if (error) {
            [weakSelf.view makeToast:error.localizedDescription];
            [self.shareHost stopBroadcaster];
        }else {
            weakSelf.isSharing = YES;
            weakSelf.shareScreenMask.hidden = NO;
            [weakSelf updateShareItemWithSelected:YES];
        }
    }];
}

//发送停止共享请求、停止录制、停止Rtc辅流
- (void)stopAllScreenShare {
    if (!self.isSharing) {
        return;
    }
    __weak typeof(self)weakSelf = self;
    [[NEEduManager shared].userService localShareScreenEnable:NO result:^(NSError * _Nonnull error) {
        if (error) {
            [self.view makeToast:error.localizedDescription];
        }else {
            weakSelf.isSharing = NO;
            weakSelf.shareScreenMask.hidden = YES;
            [weakSelf updateShareItemWithSelected:NO];
            [weakSelf.shareHost stopBroadcaster];
            [NERtcEngine.sharedEngine stopScreenCapture];
        }
    }];
}
/// 仅停止录制和rtc辅流，不发送请求。
- (void)stopRecord {
    self.isSharing = NO;
    self.shareScreenMask.hidden = YES;
    [self.shareHost stopBroadcaster];
    [NERtcEngine.sharedEngine stopScreenCapture];
}
#pragma mark - NEScreenShareHostDelegate
- (void)onBroadcastStarted {
    [self startScreenShare];
}

- (void)onBroadcastFinished {
    if (self.isSharing) {
        int result = [NERtcEngine.sharedEngine stopScreenCapture];
        if (result != 0) {
            [self.view makeToast:[NSString stringWithFormat:@"Rtc停止共享失败:%d",result]];
            return;
        }
        __weak typeof(self)weakSelf = self;
        [[NEEduManager shared].userService localShareScreenEnable:NO result:^(NSError * _Nonnull error) {
            if (error) {
                [self.view makeToast:error.localizedDescription];
            }else {
                weakSelf.isSharing = NO;
                weakSelf.shareScreenMask.hidden = YES;
                [weakSelf updateShareItemWithSelected:NO];
            }
        }];
    }
}

- (void)updateShareItemWithSelected:(BOOL)selected {
    for (NEEduMenuItem *item in [self.maskView.stackView arrangedSubviews]) {
        if (item.type == NEEduMenuItemTypeShareScreen) {
            item.isSelected = selected;
            break;
        }
    }
}

- (void)onBroadcastPaused {
}

- (void)onBroadcastResumed {
}

- (void)onReceiveVideoFrameInfo:(NSDictionary<NSString *,id> *)videoFrameInfo {
    NERtcVideoFrame *frame = [[NERtcVideoFrame alloc] init];
    frame.format = kNERtcVideoFormatI420;
    frame.width = [videoFrameInfo[NEScreenShareVideoFrameWidthKey] unsignedIntValue];
    frame.height = [videoFrameInfo[NEScreenShareVideoFrameHeightKey] unsignedIntValue];
    frame.buffer = (void *)[videoFrameInfo[NEScreenShareVideoFrameDataKey] bytes];
    frame.timestamp = [videoFrameInfo[NEScreenShareVideoFrameTimestampKey] unsignedLongLongValue];
    NSNumber *orientation = videoFrameInfo[NEScreenShareVideoFrameOrientationKey];
    switch (orientation.unsignedIntValue) {
        case kCGImagePropertyOrientationLeft: {
            frame.rotation = kNERtcVideoRotation_90;
            break;
        }
        case kCGImagePropertyOrientationRight: {
            frame.rotation = kNERtcVideoRotation_270;
            break;
        }
        default:
            break;
    }
    int ret = [NERtcEngine.sharedEngine pushExternalVideoFrame:frame];
    if (ret != 0 && ret != kNERtcErrFatal) {
        NSLog(@"发送视频流失败: %@", NERtcErrorDescription(ret));
        return;
    }
}


- (void)showMembersViewWithitem:(NEEduMenuItem *)item {
    if (!self.membersVC) {
        NSMutableArray *memberArray = [NSMutableArray array];
        self.membersVC = [[NEEduMembersVC alloc] init];
        self.membersVC.muteChat = [NEEduManager shared].profile.snapshot.room.states.muteChat.value;
        for (NEEduHttpUser *user in [NEEduManager shared].profile.snapshot.members) {
            if (![user.role isEqualToString:NEEduRoleHost]) {
                NEEduMember *member = [self memberFromHttpUser:user];
                [memberArray addObject:member];
            }
        }
        self.membersVC.members = memberArray;
        self.membersVC.modalPresentationStyle = UIModalPresentationFullScreen;
    }
    [self presentViewController:self.membersVC animated:YES completion:nil];
}
- (NEEduMember *)memberFromHttpUser:(NEEduHttpUser *)user {
    NEEduMember *member = [[NEEduMember alloc] init];
    member.name = user.userName;
    member.userID = user.userUuid;
    member.hasVideo = user.streams.video.value;
    member.hasAudio = user.streams.audio.value;
    member.shareScreenEnable = user.properties.screenShare.value;
    member.whiteboardEnable = user.properties.whiteboard.drawable;
    member.online = user.properties.avHandsUp.value == NEEduHandsupStateTeaAccept ? YES : NO;
    if ([[NEEduManager shared].localUser isTeacher]) {
        member.videoEnable = YES;
        member.audioEnable = YES;
        member.showMoreButton = YES;
    }else {
        if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
            member.videoEnable = YES;
            member.audioEnable = YES;
        }else {
            member.videoEnable = NO;
            member.audioEnable = NO;
        }
        member.showMoreButton = NO;
    }
    if ([NEEduManager shared].roomService.room.sceneType == NEEduSceneTypeBig) {
        member.isBigClass = YES;
    }
    return member;
}
- (void)showChatViewWithitem:(NEEduMenuItem *)item {
    self.chatVC = [[NEEduChatViewController alloc] init];
    self.chatVC.messages = self.messages;
    self.chatVC.muteChat = [NEEduManager shared].profile.snapshot.room.states.muteChat.value;
    NEEduNavigationViewController *chatNav = [[NEEduNavigationViewController alloc] initWithRootViewController:self.chatVC];
    chatNav.modalPresentationStyle = UIModalPresentationFullScreen;
    [self presentViewController:chatNav animated:YES completion:nil];
    //更新菜单底部聊天室红点
    self.chatItem.badgeLabel.hidden = YES;
}
//放在子类中实现
- (void)handsupItem:(NEEduMenuItem *)item {

}
- (void)setupShareKit {
    if (!self.shareHost) {
        NEScreenShareHostOptions *options = [[NEScreenShareHostOptions alloc] init];
        options.appGroup = kAppGroup;
        options.delegate = self;
        self.shareHost = [[NEScreenShareHost alloc] initWithOptions:options];
    }
}

// 音频流 视频流 屏幕共享辅流
- (void)onVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    [self updateAVEnable:enable user:user];
    if (self.membersVC) {
        for (NEEduMember *member in self.membersVC.members) {
            if ([member.userID isEqualToString:user.userUuid]) {
                member.hasVideo = enable;
                break;
            }
        }
    }
    if (self.membersVC.presentingViewController) {
        [self.membersVC reloadData];
    }
}
- (void)onAudioStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    [self updateAVEnable:enable user:user];
    if (self.membersVC) {
        for (NEEduMember *member in self.membersVC.members) {
            if ([member.userID isEqualToString:user.userUuid]) {
                member.hasAudio = enable;
                break;
            }
        }
    }
    if (self.membersVC.presentingViewController) {
        [self.membersVC reloadData];
    }
}

- (void)updateAVEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < array.count; i++) {
        NEEduHttpUser *tmpUser = array[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [array replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = array;
    [self.collectionView reloadData];
    [self updateMyselfAVItemWithUser:user];
}

#pragma mark - NEEduMessageServiceDelegate
- (void)onSubVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        return;
    }
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    // 设置屏幕共享画布
    NERtcVideoCanvasExtention *canvas = [[NERtcVideoCanvasExtention alloc] init];
    canvas.uid = user.rtcUid;
    if (enable) {
        canvas.container = self.shareScreenView;
    }else {
        canvas.container = nil;
    }
    [[NEEduManager shared].rtcService setupSubStreamVideo:canvas];
    self.shareScreenView.hidden = !enable;
}

- (void)onVideoAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        [self.view makeToast:[NSString stringWithFormat:@"老师%@了你的摄像头",enable ? @"打开" : @"关闭"]];
        [self turnOnVideo:enable];
    }
}

- (void)onAudioAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        [self.view makeToast:[NSString stringWithFormat:@"老师%@了你的麦克风",enable ? @"打开" : @"关闭"]];
        [self turnOnAudio:enable];
    }
}

- (void)onWhiteboardAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    self.whiteboardWritable = enable;
    //如果是自己的权限被修改 设置白板
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        NSString *toast = enable?@"老师授予了你白板权限":@"老师取消了你白板权限";
        [self.view makeToast:toast];
        [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
        [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
    }
    [self.collectionView reloadData];
    //更新membervc
    if (self.membersVC) {
        for (NEEduMember *member in self.membersVC.members) {
            if ([member.userID isEqualToString:user.userUuid]) {
                member.whiteboardEnable = self.whiteboardWritable;
                break;
            }
        }
    }
    if (self.membersVC.presentingViewController) {
        [self.membersVC reloadData];
    }
}

- (void)onScreenShareAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    //如果是自己的权限被修改 更新底部菜单栏
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        NSString *toast = enable?@"老师授予了你共享权限":@"老师取消了你共享权限";
        [self.view makeToast:toast];
        if (enable) {
            NEEduMenuItem *shareItem = [[NEEduMenuItem alloc] initWithTitle:@"共享屏幕" image:[UIImage ne_imageNamed:@"menu_share_screen"]];
            shareItem.type = NEEduMenuItemTypeShareScreen;
            shareItem.selectTitle = @"停止共享";
            [shareItem setSelctedImage:[UIImage ne_imageNamed:@"menu_share_screen_stop"]];
            [self.maskView insertItem:shareItem atIndex:2];
        }else {
            [self.maskView removeItemType:NEEduMenuItemTypeShareScreen];
            [self stopRecord];
        }
    }
    //更新membervc
    if (self.membersVC) {
        for (NEEduMember *member in self.membersVC.members) {
            if ([member.userID isEqualToString:user.userUuid]) {
                member.shareScreenEnable = enable;
                break;
            }
        }
    }
    if (self.membersVC.presentingViewController) {
        [self.membersVC reloadData];
    }
}

- (void)onLessonStateChange:(NEEduLessonStep *)step roomUuid:(NSString *)roomUuid {
    //0/1/2 (初始化/已开始/已结束)
    [[NSUserDefaults standardUserDefaults] setObject:roomUuid forKey:kLastRoomUuid];
    
    [self.maskView.navView updateRoomState:self.room serverTime:step.time];
    if (step.value == NEEduLessonStateClassOver) {
        [self classOver];
        return;
    }
    if (![[NEEduManager shared].localUser isTeacher]) {
        if (step.value == NEEduLessonStateClassIn) {
            self.lessonStateView.hidden = YES;
        }
    }
}

- (void)onLessonMuteAllText:(BOOL)mute roomUuid:(NSString *)roomUuid {
    if (![[NEEduManager shared].localUser isTeacher]) {
        if (self.chatVC) {
            [self.chatVC updateMuteChat:mute];
        }
        NSString *string = mute ? @"聊天室已全体禁言" : @"聊天室已取消全体禁言";
        [[UIApplication sharedApplication].keyWindow makeToast:string];
    }
}

- (void)onUserTokenExpired:(NEEduHttpUser *)user {
    __weak typeof(self)weakSelf = self;
    [self.view makeToast:@"当前账号Token校验失败" duration:2.0 position:CSToastPositionCenter title:nil image:nil style:nil completion:^(BOOL didTap) {
        [weakSelf leaveClass];
    }];
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

#pragma mark - NEEduRoomViewMaskViewDelegate
//结束课堂按钮
- (void)classOverBack {
    [self dismissViewControllerAnimated:YES completion:nil];
}
//返回按钮
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

- (void)classOver {
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
    [[NEEduManager shared] leaveClassroom];
    [[NMCWhiteboardManager sharedManager] callWebLogoutIM];
    [[NMCWhiteboardManager sharedManager] clearWebViewCache];
    [[NEEduManager shared] destoryClassroom];
}
- (void)onSectionStateChangeAtIndex:(NSInteger)index item:(NEEduMenuItem *)item {
    [self showViewWithItem:item];
}
- (void)rightButton:(UIButton *)button selected:(BOOL)selected {
    if ([[NEEduManager shared].localUser isTeacher]) {
        int state = selected ? 2 : 1;
        NSString *title = selected ? @"结束课堂":@"确认开始上课";
        NSString *msg = selected ? @"结束课堂后老师和学生均会跳转课堂结束画面，支持查看课程回放":@"开始教学内容将同步至学生端，并正式开始课堂录制";
        __weak typeof(self)weakSelf = self;
        [weakSelf.view showAlertViewOnVC:weakSelf withTitle:title subTitle:msg confirm:^{
            button.userInteractionEnabled = NO;
            [[NEEduManager shared].roomService startLesson:state completion:^(NSError * _Nonnull error, NEEduPropertyItem * _Nonnull item) {
                button.userInteractionEnabled = YES;
                if (error) {
                    [weakSelf.view makeToast:error.localizedDescription];
                }else {
                    [weakSelf.maskView selectButton:!selected];
                }
            }];
        }];
    }
}

- (void)infoButtonClick:(UIButton *)button {
    self.infoView.hidden = !self.infoView.hidden;
}

#pragma mark - NEEduIMChatDelegate
- (void)didRecieveChatMessages:(NSArray<NIMMessage *> *)messages {
    //接收成功
    NIMMessage *imMessage = messages.firstObject;
    NIMMessageChatroomExtension *ext = imMessage.messageExt;
    NEEduChatMessage *message = [[NEEduChatMessage alloc] init];
    message.imMessage = imMessage;
    message.userName = ext.roomNickname;
    message.content = imMessage.text;
    message.myself = NO;
    message.timestamp = [[NSDate date] timeIntervalSince1970];
    if([imMessage messageType] == NIMMessageTypeImage) {
        message.type = NEEduChatMessageTypeImage;
        NIMImageObject *originMessageObject = [imMessage messageObject];
        message.imageUrl = originMessageObject.url;
        message.imageThumbUrl = originMessageObject.thumbUrl;
        [[SDWebImageDownloader sharedDownloader] downloadImageWithURL:[NSURL URLWithString:originMessageObject.thumbUrl] completed:^(UIImage * _Nullable image, NSData * _Nullable data, NSError * _Nullable error, BOOL finished) {
            if (error) {
                return;
            }
            message.thumbImage = image;
            message.contentSize = [image ne_showSizeWithMaxWidth:176 maxHeight:190];
            [self addTimeMessage:message];
            [self addMessage:message];
            if (self.chatVC.presentingViewController) {
                [self.chatVC reloadTableViewToBottom:YES];
            }else {
                self.chatItem.badgeLabel.hidden = NO;
            }
        }];
        
    }else if ([imMessage messageType] == NIMMessageTypeText) {
        message.type = NEEduChatMessageTypeText;
        message.contentSize = [imMessage.text sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
        [self addTimeMessage:message];
        [self addMessage:message];
        if (self.chatVC.presentingViewController) {
            [self.chatVC reloadTableViewToBottom:YES];
        }else {
            self.chatItem.badgeLabel.hidden = NO;
        }
    }
}

- (void)willSendMessage:(NIMMessage *)message {
    //去重
    NSLog(@"willSendMessage:%@ state:%d",message.messageId,message.deliveryState);
    for (NEEduChatMessage *eduMessage in self.messages) {
        if ([eduMessage.imMessage.messageId isEqualToString:message.messageId]) {
            eduMessage.sendState = NEEduChatMessageSendStateNone;
            if (self.chatVC.presentingViewController) {
                [self.chatVC reloadTableViewToBottom:NO];
            }
            return;
        }
    }
    NEEduChatMessage *eduMessage = [[NEEduChatMessage alloc] init];
    eduMessage.imMessage = message;
    NSString *role = [[NEEduManager shared].localUser isTeacher] ? @"老师":@"学生";
    eduMessage.userName = [NSString stringWithFormat:@"%@(%@)",[NEEduManager shared].localUser.userName,role];
    eduMessage.content = message.text;
    if([message messageType] == NIMMessageTypeImage) {
        NIMImageObject *originMessageObject = [message messageObject];
        UIImage *thumbImage = [UIImage imageWithContentsOfFile:originMessageObject.thumbPath];
        eduMessage.contentSize = [thumbImage ne_showSizeWithMaxWidth:176 maxHeight:190];
        eduMessage.thumbImage = thumbImage;
        eduMessage.imageUrl = originMessageObject.path;
        eduMessage.imageThumbUrl = originMessageObject.thumbPath;
        eduMessage.type = NEEduChatMessageTypeImage;
    }else {
        eduMessage.type = NEEduChatMessageTypeText;
        eduMessage.contentSize = [message.text sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
    }
    eduMessage.myself = YES;
    eduMessage.timestamp = [[NSDate date] timeIntervalSince1970];
    eduMessage.sendState = NEEduChatMessageSendStateNone;
    
    [self addTimeMessage:eduMessage];
    [self addMessage:eduMessage];
    NSLog(@"sendMessage count:%d",self.messages.count);
    if (self.chatVC) {
        [self.chatVC reloadTableViewToBottom:YES];
    }
}

- (void)didSendMessage:(NIMMessage *)message error:(NSError *)error {
    NSLog(@"didSendMessage:%@ error:%@",message.messageId,error);
    for (NEEduChatMessage *eduMessage in self.messages) {
        if ([eduMessage.imMessage.messageId isEqualToString:message.messageId]) {
            eduMessage.sendState = error ? NEEduChatMessageSendStateFailure :NEEduChatMessageSendStateSuccess;;
            if (self.chatVC.presentingViewController) {
                [self.chatVC reloadTableViewToBottom:NO];
            }
            return;
        }
    }
}

- (void)addTimeMessage:(NEEduChatMessage *)message {
    //添加时间显示消息
    NEEduChatMessage *lastMessage = self.messages.lastObject;
    if (lastMessage.type == NEEduChatMessageTypeText) {
        CGFloat dur = message.timestamp - lastMessage.timestamp;
        CGFloat min = dur/60;
        if (min > 5) {
            NEEduChatMessage *timeMessage = [[NEEduChatMessage alloc] init];
            timeMessage.content = [NSString stringFromDate:[NSDate date]];
            timeMessage.myself = NO;
            timeMessage.contentSize = [timeMessage.content sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
            timeMessage.type = NEEduChatMessageTypeTime;
            [self addMessage:timeMessage];
        }
    }
}
- (void)addMessage:(NEEduChatMessage *)message {
    if (self.messages.count >= 5000) {
        [self.messages removeObjectAtIndex:0];
    }
    [self.messages addObject:message];
}
#pragma mark - NEEduRoomServiceDelegate
- (void)netStateChangeWithState:(AFNetworkReachabilityStatus)state {
    if (state == AFNetworkReachabilityStatusNotReachable || state == AFNetworkReachabilityStatusUnknown) {
        //断网
        //网络展示无网
        NCKLogInfo(@"net change:%d",state);
        self.netReachable = NO;
    }else {
        //有网
        // 请求房间快照
        if (self.netReachable) {
            //WIFI和4G之间的切换不需要更新
            return;
        }
        NCKLogInfo(@"net change:%d",state);
        self.netReachable = YES;
        __weak typeof(self)weakSelf = self;
        [[NEEduManager shared].roomService getRoomProfile:self.room.roomUuid completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
            if(error){
                if(error.code== NEEduErrorTypeRoomNotFound){
                    [self classOver];
                }
                return;
            }
            weakSelf.room = profile.snapshot.room;
            /**
             1.房间状态刷新
             2.更新学生列表
             3.更新用户权限的变化（音视频开关/白板/屏幕共享）
             4.更新屏幕共享
             5.更新举手 上台
             6.更新成员列表
             */
            [weakSelf updateUIWithRoom:profile.snapshot.room];
            [weakSelf membersWithProfile:profile];
            
            [weakSelf updateMemberAuthorizationWithProfile:profile];
            [weakSelf updateScreenShare];
            
            [weakSelf updateHandsupStateWithMembers:profile.snapshot.members];
            [weakSelf updateMemberVCWithProfile:profile];
        }];
    }
}
/*
 // update room info
 strongSelf.room = profile.snapshot.room;
 [strongSelf updateUIWithRoom:strongSelf.room];
 
 // update members list
 strongSelf.members = [strongSelf showMembersWithJoinedMembers:profile.snapshot.members].mutableCopy;
 [strongSelf.collectionView reloadData];
 
 [strongSelf updateScreenShare];
 //subclass update UI
 [strongSelf updateUIWithMembers:profile.snapshot.members];
 
 //load whiteboard view
 [strongSelf addWhiteboardView];
 //load chatroom function
 [strongSelf addChatroom];
 // update menue
 [strongSelf updateMenueItemWithProfile:profile];
 */

- (void)updateUIWithRoom:(NEEduHttpRoom *)room {
    [self.maskView.navView updateRoomState:room serverTime:[NEEduManager shared].profile.ts];
    //初始化上课按钮
    if ([[NEEduManager shared].localUser isTeacher]) {
        //开始上课/结束上课
        self.maskView.startLesson.hidden = NO;
        if ([NEEduManager shared].profile.snapshot.room.states.step.value ==  NEEduLessonStateClassIn) {
            [self.maskView selectButton:YES];
        }else {
            [self.maskView selectButton:NO];
        }
    }else {
//        学生在线的话 下讲台
        [self.maskView.startLesson setTitle:@"下讲台" forState:UIControlStateSelected];
        self.maskView.startLesson.hidden = YES;
        if ([NEEduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateClassIn) {
            self.lessonStateView.hidden = YES;
        }else {
            self.lessonStateView.hidden = NO;
        }
    }
}
- (NSArray <NEEduHttpUser *>*)membersWithProfile:(NEEduRoomProfile *)profile {
    return nil;
}
- (void)updateWhiteboardWritable:(BOOL)writable {
    if (self.whiteboardWritable != writable) {
        self.whiteboardWritable = writable;
        [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
        [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
    }
}
- (void)updateMemberAuthorizationWithProfile:(NEEduRoomProfile *)profile {
    NSLog(@"video:%@ audio:%@",[NEEduManager shared].localUser.properties.streamAV.video,[NEEduManager shared].localUser.properties.streamAV.audio);
    for (NEEduHttpUser *user in profile.snapshot.members) {
        if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
            NEEduHttpUser *localUser = [NEEduManager shared].localUser;
            if (user.properties.streamAV.video) {
                //update video
                if (localUser.properties.streamAV.video && ![user.properties.streamAV.video isEqualToNumber:localUser.properties.streamAV.video]) {
                    [self onVideoAuthorizationEnable:user.properties.streamAV.video.boolValue user:user];
                }else {
                    localUser.properties.streamAV.video = user.properties.streamAV.video;
                    [self onVideoAuthorizationEnable:user.properties.streamAV.video.boolValue user:user];
                }
            }
            if (user.properties.streamAV.audio) {
                //update audio
                if (localUser.properties.streamAV.audio && ![user.properties.streamAV.audio isEqualToNumber:localUser.properties.streamAV.audio]) {
                    [self onAudioAuthorizationEnable:user.properties.streamAV.audio.boolValue user:user];
                }else {
                    localUser.properties.streamAV.audio = user.properties.streamAV.audio;
                    [self onAudioAuthorizationEnable:user.properties.streamAV.audio.boolValue user:user];
                }
            }
            
            if (user.properties.whiteboard.drawable != localUser.properties.whiteboard.drawable) {
                [self onWhiteboardAuthorizationEnable:user.properties.whiteboard.drawable user:user];
            }
            if (user.properties.screenShare.value != localUser.properties.screenShare.value) {
                [self onScreenShareAuthorizationEnable:user.properties.screenShare.value user:user];
            }
            break;
        }
    }
}

- (void)updateScreenShare {
    NEEduHttpUser *user = [[NEEduManager shared].userService userIsShareScreen];
    if (user) {
        //共享按钮状态改变为选择
        [self onSubVideoStreamEnable:YES user:user];
    }
}
- (void)updateHandsupStateWithMembers:(NSArray<NEEduHttpUser *> *)members {
    //子类中实现
}
- (void)updateMemberVCWithProfile:(NEEduRoomProfile *)profile {
    if (self.membersVC) {
        NSMutableArray *memberArray = [NSMutableArray array];
        for (NEEduHttpUser *user in profile.snapshot.members) {
            if (![user.role isEqualToString:NEEduRoleHost]) {
                NEEduMember *member = [self memberFromHttpUser:user];
                [memberArray addObject:member];
            }
        }
        self.membersVC.members = memberArray;
        self.membersVC.muteChat = profile.snapshot.room.states.muteChat.value;
        if (self.membersVC.presentingViewController) {
            [self.membersVC loadData];
            [self.membersVC reloadData];
        }
    }
}
#pragma mark - private
- (void)updateMyselfAVItemWithUser:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        for (int i = 0; i < self.maskView.stackView.arrangedSubviews.count; i ++) {
            NEEduMenuItem *item = self.maskView.stackView.arrangedSubviews[i];
            if (item.type == NEEduMenuItemTypeVideo) {
                item.isSelected = !user.streams.video.value;
            }
            if (item.type == NEEduMenuItemTypeAudio) {
                item.isSelected = !user.streams.audio.value;
            }
            if (item.type == NEEduMenuItemTypeShareScreen) {
                item.isSelected = user.streams.subVideo.value;
            }
        }
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
#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.members.count;
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NEEduVideoCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:cellID forIndexPath:indexPath];
    NEEduHttpUser *user = self.members[indexPath.row];
    cell.delegate = self;
    cell.member = user;
    return cell;
}
#pragma mark - get

- (NEEduRoomViewMaskView *)maskView {
    if (!_maskView) {
        _maskView = [[NEEduRoomViewMaskView alloc] initWithMenuItems:self.menuItems];
        _maskView.delegate = self;
    }
    return _maskView;
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
- (UIView *)shareScreenView {
    if (!_shareScreenView) {
        _shareScreenView = [[UIView alloc] init];
        _shareScreenView.hidden = YES;
        _shareScreenView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _shareScreenView;
}
- (NEEduLessonStateView *)lessonStateView {
    if (!_lessonStateView) {
        _lessonStateView = [[NEEduLessonStateView alloc] init];
        _lessonStateView.hidden = YES;
    }
    return _lessonStateView;
}
- (NEEduLessonOverView *)classOverView {
    if (!_classOverView) {
        _classOverView = [[NEEduLessonOverView alloc] init];
        [_classOverView.backButton addTarget:self action:@selector(classOverBack) forControlEvents:UIControlEventTouchUpInside];
        _classOverView.hidden = YES;
    }
    return _classOverView;
}
- (NSMutableArray<NEEduChatMessage *> *)messages {
    if (!_messages) {
        _messages = [NSMutableArray array];
    }
    return _messages;
}
- (NEEduLessonInfoView *)infoView {
    if (!_infoView) {
        _infoView = [[NEEduLessonInfoView alloc] init];
        self.infoView.lessonItem.titleLabel.text = self.room.roomUuid;
        self.infoView.lessonName.text = self.room.roomName;
        NEEduHttpUser *user = self.members.firstObject;
        self.infoView.teacherName.text = user.userName;
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

- (UIView *)contentView {
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
        _contentView.backgroundColor = [UIColor whiteColor];
        _contentView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _contentView;
}

#pragma mark - whiteboard
- (WKWebView *)boardView {
    if (!_boardView) {
        _boardView = [[NMCWhiteboardManager sharedManager] createWebViewFrame:CGRectZero];
        [NMCWhiteboardManager sharedManager].delegate = self;
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

#pragma mark - NMCWhiteboardManagerDelegate
- (void)onWebPageLoaded {
    NSLog(@"wb: onWebPageLoaded");
    NMCWebLoginParam *param = [[NMCWebLoginParam alloc] init];
    param.appKey = [NEEduManager shared].imKey;
    param.uid = @([NEEduManager shared].localUser.rtcUid);
    param.channelName = [NEEduManager shared].profile.snapshot.room.properties.whiteboard.channelName;
    param.record = YES;
    param.debug = NO;
    param.nickname = [NEEduManager shared].localUser.userName;
    [[NMCWhiteboardManager sharedManager] callWebJoinRoom:param];
}

- (void)onWebGetAuth {
    NSLog(@"wb: onWebGetAuth");
    [[NMCWhiteboardManager sharedManager] sendAuthNonce:[NEEduManager shared].localUser.wbAuth.nonce curTime:[NEEduManager shared].localUser.wbAuth.curTime checksum:[NEEduManager shared].localUser.wbAuth.checksum];
}

- (void)onWebLoginIMSucceed {
    NSLog(@"wb: onWebLoginIMSucceed");
}

- (void)onWebCreateWBSucceed {
}

- (void)onWebJoinWBSucceed {
    NMCTool *tool = [[NMCTool alloc] init];
    WhiteboardItem *selectionItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameSelection];
    WhiteboardItem *penItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNamePen];
    WhiteboardItem *shapeItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameShape];
    WhiteboardItem *zoomLevel = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameZoomLevel];
    WhiteboardItem *moreItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameMore];
    WhiteboardItem *eraserItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameEraser];
    WhiteboardItem *clearItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameClear];
    WhiteboardItem *undoItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameUndo];
    WhiteboardItem *redoItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameRedo];
    WhiteboardItem *zoomInItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameZoomIn];
    WhiteboardItem *zoomOutItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameZoomOut];
    WhiteboardItem *fitToContent = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameFitToConent];
    WhiteboardItem *fitToDoc = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameFitToDoc];
    WhiteboardItem *pan = [[WhiteboardItem alloc] initWithName:WhiteboardItemNamePan];
    WhiteboardItem *uploadDoc = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameUpload];
    WhiteboardItem *visionLock = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameVisionLock];

    moreItem.subItems = @[eraserItem,clearItem,undoItem,redoItem, uploadDoc];
    tool.items = @[selectionItem,penItem,shapeItem,moreItem];
    tool.position = WhiteboardPositionBottomRight;
    
    WhiteboardItem *topRightMoreItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameMore];
    topRightMoreItem.subItems = @[zoomInItem,zoomOutItem, fitToContent, fitToDoc, pan, visionLock];
    NMCTool *toolTopRight = [[NMCTool alloc] init];
    toolTopRight.items = @[topRightMoreItem,zoomLevel];
    toolTopRight.position = WhiteboardPositionTopRight;
    
    WhiteboardItem *pageInfo = [[WhiteboardItem alloc] initWithName:WhiteboardItemNamePageBoardInfo];
    WhiteboardItem *preview = [[WhiteboardItem alloc] initWithName:WhiteboardItemNamePreview];
    NMCTool *topLeftTool = [[NMCTool alloc] init];
    topLeftTool.items = @[pageInfo,preview];
    topLeftTool.position = WhiteboardPositionTopLeft;
    
    [[NMCWhiteboardManager sharedManager] setupWhiteboardTools:@[toolTopRight,tool,topLeftTool]];
    [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
    [[NMCWhiteboardManager sharedManager] setAppConfigWithPresetId:@(104868090)];
    [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
}


- (void)onWebJoinWBFailed:(NSInteger)code error:(NSString *)error {
    NSLog(@"wb:onWebJoinWBFailed : %ld, %@",(long)code, error);
    [self showToastView:error];
}

- (void)onWebCreateWBFailed:(NSInteger)code error:(NSString *)error {
    NSLog(@"wb:onWebCreateWBFailed : %ld, %@",(long)code, error);
    [self showToastView:error];
}

- (void)onWebLeaveWB {
    NSLog(@"wb:onWebLeaveWB");
}

- (void)onWebError:(NSInteger)code error:(NSString *)error {
    NSLog(@"wb:onWebError : %ld, %@",(long)code, error);
    [self showToastView:error];
}

- (void)onWebJsError:(NSString *)error {
    NSLog(@"wb:onWebError : %@", error);
}

- (void)showToastView:(NSString *)error {
    if (error && error.length > 0) {
        [self.view makeToast:error duration:2.0 position:CSToastPositionCenter];
        [self leaveRoom];
    }
}
- (void)leaveRoom {
    [[NMCWhiteboardManager sharedManager] callWebLogoutIM];
    [[NMCWhiteboardManager sharedManager] clearWebViewCache];
}
#pragma mark - NSNotificationCenter
- (void)addNotification {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(becomeActive:) name:UIApplicationDidBecomeActiveNotification object:nil];
}
- (void)removeNotification {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (void)becomeActive:(NSNotification *)notification {
    [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger:UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
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
- (void)dealloc
{
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];
}
@end
