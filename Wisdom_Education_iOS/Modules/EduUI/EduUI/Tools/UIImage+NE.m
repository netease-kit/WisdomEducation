//
//  UIImage+NE.m
//  EduUI
//
//  Created by Groot on 2021/5/10.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "UIImage+NE.h"
#import "NEEduBaseViewController.h"

@implementation UIImage (NE)

+ (UIImage *)ne_imageNamed:(NSString *)name {
    NSString *path = [[NSBundle bundleForClass:[NEEduBaseViewController class]].resourcePath stringByAppendingPathComponent:@"/EduUIBundle.bundle"];
    NSBundle *bundle = [NSBundle bundleWithPath:path];
    return [UIImage imageNamed:name inBundle:bundle compatibleWithTraitCollection:nil];
}

@end
