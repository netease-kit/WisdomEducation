//
//  NEEduHandsupApplyCell.h
//  EduUI
//
//  Created by Groot on 2021/6/3.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "NEEduHttpUser.h"

NS_ASSUME_NONNULL_BEGIN
@protocol NEEduHandsupApplyCellDelegate <NSObject>

- (void)agreeHansupApplyWithMember:(NEEduHttpUser *)member;
- (void)disagreeHansupApplyWithMember:(NEEduHttpUser *)member;

@end

@interface NEEduHandsupApplyCell : UITableViewCell
@property (nonatomic, weak) id<NEEduHandsupApplyCellDelegate> delegate;
@property (nonatomic, strong) UILabel *nameLabel;
@property (nonatomic, strong) UIButton *agreeButton;
@property (nonatomic, strong) UIButton *disagreeButton;
@property (nonatomic, strong) UIView *line;
@property (nonatomic, strong) NEEduHttpUser *member;
@end

NS_ASSUME_NONNULL_END
