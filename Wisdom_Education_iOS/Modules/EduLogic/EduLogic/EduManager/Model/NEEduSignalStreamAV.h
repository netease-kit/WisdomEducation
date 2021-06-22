//
//  NEEduSignalStreamAV.h
//  EduLogic
//
//  Created by Groot on 2021/6/2.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduSignalStreamAV : NSObject
/// 表示授权
@property (nonatomic , strong) NSNumber *            value;
/// 授权关闭/打开音频消息
@property (nonatomic , strong) NSNumber *            audio;
/// 授权关闭/打开音频消息
@property (nonatomic , strong) NSNumber *            video;

@end

NS_ASSUME_NONNULL_END
