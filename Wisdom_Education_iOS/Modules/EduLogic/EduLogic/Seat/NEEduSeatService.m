// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import "NEEduSeatService.h"
#import <NIMSDK/NIMSDK.h>
#import "NEEduSeatInfo.h"
#import "NEEduIMAttach.h"

#import "NEEduSeatActionEvent.h"
#import "NEEduSeatItemChangeEvent.h"
#import "NEEduSeatManagerChangeEvent.h"
#import "NEEduSeatRequestItem.h"

@interface NEEduSeatService () <NIMChatManagerDelegate>
@property(nonatomic, strong) NSHashTable *listeners;
@property(nonatomic, strong) NEEduSeatItemChangeEvent *itemChangeEvent;
@end

@implementation NEEduSeatService
- (instancetype)init {
    self = [super init];
    if (self) {
        [NIMSDK.sharedSDK.chatManager addDelegate:self];
    }
    return self;
}
- (void)addSeatListener:(id<NEEduSeatEventListener>)listener {
    [self.listeners addObject:listener];
}
- (void)removeSeatListener:(id<NEEduSeatEventListener>)listener {
    [self.listeners removeObject:listener];
}
- (void)getSeatInfo:(NSString *)roomUuid
            success:(NEEduSuccessBlock)success
            failure:(NEEduFailureBlock)failure {
    [HttpManager getSeatInfo:roomUuid
                   classType:NEEduSeatInfo.class
                     success:success
                     failure:failure];
}
- (void)getSeatRequestList:(NSString *)roomUuid
                   success:(void (^)(NSArray <NEEduSeatRequestItem *> *list))success
                   failure:(_Nullable NEEduFailureBlock)failure {
    [HttpManager getSeatRequestList:roomUuid
                          classType:NEEduSeatRequestItem.class
                            success:^(id  _Nullable objModel) {
        NSArray <NEEduSeatRequestItem *> *list = (NSArray <NEEduSeatRequestItem *>*)objModel;
        if (success) success(list);
    } failure:failure];
}

- (void)applySeat:(NSString *)roomUuid
         userName:(NSString *)userName
          success:(void(^ _Nullable)(void))success
          failure:(NEEduFailureBlock)failure {
    [HttpManager userSeatOperation:roomUuid
                          userName:(NSString *)userName
                            action:[NEEduSeatUserAction action:NEEduSeatUserActionTypeApply]
                           success:success
                           failure:failure];
}
- (void)cancelApplySeat:(NSString *)roomUuid
               userName:(NSString *)userName
                success:(void(^ _Nullable)(void))success
                failure:(NEEduFailureBlock)failure {
    [HttpManager userSeatOperation:roomUuid
                          userName:(NSString *)userName
                            action:[NEEduSeatUserAction action:NEEduSeatUserActionTypeCancelApply]
                           success:success
                           failure:failure];
}

- (void)leaveSeat:(NSString *)roomUuid
         userName:(NSString *)userName
          success:(void(^ _Nullable)(void))success
          failure:(NEEduFailureBlock)failure {
    [HttpManager userSeatOperation:roomUuid
                          userName:(NSString *)userName
                            action:[NEEduSeatUserAction action:NEEduSeatUserActionTypeLeave]
                           success:success
                           failure:failure];
}

- (void)acceptSeatInvitation:(NSString *)roomUuid
                    userName:(NSString *)userName
                     success:(void(^ _Nullable)(void))success
                     failure:(NEEduFailureBlock)failure {
    
    [HttpManager userSeatOperation:roomUuid
                          userName:(NSString *)userName
                            action:[NEEduSeatUserAction action:NEEduSeatUserActionTypeAcceptInvitation]
                           success:success
                           failure:failure];
}

- (void)rejectSeatInvitation:(NSString *)roomUuid
                    userName:(NSString *)userName
                     success:(void(^ _Nullable)(void))success
                     failure:(NEEduFailureBlock)failure {
    [HttpManager userSeatOperation:roomUuid
                          userName:(NSString *)userName
                            action:[NEEduSeatUserAction action:NEEduSeatUserActionTypeRejectInvitation]
                           success:success
                           failure:failure];
}
#pragma mark ------------------------ NIMChatManagerDelegate ------------------------
- (void)onRecvMessages:(NSArray<NIMMessage *> *)messages {

    for (NIMMessage *msg in messages) {
        NIMSession *session = msg.session;
        if (!session ||
            session.sessionType != NIMSessionTypeChatroom ||
            msg.messageType != NIMMessageTypeCustom) continue;
        NIMCustomObject *custom = msg.messageObject;
        NEEduIMAttach *attach = custom.attachment;
        NEEduSignalMessage *message = attach.data;
        Class seatEventClass = [self fetchClassWithCmd:message.cmd];
        if (!seatEventClass) continue;
        NSDictionary *dic = [message yy_modelToJSONObject];
        id seatEvent = [seatEventClass yy_modelWithDictionary:dic];
        [self handleSeatEvent:seatEvent];
    }
}

