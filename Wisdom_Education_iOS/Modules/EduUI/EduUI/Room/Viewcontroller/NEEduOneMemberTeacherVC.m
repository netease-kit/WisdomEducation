//
//  NEEduOneMemberTeacherVC.m
//  EduUI
//
//  Created by Groot on 2021/6/5.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduOneMemberTeacherVC.h"
#import "UIView+Toast.h"

@implementation NEEduOneMemberTeacherVC

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)initMenuItems {
    NEEduMenuItem *audoItem = [[NEEduMenuItem alloc] initWithTitle:@"静音" image:[UIImage ne_imageNamed:@"menu_audio"]];
    audoItem.selectTitle = @"取消静音";
    audoItem.type = NEEduMenuItemTypeAudio;
    [audoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_audio_off"]];
    
    NEEduMenuItem *videoItem = [[NEEduMenuItem alloc] initWithTitle:@"关闭摄像头" image:[UIImage ne_imageNamed:@"menu_video"]];
    videoItem.selectTitle = @"打开摄像头";
    videoItem.type = NEEduMenuItemTypeVideo;
    [videoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_video_off"]];

    NEEduMenuItem *shareItem = [[NEEduMenuItem alloc] initWithTitle:@"共享屏幕" image:[UIImage ne_imageNamed:@"menu_share_screen"]];
    shareItem.type = NEEduMenuItemTypeShareScreen;
    [shareItem setSelctedImage:[UIImage ne_imageNamed:@"menu_share_screen_stop"]];
    self.menuItems = @[audoItem,videoItem,shareItem];
}
- (NSArray <NEEduHttpUser *>*)membersWithProfile:(NEEduRoomProfile *)profile {
    NEEduHttpUser *teacher = [[NEEduHttpUser alloc] init];
    teacher.role = NEEduRoleHost;
    NEEduHttpUser *student = [[NEEduHttpUser alloc] init];
    student.role = NEEduRoleBroadcaster;
    NSMutableArray *placehlodArray = [NSMutableArray arrayWithArray:@[teacher,student]];
    for (NEEduHttpUser *user in profile.snapshot.members) {
        if ([user.role isEqualToString:NEEduRoleHost]) {
            [placehlodArray replaceObjectAtIndex:0 withObject:user];
        }else {
            [placehlodArray replaceObjectAtIndex:1 withObject:user];
        }
    }
    self.members = placehlodArray;
    self.room = profile.snapshot.room;
    [self.collectionView reloadData];
    return placehlodArray;
}
#pragma mark - NEEduVideoCellDelegate
- (void)didTapCell:(NEEduVideoCell *)cell {
    if (cell.member.userUuid) {
        [self showAlertViewWithMember:cell.member cell:cell];
    }
}
#pragma mark - NEEduMessageServiceDelegate
- (void)onUserInWithUser:(NEEduHttpUser *)user members:(nonnull NSArray *)members {
    if ([user.role isEqualToString:NEEduRoleHost]) {
        [self.members replaceObjectAtIndex:0 withObject:user];
    }else {
        [self.members replaceObjectAtIndex:1 withObject:user];
    }
    [self.collectionView reloadData];
}

- (void)onUserOutWithUser:(NEEduHttpUser *)user members:(nonnull NSArray *)members {
    NEEduHttpUser *placeholdUser = [[NEEduHttpUser alloc] init];
    if ([user.role isEqualToString:NEEduRoleHost]) {
        placeholdUser.role = NEEduRoleHost;
        [self.members replaceObjectAtIndex:0 withObject:placeholdUser];
    }else {
        placeholdUser.role = NEEduRoleBroadcaster;
        [self.members replaceObjectAtIndex:1 withObject:placeholdUser];
    }
    [self.collectionView reloadData];
}
- (void)onWhiteboardAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [self.members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    self.whiteboardWritable = enable;
    //如果是自己的权限被修改 设置白板
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
        [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
    }
    [self.collectionView reloadData];
}

- (void)onScreenShareAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    //如果是自己的权限被修改 更新底部菜单栏
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        if (enable) {
            NEEduMenuItem *shareItem = [[NEEduMenuItem alloc] initWithTitle:@"共享屏幕" image:[UIImage ne_imageNamed:@"menu_share_screen"]];
            shareItem.type = NEEduMenuItemTypeShareScreen;
            [shareItem setSelctedImage:[UIImage ne_imageNamed:@"menu_share_screen_stop"]];
            [self.maskView addItem:shareItem];
            [self.maskView insertItem:shareItem atIndex:2];
        }else {
            [self.maskView removeItemType:NEEduMenuItemTypeShareScreen];
        }
    }
    [self.collectionView reloadData];
}
#pragma mark -
- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NEEduVideoCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:cellID forIndexPath:indexPath];
    NEEduHttpUser *user = self.members[indexPath.row];
    cell.showWhiteboardIcon = YES;
    cell.delegate = self;
    cell.member = user;
    return cell;
}

- (void)audioButtonClick:(UIButton *)button {
    NEEduHttpUser *student = [self.members objectAtIndex:1];
    if (student.userUuid) {
        [[EduManager shared].userService remoteUserAudioEnable:button.selected userID:student.userUuid result:^(NSError * _Nonnull error) {
            if (error) {
                [self.view makeToast:error.localizedDescription];
            }
        }];
    }
    
}
- (void)videoButtonClick:(UIButton *)button {
    NEEduHttpUser *student = [self.members objectAtIndex:1];
    if (student.userUuid) {
        [[EduManager shared].userService remoteUserVideoEnable:button.selected userID:student.userUuid result:^(NSError * _Nonnull error) {
            if (error) {
                [self.view makeToast:error.localizedDescription];
            }
        }];
    }
}

#pragma mark - NEEduMessageServiceDelegate
// 音频流 视频流 屏幕共享辅流
- (void)onVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    [self updateAVEnable:enable user:user];
}
- (void)onAudioStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    [self updateAVEnable:enable user:user];
}


@end
