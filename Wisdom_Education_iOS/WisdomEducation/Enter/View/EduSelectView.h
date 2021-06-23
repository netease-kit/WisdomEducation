//
//  EduSelectView.h
//  EduUI
//
//  Created by Groot on 2021/5/13.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
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
