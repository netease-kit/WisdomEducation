//
//  NEEduVideoCell.h
//  EduUI
//
//  Created by Groot on 2021/5/19.
//

#import <UIKit/UIKit.h>
#import <EduLogic/EduLogic.h>
NS_ASSUME_NONNULL_BEGIN
@class NEEduVideoCell;

@protocol NEEduVideoCellDelegate <NSObject>

- (void)didTapCell:(NEEduVideoCell *)cell;

@end

@interface NEEduVideoCell : UICollectionViewCell
@property (nonatomic, strong) NEEduHttpUser *member;
@property (nonatomic, strong) UIView *videoView;
@property (nonatomic, assign) BOOL showWhiteboardIcon;
@property (nonatomic, weak) id<NEEduVideoCellDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
