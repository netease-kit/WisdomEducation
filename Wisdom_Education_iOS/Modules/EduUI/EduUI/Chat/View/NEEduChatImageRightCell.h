//
//  NEEduChatImageRightCell.h
//  EduUI
//
//  Created by 郭园园 on 2021/6/30.
//


#import "NEEduChatBaseCell.h"
#import "UIImage+NE.h"
NS_ASSUME_NONNULL_BEGIN
@class NEEduChatImageRightCell;

@protocol NEEduChatImageCellDelegate <NEEduChatBaseCellDelegate>
- (void)imageCell:(NEEduChatImageRightCell *)cell retrySendMessage:(NEEduChatMessage *)message;

@end

@interface NEEduChatImageRightCell : NEEduChatBaseCell
@property (nonatomic, strong) UIImageView *contentImageView;
@property (nonatomic, strong) UIButton *sendResultView;
@property (nonatomic, strong) UIActivityIndicatorView *activityView;
@end

NS_ASSUME_NONNULL_END
