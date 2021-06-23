//
//  NEEduChatViewController.h
//  EduUI
//
//  Created by Groot on 2021/5/24.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
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
