//
//  NEEduMemberCell.m
//  EduUI
//
//  Created by Groot on 2021/5/27.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduMemberCell.h"
#import "UIImage+NE.h"
@interface NEEduMemberCell()
@property (nonatomic, strong) NSLayoutConstraint *wbWidth;
@property (nonatomic, strong) NSLayoutConstraint *ssWidth;

@end

@implementation NEEduMemberCell
- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style
                reuseIdentifier:reuseIdentifier];
    if (self) {
        [self setupSubviews];
    }
    return self;
}

- (void)setupSubviews {
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    [self.contentView addSubview:self.nameLabel];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self addConstraints:@[top,left,bottom]];
    
    [self.contentView addSubview:self.whiteBoardButton];
    NSLayoutConstraint *wbTop = [NSLayoutConstraint constraintWithItem:self.whiteBoardButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:17];
    NSLayoutConstraint *wbLeft = [NSLayoutConstraint constraintWithItem:self.whiteBoardButton attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.nameLabel attribute:NSLayoutAttributeRight multiplier:1.0 constant:8];
    self.wbWidth = [NSLayoutConstraint constraintWithItem:self.whiteBoardButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    NSLayoutConstraint *wbBottom = [NSLayoutConstraint constraintWithItem:self.whiteBoardButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-8];
    [self addConstraints:@[wbTop,wbLeft,wbBottom]];
    [self.whiteBoardButton addConstraint:self.wbWidth];
    
    [self.contentView addSubview:self.screenShareButton];
    NSLayoutConstraint *ssTop = [NSLayoutConstraint constraintWithItem:self.screenShareButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.whiteBoardButton attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *ssLeft = [NSLayoutConstraint constraintWithItem:self.screenShareButton attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.whiteBoardButton attribute:NSLayoutAttributeRight multiplier:1.0 constant:10];
    self.ssWidth = [NSLayoutConstraint constraintWithItem:self.screenShareButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    NSLayoutConstraint *ssBottom = [NSLayoutConstraint constraintWithItem:self.screenShareButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.whiteBoardButton attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self addConstraints:@[ssTop,ssLeft,ssBottom]];
    [self.screenShareButton addConstraint:self.ssWidth];
    
    [self.contentView addSubview:self.moreButton];
    NSLayoutConstraint *moreTop = [NSLayoutConstraint constraintWithItem:self.moreButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *moreRight = [NSLayoutConstraint constraintWithItem:self.moreButton attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *moreWidth = [NSLayoutConstraint constraintWithItem:self.moreButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:42];
    NSLayoutConstraint *moreBottom = [NSLayoutConstraint constraintWithItem:self.moreButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self addConstraints:@[moreTop,moreRight,moreBottom]];
    [self.moreButton addConstraint:moreWidth];
    
    [self.contentView addSubview:self.videoButton];
    NSLayoutConstraint *videoTop = [NSLayoutConstraint constraintWithItem:self.videoButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *videoRight = [NSLayoutConstraint constraintWithItem:self.videoButton attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.moreButton attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *videoWidth = [NSLayoutConstraint constraintWithItem:self.videoButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:42];
    NSLayoutConstraint *videoBottom = [NSLayoutConstraint constraintWithItem:self.videoButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self addConstraints:@[videoTop,videoRight,videoBottom]];
    [self.videoButton addConstraint:videoWidth];
    
    [self.contentView addSubview:self.audioButton];
    NSLayoutConstraint *audioTop = [NSLayoutConstraint constraintWithItem:self.audioButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *audioRight = [NSLayoutConstraint constraintWithItem:self.audioButton attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.videoButton attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *audioWidth = [NSLayoutConstraint constraintWithItem:self.audioButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:42];
    NSLayoutConstraint *audioBottom = [NSLayoutConstraint constraintWithItem:self.audioButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *audioLeft = [NSLayoutConstraint constraintWithItem:self.audioButton attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.screenShareButton attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];

    [self addConstraints:@[audioTop,audioRight,audioBottom,audioLeft]];
    [self.audioButton addConstraint:audioWidth];
}
- (void)audioButtonClick:(UIButton *)button {
    if (!self.member.audioEnable) {
        return;
    }
    button.selected = !button.selected;
    self.member.hasAudio = !button.selected;
    if (self.delegate && [self.delegate respondsToSelector:@selector(didSeletedAudio:member:)]) {
        [self.delegate didSeletedAudio:button.selected member:self.member];
    }
}
- (void)videoButtonClick:(UIButton *)button {
    if (!self.member.videoEnable) {
        return;
    }
    button.selected = !button.selected;
    self.member.hasVideo = !button.selected;
    if (self.delegate && [self.delegate respondsToSelector:@selector(didSeletedVideo:member:)]) {
        [self.delegate didSeletedVideo:button.selected member:self.member];
    }
}
- (void)moreButtonClick:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(cell:didSeletedMore:member:)]) {
        [self.delegate cell:self didSeletedMore:button.selected member:self.member];
    }
}
- (void)setMember:(NEEduMember *)member {
    _member = member;
    self.nameLabel.text = member.name;
    self.whiteBoardButton.hidden = !member.whiteboardEnable;
    self.screenShareButton.hidden = !member.shareScreenEnable;
    self.wbWidth.constant = member.whiteboardEnable?40:0;
    self.ssWidth.constant = member.shareScreenEnable?40:0;
    
    self.audioButton.selected = !member.hasAudio;
    self.videoButton.selected = !member.hasVideo;
    self.moreButton.hidden = !member.showMoreButton;
    //大班课
    if (member.isBigClass) {
        if (member.isInAllList) {
            self.audioButton.hidden = YES;
            self.videoButton.hidden = YES;
            self.moreButton.hidden = YES;
            self.whiteBoardButton.hidden = YES;
            self.screenShareButton.hidden = YES;
        }else {
            self.audioButton.hidden = NO;
            self.videoButton.hidden = NO;
        }
    }else {
        self.audioButton.hidden = NO;
        self.videoButton.hidden = NO;
    }
}

- (UILabel *)nameLabel {
    if (!_nameLabel) {
        _nameLabel = [[UILabel alloc] init];
        _nameLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _nameLabel.textColor = [UIColor whiteColor];
        _nameLabel.font = [UIFont systemFontOfSize:16];
    }
    return _nameLabel;
}
- (UIButton *)whiteBoardButton {
    if (!_whiteBoardButton) {
        _whiteBoardButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _whiteBoardButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_whiteBoardButton setImage:[UIImage ne_imageNamed:@"member_whiteboard"] forState:UIControlStateNormal];
        _whiteBoardButton.backgroundColor = [UIColor colorWithRed:35/255.0 green:44/255.0 blue:55/255.0 alpha:1.0];
        _whiteBoardButton.layer.cornerRadius = 2;
        _whiteBoardButton.clipsToBounds = YES;
        _whiteBoardButton.titleLabel.font = [UIFont systemFontOfSize:14];
    }
    return _whiteBoardButton;
}
- (UIButton *)screenShareButton {
    if (!_screenShareButton) {
        _screenShareButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _screenShareButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_screenShareButton setImage:[UIImage ne_imageNamed:@"member_screen_share"] forState:UIControlStateNormal];
        _screenShareButton.backgroundColor = [UIColor colorWithRed:35/255.0 green:44/255.0 blue:55/255.0 alpha:1.0];
        _screenShareButton.layer.cornerRadius = 2;
        _screenShareButton.clipsToBounds = YES;
        _screenShareButton.titleLabel.font = [UIFont systemFontOfSize:14];
    }
    return _screenShareButton;
}

- (UIButton *)audioButton {
    if (!_audioButton) {
        _audioButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _audioButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_audioButton setImage:[UIImage ne_imageNamed:@"member_audio"] forState:UIControlStateNormal];
        [_audioButton setImage:[UIImage ne_imageNamed:@"room_audio_off"] forState:UIControlStateSelected];
        [_audioButton addTarget:self action:@selector(audioButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _audioButton;
}
- (UIButton *)videoButton {
    if (!_videoButton) {
        _videoButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _videoButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_videoButton setImage:[UIImage ne_imageNamed:@"member_video"] forState:UIControlStateNormal];
        [_videoButton setImage:[UIImage ne_imageNamed:@"room_video_off"] forState:UIControlStateSelected];
        [_videoButton addTarget:self action:@selector(videoButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _videoButton;
}
- (UIButton *)moreButton {
    if (!_moreButton) {
        _moreButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _moreButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_moreButton setImage:[UIImage ne_imageNamed:@"member_more"] forState:UIControlStateNormal];
        [_moreButton addTarget:self action:@selector(moreButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _moreButton;
}
@end
