//
//  NEEduLessonInfoItem.m
//  EduUI
//
//  Created by Groot on 2021/6/7.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduLessonInfoItem.h"

@implementation NEEduLessonInfoItem
- (instancetype)init
{
    self = [super init];
    if (self) {
        [self addSubview:self.titleLabel];
        NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
        NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
        NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
        [self addConstraints:@[left,top,bottom]];
        
        [self addSubview:self.button];
        NSLayoutConstraint *buttonLeft = [NSLayoutConstraint constraintWithItem:self.button attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.titleLabel attribute:NSLayoutAttributeRight multiplier:1.0 constant:10];
        NSLayoutConstraint *buttonTop = [NSLayoutConstraint constraintWithItem:self.button attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
        NSLayoutConstraint *buttonBottom = [NSLayoutConstraint constraintWithItem:self.button attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
        NSLayoutConstraint *buttonW = [NSLayoutConstraint constraintWithItem:self.button attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:30];
        NSLayoutConstraint *buttonR = [NSLayoutConstraint constraintWithItem:self.button attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:-8];
        [self addConstraints:@[buttonLeft,buttonTop,buttonBottom,buttonR]];
        [self.button addConstraint:buttonW];
    }
    return self;
}
- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.font = [UIFont systemFontOfSize:14];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.text = @"课堂号";
        _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _titleLabel;
}
- (UIButton *)button {
    if (!_button) {
        _button = [UIButton buttonWithType:UIButtonTypeCustom];
        [_button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
//        [_button setImage:[UIImage ne_imageNamed:@"room_copy"] forState:UIControlStateNormal];
        _button.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _button;
}
@end
