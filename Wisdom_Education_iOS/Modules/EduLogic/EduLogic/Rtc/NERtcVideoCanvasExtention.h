//
//  NERtcVideoCanvasExtention.h
//  EduSDK
//
//  Created by netease on 2021/3/9.
//

#import <NERtcSDK/NERtcSDK.h>

NS_ASSUME_NONNULL_BEGIN

@interface NERtcVideoCanvasExtention : NERtcVideoCanvas
/// 用户Id
@property (assign, nonatomic) NSUInteger uid;
/// 频道名字
@property (copy, nonatomic) NSString* _Nullable channel;

@end

NS_ASSUME_NONNULL_END
