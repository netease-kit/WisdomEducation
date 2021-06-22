//
//  NEEduHttpUser.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//

#import <Foundation/Foundation.h>
#import "NEEduUserProperty.h"
#import "NEEduStreams.h"

NS_ASSUME_NONNULL_BEGIN
extern const NSString * NEEduRoleHost;
extern const NSString * NEEduRoleBroadcaster;
extern const NSString * NEEduRoleAudience;

@interface NEEduHttpUser : NSObject
@property (nonatomic , copy) NSString              * rtcKey;
@property (nonatomic , assign) NSInteger              rtcUid;
@property (nonatomic , copy) NSString              * userName;
@property (nonatomic , copy) NSString              * role;
@property (nonatomic , strong) NEEduUserProperty   * properties;
@property (nonatomic , copy) NSString              * userUuid;
@property (nonatomic , copy) NSString              * rtcToken;
@property (nonatomic , strong) NEEduStreams        * streams;

@end

NS_ASSUME_NONNULL_END
