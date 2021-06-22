//
//  NEEduChatRoomInfo.h
//  EduLogic
//
//  Created by Groot on 2021/5/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduChatRoomInfo : NSObject
@property (nonatomic, strong) NSString *roomId;
@property (nonatomic, strong) NSString *name;
@property (nonatomic, assign) NSInteger onlineNumber;

@end

NS_ASSUME_NONNULL_END
