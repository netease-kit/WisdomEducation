//
//  NEEduMembersHeadView.m
//  EduUI
//
//  Created by 郭园园 on 2021/6/29.
//

#import "NEEduMembersHeadView.h"

@implementation NEEduMembersHeadView

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
        [self addSubview:self.textField];
        [NSLayoutConstraint activateConstraints:@[
            [self.textField.leftAnchor constraintEqualToAnchor:self.leftAnchor constant:0],
            [self.textField.topAnchor constraintEqualToAnchor:self.topAnchor constant:8],
            [self.textField.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:-8],
        ]];
        [self addSubview:self.searchButton];
        [NSLayoutConstraint activateConstraints:@[
            [self.searchButton.leftAnchor constraintEqualToAnchor:self.textField.rightAnchor constant:16],
            [self.searchButton.topAnchor constraintEqualToAnchor:self.topAnchor constant:8],
            [self.searchButton.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:-8],
            [self.searchButton.centerYAnchor constraintEqualToAnchor:self.centerYAnchor constant:0],
            [self.searchButton.rightAnchor constraintEqualToAnchor:self.rightAnchor constant:0],
            [self.searchButton.widthAnchor constraintEqualToConstant:60]
        ]];
    }
    return self;
}
- (UITextField *)textField {
    if (!_textField) {
        _textField = [[UITextField alloc] init];
        _textField.translatesAutoresizingMaskIntoConstraints = NO;
        _textField.textColor = [UIColor whiteColor];
        NSAttributedString *string = [[NSAttributedString alloc] initWithString:@"请输入关键词搜索" attributes:@{NSForegroundColorAttributeName:[UIColor colorWithRed:74/255.0 green:86/255.0 blue:101/255.0 alpha:1.0]}];
        _textField.attributedPlaceholder = string;
        _textField.delegate = self;
        _textField.returnKeyType = UIReturnKeySearch;
        _textField.translatesAutoresizingMaskIntoConstraints = NO;
        _textField.layer.cornerRadius = 2;
        _textField.layer.borderWidth = 1.0;
        _textField.layer.borderColor = [UIColor colorWithRed:79/255.0 green:90/255.0 blue:104/255.0 alpha:1.0].CGColor;
        _textField.clipsToBounds = YES;
        _textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    }
    return _textField;
}
- (UIButton *)searchButton {
    if (!_searchButton) {
        _searchButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _searchButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_searchButton setTitle:@"搜索" forState:UIControlStateNormal];
        [_searchButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _searchButton.layer.cornerRadius = 2;
        _searchButton.clipsToBounds = YES;
        _searchButton.backgroundColor = [UIColor colorWithRed:72/255.0 green:117/255.0 blue:251/255.0 alpha:1.0];
    }
    return _searchButton;
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
