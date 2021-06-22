//
//  NSString+NE.m
//  EduUI
//
//  Created by Groot on 2021/6/10.
//

#import "NSString+NE.h"

@implementation NSString (NE)
- (CGSize)sizeWithWidth:(CGFloat)width font:(UIFont *)font {
    return [self boundingRectWithSize:CGSizeMake(width, 1000)
                         options: NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading
                    attributes:@{NSFontAttributeName:font} context:nil].size;
}

+ (NSString *)stringFromDate:(NSDate *)date {
    NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
    fmt.dateFormat = @"yyyy-MM-dd HH:mm";
    return [fmt stringFromDate:date];
}
@end
