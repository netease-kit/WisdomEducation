//
//  NEEduHandsupProperty.h
//  EduLogic
//
//  Created by Groot on 2021/6/3.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
typedef NS_ENUM(NSInteger, NEEduHandsupState) {
    NEEduHandsupStateIdle             = 0,//初始值，学生关闭
    NEEduHandsupStateApply            = 1,
    NEEduHandsupStateTeaAccept        = 2,
    NEEduHandsupStateTeaReject        = 3,
    NEEduHandsupStateStuCancel        = 4,
    NEEduHandsupStateTeaOffStage      = 5,//老师关闭
};

@interface NEEduHandsupProperty : NSObject
@property (nonatomic, assign) NEEduHandsupState value;

@end

NS_ASSUME_NONNULL_END
