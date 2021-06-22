//
//  NEEduSignalUserIn.h
//  EduLogic
//
//  Created by Groot on 2021/5/27.
//

#import <Foundation/Foundation.h>
#import "NEEduHttpUser.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduSignalUserIn : NSObject
@property (nonatomic , copy) NSString              * appId;
@property (nonatomic , copy) NSString              * roomUuid;
@property (nonatomic , strong) NEEduHttpUser              * operatorMember;
@property (nonatomic , copy) NSArray<NEEduHttpUser *>              * members;
@end

NS_ASSUME_NONNULL_END
