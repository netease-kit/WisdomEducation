//
//  CustomPickView.m
//  NEInterview
//
//  Created by Groot on 2021/1/31.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "EduCustomPickView.h"
@interface EduCustomPickView ()

@property (nonatomic,assign)NSInteger index;
@end
@implementation EduCustomPickView
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupUIWithFrame:frame];
    }
    return self;
}
- (void)setupUIWithFrame:(CGRect)frame {
    UIView *bgView = [[UIView alloc] initWithFrame:CGRectMake(0, frame.size.height - 230, frame.size.width, 250)];
    bgView.backgroundColor = [UIColor whiteColor];
    bgView.layer.cornerRadius = 8;
    bgView.clipsToBounds = YES;
    [self addSubview:bgView];
    
    self.backgroundColor = [UIColor colorWithWhite:0 alpha:0.5];
    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    cancelBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [cancelBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    cancelBtn.frame = CGRectMake(0, 0, 60, 44);
    [cancelBtn addTarget:self action:@selector(cancelBtn:) forControlEvents:UIControlEventTouchUpInside];
    [bgView addSubview:cancelBtn];
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(60, 0, frame.size.width - 120, 44)];
    titleLabel.text = @"请选择";
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.font = [UIFont systemFontOfSize:17];
    [bgView addSubview:titleLabel];
    
    UIButton *okBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [okBtn setTitle:@"确认" forState:UIControlStateNormal];
    okBtn.titleLabel.font = [UIFont systemFontOfSize:17];
    [okBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    okBtn.frame = CGRectMake(frame.size.width - 60, 0, 60, 44);
    [okBtn addTarget:self action:@selector(okBtn:) forControlEvents:UIControlEventTouchUpInside];
    [bgView addSubview:okBtn];
    
    self.pickerView.frame = CGRectMake(0, 44, frame.size.width, 230 - 44);
    [bgView addSubview:self.pickerView];
}
- (void)cancelBtn:(UIButton *)button {
    [self removeFromSuperview];
}
- (void)okBtn:(UIButton *)button {
    if (self.didSelectIndex) {
        self.didSelectIndex(self.index);
    }
    [self removeFromSuperview];
}
- (UIPickerView *)pickerView {
    if (!_pickerView) {
        _pickerView = [[UIPickerView alloc] init];
        _pickerView.backgroundColor = [UIColor whiteColor];
        _pickerView.delegate = self;
        _pickerView.dataSource = self;
    }
    return _pickerView;
}
- (void)setBusinesses:(NSArray<NSString *> *)businesses {
    _businesses = businesses;
    [self.pickerView reloadAllComponents];
}

#pragma mark - UIPickerViewDataSource
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
    return 1;
}
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    return self.businesses.count;
}
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    return self.businesses[row];
}
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    self.index = row;
}
@end
