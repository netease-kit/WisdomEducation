//
//  NEEduMemberCell.h
//  EduUI
//
//  Created by Groot on 2021/5/27.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "NEEduMember.h"
NS_ASSUME_NONNULL_BEGIN
@class NEEduMemberCell;

@protocol NEEduMemberCellDelegate <NSObject>

- (void)didSeletedAudio:(BOOL)isSelected member:(NEEduMember *)member;
- (void)didSeletedVideo:(BOOL)isSelected member:(NEEduMember *)member;
- (void)cell:(NEEduMemberCell *)cell didSeletedMore:(BOOL)isSelected member:(NEEduMember *)member;

@end

@interface NEEduMemberCell : UITableViewCell
@property (nonatomic, strong) UILabel *nameLabel;
@property (nonatomic, strong) UIButton *whiteBoardButton;
@property (nonatomic, strong) UIButton *screenShareButton;
@property (nonatomic, strong) UIButton *audioButton;
@property (nonatomic, strong) UIButton *videoButton;
@property (nonatomic, strong) UIButton *moreButton;
@property (nonatomic, strong) NEEduMember *member;
@property (nonatomic, weak) id<NEEduMemberCellDelegate> delegate;
@end

NS_ASSUME_NONNULL_END
