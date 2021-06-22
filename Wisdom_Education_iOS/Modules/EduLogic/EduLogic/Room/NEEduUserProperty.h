//
//  NEEduUserProperty.h
//  EduLogic
//
//  Created by Groot on 2021/5/18.
//

#import <Foundation/Foundation.h>
#import "NEEduPropertyItem.h"
#import "NEEduSignalStreamAV.h"
#import "NEEduHandsupProperty.h"
#import "NEEduWhiteboardProperty.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduUserProperty : NSObject
@property (nonatomic , strong) NEEduPropertyItem              * screenShare;
@property (nonatomic , strong) NEEduWhiteboardProperty              * whiteboard;
@property (nonatomic , strong) NEEduSignalStreamAV            *streamAV;
@property (nonatomic, strong) NEEduHandsupProperty            *avHandsUp;

@end

NS_ASSUME_NONNULL_END
