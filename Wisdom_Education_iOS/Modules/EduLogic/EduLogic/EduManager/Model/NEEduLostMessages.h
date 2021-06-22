//
//  NEEduLostMessages.h
//  EduLogic
//
//  Created by Groot on 2021/6/17.
//

#import <Foundation/Foundation.h>
#import "NEEduSignalMessage.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduLostMessages : NSObject
@property (nonatomic, assign) NSInteger total;
@property (nonatomic, copy) NSArray<NEEduSignalMessage *> *list;

@end

NS_ASSUME_NONNULL_END
