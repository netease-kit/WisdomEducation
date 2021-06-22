//
//  NSString+NE.h
//  EduUI
//
//  Created by Groot on 2021/6/10.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSString (NE)
- (CGSize)sizeWithWidth:(CGFloat)width font:(UIFont *)font;

+ (NSString *)stringFromDate:(NSDate *)date;
@end

NS_ASSUME_NONNULL_END
