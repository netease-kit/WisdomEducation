//
//  NEEduBigClassTeacherVC.h
//  EduUI
//
//  Created by Groot on 2021/6/3.
//

#import <EduUI/EduUI.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEEduBigClassTeacherVC : NEEduClassRoomVC
@property (nonatomic, strong) NSMutableArray<NEEduHttpUser *> *totalMembers;

@end

NS_ASSUME_NONNULL_END
