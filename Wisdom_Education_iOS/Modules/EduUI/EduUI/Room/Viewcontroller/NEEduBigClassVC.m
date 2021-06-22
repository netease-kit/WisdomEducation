//
//  NEEduBigClassVC.m
//  EduUI
//
//  Created by Groot on 2021/5/28.
//

#import "NEEduBigClassVC.h"
#import "UIView+Toast.h"
#import "UIView+NE.h"

@interface NEEduBigClassVC ()
@property (nonatomic, assign) NEEduHandsupState handsupState;
@property (nonatomic, strong) NEEduMenuItem *handsupItem;
@end

@implementation NEEduBigClassVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.handsupState = NEEduHandsupStateIdle;
    self.whiteboardWritable = NO;
    [self subscribeTeacherIfNeed];
}
- (void)subscribeTeacherIfNeed {
    NEEduHttpUser *teacher = self.members.firstObject;
    if (teacher.userUuid) {
        [[EduManager shared].videoService subscribeVideo:YES forUserID:teacher.rtcUid];
        [[EduManager shared].videoService subscribeAudio:YES forUserID:teacher.rtcUid];
    }
}
- (void)initMenuItems {
    NEEduMenuItem *membersItem = [[NEEduMenuItem alloc] initWithTitle:@"课堂成员" image:[UIImage ne_imageNamed:@"menu_members"]];
    membersItem.type = NEEduMenuItemTypeMembers;
    
    NEEduMenuItem *handsupItem = [[NEEduMenuItem alloc] initWithTitle:@"举手" image:[UIImage ne_imageNamed:@"menu_handsup"]];
    handsupItem.selectTitle = @"举手中";
    handsupItem.type = NEEduMenuItemTypeHandsup;
    [handsupItem setSelctedImage:[UIImage ne_imageNamed:@"menu_handsup_select"]];
    [handsupItem setSelctedTextColor:[UIColor colorWithRed:55/255.0 green:114/255.0 blue:255/255.0 alpha:1.0]];
    self.handsupItem = handsupItem;
    
    NEEduMenuItem *chatItem = [[NEEduMenuItem alloc] initWithTitle:@"聊天室" image:[UIImage ne_imageNamed:@"menu_chat"]];
    chatItem.type = NEEduMenuItemTypeChat;
    self.menuItems = @[membersItem,handsupItem,chatItem];
}
- (NSArray <NEEduHttpUser *>*)membersWithProfile:(NEEduRoomProfile *)profile {
    NEEduHttpUser *teacher = [[NEEduHttpUser alloc] init];
    teacher.role = NEEduRoleHost;
    NSMutableArray *totalArray = [NSMutableArray arrayWithObject:teacher];
    NSMutableArray *onlineArray = [NSMutableArray arrayWithObject:teacher];
    for (NEEduHttpUser *user in profile.snapshot.members) {
        if ([user.role isEqualToString:NEEduRoleHost]) {
            [totalArray replaceObjectAtIndex:0 withObject:user];
            [onlineArray replaceObjectAtIndex:0 withObject:user];
        }else {
            if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
                //自己
                [totalArray insertObject:user atIndex:1];
                if (user.properties.avHandsUp.value == NEEduHandsupStateTeaAccept) {
                    [onlineArray insertObject:user atIndex:1];
                }
            }else  {
                [totalArray addObject:user];
                if (user.properties.avHandsUp.value == NEEduHandsupStateTeaAccept) {
                    [onlineArray addObject:user];
                }
            }
        }
    }
    self.totalMembers = totalArray;
    self.members = onlineArray;
    self.room = profile.snapshot.room;
    return onlineArray;
}

- (void)updateHandsupStateWithProfile:(NEEduRoomProfile *)profile {
    //判断自己是否在举手中
    for (NEEduHttpUser *user in profile.snapshot.members) {
        if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
            self.handsupState = user.properties.avHandsUp.value;
            [self onHandsupStateChange:self.handsupState user:user];
            break;
        }
    }
}

