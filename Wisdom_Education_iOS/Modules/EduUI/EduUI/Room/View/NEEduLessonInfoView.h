//
//  NEEduLessonInfoView.h
//  EduUI
//
//  Created by Groot on 2021/6/7.
//

#import <UIKit/UIKit.h>
#import "NEEduLessonInfoItem.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduLessonInfoView : UIView
@property (strong, nonatomic) IBOutlet UILabel *lessonName;
@property (strong, nonatomic) IBOutlet UILabel *teacherName;
@property (nonatomic, strong) NEEduLessonInfoItem *lessonItem;
@end

NS_ASSUME_NONNULL_END
