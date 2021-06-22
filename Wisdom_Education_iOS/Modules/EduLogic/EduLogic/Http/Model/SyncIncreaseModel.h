//
//  SyncIncreaseModel.h
//  EduSDK
//
//  Created by Netease on 2020/9/1.
//

#import <Foundation/Foundation.h>
#import "BaseModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface SyncIncreaseDataModel : NSObject
@property (nonatomic, assign) NSInteger total;
@property (nonatomic, assign) NSInteger nextId;
@property (nonatomic, strong) NSArray *list;
@end

@interface SyncIncreaseModel : NSObject <BaseModel>
@property (nonatomic, strong) SyncIncreaseDataModel *data;
@end

NS_ASSUME_NONNULL_END
