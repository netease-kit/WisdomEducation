//
//  NERoomConfig.h
//  EduLogic
//
//  Created by 郭园园 on 2021/9/2.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@interface Resource : NSObject

/// 房间是否打开直播，默认：NO
@property (nonatomic, assign) BOOL live;

/// 房间是否打开直播，默认：YES
@property (nonatomic, assign) BOOL rtc;

/// 房间是否打开聊天时，默认：YES
@property (nonatomic, assign) BOOL chatroom;

/// 房间是否打开聊天时，默认：YES
@property (nonatomic, assign) BOOL whiteboard;

@end

@interface NERoomConfig : NSObject
@property (nonatomic, strong) Resource *resource;

@end

NS_ASSUME_NONNULL_END
