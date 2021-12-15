//
//  NEEduLessonInfoView.h
//  EduUI
//
//  Created by Groot on 2021/6/7.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <UIKit/UIKit.h>
#import "NEEduLessonInfoItem.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduLessonInfoView : UIView
@property (strong, nonatomic) IBOutlet UILabel *lessonName;
@property (strong, nonatomic) IBOutlet UILabel *teacherName;
@property (nonatomic, strong) NEEduLessonInfoItem *lessonItem;
@property (strong, nonatomic) IBOutlet UILabel *cid;
@end

NS_ASSUME_NONNULL_END
