//
//  NEEduVideoCell.m
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduVideoCell.h"
#import "UIImage+NE.h"
#import <EduLogic/EduLogic.h>

@interface NEEduVideoCell ()
@property (nonatomic, strong) UIImageView *avatarView;
@property (nonatomic, strong) UIView *grayView;
@property (nonatomic, strong) UILabel *namelabel;
@property (nonatomic, strong) UIImageView *audioView;
@property (nonatomic, strong) UIImageView *cameraView;
@property (nonatomic, strong) UIImageView *whiteboard;
@property (nonatomic, strong) NSLayoutConstraint *whiteWithCons;
@end

@implementation NEEduVideoCell
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithRed:16/255.0 green:20/255.0 blue:24/255.0 alpha:1.0];
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self setupSubviews];
        [self addTapGesture];
    }
    return self;
}

- (void)addTapGesture {
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapEvent)];
    [self.contentView addGestureRecognizer:tap];
}
- (void)tapEvent {
    if (self.delegate && [self.delegate respondsToSelector:@selector(didTapCell:)]) {
        [self.delegate didTapCell:self];
    }
}
- (void)setMember:(NEEduHttpUser *)member {
    _member = member;
    if (member.streams.video.value) {
        [[EduManager shared] setCanvasView:self.videoView forMember:member];
    }else {
        [[EduManager shared] setCanvasView:nil forMember:member];
    }
    if ([member.role isEqualToString:NEEduRoleHost]) {
        self.namelabel.text = member.userName.length?[NSString stringWithFormat:@"%@(老师)",member.userName]:@"";
        self.whiteboard.hidden = YES;
        self.whiteWithCons.constant = 0;
    }else {
        self.namelabel.text = member.userName.length?[NSString stringWithFormat:@"%@(学生)",member.userName]:@"";
        if (self.showWhiteboardIcon) {
            self.whiteboard.hidden = member.properties.whiteboard.drawable ? NO : YES;
            self.whiteWithCons.constant = member.properties.whiteboard.drawable ? 20 : 0;
        }
    }
    self.audioView.image = member.streams.audio.value ? [UIImage ne_imageNamed:@"room_audio"]:[UIImage ne_imageNamed:@"room_audio_off"];
    self.cameraView.image = member.streams.video.value ? [UIImage ne_imageNamed:@"room_video"]:[UIImage ne_imageNamed:@"room_video_off"];
    self.avatarView.hidden = member.streams.video.value;
    self.audioView.hidden = member.userUuid.length > 0 ? NO : YES;
    self.cameraView.hidden = member.userUuid.length > 0 ? NO : YES;
}

