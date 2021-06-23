//
//  EnterLessonViewController.m
//  NEEducation
//
//  Created by Netease on 2021/1/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "EnterLessonViewController.h"
#import "UIView+Toast.h"
#import "UIImage+NE.h"
#import "KeyCenter.h"
#import <EduUI/EduUI.h>
#import <EduLogic/EduLogic.h>
#import "NEEduClassRoomVC.h"
#import "NEEduBigClassTeacherVC.h"
#import "NEEduOneMemberTeacherVC.h"
#import "NEEduSmallClassTeacherVC.h"
#import "NEDeviceAuth.h"
#import <AFNetworking/AFNetworkReachabilityManager.h>

// 隐私政策URL
static NSString *kPrivatePolicyURL = @"https://yunxin.163.com/clauses?serviceType=3";
// 用户协议URL
static NSString *kUserAgreementURL = @"http://yunxin.163.com/clauses";

@interface EnterLessonViewController ()<UITextFieldDelegate,NEInputViewDelegate,UITableViewDelegate,UITableViewDataSource,EduSelectViewDelegate>
@property (nonatomic, strong) UIImageView *icon;
@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UILabel *subTileLabel;
@property (nonatomic, strong) EduInputView *lessonIdView;
@property (nonatomic, strong) EduInputView *lessonNameView;
@property (nonatomic, strong) EduInputView *nicknameView;
@property (nonatomic, strong) EduSelectView *selectionView;
@property (nonatomic, strong) UIButton *teacherRoleButton;
@property (nonatomic, strong) UIButton *studentRoleButton;
@property (nonatomic, strong) UIButton *currentRoleButton;
@property (nonatomic, assign) NSInteger lessonType;
@property (nonatomic, strong) UIButton *joinLessonBtn;
@property (nonatomic, strong) UITextView  *protocolView;

@property (nonatomic, strong) NSArray <NSString *>*lessonTypes;

@property (nonatomic, assign)   BOOL isLessonIdValide;
@property (nonatomic, assign)   BOOL isNicknameValide;
@property (nonatomic, strong) UIActivity *activity;
@property (nonatomic, strong) UITableView *tableview;

@end

@implementation EnterLessonViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.navigationController setNavigationBarHidden:YES];
    self.lessonTypes = @[@"一对一",@"小班课",@"大班课"];
    [self.tableview registerClass:[UITableViewCell class] forCellReuseIdentifier:@"cellID"];
    [self setupSubviews];
}

