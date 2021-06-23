//
//  NEEduChatBaseCell.h
//  EduUI
//
//  Created by Groot on 2021/5/25.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "NEEduChatMessage.h"

NS_ASSUME_NONNULL_BEGIN
@class NEEduChatBaseCell;

@protocol NEEduChatBaseCellDelegate <NSObject>
- (void)chatCell:(NEEduChatBaseCell *)cell didLongPressMessage:(NEEduChatMessage *)message;

@end
@interface NEEduChatBaseCell : UITableViewCell
@property (nonatomic, strong) UILabel *nameLabel;
@property (nonatomic, strong) UIView *bgView;
@property (nonatomic, strong) UILabel *contentLabel;
@property (nonatomic, strong) NEEduChatMessage *model;
@property (nonatomic, weak) id<NEEduChatBaseCellDelegate> delegate;
- (void)setupSubviews;
@end

NS_ASSUME_NONNULL_END