- (void)setupSubviews {
    [self.contentView addSubview:self.avatarView];
    NSLayoutConstraint *avatarTop = [NSLayoutConstraint constraintWithItem:self.avatarView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *avatarLeft = [NSLayoutConstraint constraintWithItem:self.avatarView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *avatarRight = [NSLayoutConstraint constraintWithItem:self.avatarView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *avatarBottom = [NSLayoutConstraint constraintWithItem:self.avatarView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.contentView addConstraints:@[avatarTop,avatarLeft,avatarRight,avatarBottom]];
    
    [self.contentView addSubview:self.videoView];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.videoView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.videoView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:self.videoView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.videoView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.contentView addConstraints:@[top,left,right,bottom]];
    
    [self.contentView addSubview:self.grayView];
    NSLayoutConstraint *grayBottom = [NSLayoutConstraint constraintWithItem:self.grayView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *grayLeft = [NSLayoutConstraint constraintWithItem:self.grayView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *grayRight = [NSLayoutConstraint constraintWithItem:self.grayView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *grayHeight = [NSLayoutConstraint constraintWithItem:self.grayView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:22];
    [self.contentView addConstraints:@[grayBottom,grayLeft,grayRight,grayHeight]];
    
    [self.grayView addSubview:self.namelabel];
    NSLayoutConstraint *nameLeft = [NSLayoutConstraint constraintWithItem:self.namelabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:10];
    NSLayoutConstraint *nameTop = [NSLayoutConstraint constraintWithItem:self.namelabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *nameBottom = [NSLayoutConstraint constraintWithItem:self.namelabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.grayView addConstraints:@[nameLeft,nameTop,nameBottom]];
    
    [self.grayView addSubview:self.whiteboard];
    NSLayoutConstraint *whiteboardLeft = [NSLayoutConstraint constraintWithItem:self.whiteboard attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.namelabel attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *whiteboardTop = [NSLayoutConstraint constraintWithItem:self.whiteboard attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *whiteboardBottom = [NSLayoutConstraint constraintWithItem:self.whiteboard attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *whiteWithCons = [NSLayoutConstraint constraintWithItem:self.whiteboard attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:20];
    [self.grayView addConstraints:@[whiteboardLeft,whiteboardTop,whiteboardBottom]];
    [self.whiteboard addConstraint:whiteWithCons];
    self.whiteWithCons = whiteWithCons;
    
    [self.grayView addSubview:self.audioView];
    NSLayoutConstraint *audioLeft = [NSLayoutConstraint constraintWithItem:self.audioView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.whiteboard attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *audioTop = [NSLayoutConstraint constraintWithItem:self.audioView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *audioBottom = [NSLayoutConstraint constraintWithItem:self.audioView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *audioWidth = [NSLayoutConstraint constraintWithItem:self.audioView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:20];
    [self.grayView addConstraints:@[audioLeft,audioTop,audioBottom,audioWidth]];
    
    [self.grayView addSubview:self.cameraView];
    NSLayoutConstraint *cameraLeft = [NSLayoutConstraint constraintWithItem:self.cameraView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.audioView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *cameraTop = [NSLayoutConstraint constraintWithItem:self.cameraView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *cameraBottom = [NSLayoutConstraint constraintWithItem:self.cameraView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *cameraRight = [NSLayoutConstraint constraintWithItem:self.cameraView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.grayView attribute:NSLayoutAttributeRight multiplier:1.0 constant:-5];
    NSLayoutConstraint *cameraWidth = [NSLayoutConstraint constraintWithItem:self.cameraView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:20];
    [self.grayView addConstraints:@[cameraLeft,cameraTop,cameraBottom,cameraRight]];
    [self.cameraView addConstraint:cameraWidth];
}

- (UIView *)videoView {
    if (!_videoView) {
        _videoView = [[UIView alloc] init];
        _videoView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _videoView;
}
- (UIImageView *)avatarView {
    if (!_avatarView) {
        _avatarView = [[UIImageView alloc] initWithImage:[UIImage ne_imageNamed:@"room_avatar"]];
        _avatarView.contentMode = UIViewContentModeCenter;
        _avatarView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _avatarView;
}

- (UIView *)grayView {
    if (!_grayView) {
        _grayView = [[UIView alloc] init];
        _grayView.translatesAutoresizingMaskIntoConstraints = NO;
        _grayView.backgroundColor = [UIColor colorWithWhite:0 alpha:.5];
    }
    return _grayView;
}

- (UILabel *)namelabel {
    if (!_namelabel) {
        _namelabel = [[UILabel alloc] init];
        _namelabel.translatesAutoresizingMaskIntoConstraints = NO;
        _namelabel.font = [UIFont systemFontOfSize:11];
        _namelabel.textColor = [UIColor whiteColor];
    }
    return _namelabel;
}

- (UIImageView *)audioView {
    if (!_audioView) {
        _audioView = [[UIImageView alloc] initWithImage: [UIImage ne_imageNamed:@"room_audio"]];
        _audioView.translatesAutoresizingMaskIntoConstraints = NO;
        _audioView.contentMode = UIViewContentModeCenter;
    }
    return _audioView;
}

- (UIImageView *)cameraView {
    if (!_cameraView) {
        _cameraView = [[UIImageView alloc] initWithImage: [UIImage ne_imageNamed:@"room_video"]];
        _cameraView.contentMode = UIViewContentModeCenter;
        _cameraView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _cameraView;
}
- (UIImageView *)whiteboard {
    if (!_whiteboard) {
        _whiteboard = [[UIImageView alloc] initWithImage: [UIImage ne_imageNamed:@"member_whiteboard"]];
        _whiteboard.contentMode = UIViewContentModeCenter;
        _whiteboard.translatesAutoresizingMaskIntoConstraints = NO;
        _whiteboard.hidden = YES;
    }
    return _whiteboard;
}

@end
