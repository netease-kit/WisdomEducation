//
//  NEEduChatLeftImageCellTableViewCell.m
//  EduUI
//
//  Created by jinqi on 2021/6/30.
//

#import "NEEduChatLeftImageCell.h"
#import "ImageMagnification.h"

@implementation NEEduChatLeftImageCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self setupSubviews];
        [self addSinglePress];
    }
    return self;
}

- (void)setupSubviews {
    [super setupSubviews];
    self.nameLabel.textAlignment = NSTextAlignmentLeft;
    [self.contentView addSubview:self.contentImageView];
    [NSLayoutConstraint activateConstraints:@[
        [self.contentImageView.topAnchor constraintEqualToAnchor:self.nameLabel.bottomAnchor constant:10],
        [self.contentImageView.leftAnchor constraintEqualToAnchor:self.nameLabel.leftAnchor constant:0],
        [self.contentImageView.widthAnchor constraintLessThanOrEqualToConstant:176],
        [self.contentImageView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor constant:-10]
    ]];
}

- (void)updateUIWithMessage:(NEEduChatMessage *)message {
    [super updateUIWithMessage:message];
    self.contentImageView.image = message.thumbImage;
}

- (UIImageView *)contentImageView {
    if (!_contentImageView) {
        _contentImageView = [[UIImageView alloc] init];
        _contentImageView.translatesAutoresizingMaskIntoConstraints = NO;
        _contentImageView.contentMode = UIViewContentModeScaleAspectFit;
        _contentImageView.userInteractionEnabled = YES;
    }
    return _contentImageView;
}

- (void)addSinglePress {
    UITapGestureRecognizer *press = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(singlePress:)];
    [self.contentImageView addGestureRecognizer:press];
}

- (void)singlePress:(UITapGestureRecognizer *)press {
    NSLog(@"picture pressed");
    if (self.delegate && [self.delegate respondsToSelector:@selector(chatView:didTapMessage:)]) {
        [self.delegate chatView:self.contentView didTapMessage:self.message];
    }
//    [ImageMagnification scanBigImageWithImageView:self.contentImageView alpha:1.0];
}

@end
