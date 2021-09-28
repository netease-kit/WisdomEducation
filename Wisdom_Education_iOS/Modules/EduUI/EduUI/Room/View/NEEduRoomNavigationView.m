//
//  NEEduRoomNavigationView.m
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduRoomNavigationView.h"
#import "UIImage+NE.h"

@interface NEEduRoomNavigationView () {
    dispatch_source_t timer;
}
@property (nonatomic, strong) NSString *lessonState;
@property (nonatomic, strong) NEEduHttpRoom *room;
@end



@implementation NEEduRoomNavigationView
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        self.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
        [self setupSubviews];
    }
    return self;
}
- (void)setupSubviews {
    [self addSubview:self.backButton];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1.0 constant:40];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];
    [self addConstraints:@[left,top,bottom]];
    [self.backButton addConstraint:width];
    
    [self addSubview:self.lessonStateLabel];
    NSLayoutConstraint *stateCenterX = [NSLayoutConstraint constraintWithItem:self.lessonStateLabel attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterX multiplier:1.0 constant:0];
    NSLayoutConstraint *stateTop = [NSLayoutConstraint constraintWithItem:self.lessonStateLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *stateBottom = [NSLayoutConstraint constraintWithItem:self.lessonStateLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *stateWidth = [NSLayoutConstraint constraintWithItem:self.lessonStateLabel attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:130];
    [self addConstraints:@[stateCenterX,stateTop,stateBottom]];
    [self.lessonStateLabel addConstraint:stateWidth];
    
    [self addSubview:self.netStateView];
    NSLayoutConstraint *netRight = [NSLayoutConstraint constraintWithItem:self.netStateView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:-36];
    NSLayoutConstraint *netCenterY = [NSLayoutConstraint constraintWithItem:self.netStateView attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:0];
    NSLayoutConstraint *netW = [NSLayoutConstraint constraintWithItem:self.netStateView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    NSLayoutConstraint *netH = [NSLayoutConstraint constraintWithItem:self.netStateView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    [self addConstraints:@[netRight,netCenterY]];
    [self.netStateView addConstraints:@[netW,netH]];
    
    [self addSubview:self.infoButton];
    NSLayoutConstraint *infoRight = [NSLayoutConstraint constraintWithItem:self.infoButton attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.netStateView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *infoCenterY = [NSLayoutConstraint constraintWithItem:self.infoButton attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeCenterY multiplier:1.0 constant:0];
    NSLayoutConstraint *infoW = [NSLayoutConstraint constraintWithItem:self.infoButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    NSLayoutConstraint *infoH = [NSLayoutConstraint constraintWithItem:self.infoButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    [self addConstraints:@[infoRight,infoCenterY]];
    [self.infoButton addConstraints:@[infoW,infoH]];
    
    [self addSubview:self.lessonNameLabel];
    NSLayoutConstraint *nameRight = [NSLayoutConstraint constraintWithItem:self.lessonNameLabel attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.infoButton attribute:NSLayoutAttributeLeft multiplier:1.0 constant:-10];
    NSLayoutConstraint *nameInfoTop = [NSLayoutConstraint constraintWithItem:self.lessonNameLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *nameInfoBottom = [NSLayoutConstraint constraintWithItem:self.lessonNameLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *nameInfoLeft = [NSLayoutConstraint constraintWithItem:self.lessonNameLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.lessonStateLabel attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    [self addConstraints:@[nameRight,nameInfoTop,nameInfoBottom,nameInfoLeft]];
}
- (void)updateRoomState:(NEEduHttpRoom *)room serverTime:(NSInteger)serverTime {
    if (room.states.step.value == NEEduLessonStateClassIn) {
        self.lessonState = @"正在上课";
        NSInteger sec = (serverTime - room.states.step.time)/1000;
        self.timeCount = sec;
        [self startTimer];
    }else if(room.states.step.value == NEEduLessonStateClassOver) {
        self.lessonState = @"课堂已结束";
        [self stopTimer];
    }else {
        self.lessonState = @"课堂未开始";
    }
    self.lessonStateLabel.text = self.lessonState;
    self.lessonNameLabel.text = room.roomName;
    
}

- (void)initTimerCount:(NSInteger)timeCount {
    self.timeCount = timeCount;
}

- (void)startTimer {
    if (timer) {
        return;
    }
    dispatch_queue_t globalQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, globalQueue);
    __weak typeof(self)weakself = self;
    //每秒执行一次
    dispatch_source_set_timer(timer, dispatch_walltime(NULL, 0), 1.0*NSEC_PER_SEC, 0);
    dispatch_source_set_event_handler(timer, ^{
        long hours = weakself.timeCount / 3600;
        long minutes = (weakself.timeCount - (3600*hours)) / 60;
        long seconds = weakself.timeCount % 60;
        NSString *strTime = [NSString stringWithFormat:@"%.2ld:%.2ld",minutes,seconds];
        dispatch_async(dispatch_get_main_queue(), ^{
            weakself.lessonStateLabel.text = [NSString stringWithFormat:@"%@(%@)",self.lessonState,strTime];
        });
        weakself.timeCount++;
    });
    dispatch_resume(timer);
}

- (void)stopTimer {
    if (timer) {
        dispatch_source_cancel(timer);
        timer = nil;
    }
}

- (UIButton *)backButton {
    if (!_backButton) {
        _backButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _backButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_backButton setImage:[UIImage ne_imageNamed:@"room_back"] forState:UIControlStateNormal];
    }
    return _backButton;
}
- (UILabel *)lessonStateLabel {
    if (!_lessonStateLabel) {
        _lessonStateLabel = [[UILabel alloc] init];
        _lessonStateLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _lessonStateLabel.font = [UIFont systemFontOfSize:14];
        _lessonStateLabel.textColor = [UIColor whiteColor];
        _lessonStateLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _lessonStateLabel;
}
- (UILabel *)lessonNameLabel {
    if (!_lessonNameLabel) {
        _lessonNameLabel = [[UILabel alloc] init];
        _lessonNameLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _lessonNameLabel.font = [UIFont systemFontOfSize:14];
        _lessonNameLabel.textColor = [UIColor colorWithRed:180/255.0 green:191/255.0 blue:208/255.0 alpha:1.0];
        _lessonNameLabel.textAlignment = NSTextAlignmentRight;
    }
    return _lessonNameLabel;
}
- (UIButton *)infoButton {
    if (!_infoButton) {
        _infoButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _infoButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_infoButton setImage:[UIImage ne_imageNamed:@"room_info"] forState:UIControlStateNormal];
        _infoButton.contentMode = UIViewContentModeCenter;
    }
    return _infoButton;
}
- (UIImageView *)netStateView {
    if (!_netStateView) {
        _netStateView = [[UIImageView alloc] initWithImage:[UIImage ne_imageNamed:@"net_3"]];
        _netStateView.translatesAutoresizingMaskIntoConstraints = NO;
        _netStateView.contentMode = UIViewContentModeCenter;
    }
    return _netStateView;
}
- (void)dealloc
{
   if (timer) {
        dispatch_source_cancel(timer);
    }
}

@end
