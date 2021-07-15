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
        [self addLongPress];
    }
    return self;
}
- (void)setupSubviews {
    [super setupSubviews];
    [self.contentView addSubview:self.bgView];
    NSLayoutConstraint *bgTop = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.nameLabel attribute:NSLayoutAttributeBottom multiplier:1.0 constant:5];
    NSLayoutConstraint *bgRight = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.nameLabel attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *bgBottom = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-10];
    self.bgWidth = [NSLayoutConstraint constraintWithItem:self.bgView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:0];
    [self.contentView addConstraints:@[bgTop,bgRight,bgBottom]];
    [self.bgView addConstraints:@[self.bgWidth]];
    
    [self.bgView addSubview:self.contentLabel];
    NSLayoutConstraint *contentTop = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *contentLeft = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:10];
    NSLayoutConstraint *contentRight = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeRight multiplier:1.0 constant:-10];
    NSLayoutConstraint *contentBottom = [NSLayoutConstraint constraintWithItem:self.contentLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.bgView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.contentView addConstraints:@[contentTop,contentLeft,contentRight,contentBottom]];
    self.nameLabel.textAlignment = NSTextAlignmentRight;
    
    [self.contentView addSubview:self.sendResultView];
    [NSLayoutConstraint activateConstraints:@[
        [self.sendResultView.rightAnchor constraintEqualToAnchor:self.bgView.leftAnchor constant:-5],
        [self.sendResultView.centerYAnchor constraintEqualToAnchor:self.bgView.centerYAnchor constant:0],
        [self.sendResultView.widthAnchor constraintEqualToConstant:25],
        [self.sendResultView.heightAnchor constraintEqualToConstant:25],
    ]];
    
    [self.contentView addSubview:self.activityView];
    [NSLayoutConstraint activateConstraints:@[
        [self.activityView.topAnchor constraintEqualToAnchor:self.sendResultView.topAnchor constant:0],
        [self.activityView.rightAnchor constraintEqualToAnchor:self.sendResultView.rightAnchor constant:0],
        [self.activityView.bottomAnchor constraintEqualToAnchor:self.sendResultView.bottomAnchor constant:0],
        [self.activityView.leftAnchor constraintEqualToAnchor:self.sendResultView.leftAnchor constant:0]
    ]];
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
    self.activityView.hidden = message.sendState != NEEduChatMessageSendStateNone;
    self.sendResultView.hidden = message.sendState != NEEduChatMessageSendStateFailure;
    if (message.sendState == NEEduChatMessageSendStateNone) {
        [self.activityView startAnimating];
    }else {
        [self.activityView stopAnimating];
    }
    
    for (NSLayoutConstraint *constraint in self.bgView.constraints) {
        if (constraint.firstAttribute == NSLayoutAttributeWidth) {
            constraint.constant = message.contentSize.width + 25;
        }
    }
}

- (void)retryEvent:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(textCell:retrySendMessage:)]) {
        [self.delegate textCell:self retrySendMessage:self.message];
    }
}
- (UIView *)bgView {
    if (!_bgView) {
        _bgView = [[UIView alloc] init];
        _bgView.translatesAutoresizingMaskIntoConstraints = NO;
        _bgView.layer.cornerRadius = 4;
        _bgView.clipsToBounds = YES;
        _bgView.backgroundColor = [UIColor colorWithRed:69/225.0 green:116/225.0 blue:252/225.0 alpha:1.0];
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
- (UIButton *)sendResultView {
    if (!_sendResultView) {
        _sendResultView = [UIButton buttonWithType:UIButtonTypeCustom];
        [_sendResultView setImage:[UIImage ne_imageNamed:@"chat_send_failure"] forState:UIControlStateNormal];
        [_sendResultView addTarget:self action:@selector(retryEvent:) forControlEvents:UIControlEventTouchUpInside];
        _sendResultView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _sendResultView;
}
- (UIActivityIndicatorView *)activityView {
    if (!_activityView) {
        _activityView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        _activityView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _activityView;
}
@end
