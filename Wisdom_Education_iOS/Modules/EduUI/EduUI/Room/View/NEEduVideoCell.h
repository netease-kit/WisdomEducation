//
//  NEEduVideoCell.h
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
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
