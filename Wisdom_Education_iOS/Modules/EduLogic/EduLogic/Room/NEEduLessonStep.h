//
//  NEEduLessonStep.h
//  EduLogic
//
//  Created by Groot on 2021/6/6.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, NEEduLessonState) {
    NEEduLessonStateNone, //未开课
    NEEduLessonStateClassIn,//上课中
    NEEduLessonStateClassOver //课堂结束
};

@interface NEEduLessonStep : NSObject
@property (nonatomic, assign) NEEduLessonState value;
@property (nonatomic, assign) NSInteger time;
@end

NS_ASSUME_NONNULL_END