- (void)handleSeatEvent:(NEEduSeatEvent * _Nullable)seatEvent {
    if (!seatEvent) return;
    if ([seatEvent isKindOfClass:[NEEduSeatActionEvent class]]) {
        // 处理操作事件
        [self handleSeatActionEvent:(NEEduSeatActionEvent *)seatEvent];
    } else if ([seatEvent isKindOfClass:[NEEduSeatItemChangeEvent class]]) {
        // 处理变更事件
        NEEduSeatItemChangeEvent *changeEvent = (NEEduSeatItemChangeEvent *)seatEvent;
        if (self.itemChangeEvent && self.itemChangeEvent.timestamp > changeEvent.timestamp) {
            return;
        }
        self.itemChangeEvent = changeEvent;
        
        for (id <NEEduSeatEventListener> listener in self.listeners.allObjects) {
            if (listener && [listener respondsToSelector:@selector(onSeatListChanged:)]) {
                [listener onSeatListChanged:changeEvent.data.seatList];
            }
        }
    }
}

- (void)handleSeatActionEvent:(NEEduSeatActionEvent *)event {
    NSString *user = event.data.seatUser.userUuid;
    NSString *operateBy = event.data.operatorUser.userUuid;
    switch (event.cmd) {
        case 110: { // 管理员同意申请
            for (id <NEEduSeatEventListener> listener in self.listeners.allObjects) {
                if (listener && [listener respondsToSelector:@selector(onSeatRequestApproved:operateBy:)]) {
                    [listener onSeatRequestApproved:user
                                                       operateBy:operateBy];
                }
            }
        }
            break;
        case 113: {// 管理员拒绝申请
            for (id <NEEduSeatEventListener> listener in self.listeners.allObjects) {
                if (listener && [listener respondsToSelector:@selector(onSeatRequestRejected:operateBy:)]) {
                    [listener onSeatRequestRejected:user
                                                       operateBy:operateBy];
                }
            }
        }
            break;
        case 114: {// 管理员把成员从麦位上踢出
            for (id <NEEduSeatEventListener> listener in self.listeners.allObjects) {
                if (listener && [listener respondsToSelector:@selector(onSeatKicked:operateBy:)]) {
                    [listener onSeatKicked:user
                                              operateBy:operateBy];
                }
            }
        }
            break;
        case 115: {// 成员申请
            for (id <NEEduSeatEventListener> listener in self.listeners.allObjects) {
                if (listener && [listener respondsToSelector:@selector(onSeatRequestSubmitted:)]) {
                    [listener onSeatRequestSubmitted:user];
                }
            }
        }
            break;
        case 116: {// 成员取消申请
            for (id <NEEduSeatEventListener> listener in self.listeners.allObjects) {
                if (listener && [listener respondsToSelector:@selector(onSeatRequestCancelled:)]) {
                    [listener onSeatRequestCancelled:user];
                }
            }
        }
            break;
        case 119: {// 主动离开麦位
            for (id <NEEduSeatEventListener> listener in self.listeners.allObjects) {
                if (listener && [listener respondsToSelector:@selector(onSeatLeave:)]) {
                    [listener onSeatLeave:user];
                }
            }
        }
            break;
        default: break;
    }
}

- (Class _Nullable)fetchClassWithCmd:(NSInteger)cmd {
    if (cmd == 120) {
        return NEEduSeatItemChangeEvent.class;
    } else if (cmd == 121) {
        return NEEduSeatManagerChangeEvent.class;
    } else if (cmd == 110 || cmd == 122 || (cmd >= 112 && cmd <= 119)) {
        return NEEduSeatActionEvent.class;
    }
    return nil;
}
#pragma mark ------------------------ Getter ------------------------
- (NSHashTable *)listeners {
    if (!_listeners) {
        _listeners = [NSHashTable weakObjectsHashTable];
    }
    return _listeners;
}
@end
