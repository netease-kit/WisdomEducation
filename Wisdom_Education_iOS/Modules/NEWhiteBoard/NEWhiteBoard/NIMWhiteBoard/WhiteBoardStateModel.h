//
//  WhiteBoardStateModel.h
//  NEWhiteBoard
//
//  Created by Netease on 2020/9/3.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface WhiteBoardStateModel : NSObject
@property (nonatomic, assign, readonly) BOOL follow;
@property (nonatomic, strong, readonly) NSArray<NSString *> * _Nullable grantUsers;
@end

NS_ASSUME_NONNULL_END
