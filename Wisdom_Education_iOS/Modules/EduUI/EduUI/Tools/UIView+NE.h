//
//  UIView+NE.h
//  EduUI
//
//  Created by Groot on 2021/6/7.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIView (NE)
- (void)showAlertViewOnVC:(UIViewController *)vc withTitle:(NSString *)title subTitle:(NSString *)subTitle confirm:(void(^)(void))confirm;
@end

NS_ASSUME_NONNULL_END
