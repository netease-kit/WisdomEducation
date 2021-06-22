//
//  NEEduRoleConfig.h
//  EduLogic
//
//  Created by Groot on 2021/5/17.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduRoleLimit : NSObject
@property (nonatomic, assign) NSInteger limit;
@end

@interface NEEduRoleConfig : NSObject
/// 老师角色
@property (nonatomic, strong) NEEduRoleLimit *host;
/// 学生角色
@property (nonatomic, strong) NEEduRoleLimit *broadcaster;
/// 大班课学生角色
@property (nonatomic, strong) NEEduRoleLimit *audience;

@end

NS_ASSUME_NONNULL_END
