//
//  EduSelectView.h
//  EduUI
//
//  Created by Groot on 2021/5/13.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class EduSelectView;

@protocol EduSelectViewDelegate <NSObject>

- (void)selectionView:(EduSelectView *)selectionView didSelected:(BOOL)selected;

@end

@interface EduSelectView : UIView
@property (nonatomic, weak) id<EduSelectViewDelegate> delegate;
@property (nonatomic, copy) NSString *title;
- (instancetype)initWithTitle:(NSString *)title;
@end

NS_ASSUME_NONNULL_END
