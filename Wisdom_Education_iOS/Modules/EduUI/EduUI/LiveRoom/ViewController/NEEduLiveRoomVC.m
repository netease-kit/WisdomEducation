//
//  NEEduLiveRoomVC.m
//  EduUI
//
//  Created by 郭园园 on 2021/9/13.
//

#import "NEEduLiveRoomVC.h"
#import "NEEduNavigationViewController.h"
#import <SDWebImage/SDWebImageDownloader.h>
#import "NSString+NE.h"
#import "NEEduLiveMembersVC.h"
static NSString *kLastRtcCid = @"lastRtcCid";

@interface NEEduLiveRoomVC ()<NEEduRoomViewMaskViewDelegate,NEEduIMChatDelegate,NEEduRoomServiceDelegate>
@property (nonatomic, strong) UIView *contentView;

@property (nonatomic, strong) NEEduMenuItem *chatItem;
@property (nonatomic, strong) NEEduHttpRoom *room;
@property (nonatomic, strong) NELivePlayerController *player;
@property (nonatomic, strong) NSMutableArray <NEEduChatMessage *> *messages;
@property (nonatomic, strong) NEEduRoomProfile *profile;
@property (nonatomic, strong) NEEduLiveMembersVC *membersVC;
@property (nonatomic, strong) NSMutableArray<NIMChatroomMember *> *members;
@property (nonatomic, strong) NEEduLessonInfoView *infoView;
@property (nonatomic, assign) BOOL muteChat;

@end

@implementation NEEduLiveRoomVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    [self initData];
    [self setupSubviews];
    [NEEduManager shared].roomService.delegate = self;
    //请求
    __weak typeof(self)weakSelf = self;
    [[NEEduManager shared].roomService getRoomProfile:self.roomUuid completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
        if (error) {
            [self.view makeToast:[NSString stringWithFormat:@"加入房间失败，请退出重进 %@",error.localizedDescription]];
            return;
        }
        [[NSUserDefaults standardUserDefaults] setObject:profile.snapshot.room.rtcCid forKey:kLastRtcCid];
        __strong typeof(self)strongSelf = weakSelf;
        strongSelf.profile = profile;
        strongSelf.muteChat = profile.snapshot.room.states.muteChat.value;
        if (profile.snapshot.room.states.step.value == NEEduLessonStateClassIn) {
            strongSelf.lessonStateView.hidden = YES;
            NSString *urlString = self.useFastLive ? profile.snapshot.room.properties.live.pullRtsUrl : profile.snapshot.room.properties.live.pullRtmpUrl;
            [strongSelf initLivePlayer:urlString];
            [strongSelf.player prepareToPlay];
        }else {
            strongSelf.lessonStateView.hidden = NO;
        }
        [NEEduManager shared].imService.chatDelegate = weakSelf;
        
        // update room info
        strongSelf.room = profile.snapshot.room;
        [strongSelf.maskView.navView updateRoomState:strongSelf.room serverTime:profile.ts];
        // update members list
        for (NEEduHttpUser *user in profile.snapshot.members) {
            NIMChatroomMember *chatMember = [[NIMChatroomMember alloc] init];
            chatMember.roomNickname = user.userName;
            [strongSelf.members addObject:chatMember];
        }
        //strongSelf.members = profile.snapshot.members;
        //load chatroom function
        [strongSelf addChatroom:strongSelf.room];
    }];
}

#pragma mark - private Method

