//
//  NEEduLiveMembersVC.h
//  EduUI
//
//  Created by 郭园园 on 2021/9/16.
//

#import <UIKit/UIKit.h>
#import <EduLogic/EduLogic.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduLiveMembersVC : UIViewController
@property (nonatomic, strong) NEEduHttpRoom *room;
- (void)user:(NSString *)userID online:(BOOL)online;
- (void)addMember:(NIMChatroomMember *)member;
- (void)removeMember:(NIMChatroomMember *)member;
- (void)setRefresh;
@end

NS_ASSUME_NONNULL_END
