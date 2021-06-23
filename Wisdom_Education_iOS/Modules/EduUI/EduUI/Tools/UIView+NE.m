//
//  UIView+NE.m
//  EduUI
//
//  Created by Groot on 2021/6/7.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "UIView+NE.h"

@implementation UIView (NE)
- (void)showAlertViewOnVC:(UIViewController *)vc withTitle:(NSString *)title subTitle:(NSString *)subTitle confirm:(void(^)())confirm {
    UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:title message:subTitle preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *action = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    UIAlertAction *okaction = [UIAlertAction actionWithTitle:@"确认" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        if (confirm) {
            confirm();
        }
    }];
    [alertVC addAction:action];
    [alertVC addAction:okaction];
    UIPopoverPresentationController *popover = alertVC.popoverPresentationController;
    if (popover) {
        popover.sourceRect = CGRectMake(self.center.x, self.center.y, 1, 1);
        popover.permittedArrowDirections = UIPopoverArrowDirectionAny;
    }
    [vc presentViewController:alertVC animated:YES completion:nil];
}

@end
