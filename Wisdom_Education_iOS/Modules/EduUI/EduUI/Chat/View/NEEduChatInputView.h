//
//  NEEduChatInputView.h
//  EduUI
//
//  Created by Groot on 2021/5/24.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduChatInputView : UIView<UITextFieldDelegate>

@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UIButton *sendButton;
- (void)updateUIWithMute:(BOOL)mute;
@end

NS_ASSUME_NONNULL_END
