//
//  NEEduChatTimeCell.m
//  EduUI
//
//  Created by Groot on 2021/6/10.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduChatTimeCell.h"

@implementation NEEduChatTimeCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self setupSubviews];
    }
    return self;
}
- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}
- (void)setupSubviews {
    self.backgroundColor = [UIColor clearColor];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    [self.contentView addSubview:self.timeLabel];
    NSLayoutConstraint *top = [self.timeLabel.topAnchor constraintEqualToAnchor:self.contentView.topAnchor constant:10];
    NSLayoutConstraint *left = [self.timeLabel.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor constant:10];
    NSLayoutConstraint *right = [self.timeLabel.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor constant:-10];
    NSLayoutConstraint *bottom = [self.timeLabel.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor constant:-10];
    [self.contentView addConstraints:@[top,left,right,bottom]];
    
}
- (void)setModel:(NEEduChatMessage *)model {
    self.timeLabel.text = model.content;
}
- (UILabel *)timeLabel {
    if (!_timeLabel) {
        _timeLabel = [[UILabel alloc] init];
        _timeLabel.font = [UIFont systemFontOfSize:12];
        _timeLabel.textColor = [UIColor colorWithRed:74/255.0 green:86/255.0 blue:101/255.0 alpha:1.0];
        _timeLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _timeLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _timeLabel;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
