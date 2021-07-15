//
//  NEInputView.m
//  EduUI
//
//  Created by Groot on 2021/5/12.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "EduInputView.h"
@interface EduInputView()<UITextFieldDelegate>
@property (nonatomic, strong) UIView *line;
@end

@implementation EduInputView
- (instancetype)initWithPlaceholder:(NSString *)title
{
    self = [super initWithFrame:CGRectZero];
    if (self) {
        self.placeholder = title;
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self setupSubviews];
    }
    return self;
}
- (void)setupSubviews {
    [self addSubview:self.textField];
    [self addSubview:self.line];
    self.translatesAutoresizingMaskIntoConstraints = NO;
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.textField attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *leading = [NSLayoutConstraint constraintWithItem:self.textField attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *trailing = [NSLayoutConstraint constraintWithItem:self.textField attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.textField attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self addConstraints:@[top,leading,trailing,bottom]];

    NSLayoutConstraint *lineTop = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-1];
    NSLayoutConstraint *lineLeading = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *lineTrailing = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *lineHeight = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:1];
    [self addConstraints:@[lineTop,lineLeading,lineTrailing]];
    [self.line addConstraint:lineHeight];
}

- (void)textFieldDidChange:(UITextField *)textField {
    if (self.delegate && [self.delegate respondsToSelector:@selector(textFieldDidChange:)]) {
        [self.delegate textFieldDidChange:textField];
    }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if (self.delegate && [self.delegate respondsToSelector:@selector(textField:shouldChangeCharactersInRange:replacementString:)]) {
        return [self.delegate textField:textField shouldChangeCharactersInRange:range replacementString:string];
    }
    return YES;
}
- (void)setPlaceholder:(NSString *)placeholder {
//    self.textField.placeholder = placeholder;
    NSAttributedString *string = [[NSAttributedString alloc] initWithString:placeholder attributes:@{NSForegroundColorAttributeName:[UIColor lightGrayColor]}];
    self.textField.attributedPlaceholder = string;
}

- (NSString *)text {
    return self.textField.text;
}
- (void)setText:(NSString *)text {
    self.textField.text = text;
}
- (UITextField *)textField {
    if (!_textField) {
        _textField = [[UITextField alloc] initWithFrame:CGRectZero];
        _textField.textColor = [UIColor blackColor];
        _textField.clearButtonMode = UITextFieldViewModeWhileEditing;
        _textField.font = [UIFont systemFontOfSize:17];
        _textField.delegate = self;
        _textField.keyboardType = UIKeyboardTypeNumberPad;
        _textField.translatesAutoresizingMaskIntoConstraints = NO;
        NSAttributedString *string = [[NSAttributedString alloc] initWithString:self.placeholder ?: @"请输入..." attributes:@{NSForegroundColorAttributeName:[UIColor lightGrayColor]}];
        _textField.attributedPlaceholder = string;
        [_textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    }
    return _textField;
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
