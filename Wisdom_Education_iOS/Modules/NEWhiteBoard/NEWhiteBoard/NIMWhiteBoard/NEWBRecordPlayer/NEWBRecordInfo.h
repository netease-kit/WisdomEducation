//
//  NEWBRecordInfo.h
//  NEWhiteBoard
//
//  Created by 郭园园 on 2021/8/13.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEWBRecordInfo : NSObject

/// 白板回放开始时间，ms
@property (nonatomic, assign) NSInteger beginTimeStamp;
/// 白板回放结束时间，ms
@property (nonatomic, assign) NSInteger endTimeStamp;
/// 白板回放时间，ms
@property (nonatomic, assign) NSInteger duration;
/// 本次白板会话的参与者账号列表
@property (nonatomic, strong) NSArray <NSString *> *viewerArr;
@end

NS_ASSUME_NONNULL_END
