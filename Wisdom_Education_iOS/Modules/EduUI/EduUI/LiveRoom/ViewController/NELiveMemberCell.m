//
//  NELiveMemberCell.m
//  EduUI
//
//  Created by 郭园园 on 2021/9/16.
//

#import "NELiveMemberCell.h"

@implementation NELiveMemberCell
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
    [NSLayoutConstraint activateConstraints:@[
        [self.topAnchor constraintEqualToAnchor:self.contentView.topAnchor constant:1.0],
        [self.leadingAnchor constraintEqualToAnchor:self.contentView.leadingAnchor constant:1.0],
        [self.trailingAnchor constraintEqualToAnchor:self.contentView.trailingAnchor constant:1.0],
        [self.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor constant:1.0]
    ]];
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
- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
