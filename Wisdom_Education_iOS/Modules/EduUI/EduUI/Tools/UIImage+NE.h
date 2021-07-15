//
//  UIImage+NE.h
//  EduUI
//
//  Created by Groot on 2021/5/10.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIImage (NE)

/// 读取仓库中的图片资源
/// @param name 图片名字
+ (UIImage *)ne_imageNamed:(NSString *)name;

/// 计算图片缩放后的展示尺寸
/// @param maxWidth 最大宽度
/// @param maxHeight 最大高度
- (CGSize)ne_showSizeWithMaxWidth:(CGFloat)maxWidth maxHeight:(CGFloat)maxHeight;

@end

NS_ASSUME_NONNULL_END
