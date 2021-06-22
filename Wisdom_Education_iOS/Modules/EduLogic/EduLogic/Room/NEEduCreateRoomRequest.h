//
//  NEEduCreateRoomOption.h
//  EduLogic
//
//  Created by Groot on 2021/5/17.
//

#import <Foundation/Foundation.h>
#import "NEEduRoleConfig.h"
#import "NEEduRoomConfig.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduCreateRoomRequest : NSObject
/// 班级名称
@property (nonatomic, strong) NSString *roomName;
@property (nonatomic, assign) NSInteger configId;
@property (nonatomic, strong) NSString *roomUuid;

//@property (nonatomic, strong) NEEduRoomConfig *config;


@end

NS_ASSUME_NONNULL_END
