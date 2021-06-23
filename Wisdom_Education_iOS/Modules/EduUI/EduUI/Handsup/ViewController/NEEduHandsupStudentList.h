//
//  NEEduHandsupStudentList.h
//  EduUI
//
//  Created by Groot on 2021/6/3.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "NEEduHttpUser.h"

NS_ASSUME_NONNULL_BEGIN
@protocol NEEduHandsupStudentListDelegate <NSObject>
- (void)didAgreeWithMember:(NEEduHttpUser *)member;
- (void)didDisAgreeWithMember:(NEEduHttpUser *)member;

@end

@interface NEEduHandsupStudentList : UIViewController
@property (nonatomic, weak) id<NEEduHandsupStudentListDelegate> delegate;
@property (nonatomic, strong) NSMutableArray<NEEduHttpUser *>* applyStudents;
@property (nonatomic, strong) UITableView *tableView;

@end

NS_ASSUME_NONNULL_END
