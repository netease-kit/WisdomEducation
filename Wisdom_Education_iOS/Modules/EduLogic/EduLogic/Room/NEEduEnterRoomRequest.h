//
//  NEEduEnterRoomRequest.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//

#import <Foundation/Foundation.h>
#import "NEEduStreams.h"
#import "NEEduUserProperty.h"

NS_ASSUME_NONNULL_BEGIN


@interface NEEduEnterRoomRequest : NSObject
@property (nonatomic , strong) NEEduStreams              * streams;
@property (nonatomic , strong) NEEduUserProperty              * properties;
@property (nonatomic , copy) NSString              * role;
@property (nonatomic , copy) NSString              * userName;

@end

NS_ASSUME_NONNULL_END
