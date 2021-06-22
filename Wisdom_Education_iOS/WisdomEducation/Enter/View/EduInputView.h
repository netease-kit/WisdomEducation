//
//  NEInputView.h
//  EduUI
//
//  Created by Groot on 2021/5/12.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@protocol NEInputViewDelegate <NSObject>

- (void)textFieldDidChange:(UITextField *)textField;

@end


@interface EduInputView : UIView<UITextFieldDelegate>

@property (nonatomic, copy) NSString *placeholder;
@property (nonatomic, copy) NSString *text;
@property (nonatomic, weak) id<NEInputViewDelegate> delegate;
@property (nonatomic, strong) UITextField *textField;

- (instancetype)initWithPlaceholder:(NSString *)placeholder;

@end

NS_ASSUME_NONNULL_END
