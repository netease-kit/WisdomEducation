//
//  NEEduMenuItem.h
//  EduUI
//
//  Created by Groot on 2021/5/21.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class NEEduMenuItem;
@protocol NEEduMenuItemDelegte <NSObject>
- (void)onMenuItem:(NEEduMenuItem *)item;
@end

typedef NS_ENUM(NSInteger,NEEduMenuItemType) {
    NEEduMenuItemTypeVideo,
    NEEduMenuItemTypeAudio,
    NEEduMenuItemTypeShareScreen,
    NEEduMenuItemTypeMembers,
    NEEduMenuItemTypeChat,
    NEEduMenuItemTypeHandsup,
};

@interface NEEduMenuItem : UIView
@property (nonatomic, strong) NSString *title;
@property (nonatomic, assign) NEEduMenuItemType type;
@property (nonatomic, assign) BOOL isSelected;
@property (nonatomic, strong) NSString *selectTitle;
@property (nonatomic, weak) id delegate;
@property (nonatomic, assign) NSInteger badgeNumber;
@property (nonatomic, strong) UILabel *badgeLabel;
- (instancetype)initWithTitle:(NSString *)title image:(UIImage *)image;
- (void)setSelctedTextColor:(UIColor *)textColor;
- (void)setSelctedImage:(UIImage *)image;

@end

NS_ASSUME_NONNULL_END
