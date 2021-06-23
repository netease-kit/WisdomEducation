//
//  NEEduRoomViewMaskView.h
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "NEEduRoomNavigationView.h"
#import "NEEduMenuItem.h"
NS_ASSUME_NONNULL_BEGIN

@protocol NEEduRoomViewMaskViewDelegate <NSObject>
@optional
- (void)backEvent;
- (void)onSectionStateChangeAtIndex:(NSInteger)index item:(NEEduMenuItem *)item;
- (void)rightButton:(UIButton *)button selected:(BOOL)selected;
@end

@interface NEEduRoomViewMaskView : UIView
@property (nonatomic, weak) id<NEEduRoomViewMaskViewDelegate> delegate;
@property (nonatomic, strong) NEEduRoomNavigationView *navView;
@property (nonatomic, strong) UIStackView *stackView;
@property (nonatomic, strong) UIButton *startLesson;

- (instancetype)initWithMenuItems:(NSArray<NEEduMenuItem *> *)menuItems;
- (void)addItem:(NEEduMenuItem *)item;
- (void)removeItemType:(NEEduMenuItemType)itemType;
- (void)insertItem:(NEEduMenuItem *)item atIndex:(NSInteger)index;
- (void)selectButton:(BOOL)seleted;
@end


NS_ASSUME_NONNULL_END
