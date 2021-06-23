//
//  EduSelectView.m
//  EduUI
//
//  Created by Groot on 2021/5/13.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "EduSelectView.h"
#import "UIImage+NE.h"

@interface EduSelectView ()
@property (nonatomic, strong) UIView *line;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIButton *arrowButton;
@end

@implementation EduSelectView

- (instancetype)initWithTitle:(NSString *)title {
    self = [super init];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        self.title = title;
        [self setupSubviews];
    }
    return self;
}
- (void)setupSubviews {
    [self addSubview:self.titleLabel];
    NSLayoutConstraint *labelTop = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *labelLeading = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *labelTrailing = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *labelBottom = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-1];
    [self addConstraints:@[labelTop,labelLeading,labelBottom,labelTrailing]];
    
    [self addSubview:self.arrowButton];
    NSLayoutConstraint *buttonTop = [NSLayoutConstraint constraintWithItem:self.arrowButton attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self.titleLabel attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:0];
    NSLayoutConstraint *buttonTrailing = [NSLayoutConstraint constraintWithItem:self.arrowButton attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *buttonHeight = [NSLayoutConstraint constraintWithItem:self.arrowButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeHeight multiplier:1.0 constant:0];
    NSLayoutConstraint *buttonWidth = [NSLayoutConstraint constraintWithItem:self.arrowButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:88];
    [self addConstraints:@[buttonTop,buttonTrailing,buttonHeight]];
    [self.arrowButton addConstraint:buttonWidth];

    [self addSubview:self.line];
    NSLayoutConstraint *lineTop = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-1];
    NSLayoutConstraint *lineLeading = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *lineTrailing = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *lineHeight = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:1];
    [self addConstraints:@[lineTop,lineLeading,lineTrailing]];
    [self.line addConstraint:lineHeight];
}

- (void)arrowButtonEvent:(UIButton *)button {
    button.selected = YES;
    if (self.delegate && [self.delegate respondsToSelector:@selector(selectionView:didSelected:)]) {
        [self.delegate selectionView:self didSelected:button.selected];
    }
}

- (void)setTitle:(NSString *)title {
    _title = title;
    self.titleLabel.text = title;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _titleLabel.font = [UIFont systemFontOfSize:16.0];
        _titleLabel.textColor = [UIColor colorWithRed:153/255.0 green:153/255.0 blue:153/255.0 alpha:1.0];
        _titleLabel.text = self.title;
    }
    return _titleLabel;
}
- (UIButton *)arrowButton {
    if (!_arrowButton) {
        _arrowButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _arrowButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_arrowButton setImage:[UIImage imageNamed:@"enter_down_arrow"] forState:UIControlStateNormal];
//        [_arrowButton setImage:[UIImage imageNamed:@"enter_up_arrow"] forState:UIControlStateSelected];
        [_arrowButton addTarget:self action:@selector(arrowButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
        _arrowButton.contentMode = UIViewContentModeRight;
    }
    return _arrowButton;
}
- (UIView *)line {
    if (!_line) {
        _line = [[UIView alloc] init];
        _line.translatesAutoresizingMaskIntoConstraints = NO;
        _line.backgroundColor = [UIColor colorWithRed:220/255.0 green:223/255.0 blue:229/255.0 alpha:1.0];
    }
    return _line;
}
@end