- (void)handsupItem:(NEEduMenuItem *)item {
    if ([EduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateNone) {
        [self.view makeToast:@"还未开始上课"];
        return;
    }
    if (self.handsupState == NEEduHandsupStateApply) {
        //老师还没同意 可取消
        [self.view showAlertViewOnVC:self withTitle:@"取消举手" subTitle:@"是否确认取消举手" confirm:^{
            if (self.handsupState == NEEduHandsupStateApply) {
                __weak typeof(self)weakSelf = self;
                [[EduManager shared].userService handsupStateChange:NEEduHandsupStateStuCancel userID:[EduManager shared].localUser.userUuid result:^(NSError * _Nonnull error) {
                    if (!error) {
                        weakSelf.handsupState = NEEduHandsupStateIdle;
                        weakSelf.handsupItem.isSelected = NO;
                    }
                }];
            }
        }];
        return;
    }
    if(self.handsupState == NEEduHandsupStateTeaAccept) {
        //老师已同意 点击无效
        return;
    }
    [self.view showAlertViewOnVC:self withTitle:@"举手申请" subTitle:@"申请上台与老师沟通，通过后你的视频画面将出现在屏幕上并能与老师语音" confirm:^{
        [[EduManager shared].userService handsupStateChange:NEEduHandsupStateApply userID:[EduManager shared].localUser.userUuid result:^(NSError * _Nonnull error) {
            if (!error) {
                self.handsupState = NEEduHandsupStateApply;
                self.handsupItem.isSelected = YES;
            }else {
                [self.view makeToast:error.localizedDescription];
            }
        }];
    }];
}
- (void)onHandsupStateChange:(NEEduHandsupState)state user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
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
- (void)handleHandsupClose:(NEEduHttpUser *)user {
    //学生关闭
    NEEduHttpUser *removeUser;
    for (NEEduHttpUser *userExist in self.members) {
        if ([user.userUuid isEqualToString:userExist.userUuid]) {
            removeUser = userExist;
            break;
        }
    }
    if (removeUser) {
        [self.members removeObject:removeUser];
    }
    [self.collectionView reloadData];
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        //是自己
        self.maskView.startLesson.hidden = YES;
        [self.maskView removeItemType:NEEduMenuItemTypeAudio];
        [self.maskView removeItemType:NEEduMenuItemTypeVideo];
        [self.maskView removeItemType:NEEduMenuItemTypeShareScreen];

        NEEduMenuItem *handsupItem = [[NEEduMenuItem alloc] initWithTitle:@"举手" image:[UIImage ne_imageNamed:@"menu_handsup"]];
        handsupItem.selectTitle = @"举手中";
        handsupItem.type = NEEduMenuItemTypeHandsup;
        [handsupItem setSelctedImage:[UIImage ne_imageNamed:@"menu_handsup_select"]];
        [handsupItem setSelctedTextColor:[UIColor colorWithRed:55/255.0 green:114/255.0 blue:255/255.0 alpha:1.0]];
        self.handsupItem = handsupItem;
        [self.maskView insertItem:handsupItem atIndex:1];
    }else {
        [[EduManager shared].videoService subscribeAudio:NO forUserID:user.rtcUid];
        [[EduManager shared].videoService subscribeVideo:NO forUserID:user.rtcUid];
    }
    if (self.membersVC) {
        [self.membersVC user:user.userUuid online:NO];
    }
}

- (void)handleHandsupTeacherOffStage:(NEEduHttpUser *)user {
    NEEduHttpUser *removeUser;
    for (NEEduHttpUser *userExist in self.members) {
        if ([user.userUuid isEqualToString:userExist.userUuid]) {
            removeUser = userExist;
            break;
        }
    }
    if (removeUser) {
        [self.members removeObject:removeUser];
        [self.collectionView reloadData];
    }
    
    //区分是否是自己
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        [self.view makeToast:@"老师结束了你的上台操作"];
        [EduManager shared].localUser.isOnStage = NO;
        self.maskView.startLesson.hidden = YES;
        [self.maskView removeItemType:NEEduMenuItemTypeAudio];
        [self.maskView removeItemType:NEEduMenuItemTypeVideo];
        [self.maskView removeItemType:NEEduMenuItemTypeShareScreen];
        NEEduMenuItem *handsupItem = [[NEEduMenuItem alloc] initWithTitle:@"举手" image:[UIImage ne_imageNamed:@"menu_handsup"]];
        handsupItem.selectTitle = @"举手中";
        handsupItem.type = NEEduMenuItemTypeHandsup;
        [handsupItem setSelctedImage:[UIImage ne_imageNamed:@"menu_handsup_select"]];
        [handsupItem setSelctedTextColor:[UIColor colorWithRed:55/255.0 green:114/255.0 blue:255/255.0 alpha:1.0]];
        self.handsupItem = handsupItem;
        [self.maskView insertItem:handsupItem atIndex:1];
        
        [[EduManager shared].userService localUserVideoEnable:NO result:^(NSError * _Nonnull error) {
        }];
        [[EduManager shared].userService localUserAudioEnable:NO result:^(NSError * _Nonnull error) {
        }];
    }else {
        [[EduManager shared].videoService subscribeAudio:NO forUserID:user.rtcUid];
        [[EduManager shared].videoService subscribeVideo:NO forUserID:user.rtcUid];
    }
    if (self.membersVC) {
        [self.membersVC user:user.userUuid online:NO];
    }
}

