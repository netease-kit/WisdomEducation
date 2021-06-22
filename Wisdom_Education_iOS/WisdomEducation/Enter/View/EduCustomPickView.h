//
//  CustomPickView.h
//  NEInterview
//
//  Created by Groot on 2021/1/31.
//

#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface EduCustomPickView : UIView<UIPickerViewDelegate,UIPickerViewDataSource>
@property (nonatomic,strong)UIPickerView *pickerView;
@property (nonatomic,strong)NSArray <NSString *>*businesses;
@property (nonatomic,copy)void(^didSelectIndex)(NSInteger index);
@end

NS_ASSUME_NONNULL_END
