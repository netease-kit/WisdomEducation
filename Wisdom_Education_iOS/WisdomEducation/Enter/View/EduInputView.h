//
//  NEInputView.h
//  EduUI
//
//  Created by Groot on 2021/5/12.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@protocol NEInputViewDelegate <NSObject>

- (void)textFieldDidChange:(UITextField *)textField;
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string;
@end


@interface EduInputView : UIView<UITextFieldDelegate>

@property (nonatomic, copy) NSString *placeholder;
@property (nonatomic, copy) NSString *text;
@property (nonatomic, weak) id<NEInputViewDelegate> delegate;
@property (nonatomic, strong) UITextField *textField;

- (instancetype)initWithPlaceholder:(NSString *)placeholder;

@end

NS_ASSUME_NONNULL_END
