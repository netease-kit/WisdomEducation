//
//  NEEduLiveInfo.h
//  EduLogic
//
//  Created by 郭园园 on 2021/9/14.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduLiveInfo : NSObject
@property (nonatomic, strong) NSString *pushUrl;
@property (nonatomic, strong) NSString *pullHttpUrl;

@property (nonatomic, strong) NSString *pullRtmpUrl;
@property (nonatomic, strong) NSString *pullHlsUrl;

@property (nonatomic, strong) NSString *pullRtsUrl;

@end

NS_ASSUME_NONNULL_END
