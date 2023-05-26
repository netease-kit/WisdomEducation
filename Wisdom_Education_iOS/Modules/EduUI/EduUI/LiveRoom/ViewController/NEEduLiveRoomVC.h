//
//  NEEduLiveRoomVC.h
//  EduUI
//
//  Created by 郭园园 on 2021/9/13.
//

#import <EduUI/EduUI.h>
#import "UIView+Toast.h"
#import "NEEduLessonInfoView.h"
#import "NEEduLessonOverView.h"
#import "NEEduChatViewController.h"
#import "NEEduMembersVC.h"
#import "NEEduLiveMembersVC.h"
#import <NELivePlayerFramework/NELivePlayerController.h>

NS_ASSUME_NONNULL_BEGIN
typedef NS_ENUM(NSInteger, NEEduLeaveSeatState) {
    NEEduLeaveSeatActive              = 0,//用户主动离开麦位
    NEEduLeaveSeatPassivity           = 1,//调用delete后被动离开麦位
};

@interface NEEduLiveRoomVC : //NEEduBaseViewController
UIViewController<NEEduMessageServiceDelegate,UICollectionViewDelegate,UICollectionViewDataSource,NEEduVideoCellDelegate>
/// 底部菜单
@property (nonatomic, strong) NSArray<NEEduMenuItem *> *menuItems;
/// 菜单栏
@property (nonatomic, strong) NEEduRoomViewMaskView *maskView;
/// 内容视图
@property (nonatomic, strong) UIView *contentView;
/// 课程未开始展示图
@property (nonatomic, strong) NEEduLessonStateView *lessonStateView;
/// 课程结束展示图
@property (nonatomic, strong) NEEduLessonOverView *classOverView;

@property (nonatomic, strong) UICollectionView *collectionView;
/// 聊天VC
@property (nonatomic, strong) NEEduChatViewController *chatVC;
/// 成员vc
@property (nonatomic, strong) NEEduLiveMembersVC *membersVC;
/// 举手menu
@property(nonatomic, strong) NEEduMenuItem *handsupItem;
/// 聊天室menu
@property (nonatomic, strong) NEEduMenuItem *chatItem;
///举手状态
@property (nonatomic, assign) NEEduHandsupState handsupState;
///用户主动/被动离开麦位
@property (nonatomic, assign) NEEduLeaveSeatState leaveState;
@property (nonatomic, strong) NEEduSeatUserAction  *seatAction;
@property (nonatomic, strong) NEEduSeatItem *seatItem;
/// 课堂信息
@property (nonatomic, strong) NEEduLessonInfoView *infoView;
/// 房间uuid
@property (nonatomic, strong) NSString *roomUuid;
/// 用户名
@property (nonatomic, strong) NSString *userName;
/// 所有rtc成员
@property (nonatomic, strong) NSMutableArray<NEEduHttpUser *> *totalMembers;
/// 所有聊天室成员
@property (nonatomic, strong) NSMutableArray<NIMChatroomMember *> *members;
/// 消息数组
@property (nonatomic, strong) NSMutableArray <NEEduChatMessage *> *messages;
///白板
@property (nonatomic, strong) WKWebView *boardView;
///是否屏幕共享
@property (nonatomic, assign) BOOL isSharing;
@property (nonatomic, assign) BOOL userIsShareScreen;
///屏幕共享遮罩
@property (nonatomic, strong) UILabel *shareScreenMask;
/// 屏幕共享视图
@property (nonatomic, strong) UIView *shareScreenView;
///屏幕共享
@property (nonatomic, strong) NEScreenShareHost *shareHost;
@property (nonatomic, strong) NSMutableIndexSet *subscribeSet;
@property (nonatomic, assign) BOOL whiteboardWritable;
/// 是否使用低延时直播
@property (nonatomic, assign) BOOL useFastLive;
/// 房间快照信息
@property (nonatomic, strong) NEEduRoomProfile *profile;
/// 房间信息
@property (nonatomic, strong) NEEduHttpRoom *room;
/// 聊天室是否禁言
@property (nonatomic, assign) BOOL muteChat;
/// 直播播放器
@property (nonatomic, strong) NELivePlayerController *player;

- (void)updateUIWithMembers:(NSArray *)members;
/// 通过房间信息加入聊天室
- (void)addChatroom:(NEEduHttpRoom *)room;
/// 处理举手
- (void)handsupItem:(NEEduMenuItem *)item;
//过滤RTC房间内的成员
- (NSArray <NEEduHttpUser *> *)showMembersWithJoinedMembers:(NSArray <NEEduHttpUser *> *)members;
///更新屏幕共享按钮
- (void)updateScreenShare;
- (void)handleHandsupClose:(NEEduHttpUser *)user;
- (void)updateHandsupStateWithMembers:(NSArray<NEEduHttpUser *> *)members;
- (void)handleSeatRequestApproved:(NSString *)userUuid;
- (void)handleSeatLeave;
- (void)handleSeatKicked;
///举手申请被拒绝
- (void)handleHandsupReject:(NEEduHttpUser *)user;
/// 课程结束
- (void)classOver;
///离开教室
- (void)leaveClass;
@end

NS_ASSUME_NONNULL_END
