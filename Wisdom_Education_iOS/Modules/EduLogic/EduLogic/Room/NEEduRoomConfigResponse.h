//
//  NEEduRoomConfigResponse.h
//  EduLogic
//
//  Created by 郭园园 on 2021/10/13.
//

#import <Foundation/Foundation.h>
#import "NEEduEnterRoomResponse.h"
#import "NEEduRoomConfigResource.h"
NS_ASSUME_NONNULL_BEGIN

/// 房间配置信息
@interface NEEduRoomConfigResponse : NSObject
@property (nonatomic, strong) NEEduEnterRoomResponse *permissions;
@property (nonatomic, strong) NEEduRoomConfigResource *resource;
- (BOOL)isLiveClass;
@end

NS_ASSUME_NONNULL_END
