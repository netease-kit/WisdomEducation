// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import "NEEduLiveRoomVC+UI.h"

@implementation NEEduLiveRoomVC (UI)
- (void)setupDefaultSubviews {
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
            [self.contentView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:0],
            [self.contentView.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-60],
        ]];
    }else {
        [NSLayoutConstraint activateConstraints:@[
            [self.contentView.topAnchor constraintEqualToAnchor:self.view.topAnchor constant:40],
            [self.contentView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:0],
            [self.contentView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:0],
            [self.contentView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor constant:-60],
        ]];
    }
    [self.view addSubview:self.lessonStateView];
    [NSLayoutConstraint activateConstraints:@[
        [self.lessonStateView.topAnchor constraintEqualToAnchor:self.contentView.topAnchor],
        [self.lessonStateView.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor],
        [self.lessonStateView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor],
        [self.lessonStateView.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor]
    ]];
    
    [self.view addSubview:self.classOverView];
    [NSLayoutConstraint activateConstraints:@[
        [self.classOverView.topAnchor constraintEqualToAnchor:self.view.topAnchor],
        [self.classOverView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor],
        [self.classOverView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor],
        [self.classOverView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor]
    ]];
    [self.maskView.navView.infoButton addTarget:self action:@selector(infoButtonClick:) forControlEvents:UIControlEventTouchUpInside];
}
- (void)infoButtonClick:(UIButton *)button {
    [self infoView];
    self.infoView.hidden = !self.infoView.hidden;
    [self.view bringSubviewToFront:self.infoView];
}

-(void)setupNewSubview{
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
            [self.collectionView.leftAnchor constraintEqualToAnchor:self.contentView.rightAnchor constant:0],
            [self.collectionView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-40],
            [self.collectionView.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor constant:40],
            [self.collectionView.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-60],
            [self.collectionView.widthAnchor constraintEqualToConstant:140]
        ]];
    }else {
        [NSLayoutConstraint activateConstraints:@[
            [self.collectionView.leftAnchor constraintEqualToAnchor:self.contentView.rightAnchor constant:0],
            [self.collectionView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-40],
            [self.collectionView.topAnchor constraintEqualToAnchor:self.view.topAnchor constant:40],
            [self.collectionView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor constant:-60],
            [self.collectionView.widthAnchor constraintEqualToConstant:140]
        ]];
    }
    
    [self.contentView addSubview:self.shareScreenView];
    [NSLayoutConstraint activateConstraints:@[
        [self.shareScreenView.topAnchor constraintEqualToAnchor:self.contentView.topAnchor],
        [self.shareScreenView.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor],
        [self.shareScreenView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor],
        [self.shareScreenView.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor]
    ]];
    
    [self.view addSubview:self.shareScreenMask];
    [NSLayoutConstraint activateConstraints:@[
        [self.shareScreenMask.topAnchor constraintEqualToAnchor:self.shareScreenView.topAnchor],
        [self.shareScreenMask.leftAnchor constraintEqualToAnchor:self.shareScreenView.leftAnchor],
        [self.shareScreenMask.rightAnchor constraintEqualToAnchor:self.view.rightAnchor],
        [self.shareScreenMask.bottomAnchor constraintEqualToAnchor:self.shareScreenView.bottomAnchor]
    ]];
}
-(void)setupDefaultContentView{
    [self.view addSubview:self.contentView];
    if (@available(iOS 11.0, *)) {
        [NSLayoutConstraint activateConstraints:@[
            [self.contentView.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor constant:40],
            [self.contentView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:0],
            [self.contentView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:0],
            [self.contentView.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-60],
        ]];
    }else {
        [NSLayoutConstraint activateConstraints:@[
            [self.contentView.topAnchor constraintEqualToAnchor:self.view.topAnchor constant:40],
            [self.contentView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:0],
            [self.contentView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:0],
            [self.contentView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor constant:-60],
        ]];
    }
}
- (void)addChatMenue {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.maskView addItem:self.chatItem];
    });
}
- (void)addHandsUpMenue {
    [self.maskView addItem:self.handsupItem];
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

- (void)updateUIWithRoom:(NEEduHttpRoom *)room {
    [self.maskView.navView updateRoomState:room serverTime:[NEEduManager shared].profile.ts];
    //初始化上课按钮
    // 学生在线的话 下讲台
    [self.maskView.startLesson setTitle:@"下讲台" forState:UIControlStateSelected];
    self.maskView.startLesson.hidden = YES;
    if ([NEEduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateClassIn) {
        self.lessonStateView.hidden = YES;
    }else {
        self.lessonStateView.hidden = NO;
    }
}

- (NSArray <NEEduHttpUser *>*)membersWithProfile:(NEEduRoomProfile *)profile {
    NEEduHttpUser *teacher = [[NEEduHttpUser alloc] init];
    teacher.role = NEEduRoleHost;
    NSMutableArray *totalArray = [NSMutableArray arrayWithObject:teacher];
    for (NEEduHttpUser *user in profile.snapshot.members) {
        if ([user.role isEqualToString:NEEduRoleHost]) {
            [totalArray replaceObjectAtIndex:0 withObject:user];
        }else {
            if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
                //自己
                [totalArray insertObject:user atIndex:1];
            }else  {
                [totalArray addObject:user];
            }
        }
    }
    
    self.members = totalArray;
    self.room = profile.snapshot.room;
    return totalArray;
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
@end
