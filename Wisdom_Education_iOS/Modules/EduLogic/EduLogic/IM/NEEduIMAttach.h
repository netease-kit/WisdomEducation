//
//  NEEduIMAttach.h
//  EduLogic
//
//  Created by 郭园园 on 2021/9/15.
//

#import <Foundation/Foundation.h>
#import "NEEduSignalMessage.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduIMAttach : NSObject
@property (nonatomic, assign) NSInteger type;
@property (nonatomic, strong) NEEduSignalMessage *data;

@end

NS_ASSUME_NONNULL_END
