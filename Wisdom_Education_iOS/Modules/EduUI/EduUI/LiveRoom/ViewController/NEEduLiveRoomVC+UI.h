// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import "NEEduLiveRoomVC.h"

NS_ASSUME_NONNULL_BEGIN
/// 直播大班课UI扩展
@interface NEEduLiveRoomVC (UI)
/// 默认子视图布局
//- (WKWebView *)boardView;
- (void)infoButtonClick:(UIButton *)button;
/// 默认直播大班课视图
- (void)setupDefaultSubviews;
- (void)setupNewSubview;
- (void)setupDefaultContentView;
/// 添加聊天室menu
- (void)addChatMenue;
/// 添加举手menu
- (void)addHandsUpMenue;
/// 添加白板
- (void)addWhiteboardView;

- (void)updateUIWithRoom:(NEEduHttpRoom *)room;
- (void)updateMemberVCWithProfile:(NEEduRoomProfile *)profile;
- (NSArray <NEEduHttpUser *>*)membersWithProfile:(NEEduRoomProfile *)profile;
@end

NS_ASSUME_NONNULL_END
