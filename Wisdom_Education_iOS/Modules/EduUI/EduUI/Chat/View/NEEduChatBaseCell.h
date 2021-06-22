//
//  NEEduChatBaseCell.h
//  EduUI
//
//  Created by Groot on 2021/5/25.
//

#import <UIKit/UIKit.h>
#import "NEEduChatMessage.h"

NS_ASSUME_NONNULL_BEGIN
@class NEEduChatBaseCell;

@protocol NEEduChatBaseCellDelegate <NSObject>
- (void)chatCell:(NEEduChatBaseCell *)cell didLongPressMessage:(NEEduChatMessage *)message;

@end
@interface NEEduChatBaseCell : UITableViewCell
@property (nonatomic, strong) UILabel *nameLabel;
@property (nonatomic, strong) UIView *bgView;
@property (nonatomic, strong) UILabel *contentLabel;
@property (nonatomic, strong) NEEduChatMessage *model;
@property (nonatomic, weak) id<NEEduChatBaseCellDelegate> delegate;
- (void)setupSubviews;
@end

NS_ASSUME_NONNULL_END
