//
//  NEEduRoomNavigationView.h
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "NEEduHttpRoom.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduRoomNavigationView : UIView
@property (nonatomic, strong) UIButton *backButton;
@property (nonatomic, strong) UILabel *lessonStateLabel;
@property (nonatomic, strong) UILabel *lessonNameLabel;
@property (nonatomic, strong) UIButton *infoButton;
@property (nonatomic, strong) UIImageView *netStateView;

- (void)updateRoomState:(NEEduHttpRoom *)room serverTime:(NSInteger)serverTime;
@end

NS_ASSUME_NONNULL_END
