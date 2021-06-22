//
//  NEEduChatViewController.h
//  EduUI
//
//  Created by Groot on 2021/5/24.
//

#import <UIKit/UIKit.h>
#import "NEEduChatMessage.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduChatViewController : UIViewController
@property (nonatomic, strong) NSMutableArray<NEEduChatMessage *> *messages;
@property (nonatomic, assign) BOOL muteChat;
- (void)updateMuteChat:(BOOL)muteChat;
- (void)reloadTableViewToBottom:(BOOL)bottom;
@end

NS_ASSUME_NONNULL_END
