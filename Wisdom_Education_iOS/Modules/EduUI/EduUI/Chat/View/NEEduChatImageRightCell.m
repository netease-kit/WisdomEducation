//
//  NEEduChatImageRightCell.m
//  EduUI
//
//  Created by 郭园园 on 2021/6/30.
//

#import "NEEduChatImageRightCell.h"
#import "ImageMagnification.h"

@implementation NEEduChatImageRightCell

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
    self.nameLabel.textAlignment = NSTextAlignmentRight;
    [self.contentView addSubview:self.contentImageView];
    [NSLayoutConstraint activateConstraints:@[
        [self.contentImageView.topAnchor constraintEqualToAnchor:self.nameLabel.bottomAnchor constant:10],
        [self.contentImageView.rightAnchor constraintEqualToAnchor:self.nameLabel.rightAnchor constant:0],
        [self.contentImageView.widthAnchor constraintLessThanOrEqualToConstant:176],
        [self.contentImageView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor constant:-10],
    ]];
    
    [self.contentView addSubview:self.sendResultView];
    [NSLayoutConstraint activateConstraints:@[
        [self.sendResultView.rightAnchor constraintEqualToAnchor:self.contentImageView.leftAnchor constant:-5],
        [self.sendResultView.centerYAnchor constraintEqualToAnchor:self.contentImageView.centerYAnchor constant:0],
        [self.sendResultView.widthAnchor constraintEqualToConstant:25],
        [self.sendResultView.heightAnchor constraintEqualToConstant:25],
    ]];
    
    [self.contentView addSubview:self.activityView];
    [NSLayoutConstraint activateConstraints:@[
        [self.activityView.topAnchor constraintEqualToAnchor:self.sendResultView.topAnchor constant:0],
        [self.activityView.rightAnchor constraintEqualToAnchor:self.sendResultView.rightAnchor constant:0],
        [self.activityView.bottomAnchor constraintEqualToAnchor:self.sendResultView.bottomAnchor constant:0],
        [self.activityView.leftAnchor constraintEqualToAnchor:self.sendResultView.leftAnchor constant:0]
    ]];
}
- (void)updateUIWithMessage:(NEEduChatMessage *)message {
    [super updateUIWithMessage:message];
    self.contentImageView.image = message.thumbImage;
    self.activityView.hidden = message.sendState != NEEduChatMessageSendStateNone;
    self.sendResultView.hidden = message.sendState != NEEduChatMessageSendStateFailure;
    if (message.sendState == NEEduChatMessageSendStateNone) {
        [self.activityView startAnimating];
    }else {
        [self.activityView stopAnimating];
    }
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
    if (self.delegate && [self.delegate respondsToSelector:@selector(chatView:didTapMessage:)]) {
        [self.delegate chatView:self.contentView didTapMessage:self.message];
    }
}
- (void)retryEvent:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(imageCell:retrySendMessage:)]) {
        [self.delegate imageCell:self retrySendMessage:self.message];
    }
}
- (UIButton *)sendResultView {
    if (!_sendResultView) {
        _sendResultView = [UIButton buttonWithType:UIButtonTypeCustom];
        [_sendResultView setImage:[UIImage ne_imageNamed:@"chat_send_failure"] forState:UIControlStateNormal];
        [_sendResultView addTarget:self action:@selector(retryEvent:) forControlEvents:UIControlEventTouchUpInside];
        _sendResultView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _sendResultView;
}
- (UIActivityIndicatorView *)activityView {
    if (!_activityView) {
        _activityView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        _activityView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _activityView;
}
@end
