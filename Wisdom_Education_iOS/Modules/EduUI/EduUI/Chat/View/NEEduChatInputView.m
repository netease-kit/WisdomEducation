//
//  NEEduChatInputView.m
//  EduUI
//
//  Created by Groot on 2021/5/24.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduChatInputView.h"
#import <EduLogic/EduLogic.h>

@interface NEEduChatInputView ()
@property (nonatomic, assign) BOOL muteChat;

@end

@implementation NEEduChatInputView

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self setupSubviews];
    }
    return self;
}
- (void)setupSubviews {
    [self addSubview:self.textField];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.textField attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1.0 constant:46];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.textField attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:10];
    NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.textField attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-10];
    [self addConstraints:@[left,top,bottom]];
    
    [self addSubview:self.sendButton];
    NSLayoutConstraint *buttonLeft = [NSLayoutConstraint constraintWithItem:self.sendButton attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.textField attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:10];
    NSLayoutConstraint *buttonRight = [NSLayoutConstraint constraintWithItem:self.sendButton attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:-45];
    NSLayoutConstraint *buttonWidth = [NSLayoutConstraint constraintWithItem:self.sendButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:56];
    NSLayoutConstraint *buttonHeight = [NSLayoutConstraint constraintWithItem:self.sendButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    NSLayoutConstraint *buttonBottom = [NSLayoutConstraint constraintWithItem:self.sendButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-16];
    [self addConstraints:@[buttonLeft,buttonRight,buttonBottom]];
    [self.sendButton addConstraints:@[buttonHeight,buttonWidth]];
    
}
- (void)updateUIWithMute:(BOOL)mute {
    self.muteChat = mute;
    NSString *toast = self.muteChat ? @"已全体禁言" : @"请输入";
    NSAttributedString *string = [[NSAttributedString alloc] initWithString:toast attributes:@{NSForegroundColorAttributeName:[UIColor colorWithRed:74/255.0 green:86/255.0 blue:101/255.0 alpha:1.0]}];
    self.textField.attributedPlaceholder = string;
}
#pragma mark -
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    return !self.muteChat;
}
- (UITextField *)textField {
    if (!_textField) {
        _textField = [[UITextField alloc] init];
        _textField.textColor = [UIColor whiteColor];
        NSAttributedString *string = [[NSAttributedString alloc] initWithString:@"请输入" attributes:@{NSForegroundColorAttributeName:[UIColor colorWithRed:74/255.0 green:86/255.0 blue:101/255.0 alpha:1.0]}];
        self.textField.attributedPlaceholder = string;
        _textField.delegate = self;
        _textField.returnKeyType = UIReturnKeyDone;
        _textField.translatesAutoresizingMaskIntoConstraints = NO;
        
    }
    return _textField;
}
- (UIButton *)sendButton {
    if (!_sendButton) {
        _sendButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_sendButton setTitle:@"发送" forState:UIControlStateNormal];
        _sendButton.backgroundColor = [UIColor colorWithRed:55/255.0 green:114/255.0 blue:254/255.0 alpha:1];
        _sendButton.layer.cornerRadius = 2;
        _sendButton.clipsToBounds = YES;
        _sendButton.titleLabel.font = [UIFont systemFontOfSize:12];
        _sendButton.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _sendButton;
}
@end
