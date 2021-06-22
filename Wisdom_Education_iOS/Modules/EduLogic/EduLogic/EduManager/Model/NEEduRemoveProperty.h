//
//  NEEduRemoveProperty.h
//  EduLogic
//
//  Created by Groot on 2021/6/21.
//

#import <Foundation/Foundation.h>
#import "NEEduHttpUser.h"
NS_ASSUME_NONNULL_BEGIN

@interface NEEduRemoveProperty : NSObject
@property (nonatomic, strong) NSString *key;
@property (nonatomic, strong) NEEduHttpUser *member;

@end

NS_ASSUME_NONNULL_END
