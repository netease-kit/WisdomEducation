//
//  NEEduSmallClassTeacherVC.m
//  EduUI
//
//  Created by Groot on 2021/6/8.
//

#import "NEEduSmallClassTeacherVC.h"

@interface NEEduSmallClassTeacherVC ()

@end

@implementation NEEduSmallClassTeacherVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.whiteboardWritable = YES;
}
- (NSArray <NEEduHttpUser *>*)membersWithProfile:(NEEduRoomProfile *)profile {
    NEEduHttpUser *teacher = [[NEEduHttpUser alloc] init];
    teacher.role = NEEduRoleHost;
    NSMutableArray *placehlodArray = [NSMutableArray arrayWithArray:@[teacher]];
    for (NEEduHttpUser *user in profile.snapshot.members) {
        if ([user.role isEqualToString:NEEduRoleHost]) {
            [placehlodArray replaceObjectAtIndex:0 withObject:user];
        }else {
            if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
                //自己
                [placehlodArray insertObject:user atIndex:1];
            }else  {
                [placehlodArray addObject:user];
            }
        }
    }
    self.members = placehlodArray;
    self.room = profile.snapshot.room;
    return placehlodArray;
}
#pragma mark - NEEduMessageServiceDelegate
- (void)onUserInWithUser:(NEEduHttpUser *)user members:(NSArray *)members {
//    NSMutableArray *muteMembers = [[NSMutableArray alloc] initWithArray:self.members];
    if ([user.role isEqualToString:NEEduRoleHost]) {
        [self.members replaceObjectAtIndex:0 withObject:user];
    }else {
        BOOL exist = NO;
        NSInteger index;
        for (int i = 0; i < self.members.count; i ++) {
            NEEduHttpUser *tmpUser = self.members[i];
            if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
                exist = YES;
                index = i;
                break;
            }
        }
        if (exist) {
            [self.members replaceObjectAtIndex:index withObject:user];
        }else {
            if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
                [self.members insertObject:user atIndex:1];
            }else {
                [self.members addObject:user];
            }
        }
    }
    [self.collectionView reloadData];
    //更新课堂成员页面
    if (self.membersVC) {
        if ([user.role isEqualToString:NEEduRoleHost]) {
            return;
        }
        [self.membersVC memberIn:[self memberFromHttpUser:user]];
    }
}

- (void)onUserOutWithUser:(NEEduHttpUser *)user members:(nonnull NSArray *)members {
    if ([user.role isEqualToString:NEEduRoleHost]) {
        NEEduHttpUser *placeholdUser = [[NEEduHttpUser alloc] init];
        placeholdUser.role = NEEduRoleHost;
        [self.members replaceObjectAtIndex:0 withObject:placeholdUser];
    }else {
        NEEduHttpUser *removeUser = nil;
        for (NEEduHttpUser *tempUser in self.members) {
            if ([tempUser.userUuid isEqualToString:user.userUuid]) {
                removeUser = tempUser;
                break;
            }
        }
        if (removeUser) {
            [self.members removeObject:removeUser];
        }
    }
    [self.collectionView reloadData];
    //更新课堂成员页面
    if (self.membersVC) {
        if ([user.role isEqualToString:NEEduRoleHost]) {
            return;
        }
        [self.membersVC memberOut:user.userUuid];
    }
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
