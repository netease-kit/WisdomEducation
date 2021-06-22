//
//  NEEduChatTimeCell.h
//  EduUI
//
//  Created by Groot on 2021/6/10.
//

#import <UIKit/UIKit.h>
#import "NEEduChatMessage.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduChatTimeCell : UITableViewCell
@property (nonatomic, strong) UILabel *timeLabel;
@property (nonatomic, strong) NEEduChatMessage *model;

@end

NS_ASSUME_NONNULL_END
