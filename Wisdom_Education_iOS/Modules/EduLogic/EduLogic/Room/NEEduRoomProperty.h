//
//  NEEduRoomProperty.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//

#import <Foundation/Foundation.h>
#import "NEEduChatRoom.h"
#import "NEEduHandsupProperty.h"
#import "NEEduWhiteboardInfo.h"
NS_ASSUME_NONNULL_BEGIN

@interface NEEduRoomProperty : NSObject
@property (nonatomic, strong) NEEduChatRoom *chatRoom;
@property (nonatomic, strong) NEEduHandsupProperty *avHandsUp;
@property (nonatomic, strong) NEEduWhiteboardInfo *whiteboard;
@end

NS_ASSUME_NONNULL_END
