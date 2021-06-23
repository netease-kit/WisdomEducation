//
//  NEEduChatRightCell.m
//  EduUI
//
//  Created by Groot on 2021/5/25.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduChatRightCell.h"

@implementation NEEduChatRightCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self setupSubviews];
    }
    return self;
}
- (void)setupSubviews {
    [super setupSubviews];
    NSLayoutConstraint *bgTop = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.nameLabel attribute:NSLayoutAttributeBottom multiplier:1.0 constant:5];
    NSLayoutConstraint *bgRight = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.nameLabel attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *bgBottom = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-10];
    self.bgWidth = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:0];
    
    [self.contentView addConstraints:@[bgTop,bgRight,bgBottom]];
    [self.bgView addConstraints:@[self.bgWidth]];
    
    NSLayoutConstraint *contentTop = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *contentLeft = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:10];
    NSLayoutConstraint *contentRight = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeRight multiplier:1.0 constant:-10];
    NSLayoutConstraint *contentBottom = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.contentView addConstraints:@[contentTop,contentLeft,contentRight,contentBottom]];
    self.nameLabel.textAlignment = NSTextAlignmentRight;
    self.bgView.backgroundColor = [UIColor colorWithRed:69/225.0 green:116/225.0 blue:252/225.0 alpha:1.0];
}

@end
