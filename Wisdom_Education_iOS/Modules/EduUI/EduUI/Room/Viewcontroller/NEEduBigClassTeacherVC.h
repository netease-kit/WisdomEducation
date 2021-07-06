//
//  NEEduBigClassTeacherVC.h
//  EduUI
//
//  Created by Groot on 2021/6/3.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <EduUI/EduUI.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduBigClassTeacherVC : NEEduClassRoomVC
@property (nonatomic, strong) NSMutableArray<NEEduHttpUser *> *totalMembers;

@end

NS_ASSUME_NONNULL_END