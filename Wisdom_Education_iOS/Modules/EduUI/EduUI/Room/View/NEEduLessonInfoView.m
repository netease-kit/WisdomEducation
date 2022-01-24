//
//  NEEduLessonInfoView.m
//  EduUI
//
//  Created by Groot on 2021/6/7.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduLessonInfoView.h"
#import "NEEduBaseViewController.h"
#import "UIView+Toast.h"

@interface NEEduLessonInfoView ()
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIStackView *stackView;
@property (nonatomic, strong) UIStackView *leftStackView;
@property (nonatomic, strong) NSMutableArray *leftLabels;
@property (nonatomic, strong) NSArray *rightLabels;

@end

@implementation NEEduLessonInfoView

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        self.contentView.backgroundColor = [UIColor colorWithRed:39/255.0 green:46/255.0 blue:55/255.0 alpha:1.0];
        [self loadData];
        [self setupSubviews];
    }
    return self;
}
- (void)setupSubviews {
    [self addSubview:self.contentView];
    NSLayoutConstraint *top;
    if (@available(iOS 11.0, *)) {
        top = [NSLayoutConstraint constraintWithItem:self.contentView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:40];
    } else {
        top = [NSLayoutConstraint constraintWithItem:self.contentView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:40];
    }
    NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:self.contentView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:-30];
    NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:self.contentView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:287];
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:self.contentView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:132];
    [self addConstraints:@[top,right]];
    [self.contentView addConstraints:@[width,height]];
    
    [self.contentView addSubview:self.stackView];
    NSLayoutConstraint *stackViewLeft = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:24];
    NSLayoutConstraint *stackViewTop = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1.0 constant:24];
    NSLayoutConstraint *stackViewBottom = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-24];
    NSLayoutConstraint *stackViewWidth = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:80];
    [self addConstraints:@[stackViewLeft,stackViewTop,stackViewBottom]];
    [self.stackView addConstraint:stackViewWidth];
    
    [self.contentView addSubview:self.leftStackView];
    NSLayoutConstraint *stackLeft = [NSLayoutConstraint constraintWithItem:self.leftStackView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.stackView attribute:NSLayoutAttributeRight multiplier:1.0 constant:24];
    NSLayoutConstraint *stacktop = [NSLayoutConstraint constraintWithItem:self.leftStackView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1.0 constant:24];
    NSLayoutConstraint *stackbottom = [NSLayoutConstraint constraintWithItem:self.leftStackView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-24];
    NSLayoutConstraint *stackRight = [NSLayoutConstraint constraintWithItem:self.leftStackView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeRight multiplier:1.0 constant:-24];
    [self addConstraints:@[stackLeft,stacktop,stackbottom,stackRight]];
}
- (void)loadData {
#ifdef DEBUG
    NSArray *leftTitle = @[@"课堂号",@"课堂名称",@"老师",@"cid"];
#else
    NSArray *leftTitle = @[@"课堂号",@"课堂名称",@"老师"];
#endif
    for (NSString *title in leftTitle) {
        UILabel *lable  = [[UILabel alloc] init];
        lable.textColor = [UIColor colorWithRed:148/255.0 green:151/255.0 blue:154/255.0 alpha:1.0];
        lable.font = [UIFont systemFontOfSize:14];
        lable.text = title;
        [self.leftLabels addObject:lable];
    }
#ifdef DEBUG
    self.rightLabels = @[self.lessonItem,self.lessonName,self.teacherName,self.cid];
#else
    self.rightLabels = @[self.lessonItem,self.lessonName,self.teacherName];
#endif
    
}
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    self.hidden = !self.hidden;
}
- (UIView *)contentView {
    if (!_contentView) {
        _contentView = [[UIView alloc] init];
        _contentView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _contentView;
}
- (UIStackView *)stackView {
    if (!_stackView) {
        _stackView = [[UIStackView alloc] initWithArrangedSubviews:self.leftLabels];
        _stackView.axis = UILayoutConstraintAxisVertical;
        _stackView.distribution = UIStackViewDistributionFillEqually;
        _stackView.alignment = UIStackViewAlignmentFill;
        _stackView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _stackView;
}
- (UIStackView *)leftStackView {
    if (!_leftStackView) {
        _leftStackView = [[UIStackView alloc] initWithArrangedSubviews:self.rightLabels];
        _leftStackView.axis = UILayoutConstraintAxisVertical;
        _leftStackView.distribution = UIStackViewDistributionFillEqually;
        _leftStackView.alignment = UIStackViewAlignmentFill;
        _leftStackView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _leftStackView;
}
- (NEEduLessonInfoItem *)lessonItem {
    if (!_lessonItem) {
        _lessonItem = [[NEEduLessonInfoItem alloc] init];
        _lessonItem.translatesAutoresizingMaskIntoConstraints = NO;
        [_lessonItem.button addTarget:self action:@selector(copyClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _lessonItem;
}

- (UILabel *)lessonName {
    if (!_lessonName) {
        _lessonName = [[UILabel alloc] init];
        _lessonName.font = [UIFont systemFontOfSize:14];
        _lessonName.textColor = [UIColor whiteColor];
        _lessonName.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _lessonName;
}
- (UILabel *)teacherName {
    if (!_teacherName) {
        _teacherName = [[UILabel alloc] init];
        _teacherName.font = [UIFont systemFontOfSize:14];
        _teacherName.textColor = [UIColor whiteColor];
        _teacherName.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _teacherName;
}
- (UILabel *)cid {
    if (!_cid) {
        _cid = [[UILabel alloc] init];
        _cid.font = [UIFont systemFontOfSize:14];
        _cid.textColor = [UIColor whiteColor];
        _cid.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _cid;
}
- (NSMutableArray *)leftLabels {
    if (!_leftLabels) {
        _leftLabels = [NSMutableArray array];
    }
    return _leftLabels;
}
- (IBAction)copyClick:(UIButton *)sender {
    if (!self.lessonItem.titleLabel.text) return;
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = self.lessonItem.titleLabel.text;
    [self.superview makeToast:@"复制成功，可粘贴课堂号"];
}

@end
