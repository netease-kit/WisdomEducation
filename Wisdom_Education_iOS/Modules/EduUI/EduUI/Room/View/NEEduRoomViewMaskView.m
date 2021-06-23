//
//  NEEduRoomViewMaskView.m
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduRoomViewMaskView.h"
@interface NEEduRoomViewMaskView ()<NEEduMenuItemDelegte>
@property (nonatomic, strong) NSLayoutConstraint *top;
@property (nonatomic, strong) NSLayoutConstraint *bottomViewBottom;
@property (nonatomic, strong) NSLayoutConstraint *bottomWidth;
@property (nonatomic, assign) CGFloat itemWidth;
@property (nonatomic, assign) BOOL hidenNav;
@property (nonatomic, copy) NSArray<NEEduMenuItem *> *menuItems;
@property (nonatomic, assign) NSInteger selectedIndex;
@property (nonatomic, strong) UIView *bottomView;

@end

@implementation NEEduRoomViewMaskView
- (instancetype)initWithMenuItems:(NSArray<NEEduMenuItem *> *)menuItems
{
    self = [super init];
    if (self) {
        self.translatesAutoresizingMaskIntoConstraints = NO;
//        self.backgroundColor = [UIColor blackColor];
        self.itemWidth = 60;
        self.selectedIndex = 0;
        self.menuItems = menuItems;
//        [self addTapGesture];
        [self setupSubviews];
        [self addDelegateForItems:self.menuItems];
    }
    return self;
}
- (void)setupSubviews {
    [self addSubview:self.navView];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.navView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    self.top = [NSLayoutConstraint constraintWithItem:self.navView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:self.navView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:self.navView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    
    [self addConstraints:@[left,self.top,right]];
    [self.navView addConstraint:height];
    
    [self addSubview:self.bottomView];
    NSLayoutConstraint *bottomLeft = [NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *bottomRight = [NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *bottomHeight = [NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:60];
    self.bottomViewBottom = [NSLayoutConstraint constraintWithItem:self.bottomView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self addConstraints:@[bottomLeft,bottomRight,self.bottomViewBottom]];
    [self.bottomView addConstraint:bottomHeight];
    
    [self.bottomView addSubview:self.stackView];
    NSLayoutConstraint *stackTop = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.bottomView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *bottomCenterX = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self.bottomView attribute:NSLayoutAttributeCenterX multiplier:1.0 constant:0];
    self.bottomWidth = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:self.itemWidth * self.menuItems.count];
    NSLayoutConstraint *stackBottom = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.bottomView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self addConstraints:@[stackTop,bottomCenterX,stackBottom]];
    [self.stackView addConstraint:self.bottomWidth];
    
    [self.bottomView addSubview:self.startLesson];
    NSLayoutConstraint *startLessonTop = [NSLayoutConstraint constraintWithItem:self.startLesson attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.bottomView attribute:NSLayoutAttributeTop multiplier:1.0 constant:16];
    NSLayoutConstraint *startLessonBottom = [NSLayoutConstraint constraintWithItem:self.startLesson attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.bottomView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-16];
    NSLayoutConstraint *startLessonRight = [NSLayoutConstraint constraintWithItem:self.startLesson attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.bottomView attribute:NSLayoutAttributeRight multiplier:1.0 constant:-46];
    NSLayoutConstraint *startLessonWidth = [NSLayoutConstraint constraintWithItem:self.startLesson attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:76];
    [self.bottomView addConstraints:@[startLessonTop,startLessonBottom,startLessonRight]];
    [self.startLesson addConstraint:startLessonWidth];
}

- (void)addTapGesture {
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap:)];
    [self addGestureRecognizer:tap];
}
- (void)addDelegateForItems:(NSArray<NEEduMenuItem *> *)items {
    for (NEEduMenuItem *item in items) {
        item.delegate = self;
    }
}
- (void)tap:(UITapGestureRecognizer *)tap {
    self.hidenNav = !self.hidenNav;
    if (self.hidenNav) {
        [self removeConstraint:self.top];
        self.top = [NSLayoutConstraint constraintWithItem:self.navView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:-40];
        [self addConstraint:self.top];
        
        [self removeConstraint:self.bottomViewBottom];
        self.bottomViewBottom = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
        [self addConstraint:self.bottomViewBottom];
        
    }else {
        [self removeConstraint:self.top];
        self.top = [NSLayoutConstraint constraintWithItem:self.navView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
        [self addConstraint:self.top];
        
        [self removeConstraint:self.bottomViewBottom];
        self.bottomViewBottom = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
        [self addConstraint:self.bottomViewBottom];
    }
}
- (void)addItem:(NEEduMenuItem *)item {
    for (NEEduMenuItem *menuItem in self.menuItems) {
        if (menuItem.type == item.type) {
            return;
        }
    }
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.menuItems];
    [array addObject:item];
    item.delegate = self;
    [self.stackView addArrangedSubview:item];
    self.menuItems = array;
    self.bottomWidth.constant = self.menuItems.count * self.itemWidth;
}
- (void)removeItemType:(NEEduMenuItemType)itemType {
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.menuItems];
    NEEduMenuItem *removeItem = nil;
    for (NEEduMenuItem *item in array) {
        if (item.type == itemType) {
            removeItem = item;
        }
    }
    [array removeObject:removeItem];
    [self.stackView removeArrangedSubview:removeItem];
    [removeItem removeFromSuperview];
    self.menuItems = array;
    self.bottomWidth.constant = self.menuItems.count * self.itemWidth;
}
- (void)insertItem:(NEEduMenuItem *)item atIndex:(NSInteger)index {
    for (NEEduMenuItem *menuItem in self.menuItems) {
        if (menuItem.type == item.type) {
            return;
        }
    }
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.menuItems];
    if (array.count > index) {
        [array insertObject:item atIndex:index];
        item.delegate = self;
        [self.stackView insertArrangedSubview:item atIndex:index];
    }else {
        [array addObject:item];
        item.delegate = self;
        [self.stackView addArrangedSubview:item];
    }
    self.menuItems = array;
    self.bottomWidth.constant = self.menuItems.count * self.itemWidth;
}