- (void)initData {
    NEEduMenuItem *membersItem = [[NEEduMenuItem alloc] initWithTitle:@"课堂成员" image:[UIImage ne_imageNamed:@"menu_members"]];
    membersItem.type = NEEduMenuItemTypeMembers;
    self.menuItems = @[membersItem];
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

- (void)initLivePlayer:(NSString *)urlString {
    if (!urlString.length) {
        return;
    }
    NSURL *url = [[NSURL alloc] initWithString:urlString];
    NSError *error;
    NELivePlayerController *player = [[NELivePlayerController alloc] initWithContentURL:url error:&error];
    if (error) {
        NSLog(@"error:%@",error);
        return;
    }
    [self addNotificaton];
//    [self.contentView addSubview:player.view];
//    [NSLayoutConstraint activateConstraints:@[
//        [player.view.topAnchor constraintEqualToAnchor:self.contentView.topAnchor],
//        [player.view.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor],
//        [player.view.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor],
//        [player.view.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor]
//    ]];
    player.view.frame = self.contentView.bounds;
    [self.contentView addSubview:player.view];
    self.player = player;
    [self.player setScalingMode:NELPMovieScalingModeAspectFit];
}

- (void)addNotificaton {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPrepared:) name:NELivePlayerDidPreparedToPlayNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayStateChange:) name:NELivePlayerPlaybackStateChangedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayFinished:) name:NELivePlayerPlaybackFinishedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onSeeked:) name:NELivePlayerMoviePlayerSeekCompletedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onLoadFirstFrame:) name:NELivePlayerFirstVideoDisplayedNotification object:nil];
}

