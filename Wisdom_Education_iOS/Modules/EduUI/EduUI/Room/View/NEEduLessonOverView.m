//
//  NEEduLessonOverView.m
//  EduUI
//
//  Created by Groot on 2021/6/6.
//

#import "NEEduLessonOverView.h"

@implementation NEEduLessonOverView

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        self.backgroundColor = [UIColor colorWithRed:16/255.0 green:20/255.0 blue:24/255.0 alpha:1.0];
        [self addSubview:self.label];
        NSLayoutConstraint *labelLeft = [NSLayoutConstraint constraintWithItem:self.label attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1.0 constant:10];
        NSLayoutConstraint *labelRight = [NSLayoutConstraint constraintWithItem:self.label attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:-10];
        NSLayoutConstraint *labelCenterY = [NSLayoutConstraint constraintWithItem:self.label attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:-20];
        NSLayoutConstraint *labelH = [NSLayoutConstraint constraintWithItem:self.label attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:25];
        [self addConstraints:@[labelLeft,labelRight,labelCenterY]];
        [self.label addConstraint:labelH];
        
        [self addSubview:self.backButton];
        NSLayoutConstraint *backButtonTop = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.label attribute:NSLayoutAttributeBottom multiplier:1.0 constant:20];
        NSLayoutConstraint *backButtonCenterX = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self.label attribute:NSLayoutAttributeCenterX multiplier:1.0 constant:0];
        NSLayoutConstraint *backButtonWidth = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:104];
        NSLayoutConstraint *backButtonH = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
        [self addConstraints:@[backButtonTop,backButtonCenterX]];
        [self.backButton addConstraints:@[backButtonWidth,backButtonH]];
    }
    return self;
}

- (UILabel *)label {
    if (!_label) {
        _label = [[UILabel alloc] init];
        _label.text = @"课程已结束";
        _label.textColor =[UIColor colorWithRed:141/255.0 green:148/255.0 blue:160/255.0 alpha:1.0];
        _label.font = [UIFont systemFontOfSize:16];
        _label.textAlignment = NSTextAlignmentCenter;
        _label.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _label;
}
- (UIButton *)backButton {
    if (!_backButton) {
        _backButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _backButton.layer.cornerRadius = 2;
        _backButton.titleLabel.font = [UIFont systemFontOfSize:14];
        [_backButton setTitle:@"返回" forState:UIControlStateNormal];
        [_backButton setTitleColor:[UIColor colorWithRed:141/255.0 green:148/255.0 blue:160/255.0 alpha:1.0] forState:UIControlStateNormal];
        _backButton.layer.borderWidth = 1.0;
        _backButton.layer.borderColor = [UIColor colorWithRed:141/255.0 green:148/255.0 blue:160/255.0 alpha:1.0].CGColor;
        _backButton.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _backButton;
}
@end
