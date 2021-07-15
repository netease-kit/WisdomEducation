//
//  NEEduChatMessage.h
//  EduUI
//
//  Created by Groot on 2021/5/25.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import <NIMSDK/NIMSDK.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger,NEEduChatMessageType) {
    NEEduChatMessageTypeText,
    NEEduChatMessageTypeTime,
    NEEduChatMessageTypeImage,
};
typedef NS_ENUM(NSInteger,NEEduChatMessageSendState) {
    NEEduChatMessageSendStateNone,
    NEEduChatMessageSendStateSending,
    NEEduChatMessageSendStateSuccess,
    NEEduChatMessageSendStateFailure,
};

@interface NEEduChatMessage : NSObject
@property (nonatomic, copy) NSString *userName;
@property (nonatomic, copy) NSString *content;
@property (nonatomic, strong) UIImage *thumbImage;
@property (nonatomic, assign) CGSize contentSize;
@property (nonatomic, assign) BOOL myself;
@property (nonatomic, assign) NEEduChatMessageType type;
@property (nonatomic, assign) NSTimeInterval timestamp;
@property (nonatomic, copy) NSString *imageUrl;
@property (nonatomic, copy) NSString *imageThumbUrl;
@property (nonatomic, assign) BOOL sendSuccess;
@property (nonatomic, assign) NEEduChatMessageSendState sendState;

@property (nonatomic, strong) NIMMessage *imMessage;
@end

NS_ASSUME_NONNULL_END
