//
//  NEEduMembersVC.h
//  EduLogic
//
//  Created by Groot on 2021/5/27.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "NEEduMember.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduMembersVC : UIViewController<UITextFieldDelegate>
@property (nonatomic, assign) BOOL muteChat;
@property (nonatomic, strong) NSMutableArray<NEEduMember *> *members;
- (void)user:(NSString *)userID online:(BOOL)online;
- (void)memberIn:(NEEduMember *)member;
- (void)memberOut:(NSString *)userID;
// 仅刷新列表
- (void)reloadData;
//重新获取上台用户列表，布局UI
- (void)loadData;
@end

NS_ASSUME_NONNULL_END
