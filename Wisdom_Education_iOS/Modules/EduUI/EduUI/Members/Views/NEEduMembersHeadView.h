//
//  NEEduMembersHeadView.h
//  EduUI
//
//  Created by 郭园园 on 2021/6/29.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduMembersHeadView : UIView<UITextFieldDelegate>
@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UIButton *searchButton;

@end

NS_ASSUME_NONNULL_END
