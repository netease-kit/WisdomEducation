//
//  UIView+NE.h
//  EduUI
//
//  Created by Groot on 2021/6/7.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIView (NE)
- (void)showAlertViewOnVC:(UIViewController *)vc withTitle:(NSString *)title subTitle:(NSString *)subTitle confirm:(void(^)(void))confirm;

@end

NS_ASSUME_NONNULL_END
