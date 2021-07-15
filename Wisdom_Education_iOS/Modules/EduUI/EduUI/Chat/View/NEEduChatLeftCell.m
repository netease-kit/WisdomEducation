//
//  NEEduChatLeftCell.m
//  EduUI
//
//  Created by Groot on 2021/5/25.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduChatLeftCell.h"

@implementation NEEduChatLeftCell
- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self setupSubviews];
        [self addLongPress];
    }
    return self;
}
- (void)setupSubviews {
    [super setupSubviews];
    [self.contentView addSubview:self.bgView];
    NSLayoutConstraint *bgTop = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.nameLabel attribute:NSLayoutAttributeBottom multiplier:1.0 constant:5];
    NSLayoutConstraint *bgBottom = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-10];
    NSLayoutConstraint *bgLeft = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.nameLabel attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    self.bgWidth = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:0];
    [self.contentView addConstraints:@[bgTop,bgLeft,bgBottom]];
    [self.bgView addConstraints:@[self.bgWidth]];
    
    [self.bgView addSubview:self.contentLabel];
    NSLayoutConstraint *contentTop = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *contentLeft = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:10];
    NSLayoutConstraint *contentRight = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeRight multiplier:1.0 constant:-10];
    NSLayoutConstraint *contentBottom = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.contentView addConstraints:@[contentTop,contentLeft,contentRight,contentBottom]];
    self.nameLabel.textAlignment = NSTextAlignmentLeft;
}
- (void)addLongPress {
    UILongPressGestureRecognizer *press = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPress:)];
    [self.bgView addGestureRecognizer:press];
}
- (void)longPress:(UILongPressGestureRecognizer *)press {
    if (press.state == UIGestureRecognizerStateBegan) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(chatView:didLongPressMessage:)]) {
            [self.delegate chatView:self.bgView didLongPressMessage:self.message];
        }
    }
}
- (void)updateUIWithMessage:(NEEduChatMessage *)message {
    [super updateUIWithMessage:message];
    self.contentLabel.text = message.content;
    for (NSLayoutConstraint *constraint in self.bgView.constraints) {
        if (constraint.firstAttribute == NSLayoutAttributeWidth) {
            constraint.constant = message.contentSize.width + 20;
        }
    }
}
- (UIView *)bgView {
    if (!_bgView) {
        _bgView = [[UIView alloc] init];
        _bgView.translatesAutoresizingMaskIntoConstraints = NO;
        _bgView.layer.cornerRadius = 4;
        _bgView.clipsToBounds = YES;
        _bgView.backgroundColor = [UIColor colorWithRed:63/225.0 green:74/225.0 blue:87/225.0 alpha:1.0];
    }
    return _bgView;
}
- (UILabel *)contentLabel {
    if (!_contentLabel) {
        _contentLabel = [[UILabel alloc] init];
        _contentLabel.font = [UIFont systemFontOfSize:14];
        _contentLabel.textColor = [UIColor whiteColor];
        _contentLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _contentLabel.textAlignment = NSTextAlignmentLeft;
        _contentLabel.numberOfLines = 0;
    }
    return _contentLabel;
}

@end