- (void)handleHandsupReject:(NEEduHttpUser *)user {
    //区分是否是自己
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        [self.view makeToast:@"举手申请被拒绝"];
    }
    self.handsupItem.isSelected = NO;
}
- (void)handleHandsupAccept:(NEEduHttpUser *)user {
    BOOL exist = NO;
    NSInteger existIndex =  0;
    NEEduHttpUser *existUser;
    for (int i = 0; i < self.members.count; i++) {
        existUser = self.members[i];
        if ([existUser.userUuid isEqualToString:user.userUuid]) {
            exist = YES;
            existIndex = i;
            break;
        }
    }
    if (exist) {
        [self.members replaceObjectAtIndex:existIndex withObject:user];
    }else {
        [self.members addObject:user];
    }
    
    //区分是否是自己
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        [self.view makeToast:@"举手通过，您已上台"];
        [EduManager shared].localUser.isOnStage = YES;
        //更新UI
        
        [self.maskView selectButton:YES];
        self.maskView.startLesson.hidden = NO;
        [self.maskView removeItemType:NEEduMenuItemTypeHandsup];
        
        NEEduMenuItem *videoItem = [[NEEduMenuItem alloc] initWithTitle:@"关闭摄像头" image:[UIImage ne_imageNamed:@"menu_video"]];
        videoItem.selectTitle = @"打开摄像头";
        videoItem.type = NEEduMenuItemTypeVideo;
        [videoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_video_off"]];
        [self.maskView insertItem:videoItem atIndex:0];
        
        NEEduMenuItem *audoItem = [[NEEduMenuItem alloc] initWithTitle:@"静音" image:[UIImage ne_imageNamed:@"menu_audio"]];
        audoItem.selectTitle = @"取消静音";
        audoItem.type = NEEduMenuItemTypeAudio;
        [audoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_audio_off"]];
        [self.maskView insertItem:audoItem atIndex:0];
        //RTC推流
        [[EduManager shared].userService localUserVideoEnable:YES result:^(NSError * _Nonnull error) {
            [[EduManager shared].userService localUserAudioEnable:YES result:^(NSError * _Nonnull error) {
            }];
        }];
    }else {
        [[EduManager shared].videoService subscribeAudio:YES forUserID:user.rtcUid];
        [[EduManager shared].videoService subscribeVideo:YES forUserID:user.rtcUid];
    }
    if (self.membersVC) {
        [self.membersVC user:user.userUuid online:YES];
    }
}

#pragma mark - NEEduRoomViewMaskViewDelegate
- (void)rightButton:(UIButton *)button selected:(BOOL)selected {
    [self.view showAlertViewOnVC:self withTitle:@"下讲台" subTitle:@"下讲台后，你的视频画面将不再显示在屏幕上，不能继续与老师语音交流" confirm:^{
        button.userInteractionEnabled = NO;
        //学生点击下课堂按钮
        __weak typeof(self)weakSelf = self;
        [[EduManager shared].userService localUserVideoEnable:NO result:^(NSError * _Nonnull error) {
            __weak typeof(weakSelf)strongSelf = weakSelf;
            if (error) {
                button.userInteractionEnabled = YES;
                [self.view makeToast:error.localizedDescription];
                return;
            }
            __weak typeof(strongSelf)weakSelf = strongSelf;
            [[EduManager shared].userService localUserAudioEnable:NO result:^(NSError * _Nonnull error) {
                __weak typeof(weakSelf)strongSelf = weakSelf;
                if (error) {
                    button.userInteractionEnabled = YES;
                    [self.view makeToast:error.localizedDescription];
                    return;
                }
                [[EduManager shared].userService handsupStateChange:NEEduHandsupStateIdle userID:[EduManager shared].localUser.userUuid result:^(NSError * _Nonnull error) {
                    //
                    if (error) {
                        button.userInteractionEnabled = YES;
                        [self.view makeToast:error.localizedDescription];
                        return;
                    }
                    button.userInteractionEnabled = YES;
                    NEEduHttpUser *removeUser;
                    for (NEEduHttpUser *user in strongSelf.members) {
                        if ([user.userUuid isEqual:[EduManager shared].localUser.userUuid]) {
                            removeUser = user;
                            break;
                        }
                    }
                    if (removeUser) {
                        [strongSelf.members removeObject:removeUser];
                        [strongSelf.collectionView reloadData];
                    }
                    button.hidden = YES;
                }];
            }];
        }];
    }];
}

#pragma mark - NEEduMessageServiceDelegate
- (void)onUserInWithUser:(NEEduHttpUser *)user members:(NSArray *)members {
    if ([user.role isEqualToString:NEEduRoleHost]) {
        [self.members replaceObjectAtIndex:0 withObject:user];
        [self.totalMembers replaceObjectAtIndex:0 withObject:user];
        [[EduManager shared].videoService subscribeVideo:YES forUserID:user.rtcUid];
        [[EduManager shared].videoService subscribeAudio:YES forUserID:user.rtcUid];
    }else {
        if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
            //自己
            [self.totalMembers insertObject:user atIndex:1];
            if (user.properties.avHandsUp.value == NEEduHandsupStateTeaAccept) {
                [self.members insertObject:user atIndex:1];
            }
        }else {
            [self.totalMembers addObject:user];
            if (user.properties.avHandsUp.value == NEEduHandsupStateTeaAccept) {
                [self.members addObject:user];
            }
        }
    }
    [self.collectionView reloadData];
    if (self.membersVC) {
        if ([user.role isEqualToString:NEEduRoleHost]) {
            return;
        }
        [self.membersVC memberIn:[self memberFromHttpUser:user]];
    }
}

