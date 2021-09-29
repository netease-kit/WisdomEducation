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
//#import <NELivePlayerFramework/NELivePlayer.h>
#import <NELivePlayerFramework/NELivePlayerController.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduLiveRoomVC : NEEduBaseViewController
@property (nonatomic, strong) NSArray<NEEduMenuItem *> *menuItems;
@property (nonatomic, strong) NEEduRoomViewMaskView *maskView;
@property (nonatomic, strong) NEEduLessonStateView *lessonStateView;
@property (nonatomic, strong) NEEduLessonOverView *classOverView;
@property (nonatomic, strong) NEEduChatViewController *chatVC;
@property (nonatomic, strong) NSString *roomUuid;
@property (nonatomic, strong) NSString *userName;
@property (nonatomic, assign) BOOL useFastLive;

@end

NS_ASSUME_NONNULL_END
