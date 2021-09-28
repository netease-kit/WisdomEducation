//
//  NEEduIMCustomDecoder.m
//  EduLogic
//
//  Created by 郭园园 on 2021/9/15.
//

#import "NEEduIMCustomDecoder.h"
#import <YYModel/YYModel.h>
@implementation NEEduIMCustomDecoder
- (nullable id<NIMCustomAttachment>)decodeAttachment:(nullable NSString *)content {
    NSLog(@"content:%@",content);
    NEEduIMAttach *attach = [NEEduIMAttach yy_modelWithJSON:content];
    return attach;
}
@end