- (void)onUserOutWithUser:(NEEduHttpUser *)user members:(nonnull NSArray *)members {
    NEEduHttpUser *placeholdUser = [[NEEduHttpUser alloc] init];
    if ([user.role isEqualToString:NEEduRoleHost]) {
        placeholdUser.role = NEEduRoleHost;
        [self.totalMembers replaceObjectAtIndex:0 withObject:placeholdUser];
        [self.members replaceObjectAtIndex:0 withObject:placeholdUser];
        //大班课手动管理订阅/取消订阅
        [[EduManager shared].videoService subscribeVideo:NO forUserID:user.rtcUid];
        [[EduManager shared].videoService subscribeAudio:NO forUserID:user.rtcUid];
    }else {
        NEEduHttpUser *removeUser;
        for (NEEduHttpUser *tempUser in self.members) {
            if ([tempUser.userUuid isEqualToString:user.userUuid]) {
                removeUser = tempUser;
                break;
            }
        }
        if (removeUser) {
            [self.members removeObject:removeUser];
        }
        NEEduHttpUser *remove;
        for (NEEduHttpUser *tempUser in self.totalMembers) {
            if ([tempUser.userUuid isEqualToString:user.userUuid]) {
                remove = tempUser;
            }
        }
        if (remove) {
            [self.totalMembers removeObject:remove];
        }
    }
    [self.collectionView reloadData];
    //更新课堂成员页面
    if (self.membersVC) {
        if ([user.role isEqualToString:NEEduRoleHost]) {
            return;
        }
        [self.membersVC memberOut:user.userUuid];
    }

}
- (void)onLessonMuteAllAudio:(BOOL)mute roomUuid:(NSString *)roomUuid {
    //仅上上台的同学才需要调用静音接口
    if ([EduManager shared].localUser.isOnStage) {
        [[EduManager shared].userService localUserAudioEnable:!mute result:^(NSError * _Nonnull error) {
            if (!error) {
                NSString *string = mute ? @"已全体静音" : @"已取消全体静音";
                [[UIApplication sharedApplication].keyWindow makeToast:string];
            }else {
                [self.view makeToast:error.localizedDescription];
            }
        }];
    }
}

@end
