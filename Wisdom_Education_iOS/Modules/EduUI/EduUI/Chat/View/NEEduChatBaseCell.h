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

@protocol NEEduChatBaseCellDelegate <NSObject>
- (void)chatView:(UIView *)tapView didLongPressMessage:(NEEduChatMessage *)message;
- (void)chatView:(UIView *)tapView didTapMessage:(NEEduChatMessage *)message;

@end

@interface NEEduChatBaseCell : UITableViewCell
@property (nonatomic, weak) id delegate;
@property (nonatomic, strong) UILabel *nameLabel;
@property (nonatomic, strong) NEEduChatMessage *message;
- (void)updateUIWithMessage:(NEEduChatMessage *)message;
- (void)setupSubviews;
- (void)setModel:(NEEduChatMessage *)model;
@end

NS_ASSUME_NONNULL_END
