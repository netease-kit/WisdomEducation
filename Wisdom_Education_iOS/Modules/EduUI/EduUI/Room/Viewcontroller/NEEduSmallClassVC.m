//
//  NEEduSmallClassVC.m
//  EduUI
//
//  Created by Groot on 2021/5/28.
//

#import "NEEduSmallClassVC.h"
#import "UIView+Toast.h"

@interface NEEduSmallClassVC ()

@end

@implementation NEEduSmallClassVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.whiteboardWritable = NO;
}

- (void)initMenuItems {
    NEEduMenuItem *audoItem = [[NEEduMenuItem alloc] initWithTitle:@"静音" image:[UIImage ne_imageNamed:@"menu_audio"]];
    audoItem.type = NEEduMenuItemTypeAudio;
    audoItem.selectTitle = @"取消静音";
    [audoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_audio_off"]];
    
    NEEduMenuItem *videoItem = [[NEEduMenuItem alloc] initWithTitle:@"关闭摄像头" image:[UIImage ne_imageNamed:@"menu_video"]];
    videoItem.selectTitle = @"打开摄像头";
    videoItem.type = NEEduMenuItemTypeVideo;
    [videoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_video_off"]];
    NEEduMenuItem *membersItem = [[NEEduMenuItem alloc] initWithTitle:@"课堂成员" image:[UIImage ne_imageNamed:@"menu_members"]];
    membersItem.type = NEEduMenuItemTypeMembers;
    NEEduMenuItem *chatItem = [[NEEduMenuItem alloc] initWithTitle:@"聊天室" image:[UIImage ne_imageNamed:@"menu_chat"]];
    chatItem.type = NEEduMenuItemTypeChat;
    self.menuItems = @[audoItem,videoItem,membersItem,chatItem];
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
- (void)onLessonMuteAllAudio:(BOOL)mute roomUuid:(NSString *)roomUuid {
    [[EduManager shared].userService localUserAudioEnable:!mute result:^(NSError * _Nonnull error) {
        if (!error) {
            NSString *string = mute ? @"已全体静音" : @"已取消全体静音";
            [[UIApplication sharedApplication].keyWindow makeToast:string];
        }else {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}

- (void)onUserInWithUser:(NEEduHttpUser *)user members:(NSArray *)members {
    if ([user.role isEqualToString:NEEduRoleHost]) {
        [self.members replaceObjectAtIndex:0 withObject:user];
    }else {
        BOOL exist = NO;
        BOOL index = 0;
        for (int i = 0; i < self.members.count; i ++) {
            NEEduHttpUser *tmpUser = self.members[i];
            if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
                exist = YES;
                index = i;
            }
        }
        if (!exist) {
            if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
                [self.members insertObject:user atIndex:1];
            }else {
                [self.members addObject:user];
            }
        }else {
            [self.members replaceObjectAtIndex:index withObject:user];
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
    NEEduHttpUser *placeholdUser = [[NEEduHttpUser alloc] init];
    if ([user.role isEqualToString:NEEduRoleHost]) {
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
    
@end
