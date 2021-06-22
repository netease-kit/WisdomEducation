//
//  NEEduEnterRoomResponse.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//

#import <Foundation/Foundation.h>
#import "NEEduHttpRoom.h"
#import "NEEduHttpUser.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduEnterRoomResponse : NSObject
@property (nonatomic , strong) NEEduHttpUser              * member;
@property (nonatomic , strong) NEEduHttpRoom              * room;
@end

NS_ASSUME_NONNULL_END
