//
//  NEEduMenuItem.m
//  EduUI
//
//  Created by Groot on 2021/5/21.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduMenuItem.h"
@interface NEEduMenuItem ()
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIImageView *imageView;

@property (nonatomic, strong) UIImage *image;
@property (nonatomic, strong) UIImage *selectImage;
@property (nonatomic, strong) UIColor *color;
@property (nonatomic, strong) UIColor *selectColor;


@end

@implementation NEEduMenuItem
- (instancetype)initWithTitle:(NSString *)title image:(UIImage *)image
{
    self = [super init];
    if (self) {
        self.title = title;
        self.image = image;
        self.selectTitle = title;
        self.color = [UIColor whiteColor];
        self.selectColor = [UIColor redColor];
        self.titleLabel.text = title;
        [self addTapGesture];
        [self setupSubviews];
        self.isSelected = NO;
        self.badgeLabel.hidden = YES;
    }
    return self;
}

- (void)setupSubviews {
    [self addSubview:self.imageView];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.imageView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.imageView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:self.imageView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    [self addConstraints:@[top,left,right]];
    
    
    [self addSubview:self.titleLabel];
    NSLayoutConstraint *titleTop = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.imageView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *titleLeft = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *titleRight = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *titleHeight = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:26];
    NSLayoutConstraint *titleBottom = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];

    [self addConstraints:@[titleTop,titleLeft,titleRight,titleBottom]];
    [self.titleLabel addConstraint:titleHeight];
    
    [self addSubview:self.badgeLabel];
    NSLayoutConstraint *badgeCenterX = [NSLayoutConstraint constraintWithItem:self.badgeLabel attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self.imageView attribute:NSLayoutAttributeRight multiplier:1.0 constant:-20];
    NSLayoutConstraint *badgeCenterY = [NSLayoutConstraint constraintWithItem:self.badgeLabel attribute:NSLayoutAttributeCenterY relatedBy:NSLayoutRelationEqual toItem:self.imageView attribute:NSLayoutAttributeTop multiplier:1.0 constant:8];
    NSLayoutConstraint *badgeWidth = [NSLayoutConstraint constraintWithItem:self.badgeLabel attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:14];
    NSLayoutConstraint *badgeHeight = [NSLayoutConstraint constraintWithItem:self.badgeLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:14];
    [self addConstraints:@[badgeCenterX,badgeCenterY]];
    [self.badgeLabel addConstraints:@[badgeWidth,badgeHeight]];

}
- (void)setBadgeNumber:(NSInteger)badgeNumber {
    self.badgeLabel.hidden = badgeNumber == 0 ? YES : NO;
    self.badgeLabel.text = [NSString stringWithFormat:@"%ld",(long)badgeNumber];
}
- (void)setSelctedTextColor:(UIColor *)textColor {
    self.selectColor = textColor;
}
- (void)setSelctedImage:(UIImage *)image {
    self.selectImage = image;
}
//- (void)setTitle:(NSString *)title {
//    self.titleLabel.text = title;
//}
- (void)addTapGesture {
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap:)];
    [self addGestureRecognizer:tap];
}
- (void)tap:(UITapGestureRecognizer *)tap {
    if (self.delegate && [self.delegate respondsToSelector:@selector(onMenuItem:)]) {
        [self.delegate onMenuItem:self];
    }
}

- (void)setIsSelected:(BOOL)isSelected {
    _isSelected = isSelected;
    self.titleLabel.textColor = isSelected ? self.selectColor : self.color;
    self.imageView.image = isSelected ? self.selectImage : self.image;
    self.titleLabel.text = isSelected ? self.selectTitle : self.title;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.font = [UIFont systemFontOfSize:11.0];
        _titleLabel.textColor = self.color;
        _titleLabel.text = self.title;
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _titleLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _titleLabel;
}

- (UIImageView *)imageView {
    if (!_imageView) {
        _imageView = [[UIImageView alloc] init];
        _imageView.contentMode = UIViewContentModeCenter;
        _imageView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _imageView;
}
- (UILabel *)badgeLabel {
    if (!_badgeLabel) {
        _badgeLabel = [[UILabel alloc] init];
        _badgeLabel.layer.cornerRadius = 7;
        _badgeLabel.clipsToBounds = YES;
        _badgeLabel.font = [UIFont systemFontOfSize:8];
        _badgeLabel.textColor = [UIColor whiteColor];
        _badgeLabel.backgroundColor = [UIColor redColor];
        _badgeLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _badgeLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _badgeLabel;
}
@end
