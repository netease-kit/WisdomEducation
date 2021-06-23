//
//  NEEduLessonStateView.m
//  EduUI
//
//  Created by Groot on 2021/6/6.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduLessonStateView.h"
#import "UIImage+NE.h"

@implementation NEEduLessonStateView

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        self.backgroundColor = [UIColor colorWithRed:16/255.0 green:20/255.0 blue:24/255.0 alpha:1.0];
        [self addSubview:self.imageView];
        NSLayoutConstraint *centerx = [NSLayoutConstraint constraintWithItem:self.imageView attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterX multiplier:1.0 constant:0];
        NSLayoutConstraint *centerY = [NSLayoutConstraint constraintWithItem:self.imageView attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:-20];
        NSLayoutConstraint *imageW = [NSLayoutConstraint constraintWithItem:self.imageView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:60];
        NSLayoutConstraint *imageH = [NSLayoutConstraint constraintWithItem:self.imageView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:142];

        [self addConstraints:@[centerx,centerY]];
        [self.imageView addConstraints:@[imageW,imageH]];
        
        [self addSubview:self.label];
        NSLayoutConstraint *labelLeft = [NSLayoutConstraint constraintWithItem:self.label attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1.0 constant:10];
        NSLayoutConstraint *labelRight = [NSLayoutConstraint constraintWithItem:self.label attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:-10];
        NSLayoutConstraint *labelTop = [NSLayoutConstraint constraintWithItem:self.label attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.imageView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:10];
        NSLayoutConstraint *labelH = [NSLayoutConstraint constraintWithItem:self.label attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:25];
        [self addConstraints:@[labelLeft,labelRight,labelTop]];
        [self.label addConstraint:labelH];
        
    }
    return self;
}
- (UIImageView *)imageView {
    if (!_imageView) {
        _imageView = [[UIImageView alloc] init];
        _imageView.translatesAutoresizingMaskIntoConstraints = NO;
        _imageView.image = [UIImage ne_imageNamed:@"room_placeholder"];
    }
    return _imageView;
}
- (UILabel *)label {
    if (!_label) {
        _label = [[UILabel alloc] init];
        _label.text = @"课程还未开始，请耐心等待哦";
        _label.textColor =[UIColor colorWithRed:141/255.0 green:148/255.0 blue:160/255.0 alpha:1.0];
        _label.font = [UIFont systemFontOfSize:16];
        _label.textAlignment = NSTextAlignmentCenter;
        _label.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _label;
}
@end
