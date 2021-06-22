//
//  NEEduSignalBaseModel.h
//  EduLogic
//
//  Created by Groot on 2021/6/2.
//

#import <Foundation/Foundation.h>
#import "NEEduHttpUser.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduSignalBaseModel : NSObject
@property (nonatomic , copy) NSString              * appId;
@property (nonatomic , strong) NEEduHttpUser              * member;
@property (nonatomic , copy) NSString              * roomUuid;
@property (nonatomic , strong) NEEduHttpUser              * operatorMember;
@property (nonatomic, strong) NSString *streamType;
@end

NS_ASSUME_NONNULL_END