//- (void)updateItems:(NSArray <NEEduMenuItem *> *)items {
//    self.menuItems = items;
//    self.stackWidth.constant = items.count * self.itemWidth;
//}
- (void)backButtonEvent {
    if (self.delegate && [self.delegate respondsToSelector:@selector(backEvent)]) {
        [self.delegate backEvent];
    }
}
- (void)startLessonEvent:(UIButton *)button {
//    button.selected = !button.selected;
    if (self.delegate && [self.delegate respondsToSelector:@selector(rightButton:selected:)]) {
        [self.delegate rightButton:button selected:button.selected];
    }
    
}
- (void)selectButton:(BOOL)seleted {
    self.startLesson.selected = seleted;
    if (seleted) {
        self.startLesson.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
        self.startLesson.layer.borderColor = [UIColor colorWithRed:137/255.0 green:144/255.0 blue:156/255.0 alpha:1.0].CGColor;
    }else {
        self.startLesson.backgroundColor = [UIColor colorWithRed:55/255.0 green:114/255.0 blue:254/255.0 alpha:1.0];
        self.startLesson.layer.borderColor = [UIColor colorWithRed:55/255.0 green:114/255.0 blue:254/255.0 alpha:1.0].CGColor;
    }
}
#pragma mark - NEEduMenuItemDelegte
- (void)onMenuItem:(NEEduMenuItem *)item {
    for (int i = 0; i < self.menuItems.count; i ++) {
        NEEduMenuItem *itemInArray = self.menuItems[i];
        if ([itemInArray isEqual:item]) {
            self.selectedIndex = i;
        }
    }
    if (item.type != NEEduMenuItemTypeMembers && item.type != NEEduMenuItemTypeChat && item.type != NEEduMenuItemTypeHandsup && item.type != NEEduMenuItemTypeShareScreen) {
        item.isSelected = !item.isSelected;
    }
    if (self.delegate && [self.delegate respondsToSelector:@selector(onSectionStateChangeAtIndex:item:)]) {
        [self.delegate onSectionStateChangeAtIndex:self.selectedIndex item:item];
    }
}
#pragma mark - 手势
//- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
//    NSLog(@"event:%@",event);
//    if (self.hidenNav) {
//        return nil;
//    }
//    return self;
//}

- (NEEduRoomNavigationView *)navView {
    if (!_navView) {
        _navView = [[NEEduRoomNavigationView alloc] init];
        [_navView.backButton addTarget:self action:@selector(backButtonEvent) forControlEvents:UIControlEventTouchUpInside];
    }
    return _navView;
}
- (UIStackView *)stackView {
    if (!_stackView) {
        _stackView = [[UIStackView alloc] initWithArrangedSubviews:self.menuItems];
        _stackView.distribution = UIStackViewDistributionFillEqually;
        _stackView.alignment = UIStackViewAlignmentFill;
        _stackView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _stackView;
}
- (UIView *)bottomView {
    if (!_bottomView) {
        _bottomView = [[UIView alloc] init];
        _bottomView.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
        _bottomView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _bottomView;
}
- (UIButton *)startLesson {
    if (!_startLesson) {
        _startLesson = [UIButton buttonWithType:UIButtonTypeCustom];
        _startLesson.translatesAutoresizingMaskIntoConstraints = NO;
        _startLesson.layer.cornerRadius = 2;
        _startLesson.clipsToBounds = YES;
        _startLesson.layer.borderWidth = 1.0;
        [_startLesson setTitle:@"开始上课" forState:UIControlStateNormal];
        [_startLesson setTitle:@"结束课堂" forState:UIControlStateSelected];
        [_startLesson setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_startLesson setTitleColor:[UIColor colorWithRed:137/255.0 green:144/255.0 blue:156/255.0 alpha:1.0] forState:UIControlStateSelected];
        _startLesson.layer.borderColor = [UIColor colorWithRed:55/255.0 green:114/255.0 blue:254/255.0 alpha:1.0].CGColor;
        [_startLesson addTarget:self action:@selector(startLessonEvent:) forControlEvents:UIControlEventTouchUpInside];
        _startLesson.titleLabel.font = [UIFont systemFontOfSize:14];
    }
    return _startLesson;
}
@end