- (void)setupSubviews {
    self.view.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:self.icon];
    NSLayoutConstraint *iconTop = [NSLayoutConstraint constraintWithItem:self.icon attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:92];
    NSLayoutConstraint *iconCenterX = [NSLayoutConstraint constraintWithItem:self.icon attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeCenterX multiplier:1.0 constant:- 67];
    NSLayoutConstraint *iconWidth = [NSLayoutConstraint constraintWithItem:self.icon attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:36];
    NSLayoutConstraint *iconHeight = [NSLayoutConstraint constraintWithItem:self.icon attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:28];
    [self.view addConstraints:@[iconTop,iconCenterX]];
    [self.icon addConstraints:@[iconWidth,iconHeight]];
    
    [self.view addSubview:self.titleLab];
    NSLayoutConstraint *titleTop = [NSLayoutConstraint constraintWithItem:self.titleLab attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:92];
    NSLayoutConstraint *titleLeft = [NSLayoutConstraint constraintWithItem:self.titleLab attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.icon attribute:NSLayoutAttributeRight multiplier:1.0 constant:10];
    NSLayoutConstraint *titleWidth = [NSLayoutConstraint constraintWithItem:self.titleLab attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:125];
    NSLayoutConstraint *titleHeight = [NSLayoutConstraint constraintWithItem:self.titleLab attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:28];
    [self.view addConstraints:@[titleTop,titleLeft]];
    [self.titleLab addConstraints:@[titleWidth,titleHeight]];
    
    [self.view addSubview:self.subTileLabel];
    NSLayoutConstraint *subTitleTop = [NSLayoutConstraint constraintWithItem:self.subTileLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:130];
    NSLayoutConstraint *subTitleLeading = [NSLayoutConstraint constraintWithItem:self.subTileLabel attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:30];
    NSLayoutConstraint *subTitleTrailing = [NSLayoutConstraint constraintWithItem:self.subTileLabel attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:-30];
    NSLayoutConstraint *subTitleHeight = [NSLayoutConstraint constraintWithItem:self.subTileLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    [self.view addConstraints:@[subTitleTop,subTitleLeading,subTitleTrailing]];
    [self.subTileLabel addConstraint:subTitleHeight];

    [self.view addSubview:self.lessonIdView];
    NSLayoutConstraint *leading = [NSLayoutConstraint constraintWithItem:self.lessonIdView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:30];
    NSLayoutConstraint *trailing = [NSLayoutConstraint constraintWithItem:self.lessonIdView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:-30];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.lessonIdView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.subTileLabel attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:self.lessonIdView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];
    [self.lessonIdView addConstraint:height];
    [self.view addConstraints:@[leading,trailing,top]];
    
    [self.view addSubview:self.lessonNameView];
    NSLayoutConstraint *nameLeading = [NSLayoutConstraint constraintWithItem:self.lessonNameView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *nameTrailing = [NSLayoutConstraint constraintWithItem:self.lessonNameView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *nameTop = [NSLayoutConstraint constraintWithItem:self.lessonNameView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
    NSLayoutConstraint *nameHeight = [NSLayoutConstraint constraintWithItem:self.lessonNameView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];
    [self.lessonNameView addConstraint:nameHeight];
    [self.view addConstraints:@[nameLeading,nameTrailing,nameTop]];
    
    [self.view addSubview:self.nicknameView];
    NSLayoutConstraint *nicknameLeading = [NSLayoutConstraint constraintWithItem:self.nicknameView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *nicknameTrailing = [NSLayoutConstraint constraintWithItem:self.nicknameView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *nicknameTop = [NSLayoutConstraint constraintWithItem:self.nicknameView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.lessonNameView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
    NSLayoutConstraint *nicknameHeight = [NSLayoutConstraint constraintWithItem:self.nicknameView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];
    [self.nicknameView addConstraint:nicknameHeight];
    [self.view addConstraints:@[nicknameLeading,nicknameTrailing,nicknameTop]];

    [self.view addSubview:self.selectionView];
    NSLayoutConstraint *selectionLeading = [NSLayoutConstraint constraintWithItem:self.selectionView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *selectionTrailing = [NSLayoutConstraint constraintWithItem:self.selectionView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *selectionTop = [NSLayoutConstraint constraintWithItem:self.selectionView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.nicknameView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
    NSLayoutConstraint *selectionHeight = [NSLayoutConstraint constraintWithItem:self.selectionView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];
    
    [self.view addConstraints:@[selectionLeading,selectionTrailing,selectionTop]];
    [self.selectionView addConstraint:selectionHeight];

    [self.view addSubview:self.teacherRoleButton];
    NSLayoutConstraint *teacherTop = [NSLayoutConstraint constraintWithItem:self.teacherRoleButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.selectionView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
    NSLayoutConstraint *teacherLeading = [NSLayoutConstraint constraintWithItem:self.teacherRoleButton attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.selectionView attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *teacherWidth = [NSLayoutConstraint constraintWithItem:self.teacherRoleButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:64];
    NSLayoutConstraint *teacherHeight = [NSLayoutConstraint constraintWithItem:self.teacherRoleButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    [self.view addConstraints:@[teacherTop,teacherLeading]];
    [self.teacherRoleButton addConstraints:@[teacherWidth,teacherHeight]];
    
    [self.view addSubview:self.studentRoleButton];
    NSLayoutConstraint *studentTop = [NSLayoutConstraint constraintWithItem:self.studentRoleButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.teacherRoleButton attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *studentLeading = [NSLayoutConstraint constraintWithItem:self.studentRoleButton attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.teacherRoleButton attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:10];
    NSLayoutConstraint *studentWidth = [NSLayoutConstraint constraintWithItem:self.studentRoleButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:64];
    NSLayoutConstraint *studentHeight = [NSLayoutConstraint constraintWithItem:self.studentRoleButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    [self.view addConstraints:@[studentLeading,studentTop]];
    [self.studentRoleButton addConstraints:@[studentWidth,studentHeight]];

    [self.view addSubview:self.joinLessonBtn];
    NSLayoutConstraint *joinTop = [NSLayoutConstraint constraintWithItem:self.joinLessonBtn attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.teacherRoleButton attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
    NSLayoutConstraint *joinLeading = [NSLayoutConstraint constraintWithItem:self.joinLessonBtn attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.teacherRoleButton attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *joinTrailing = [NSLayoutConstraint constraintWithItem:self.joinLessonBtn attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *joinHeight = [NSLayoutConstraint constraintWithItem:self.joinLessonBtn attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    [self.view addConstraints:@[joinTop,joinLeading,joinTrailing,joinHeight]];

//    self.protocolView.frame = CGRectMake(20, self.joinLessonBtn.frame.origin.y + 50 + 15, self.view.bounds.size.width - 40, 30);
//    [self.view addSubview:self.protocolView];
//    self.protocolView.attributedText = [self protocolText];
//    self.protocolView.textAlignment = NSTextAlignmentCenter;

}
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self endEditing];
}

- (void)endEditing {
    [self.view endEditing:YES];
    self.tableview.hidden = YES;
}

- (BOOL)isValidLessonId:(NSString *)lessonId {
    if (lessonId.length > 11) {
        return NO;
    }
    NSString *string = [lessonId stringByTrimmingCharactersInSet:[NSCharacterSet decimalDigitCharacterSet]];
    string = [string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    return string.length ? NO : YES;
}
- (BOOL)isValidNickname:(NSString *)nickname {
    if (nickname.length <= 0) {
        return NO;
    }
    return YES;
}

- (NSString *)numberOnly:(NSString *)text {
    NSMutableArray *characters = [NSMutableArray array];
    NSMutableString *mutStr = [NSMutableString string];
    // 分离出字符串中的所有字符，并存储到数组characters中
    for (int i = 0; i < text.length; i ++) {
        NSString *subString = [text substringToIndex:i + 1];
        subString = [subString substringFromIndex:i];
        [characters addObject:subString];
    }
    
    // 利用正则表达式，匹配数组中的每个元素，判断是否是数字，将数字拼接在可变字符串mutStr中
    for (NSString *b in characters) {
        NSString *regex = @"^[0-9]*$";
        NSPredicate *pre = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", regex];// 谓词
        BOOL isNum = [pre evaluateWithObject:b];// 对b进行谓词运算
        if (isNum) {
            [mutStr appendString:b];
        }
    }
    return mutStr;
}

#pragma mark - private mothod

- (NSAttributedString *)protocolText {
    NSDictionary *norAttr = @{NSForegroundColorAttributeName: [UIColor colorWithRed:153/255.0 green:153/255.0 blue:153/255.0 alpha:1.0]};
    NSMutableAttributedString *attr = [[NSMutableAttributedString alloc] initWithString:@"加入课堂即视为您已同意 " attributes:norAttr];
    
    NSMutableAttributedString *tempAttr = [[NSMutableAttributedString alloc] initWithString:@"隐私政策" attributes:@{NSForegroundColorAttributeName: [UIColor colorWithRed:51/255.0 green:126/255.0 blue:255/255.0 alpha:1.0], NSLinkAttributeName: kPrivatePolicyURL}];
    [attr appendAttributedString:[tempAttr copy]];
    
    tempAttr = [[NSMutableAttributedString alloc] initWithString:@" 和 " attributes:norAttr];
    [attr appendAttributedString:[tempAttr copy]];
    
    tempAttr = [[NSMutableAttributedString alloc] initWithString:@"用户协议" attributes:@{NSForegroundColorAttributeName:[UIColor colorWithRed:51/255.0 green:126/255.0 blue:255/255.0 alpha:1.0] , NSLinkAttributeName: kUserAgreementURL}];
    [attr appendAttributedString:[tempAttr copy]];
    
    return [attr copy];
}

#pragma mark - event
- (void)roleButtonEvent:(UIButton *)button {
    if (self.currentRoleButton) {
        self.currentRoleButton.selected = !self.currentRoleButton.selected;
    }
    button.selected = !button.selected;
    self.currentRoleButton = button;
}
- (void)enterLessonEvent:(UIButton *)button {
    [NEDeviceAuth requestAudioAuthorization:^(BOOL granted) {
        if (!granted) {
            [self.view makeToast:@"请在设置页面先打开麦克权限"];
            return;
        }else {
            [NEDeviceAuth requestCameraAuthorization:^(BOOL granted) {
                if (!granted) {
                    [self.view makeToast:@"请在设置页面先打开摄像头权限"];
                    return;
                }else {
                    [self enterRoom];
                }
            }];
        }
    }];
}
- (void)enterRoom {
    if (self.lessonIdView.text.length <= 0) {
        [self.view makeToast:@"课堂号不可为空"];
        return;
    }
    if (![self isValidLessonId:self.lessonIdView.text]) {
        [self.view makeToast:@"课堂号仅支持11位以内数字"];
        return;
    }
    if (self.lessonNameView.text.length <= 0) {
        [self.view makeToast:@"课堂名称不可为空"];
        return;
    }
    if (self.nicknameView.text.length <= 0) {
        [self.view makeToast:@"昵称不可为空"];
        return;
    }
    if ([self.selectionView.title isEqualToString:@"请选择课堂类型"]) {
        [self.view makeToast:@"课堂类型未选择"];
        return;
    }
    if (!self.currentRoleButton) {
        [self.view makeToast:@"未选择角色"];
        return;
    }
    [self.view makeToastActivity:CSToastPositionCenter];
    self.view.userInteractionEnabled = NO;
//     1.初始化SDK
    NEEduKitOptions *option = [[NEEduKitOptions alloc] init];
    option.authorization = [KeyCenter authorization];
    option.baseURL = [KeyCenter baseURL];
    [[EduManager shared] setupAppId:[KeyCenter appId] options:option];
//    2.登录
    __weak typeof(self)weakSelf = self;
    [[EduManager shared] login:nil success:^(NEEduUser * _Nonnull user) {
        __strong typeof(self)strongSelf = weakSelf;
//        3.创建房间
        [strongSelf createRoom];
        
    } failure:^(NSError * _Nonnull error) {
        __strong typeof(self)strongSelf = weakSelf;
        [strongSelf.view hideToastActivity];
        strongSelf.view.userInteractionEnabled = YES;
        if (error.code == -1009) {
            [strongSelf.view makeToast:@"网络连接失败，请稍后再试"];
        }else {
            [strongSelf.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)createRoom {
    NEEduRoom *room = [[NEEduRoom alloc] init];
    room.roomName = self.lessonNameView.text;
    room.sceneType = self.lessonType;
    switch (room.sceneType) {
        case NEEduSceneType1V1:
        {
            room.configId = 5;
        }
            break;
        case NEEduSceneTypeSmall:
        {
            room.configId = 6;
        }
            break;
        case NEEduSceneTypeBig:
        {
            room.configId = 7;
        }
            break;
        default:
            break;
    }
    room.roomUuid = [NSString stringWithFormat:@"%@%d",self.lessonIdView.text,room.configId];
    __weak typeof(self)weakSelf = self;
    [[EduManager shared].roomService createRoom:room completion:^(NEEduCreateRoomRequest *result,NSError * _Nonnull error) {
        __strong typeof(self)strongSelf = weakSelf;
        if (error) {
            [strongSelf.view hideToastActivity];
            strongSelf.view.userInteractionEnabled = YES;
            [strongSelf.view makeToast:error.localizedDescription];
        }else {
            [strongSelf enterRoom:result];
        }
    }];
}
- (void)enterRoom:(NEEduCreateRoomRequest *)resRoom {
    NEEduEnterRoomParam *room = [[NEEduEnterRoomParam alloc] init];
    room.autoPublish = YES;
    room.autoSubscribeVideo = YES;
    room.autoSubscribeAudio = YES;
    room.roomUuid = resRoom.roomUuid;
    room.roomName = resRoom.roomName;
    room.sceneType = self.lessonType;
    if ([self.currentRoleButton.titleLabel.text isEqualToString:self.studentRoleButton.titleLabel.text]) {
        room.role = NEEduRoleTypeStudent;
    }else {
        room.role = NEEduRoleTypeTeacher;
    }
    if (room.sceneType == NEEduSceneTypeBig) {
        room.autoSubscribeVideo = NO;
        room.autoSubscribeAudio = NO;
        if (room.role == NEEduRoleTypeStudent) {
            room.autoPublish = NO;
        }
    }
    room.userName = self.nicknameView.text;
    __weak typeof(self)weakSelf = self;
    [[EduManager shared] enterClassroom:room success:^(NEEduRoomProfile * _Nonnull roomProfile) {
        __strong typeof(self)strongSelf = weakSelf;
        [strongSelf.view hideToastActivity];
        strongSelf.view.userInteractionEnabled = YES;
        NEEduHttpUser *teacher = [[NEEduHttpUser alloc] init];
        teacher.role = NEEduRoleHost;
        NEEduHttpUser *student = [[NEEduHttpUser alloc] init];
        student.role = NEEduRoleBroadcaster;
        if (room.sceneType == NEEduSceneType1V1) {
            //一对一
            NSMutableArray *placehlodArray = [NSMutableArray arrayWithArray:@[teacher,student]];
            NEEduClassRoomVC *oneMemberVC;
            for (NEEduHttpUser *user in roomProfile.snapshot.members) {
                if ([user.role isEqualToString:NEEduRoleHost]) {
                    [placehlodArray replaceObjectAtIndex:0 withObject:user];
                }else {
                    [placehlodArray replaceObjectAtIndex:1 withObject:user];
                }
            }
            if ([EduManager shared].localUser.roleType == NEEduRoleTypeTeacher) {
                oneMemberVC = [[NEEduOneMemberTeacherVC alloc] init];
            }else {
                oneMemberVC = [[NEEduOneMemberVC alloc] init];
            }
            oneMemberVC.members = placehlodArray;
            oneMemberVC.room = roomProfile.snapshot.room;
            oneMemberVC.modalPresentationStyle = UIModalPresentationFullScreen;
            [strongSelf presentViewController:oneMemberVC animated:YES completion:nil];
        }else if(room.sceneType == NEEduSceneTypeSmall) {
            //小班课
            NSMutableArray *placehlodArray = [NSMutableArray arrayWithArray:@[teacher]];
            NEEduClassRoomVC *smallVC;
            for (NEEduHttpUser *user in roomProfile.snapshot.members) {
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
            if ([EduManager shared].localUser.roleType == NEEduRoleTypeTeacher) {
                smallVC = [[NEEduSmallClassTeacherVC alloc] init];
            }else {
                smallVC = [[NEEduSmallClassVC alloc] init];
            }
            smallVC.members = placehlodArray;
            smallVC.room = roomProfile.snapshot.room;
            smallVC.modalPresentationStyle = UIModalPresentationFullScreen;
            [strongSelf presentViewController:smallVC animated:YES completion:nil];
            
        }else {
            //大班课
            NSMutableArray *totalArray = [NSMutableArray arrayWithObject:teacher];
            NSMutableArray *onlineArray = [NSMutableArray arrayWithObject:teacher];
            for (NEEduHttpUser *user in roomProfile.snapshot.members) {
                if ([user.role isEqualToString:NEEduRoleHost]) {
                    [totalArray replaceObjectAtIndex:0 withObject:user];
                    [onlineArray replaceObjectAtIndex:0 withObject:user];
                }else {
                    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
                        //自己
                        [totalArray insertObject:user atIndex:1];
                        if (user.properties.avHandsUp.value == NEEduHandsupStateTeaAccept) {
                            [onlineArray insertObject:user atIndex:1];
                        }
                    }else  {
                        [totalArray addObject:user];
                        if (user.properties.avHandsUp.value == NEEduHandsupStateTeaAccept) {
                            [onlineArray addObject:user];
                        }
                    }
                }
            }
            NEEduClassRoomVC *classVC;
            if ([EduManager shared].localUser.roleType == NEEduRoleTypeTeacher) {
                classVC = [[NEEduBigClassTeacherVC alloc] init];
                NEEduBigClassTeacherVC *teacherVC = (NEEduBigClassTeacherVC *)classVC;
                teacherVC.totalMembers = totalArray;
            }else {
                classVC = [[NEEduBigClassVC alloc] init];
                NEEduBigClassTeacherVC *studentVC = (NEEduBigClassTeacherVC *)classVC;
                studentVC.totalMembers = totalArray;
            }
            classVC.members = onlineArray;
            classVC.room = roomProfile.snapshot.room;
            classVC.modalPresentationStyle = UIModalPresentationFullScreen;
            [strongSelf presentViewController:classVC animated:YES completion:nil];
        }
    } failure:^(NSError * _Nonnull error) {
        __strong typeof(self)strongSelf = weakSelf;
        [strongSelf.view hideToastActivity];
        strongSelf.view.userInteractionEnabled = YES;
        if (error.code == 1002) {
            if (room.role == NEEduRoleTypeTeacher) {
                [strongSelf.view makeToast:@"老师数量超过限制"];
            }else {
                [strongSelf.view makeToast:@"学生数量超过限制"];
            }
        }else {
            [strongSelf.view makeToast:error.localizedDescription];
        }
    }];
}
#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.lessonTypes.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cellID" forIndexPath:indexPath];
    cell.textLabel.text = self.lessonTypes[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    self.lessonType = indexPath.row;
    self.selectionView.title = self.lessonTypes[indexPath.row];
    [tableView removeFromSuperview];
}
#pragma mark - EduSelectViewDelegate
- (void)selectionView:(EduSelectView *)selectionView didSelected:(BOOL)selected {
    [self.view endEditing:YES];
    if (selected) {
        if (!self.tableview.superview) {
            [self.view addSubview:self.tableview];
            NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.tableview attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.selectionView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
            NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:self.tableview attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.selectionView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
            NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.tableview attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.selectionView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
            NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:self.tableview attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:132];
            [self.view addConstraints:@[left,right,top]];
            [self.tableview addConstraint:height];
        }else {
            self.tableview.hidden = NO;
        }
    }else {
        self.tableview.hidden = YES;
    }
}
#pragma mark - lazy method

- (UIImageView *)icon {
    if (!_icon) {
        _icon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"enter_icon"]];
        _icon.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _icon;
}
- (UILabel *)titleLab {
    if (!_titleLab) {
        _titleLab = [[UILabel alloc] init];
        _titleLab.translatesAutoresizingMaskIntoConstraints = NO;
        _titleLab.font = [UIFont systemFontOfSize:24];
        _titleLab.textColor = [UIColor colorWithRed:34/255.0 green:34/255.0 blue:34/255.0 alpha:1.0];
        _titleLab.text = @"智慧云课堂";
    }
    return _titleLab;
}
- (UILabel *)subTileLabel {
    if (!_subTileLabel) {
        _subTileLabel = [[UILabel alloc] init];
        _subTileLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _subTileLabel.textAlignment = NSTextAlignmentCenter;
#ifdef DEBUG
        _subTileLabel.text = @"若课堂不存在则会创建课堂(测试)";
#else
        _subTileLabel.text = @"若课堂不存在则会创建课堂";
#endif
        _subTileLabel.font = [UIFont systemFontOfSize:12];
        _subTileLabel.textColor = [UIColor colorWithRed:153/255.0 green:153/255.0 blue:153/255.0 alpha:1.0];
    }
    return _subTileLabel;
}
- (EduInputView *)lessonIdView {
    if (!_lessonIdView) {
        _lessonIdView = [[EduInputView alloc] initWithPlaceholder:@"请输入课堂号"];
        _lessonIdView.delegate = self;
    }
    return _lessonIdView;
}
- (EduInputView *)lessonNameView {
    if (!_lessonNameView) {
        _lessonNameView = [[EduInputView alloc] initWithPlaceholder:@"请输入课堂名称"];
        _lessonNameView.delegate = self;
        _lessonNameView.textField.keyboardType = UIKeyboardTypeDefault;
    }
    return _lessonNameView;
}
- (EduInputView *)nicknameView {
    if (!_nicknameView) {
        _nicknameView = [[EduInputView alloc] initWithPlaceholder:@"请输入昵称"];
        _nicknameView.delegate = self;
        _nicknameView.textField.keyboardType = UIKeyboardTypeDefault;
    }
    return _nicknameView;
}
- (EduSelectView *)selectionView {
    if (!_selectionView) {
        _selectionView = [[EduSelectView alloc] initWithTitle:@"请选择课堂类型"];
        _selectionView.delegate = self;
    }
    return _selectionView;
}
- (UIButton *)teacherRoleButton {
    if (!_teacherRoleButton) {
        _teacherRoleButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _teacherRoleButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_teacherRoleButton setImage:[UIImage imageNamed:@"enter_radio"] forState:UIControlStateNormal];
        [_teacherRoleButton setImage:[UIImage imageNamed:@"enter_radio_selected"] forState:UIControlStateSelected];
        [_teacherRoleButton setTitle:@"老师" forState:UIControlStateNormal];
        [_teacherRoleButton setTitleColor:[UIColor colorWithRed:51/255.0 green:51/255.0 blue:51/255.0 alpha:1.0] forState:UIControlStateNormal];
        _teacherRoleButton.titleLabel.font = [UIFont systemFontOfSize:14];
        [_teacherRoleButton setTitleEdgeInsets:UIEdgeInsetsMake(0, 12, 0, 0)];
        [_teacherRoleButton addTarget:self action:@selector(roleButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _teacherRoleButton;
}
- (UIButton *)studentRoleButton {
    if (!_studentRoleButton) {
        _studentRoleButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _studentRoleButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_studentRoleButton setImage:[UIImage imageNamed:@"enter_radio"] forState:UIControlStateNormal];
        [_studentRoleButton setImage:[UIImage imageNamed:@"enter_radio_selected"] forState:UIControlStateSelected];
        [_studentRoleButton setTitle:@"学生" forState:UIControlStateNormal];
        [_studentRoleButton setTitleColor:[UIColor colorWithRed:51/255.0 green:51/255.0 blue:51/255.0 alpha:1.0] forState:UIControlStateNormal];
        _studentRoleButton.titleLabel.font = [UIFont systemFontOfSize:14];
        [_studentRoleButton setTitleEdgeInsets:UIEdgeInsetsMake(0, 12, 0, 0)];
        [_studentRoleButton addTarget:self action:@selector(roleButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _studentRoleButton;
}

- (UIButton *)joinLessonBtn {
    if (!_joinLessonBtn) {
        _joinLessonBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _joinLessonBtn.translatesAutoresizingMaskIntoConstraints = NO;
        [_joinLessonBtn setTitle:@"加入课堂" forState:UIControlStateNormal];
        [_joinLessonBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_joinLessonBtn setBackgroundColor:[UIColor colorWithRed:81/255.0 green:116/255.0 blue:246/255.0 alpha:1.0]];
        [_joinLessonBtn addTarget:self action:@selector(enterLessonEvent:) forControlEvents:UIControlEventTouchUpInside];
//        _joinLessonBtn.enabled = NO;
    }
    return _joinLessonBtn;
}

- (UITableView *)tableview {
    if (!_tableview) {
        _tableview = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableview.translatesAutoresizingMaskIntoConstraints = NO;
        _tableview.delegate = self;
        _tableview.dataSource = self;
        _tableview.rowHeight = 44;
        _tableview.separatorStyle = UITableViewCellSeparatorStyleNone;
    }
    return _tableview;
}
- (UITextView *)protocolView {
    if (!_protocolView) {
        _protocolView = [[UITextView alloc] init];
        _protocolView.textAlignment = NSTextAlignmentCenter;
        _protocolView.editable = NO;
        _protocolView.scrollEnabled = NO;
        _protocolView.backgroundColor = [UIColor whiteColor];
    }
    return _protocolView;
}

#pragma mark - Orientations

-(BOOL)shouldAutorotate {
    return NO;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
     return UIInterfaceOrientationMaskPortrait;
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return UIInterfaceOrientationPortrait;
}
@end
