//
//  NEEduChatBaseCell.m
//  EduUI
//
//  Created by Groot on 2021/5/25.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduChatBaseCell.h"

@implementation NEEduChatBaseCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.contentView.backgroundColor = [UIColor clearColor];
        self.backgroundColor = [UIColor clearColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        [self setupSubviews];
        
    }
    return self;
}
- (void)setupSubviews {
    [self.contentView addSubview:self.nameLabel];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:46];
    NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeRight multiplier:1.0 constant:-46];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1.0 constant:10];
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeHeight multiplier:1.0 constant:20];
    [self.contentView addConstraints:@[left,right,top]];
    [self.nameLabel addConstraint:height];
}

- (void)updateUIWithMessage:(NEEduChatMessage *)message {
    _message = message;
    self.nameLabel.text = message.userName;
}

- (UILabel *)nameLabel {
    if (!_nameLabel) {
        _nameLabel = [[UILabel alloc] init];
        _nameLabel.textColor = [UIColor colorWithRed:74/255.0 green:86/255.0 blue:101/255.0 alpha:1.0];
        _nameLabel.font = [UIFont systemFontOfSize:12];
        _nameLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _nameLabel.numberOfLines = 0;
    }
    return _nameLabel;
}



@end
