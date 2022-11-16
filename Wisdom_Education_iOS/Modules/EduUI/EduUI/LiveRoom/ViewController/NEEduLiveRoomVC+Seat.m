// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import "NEEduLiveRoomVC+Seat.h"
#import "NSArray+NEUIExtension.h"
#import "NEEduLiveRoomVC+Logic.h"
#import "NEEduLiveRoomVC+UI.h"

@implementation NEEduLiveRoomVC (Seat)
- (void)getSeatRequestList{
    [NEEduManager.shared.seatService getSeatRequestList:self.roomUuid success:^(id  _Nonnull objModel) {
        for (NEEduSeatRequestItem *item in objModel){
            if([NEEduManager.shared.localUser.userUuid isEqualToString:item.userUuid]){
                // 取消申请上麦
                [[NEEduManager shared].seatService cancelApplySeat:self.roomUuid
                                                          userName:self.userName
                                                           success:^{}
                                                           failure:^(NSError * _Nullable error, NSInteger statusCode) {
                    if (!error) {
                        self.seatAction.action = NEEduSeatUserActionTypeCancelApply;
                    }
                    else{
                        [self.view makeToast:[NSString stringWithFormat:@"取消举手失败:  %@", error.localizedDescription]];
                    }
                }];
            }
        }
    }failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (!error) return;
        [self.view makeToast:[NSString stringWithFormat:@"获取麦位请求列表失败:  %@", error.localizedDescription]];
    }];
}
- (void)getSeatInfo {
    [NEEduManager.shared.seatService getSeatInfo:self.roomUuid
                                         success:^(id  _Nonnull objModel) {
        NEEduSeatInfo *info = (NEEduSeatInfo *)objModel;
        NEEduSeatItem *currentItem = [info.seatIndexList ne_find:^BOOL(NEEduSeatItem * _Nonnull  item) {
            return [item.userUuid isEqualToString:NEEduManager.shared.localUser.userUuid];
        }];
        
        if (currentItem.status == NEEduSeatItemStatusTaken) { // 说明在麦位上
            [self stopScreenShare:NO];
            // 调用服务端deleteMember 接口
            self.leaveState = NEEduLeaveSeatPassivity;
            [HttpManager leaveRoomWithRoomUuid:self.roomUuid userUuid:NEEduManager.shared.localUser.userUuid success:^{
                self.handsupState = NEEduHandsupStateIdle;
            }failure:^(NSError * _Nullable error, NSInteger statusCode) {
                
            }];
        }
    } failure:^(NSError * _Nullable error, NSInteger statusCode) {
        if (!error) return;
        [self.view makeToast:[NSString stringWithFormat:@"获取麦位信息失败:  %@", error.localizedDescription]];
    }];
}


- (void)onSeatRequestSubmitted:(NSString *)user {
    NSLog(@"成员申请");
    if ([user isEqualToString:[NEEduManager shared].localUser.userUuid]){
        self.handsupState = NEEduHandsupStateApply;
        self.handsupItem.selectTitle = @"举手中";
        self.handsupItem.isSelected = YES;
    }
}
- (void)onSeatRequestCancelled:(NSString *)user {
    NSLog(@"成员取消申请");
    if ([user isEqualToString:[NEEduManager shared].localUser.userUuid]){
        self.handsupState = NEEduHandsupStateIdle;
        self.handsupItem.isSelected = NO;
    }
}
- (void)onSeatRequestApproved:(NSString *)user operateBy:(NSString *)operateBy {
    NSLog(@"管理员同意申请");
    if ([user isEqualToString:[NEEduManager shared].localUser.userUuid]){
        [self handleSeatRequestApproved:user];
    }

}
- (void)onSeatRequestRejected:(NSString *)user operateBy:(NSString *)operateBy {
    NSLog(@"管理员拒绝申请");
    if ([user isEqualToString:[NEEduManager shared].localUser.userUuid]){
        self.handsupState = NEEduHandsupStateTeaReject;
        [self.view makeToast:@"举手申请被拒绝"];
        self.handsupItem.isSelected = NO;
    }
}
- (void)onSeatLeave:(NSString *)user {
    if (self.leaveState == NEEduLeaveSeatPassivity){
        return ;
    }
    NSLog(@"成员离开");
    if ([user isEqualToString:[NEEduManager shared].localUser.userUuid]){
        [self handleSeatLeave];
    }
}
- (void)onSeatKicked:(NSString *)user operateBy:(NSString *)operateBy {
    NSLog(@"管理员踢成员下麦");
    if ([user isEqualToString:[NEEduManager shared].localUser.userUuid]){
        self.handsupState = NEEduHandsupStateIdle;
        [self handleSeatKicked];
    }
}
- (void)onSeatListChanged:(NSArray <NEEduSeatItem *> *)seatItems {
    NSLog(@"麦位列表变更");
}

@end
