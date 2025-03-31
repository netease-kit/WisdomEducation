//
//  NEEduClassRoomVC.h
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <EduLogic/EduLogic.h>
#import "NEEduMenuItem.h"
#import <WebKit/WebKit.h>
#import <NEScreenShareHost/NEScreenShareHost.h>
#import "NEEduRoomViewMaskView.h"
#import "NEEduVideoCell.h"
#import <NEWhiteBoard/NMCWhiteboardManager.h>
#import "NEEduMembersVC.h"
#import "NEEduLessonStateView.h"

NS_ASSUME_NONNULL_BEGIN
//临时记录上一次房间&用户信息，用于请求回放地址
static NSString *kLastRoomUuid = @"lastRoomUuid";

static NSString *cellID = @"NEEduVideoCellID";
@interface NEEduClassRoomVC : UIViewController<NEEduMessageServiceDelegate,UICollectionViewDelegate,UICollectionViewDataSource,NEEduVideoCellDelegate>
@property (nonatomic, strong) NSMutableArray<NEEduHttpUser *> *members;
@property (nonatomic, strong) NEEduHttpRoom *room;
@property (nonatomic, strong) NSArray<NEEduMenuItem *> *menuItems;
@property (nonatomic, strong) UICollectionView *collectionView;

@property (nonatomic, strong) WKWebView *boardView;
// default YES
@property (nonatomic, assign) BOOL whiteboardWritable;

@property (nonatomic, strong) NEScreenShareHost *shareHost;
@property (nonatomic, strong) UIView *shareScreenView;
@property (nonatomic, strong) NEEduRoomViewMaskView *maskView;
@property (nonatomic, strong) NEEduMembersVC *membersVC;
@property (nonatomic, strong) NEEduLessonStateView *lessonStateView;
@property (nonatomic, strong) NEEduMenuItem *chatItem;
/// 是否推流
@property (nonatomic, assign) BOOL isPushStream;

// 子类实现
- (void)initMenuItems;
// 子类根据课堂数据更新菜单
- (void)updateMenueItemWithProfile:(NEEduRoomProfile *)profile;
- (NSArray <NEEduHttpUser *> *)placeholderMembers;
- (NSArray <NEEduHttpUser *> *)showMembersWithJoinedMembers:(NSArray <NEEduHttpUser *> *)members;
- (void)updateUIWithMembers:(NSArray *)members;
- (void)handsupItem:(NEEduMenuItem *)item;
// 对members数据进行重组 便于UI展示
- (NSArray <NEEduHttpUser *>*)membersWithProfile:(NEEduRoomProfile *)profile;
// 举手状态 UI更新
- (void)updateHandsupStateWithMembers:(NSArray <NEEduHttpUser *> *)members;
//更新底部菜单栏音频状态，更新右侧图像列表音视频状态
- (void)updateAVEnable:(BOOL)enable user:(NEEduHttpUser *)user;
//子类调用
- (void)showAlertViewWithMember:(NEEduHttpUser *)member cell:(NEEduVideoCell *)cell;
- (NEEduMember *)memberFromHttpUser:(NEEduHttpUser *)user;
- (void)stopAllScreenShare;
- (void)stopRecord;
- (void)classOver;

@end

NS_ASSUME_NONNULL_END
