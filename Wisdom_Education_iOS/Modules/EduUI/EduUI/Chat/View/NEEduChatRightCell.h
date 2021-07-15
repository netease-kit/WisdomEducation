//
//  NEEduChatRightCell.h
//  EduUI
//
//  Created by Groot on 2021/5/25.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduChatBaseCell.h"
#import "UIImage+NE.h"
NS_ASSUME_NONNULL_BEGIN
@class NEEduChatRightCell;

@protocol NEEduChatTextCellDelegate <NEEduChatBaseCellDelegate>
- (void)textCell:(NEEduChatRightCell *)cell retrySendMessage:(NEEduChatMessage *)message;

@end

@interface NEEduChatRightCell : NEEduChatBaseCell
@property (nonatomic, strong) UIView *bgView;
@property (nonatomic, strong) UILabel *contentLabel;
@property (nonatomic, strong) NSLayoutConstraint *bgWidth;
@property (nonatomic, strong) UIButton *sendResultView;
@property (nonatomic, strong) UIActivityIndicatorView *activityView;


@end

NS_ASSUME_NONNULL_END
