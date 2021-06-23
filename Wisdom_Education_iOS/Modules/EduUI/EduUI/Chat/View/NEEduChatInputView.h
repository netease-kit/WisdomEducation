//
//  NEEduChatInputView.h
//  EduUI
//
//  Created by Groot on 2021/5/24.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduChatInputView : UIView<UITextFieldDelegate>

@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UIButton *sendButton;
- (void)updateUIWithMute:(BOOL)mute;
@end

NS_ASSUME_NONNULL_END
