//
//  NEEduRoomConfigResource.h
//  EduLogic
//
//  Created by 郭园园 on 2021/10/13.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 房间能力配置
@interface NEEduRoomConfigResource : NSObject
@property (nonatomic, assign) BOOL rtc;
@property (nonatomic, assign) BOOL whiteboard;
@property (nonatomic, assign) BOOL live;
@end

NS_ASSUME_NONNULL_END
