//
//  NEEduHandsupApplyCell.m
//  EduUI
//
//  Created by Groot on 2021/6/3.
//

#import "NEEduHandsupApplyCell.h"

@implementation NEEduHandsupApplyCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self setupSubviews];
    }
    return self;
}
- (void)setupSubviews {
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.contentView.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    [self.contentView addSubview:self.nameLabel];
    NSLayoutConstraint *nameLeft = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *nameTop = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *nameBottom = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *nameWidth = [NSLayoutConstraint constraintWithItem:self.nameLabel attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:80];
    [self.contentView addConstraints:@[nameLeft,nameTop,nameBottom]];
    [self.nameLabel addConstraint:nameWidth];
    
    [self.contentView addSubview:self.disagreeButton];
    NSLayoutConstraint *disagreeRight = [NSLayoutConstraint constraintWithItem:self.disagreeButton attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *disagreeTop = [NSLayoutConstraint constraintWithItem:self.disagreeButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1.0 constant:8];
    NSLayoutConstraint *disagreeBottom = [NSLayoutConstraint constraintWithItem:self.disagreeButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-8];
    NSLayoutConstraint *disagreeWidth = [NSLayoutConstraint constraintWithItem:self.disagreeButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:76];
    [self.contentView addConstraints:@[disagreeRight,disagreeTop,disagreeBottom]];
    [self.disagreeButton addConstraint:disagreeWidth];
    
    [self.contentView addSubview:self.agreeButton];
    NSLayoutConstraint *agreeRight = [NSLayoutConstraint constraintWithItem:self.agreeButton attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.disagreeButton attribute:NSLayoutAttributeLeft multiplier:1.0 constant:- 12];
    NSLayoutConstraint *agreeTop = [NSLayoutConstraint constraintWithItem:self.agreeButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeTop multiplier:1.0 constant:8];
    NSLayoutConstraint *agreeBottom = [NSLayoutConstraint constraintWithItem:self.agreeButton attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-8];
    NSLayoutConstraint *agreeWidth = [NSLayoutConstraint constraintWithItem:self.agreeButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:76];
    [self.contentView addConstraints:@[agreeRight,agreeTop,agreeBottom]];
    [self.agreeButton addConstraint:agreeWidth];
    
    [self.contentView addSubview:self.line];
    NSLayoutConstraint *lineRight = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *lineLeft = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *lineHeight = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:1];
    NSLayoutConstraint *lineBottom = [NSLayoutConstraint constraintWithItem:self.line attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.contentView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-1];
    [self.contentView addConstraints:@[lineRight,lineLeft,lineBottom]];
    [self.line addConstraint:lineHeight];

}
- (void)agreenButtonClick:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(agreeHansupApplyWithMember:)]) {
        [self.delegate agreeHansupApplyWithMember:self.member];
    }
    
}
- (void)disagreenButtonClick:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(disagreeHansupApplyWithMember:)]) {
        [self.delegate disagreeHansupApplyWithMember:self.member];
    }
}
- (void)setMember:(NEEduHttpUser *)member {
    _member = member;
    self.nameLabel.text = member.userName;
}
- (UILabel *)nameLabel {
    if (!_nameLabel) {
        _nameLabel = [[UILabel alloc] init];
        _nameLabel.textColor = [UIColor whiteColor];
        _nameLabel.font = [UIFont systemFontOfSize:16];
        _nameLabel.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _nameLabel;
}
- (UIButton *)agreeButton {
    if (!_agreeButton) {
        _agreeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _agreeButton.backgroundColor = [UIColor colorWithRed:35/255.0 green:44/255.0 blue:55/255.0 alpha:1.0];
        _agreeButton.titleLabel.font = [UIFont systemFontOfSize:14];
        [_agreeButton setTitleColor:[UIColor colorWithRed:180/255.0 green:191/255.0 blue:208/255.0 alpha:1.0] forState:UIControlStateNormal];
        [_agreeButton addTarget:self action:@selector(agreenButtonClick:) forControlEvents:UIControlEventTouchUpInside];
        _agreeButton.layer.cornerRadius = 2.0;
        _agreeButton.clipsToBounds = YES;
        [_agreeButton setTitle:@"同意" forState:UIControlStateNormal];
        _agreeButton.translatesAutoresizingMaskIntoConstraints = NO;

    }
    return _agreeButton;
}
- (UIButton *)disagreeButton {
    if (!_disagreeButton) {
        _disagreeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _disagreeButton.backgroundColor = [UIColor colorWithRed:35/255.0 green:44/255.0 blue:55/255.0 alpha:1.0];
        _disagreeButton.titleLabel.font = [UIFont systemFontOfSize:14];
        [_disagreeButton setTitleColor:[UIColor colorWithRed:180/255.0 green:191/255.0 blue:208/255.0 alpha:1.0] forState:UIControlStateNormal];
        [_disagreeButton addTarget:self action:@selector(disagreenButtonClick:) forControlEvents:UIControlEventTouchUpInside];
        [_disagreeButton setTitle:@"拒绝" forState:UIControlStateNormal];
        _disagreeButton.layer.cornerRadius = 2.0;
        _disagreeButton.clipsToBounds = YES;
        _disagreeButton.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _disagreeButton;
}
- (UIView *)line {
    if (!_line) {
        _line = [[UIView alloc] init];
        _line.backgroundColor = [UIColor colorWithRed:106/255.0 green:118/255.0 blue:135/255.0 alpha:1.0];
        _line.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _line;
}
@end
