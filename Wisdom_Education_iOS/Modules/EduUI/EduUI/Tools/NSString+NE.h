//
//  NSString+NE.h
//  EduUI
//
//  Created by Groot on 2021/6/10.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSString (NE)

/// 计算文本的尺寸
/// @param width 最大宽度
/// @param font 文字大小
- (CGSize)sizeWithWidth:(CGFloat)width font:(UIFont *)font;

/// 日期转字符串
/// @param date 日期
+ (NSString *)stringFromDate:(NSDate *)date;

@end

NS_ASSUME_NONNULL_END
