//
//  NEEduSignalMessage.h
//  EduLogic
//
//  Created by Groot on 2021/5/24.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

//@interface Data :NSObject
//@property (nonatomic , copy) NSString              * appId;
//@property (nonatomic , copy) NSString              * roomUuid;
//@property (nonatomic , strong) NEEduHttpUser              * operatorMember;
//@property (nonatomic , copy) NSArray<NEEduHttpUser *>              * members;
//
//@end

@interface NEEduSignalMessage : NSObject
@property (nonatomic , assign) NSInteger              sequence;
@property (nonatomic , strong) NSDictionary         * data;
@property (nonatomic , assign) NSInteger              timestamp;
@property (nonatomic , assign) NSInteger              cmd;
@property (nonatomic , assign) NSInteger              version;

@property (nonatomic , copy) NSString               * roomUuid;
@property (nonatomic, strong) NSString              * appId;
//inboxType=R表示房间事件 RM表示房间里的透传消息
@property (nonatomic, strong) NSString              * type;

@end

NS_ASSUME_NONNULL_END
