//
//  CustomPickView.h
//  NEInterview
//
//  Created by Groot on 2021/1/31.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface EduCustomPickView : UIView<UIPickerViewDelegate,UIPickerViewDataSource>
@property (nonatomic,strong)UIPickerView *pickerView;
@property (nonatomic,strong)NSArray <NSString *>*businesses;
@property (nonatomic,copy)void(^didSelectIndex)(NSInteger index);
@end

NS_ASSUME_NONNULL_END
