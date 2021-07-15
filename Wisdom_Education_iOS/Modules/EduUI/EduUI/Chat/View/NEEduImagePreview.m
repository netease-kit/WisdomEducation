//
//  NEEduImagePreview.m
//  EduUI
//
//  Created by 郭园园 on 2021/7/2.
//

#import "NEEduImagePreview.h"
#import <SDWebImage/SDWebImage.h>
#import "UIImage+NE.h"

@interface NEEduImagePreview ()
@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) NSLayoutConstraint *imageViewW;
@property (nonatomic, strong) NSLayoutConstraint *imageViewH;
@end

@implementation NEEduImagePreview

- (instancetype)initWithImageUrl:(NSString *)url
{
    self = [super init];
    if (self) {
        [self setupSubviews];
        [self loadImageWithImageUrl:url];
    }
    return self;
}
- (void)setupSubviews {
    self.backgroundColor = [UIColor colorWithWhite:0 alpha:0.5];
    self.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:self.imageView];
    self.imageViewW = [self.imageView.widthAnchor constraintEqualToConstant:0];
    self.imageViewH = [self.imageView.heightAnchor constraintEqualToConstant:0];
    [NSLayoutConstraint activateConstraints:@[
        [self.imageView.centerYAnchor constraintEqualToAnchor:self.centerYAnchor],
        [self.imageView.centerXAnchor constraintEqualToAnchor:self.centerXAnchor],
        self.imageViewW,
        self.imageViewH
    ]];
}
- (void)loadImageWithImageUrl:(NSString *)imageUrl {
    if ([[NSFileManager defaultManager] fileExistsAtPath:imageUrl]) {
        UIImage *image = [UIImage imageWithContentsOfFile:imageUrl];
        self.imageView.image = image;
        CGSize size = [image ne_showSizeWithMaxWidth:[UIScreen mainScreen].bounds.size.width maxHeight:[UIScreen mainScreen].bounds.size.height];
        self.imageViewH.constant = size.height;
        self.imageViewW.constant = size.width;
    }else {
        [self.imageView sd_setImageWithURL:[NSURL URLWithString:imageUrl] completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
            CGSize size = [image ne_showSizeWithMaxWidth:[UIScreen mainScreen].bounds.size.width maxHeight:[UIScreen mainScreen].bounds.size.height];
            self.imageViewH.constant = size.height;
            self.imageViewW.constant = size.width;
        }];
    }
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    
    [self removeFromSuperview];
}

- (UIImageView *)imageView {
    if (!_imageView) {
        _imageView = [[UIImageView alloc] init];
        _imageView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _imageView;
}

@end
