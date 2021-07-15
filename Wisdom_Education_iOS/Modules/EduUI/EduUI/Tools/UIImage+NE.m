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

- (CGSize)ne_showSizeWithMaxWidth:(CGFloat)maxWidth maxHeight:(CGFloat)maxHeight {
    CGFloat imageScale = self.size.width/self.size.height;
    CGFloat scale = maxWidth/maxHeight;
    if (imageScale >= scale) {
        //宽照片 使用最大宽度值，计算高度
        CGFloat height = maxWidth / imageScale;
        return CGSizeMake(maxWidth, height);
    }else {
        CGFloat width = maxHeight * imageScale;
        return CGSizeMake(width, maxHeight);
    }
}
@end