- (void)removeNotification {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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

- (void)addChatMenue {
    NEEduMenuItem *chatItem = [[NEEduMenuItem alloc] initWithTitle:@"聊天室" image:[UIImage ne_imageNamed:@"menu_chat"]];
    chatItem.type = NEEduMenuItemTypeChat;
    self.chatItem = chatItem;
    [self.maskView addItem:self.chatItem];
}

- (void)startClassWithServerTime:(NSInteger)time {
    if (self.room.states.step.value == NEEduLessonStateClassIn) {
        return;
    }
    self.lessonStateView.hidden = YES;
    NEEduLessonStep *step = [[NEEduLessonStep alloc] init];
    step.value = NEEduLessonStateClassIn;
    step.time = time;
    self.room.states.step = step;
    [self.maskView.navView updateRoomState:self.room serverTime:time];
    //player
    NSString *urlString = self.useFastLive ? self.profile.snapshot.room.properties.live.pullRtsUrl : self.profile.snapshot.room.properties.live.pullRtmpUrl;
    [self initLivePlayer:urlString];
    [self.player prepareToPlay];
}

- (void)classOver {
    if (self.room.states.step.value == NEEduLessonStateClassOver) {
        return;
    }
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
}

- (void)classOverBack {
    [self backEvent];
}

- (void)showChatViewWithitem:(NEEduMenuItem *)item {
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

- (void)infoButtonClick:(UIButton *)button {
    self.infoView.hidden = !self.infoView.hidden;
}
#pragma mark - notification
- (void)onPrepared:(NSNotification *)notification {
    [self.player play];
    NSLog(@"player:%s",__func__);
}
- (void)onPlayStateChange:(NSNotification *)notification {
    NSLog(@"player:%s",__func__);
}
- (void)onPlayFinished:(NSNotification *)notification {
    NSLog(@"player:%s",__func__);
}
- (void)onSeeked:(NSNotification *)notification {
    NSLog(@"player:%s",__func__);
}
- (void)onLoadFirstFrame:(NSNotification *)notification {
    NSLog(@"player:%s",__func__);
}

#pragma mark - NEEduIMChatDelegate
- (void)didRecieveChatMessages:(NSArray<NIMMessage *> *)messages {
    //接收成功
    NIMMessage *imMessage = messages.firstObject;
    NSLog(@"imMessage: %d %@",imMessage.messageType,imMessage.messageObject);
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
    }else if ([imMessage messageType] == NIMMessageTypeCustom) {
        NIMCustomObject *custom = imMessage.messageObject;
        NEEduIMAttach *attach = custom.attachment;
        NEEduSignalMessage *message = attach.data;
        NSDictionary *states = message.data[@"states"];
        NSDictionary *step = [states objectForKey:@"step"];
        if (step) {
            [[NSUserDefaults standardUserDefaults] setObject:self.room.roomUuid forKey:kLastRoomUuid];
            NSNumber *value = step[@"value"];
            NSNumber *time = step[@"time"];
            if ([value isEqualToNumber:@(1)]) {
                //start Class
                [self startClassWithServerTime:time.integerValue];
            }else if([value isEqualToNumber:@(2)]) {
                // end Class
                [self classOver];
            }
            return;
        }
        NSDictionary *muteChat = [states objectForKey:@"muteChat"];
        if (muteChat) {
            NSNumber *value = muteChat[@"value"];
            if ([value isEqualToNumber:@(1)]) {
                self.muteChat = YES;
                //muteChat
                if (self.chatVC) {
                    [self.chatVC updateMuteChat:YES];
                }
            }else {
                self.muteChat = NO;
                [self.chatVC updateMuteChat:NO];
            }
            return;
        }
        
    }else if([imMessage messageType] == NIMMessageTypeNotification){
        //user in/out
        NSLog(@"class:%@",[imMessage.messageObject class]);
        NIMNotificationObject *object = imMessage.messageObject;
        NSLog(@"content:%@",object.content);
        NIMNotificationContent *content = object.content;
        NSInteger eventType = [[content valueForKey:@"eventType"] integerValue];
        NIMChatroomNotificationMember *member = [content valueForKey:@"source"];
        NSLog(@"nickName:%@",member.nick);
        if (eventType == 301) {
            //user in
            if (self.membersVC) {
                NIMChatroomMember *chatMember = [[NIMChatroomMember alloc] init];
                chatMember.roomNickname = member.nick;
                chatMember.userId = member.userId;
                [self.membersVC addMember:chatMember];
            }
        }else if (eventType == 302) {
            //user out
            if (self.membersVC) {
                NIMChatroomMember *chatMember = [[NIMChatroomMember alloc] init];
                chatMember.roomNickname = member.nick;
                chatMember.userId = member.userId;
                [self.membersVC removeMember:chatMember];
            }
        }
    }
}

- (void)willSendMessage:(NIMMessage *)message {
    //去重
    NSLog(@"willSendMessage:%@ state:%ld",message.messageId,(long)(message.deliveryState));
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
    eduMessage.userName = [NSString stringWithFormat:@"%@(%@)",self.userName,role];
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
    NSLog(@"sendMessage count:%lu",(unsigned long)(self.messages.count));
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

#pragma mark - NEEduRoomViewMaskViewDelegate
- (void)backEvent {
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];
    [self.player shutdown];
    [self removeNotification];
    // IM退出聊天室
    [[NEEduManager shared].imService leaveChatRoom];
    if (![NEEduManager shared].reuseIM) {
        [[NEEduManager shared].imService logout];
    }
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (void)onSectionStateChangeAtIndex:(NSInteger)index item:(NEEduMenuItem *)item {
    switch (item.type) {
        case NEEduMenuItemTypeMembers:
            [self showMembersViewWithItem:item];
            break;
        case NEEduMenuItemTypeChat:
            [self showChatViewWithitem:item];
            break;
        default:
            break;
    }
}
#pragma mark - NEEduRoomServiceDelegate
- (void)netStateChangeWithState:(AFNetworkReachabilityStatus)state {
    switch (state) {
        case AFNetworkReachabilityStatusUnknown:
        case AFNetworkReachabilityStatusNotReachable:
            self.maskView.navView.netStateView.image = [UIImage ne_imageNamed:@"net_0"];
            break;
        case AFNetworkReachabilityStatusReachableViaWWAN:
        case AFNetworkReachabilityStatusReachableViaWiFi:{
            self.maskView.navView.netStateView.image = [UIImage ne_imageNamed:@"net_3"];
            [[NEEduManager shared].roomService getRoomProfile:self.room.roomUuid completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
                if(error){
                    if(error.code == NEEduErrorTypeRoomNotFound){
                        [self classOver];
                    }
                }
            }];
            break;
        }
        default:
            break;
    }
}
#pragma mark - get
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
