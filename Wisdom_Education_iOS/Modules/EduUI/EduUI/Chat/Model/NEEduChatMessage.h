//
//  NEEduChatMessage.h
//  EduUI
//
//  Created by Groot on 2021/5/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger,NEEduChatMessageType) {
    NEEduChatMessageTypeText,
    NEEduChatMessageTypeTime,
};

@interface NEEduChatMessage : NSObject
@property (nonatomic, strong) NSString *userName;
@property (nonatomic, strong) NSString *content;
@property (nonatomic, assign) CGSize textSize;
@property (nonatomic, assign) BOOL myself;
@property (nonatomic, assign) NEEduChatMessageType type;
@property (nonatomic, assign) NSTimeInterval timestamp;
@end

NS_ASSUME_NONNULL_END
