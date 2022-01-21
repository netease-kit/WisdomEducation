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
#import "NEAVAuthorization.h"
#import <AFNetworking/AFNetworkReachabilityManager.h>
#import "NEEduChatViewController.h"
#import "IMLoginVC.h"
#import "NESettingTableViewController.h"
@import NERecordPlayUI;
@import NERecordPlay;


// 隐私政策URL
static NSString *kPrivatePolicyURL = @"https://yunxin.163.com/clauses?serviceType=3";
// 用户协议URL
static NSString *kUserAgreementURL = @"http://yunxin.163.com/clauses";


static NSString *kLastRtcCid = @"lastRtcCid";
static NSString *kLastUserUuid = @"lastUserUuid";
static NSString *kLastUserToken = @"lastUserToken";

@interface EnterLessonViewController ()<UITextFieldDelegate,NEInputViewDelegate,UITableViewDelegate,UITableViewDataSource,EduSelectViewDelegate>
@property (nonatomic, strong) UIImageView *icon;
@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UILabel *subTileLabel;
@property (nonatomic, strong) EduInputView *lessonIdView;
@property (nonatomic, strong) EduInputView *nicknameView;
@property (nonatomic, strong) EduInputView *userIdView;
@property (nonatomic, strong) EduInputView *tokenView;

@property (nonatomic, strong) EduSelectView *selectionView;
@property (nonatomic, strong) UIButton *teacherRoleButton;
@property (nonatomic, strong) UIButton *studentRoleButton;
@property (nonatomic, strong) UIButton *currentRoleButton;
@property (nonatomic, assign) NSInteger lessonType;
@property (nonatomic, strong) UIButton *joinLessonBtn;
@property (nonatomic, strong) UIButton *recordBtn;
@property (nonatomic, strong) UILabel *infoLabel;
@property (nonatomic, strong) UIButton *settingButton;

@property (nonatomic, strong) NSArray <NSString *>*lessonTypes;

@property (nonatomic, assign) BOOL isLessonIdValide;
@property (nonatomic, assign) BOOL isNicknameValide;
@property (nonatomic, strong) UIActivity *activity;
@property (nonatomic, strong) UITableView *tableview;
@property (nonatomic, strong) NEEduChatViewController *chatVC;
@property (nonatomic, assign) NEEduRoleType role;

@end

@implementation EnterLessonViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.lessonTypes = @[@"一对一教学",@"多人小班课",@"互动大班课",@"直播大班课"];
    [self.tableview registerClass:[UITableViewCell class] forCellReuseIdentifier:@"cellID"];
    [self setupSubviews];
    [self preSetting];
}
- (void)viewWillAppear:(BOOL)animated {
    [self.navigationController setNavigationBarHidden:YES];
    NSString *lastRoomUuid = [[NSUserDefaults standardUserDefaults] objectForKey:kLastRoomUuid];
    self.recordBtn.hidden = lastRoomUuid.length > 0 ? NO : YES;
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
    [self.view addSubview:self.settingButton];
    if (@available(iOS 11.0, *)) {
        [NSLayoutConstraint activateConstraints:@[
            [self.settingButton.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-30],
            [self.settingButton.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor constant:30],
            [self.settingButton.heightAnchor constraintEqualToConstant:40],
            [self.settingButton.widthAnchor constraintEqualToConstant:60]
        ]];
    } else {
        [NSLayoutConstraint activateConstraints:@[
            [self.settingButton.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-30],
            [self.settingButton.topAnchor constraintEqualToAnchor:self.view.topAnchor constant:30],
            [self.settingButton.heightAnchor constraintEqualToConstant:40],
            [self.settingButton.widthAnchor constraintEqualToConstant:60]
        ]];
    }
    
#ifdef DEBUG
    EduInputView *userIDView = [[EduInputView alloc] initWithPlaceholder:@"请输入ID(可选)"];
    userIDView.textField.keyboardType = UIKeyboardTypeDefault;
    [self.view addSubview:userIDView];
    [NSLayoutConstraint activateConstraints:@[
        [userIDView.topAnchor constraintEqualToAnchor:self.subTileLabel.bottomAnchor constant:12],
        [userIDView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:30],
        [userIDView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-30],
        [userIDView.heightAnchor constraintEqualToConstant:44]
    ]];
    self.userIdView = userIDView;
    EduInputView *tokenView = [[EduInputView alloc] initWithPlaceholder:@"请输入token(可选)"];
    tokenView.textField.keyboardType = UIKeyboardTypeDefault;
    [self.view addSubview:tokenView];
    [NSLayoutConstraint activateConstraints:@[
        [tokenView.topAnchor constraintEqualToAnchor:userIDView.bottomAnchor constant:12],
        [tokenView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:30],
        [tokenView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-30],
        [tokenView.heightAnchor constraintEqualToConstant:44]
    ]];
    self.tokenView = tokenView;
    [self.view addSubview:self.lessonIdView];
    [NSLayoutConstraint activateConstraints:@[
        [self.lessonIdView.topAnchor constraintEqualToAnchor:tokenView.bottomAnchor constant:12],
        [self.lessonIdView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:30],
        [self.lessonIdView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-30],
        [self.lessonIdView.heightAnchor constraintEqualToConstant:44]
    ]];
#else
    [self.view addSubview:self.lessonIdView];
    [NSLayoutConstraint activateConstraints:@[
        [self.lessonIdView.topAnchor constraintEqualToAnchor:self.subTileLabel.bottomAnchor constant:12],
        [self.lessonIdView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor constant:30],
        [self.lessonIdView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor constant:-30],
        [self.lessonIdView.heightAnchor constraintEqualToConstant:44]
    ]];
    
#endif
    [self.view addSubview:self.nicknameView];
    NSLayoutConstraint *nicknameLeading = [NSLayoutConstraint constraintWithItem:self.nicknameView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *nicknameTrailing = [NSLayoutConstraint constraintWithItem:self.nicknameView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *nicknameTop = [NSLayoutConstraint constraintWithItem:self.nicknameView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.lessonIdView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
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
    
    [self.view addSubview:self.recordBtn];
    [NSLayoutConstraint activateConstraints:@[
                                             [self.recordBtn.topAnchor constraintEqualToAnchor:self.joinLessonBtn.bottomAnchor constant:12],
                                             [self.recordBtn.leadingAnchor constraintEqualToAnchor:self.joinLessonBtn.leadingAnchor],
                                             [self.recordBtn.trailingAnchor constraintEqualToAnchor:self.joinLessonBtn.trailingAnchor],
                                             [self.recordBtn.heightAnchor constraintEqualToConstant:44]
                                             ]];
    NSString *lastRoomUuid = [[NSUserDefaults standardUserDefaults] objectForKey:kLastRoomUuid];
    self.recordBtn.hidden = lastRoomUuid.length > 0 ? NO : YES;
    
    [self.view addSubview:self.infoLabel];
    NSLayoutConstraint *infoTop = [NSLayoutConstraint constraintWithItem:self.infoLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.recordBtn attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
    NSLayoutConstraint *infoLeading = [NSLayoutConstraint constraintWithItem:self.infoLabel attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.recordBtn attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *infoTrailing = [NSLayoutConstraint constraintWithItem:self.infoLabel attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.recordBtn attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *infoHeight = [NSLayoutConstraint constraintWithItem:self.infoLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:40];
    [self.view addConstraints:@[infoTop,infoLeading,infoTrailing,infoHeight]];
}

- (void)preSetting {
    [[NSUserDefaults standardUserDefaults] setObject:@(YES) forKey:showChatroomKey];
}
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self endEditing];
}

- (void)endEditing {
    [self.view endEditing:YES];
    self.tableview.hidden = YES;
}

- (BOOL)isValidLessonId:(NSString *)lessonId {
    if (lessonId.length > 11 || lessonId.length <= 0) {
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
    if ([button isEqual:self.teacherRoleButton]) {
        self.role = NEEduRoleTypeTeacher;
    }else {
        self.role = NEEduRoleTypeStudent;
    }
    [self checkJoinButton];
}
- (void)enterLessonEvent:(UIButton *)button {
    [NEAVAuthorization requestAudioAuthorization:^(BOOL granted) {
        if (!granted) {
            [self.view makeToast:@"请在设置页面先打开麦克权限"];
            return;
        }else {
            [NEAVAuthorization requestCameraAuthorization:^(BOOL granted) {
                if (!granted) {
                    [self.view makeToast:@"请在设置页面先开启视频权限"];
                    return;
                }else {
                    [self enterRoom];
                }
            }];
        }
    }];
}

#pragma mark - record player
- (void)recordPlayEvent:(UIButton *)button {
    NSString *lastRoomUuid = [[NSUserDefaults standardUserDefaults] objectForKey:kLastRoomUuid];
    NSString *lastRtcCid = [[NSUserDefaults standardUserDefaults] objectForKey:kLastRtcCid];
    NSString *lastUserUuid = [[NSUserDefaults standardUserDefaults] objectForKey:kLastUserUuid];
    NSString *lastUserToken = [[NSUserDefaults standardUserDefaults] objectForKey:kLastUserToken];
    NERecordRequest *request = [[NERecordRequest alloc] initWithAppKey:[KeyCenter appKey] authorization:[KeyCenter authorization] baseUrl:[KeyCenter baseURL] userUuid:lastUserUuid token:lastUserToken];
    [request getRecordListWithRoomUuid:lastRoomUuid rtcCid:lastRtcCid success:^(id _Nonnull data) {
        if (!data) {
            [self.view makeToast:@"课程结束后，需进行文件转码，预计20分钟后可观看回放"];
            return;
        }
        NERecordViewController *vc = [[NERecordViewController alloc] init];
        vc.modalPresentationStyle = UIModalPresentationOverFullScreen;
        vc.recordData = data;
        [self presentViewController:vc animated:YES completion:nil];
    } failure:^(NSError * _Nonnull error) {
        [self.view makeToast:error.localizedDescription];
    }];
}
- (void)enterRoom {
    [self.view makeToastActivity:CSToastPositionCenter];
    self.view.userInteractionEnabled = NO;
//     1.初始化SDK
    NEEduKitOptions *option = [[NEEduKitOptions alloc] init];
    option.authorization = [KeyCenter authorization];
    option.baseURL = [KeyCenter baseURL];
    // 开启自动读取 config文件中配置
//    option.configRead = YES;
    [[NEEduManager shared] setupAppKey:[KeyCenter appKey] options:option];
    NSString *userId = [self.userIdView.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    NSString *token = [self.tokenView.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    if (userId.length && token.length) {
        //userID login
        __weak typeof(self)weakSelf = self;
        [[NEEduManager shared] login:self.userIdView.text token:self.tokenView.text success:^(NEEduUser * _Nonnull user) {
            __strong typeof(self)strongSelf = weakSelf;
            if (user.userUuid.length) {
                [[NSUserDefaults standardUserDefaults] setObject:user.userUuid forKey:kLastUserUuid];
            }
            if (user.userToken.length) {
                [[NSUserDefaults standardUserDefaults] setObject:user.userToken forKey:kLastUserToken];
            }
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
    }else {
        if (userId.length) {
            [self.view hideToastActivity];
            self.view.userInteractionEnabled = YES;
            [self.view makeToast:@"参数错误"];
            return;
        }
        if (token.length) {
            [self.view hideToastActivity];
            self.view.userInteractionEnabled = YES;
            [self.view makeToast:@"参数错误"];
            return;
        }
        // 匿名登录
        //    2.登录
            __weak typeof(self)weakSelf = self;
            [[NEEduManager shared] easyLoginWithSuccess:^(NEEduUser * _Nonnull user) {
                __strong typeof(self)strongSelf = weakSelf;
                if (user.userUuid.length) {
                    [[NSUserDefaults standardUserDefaults] setObject:user.userUuid forKey:kLastUserUuid];
                }
                if (user.userToken.length) {
                    [[NSUserDefaults standardUserDefaults] setObject:user.userToken forKey:kLastUserToken];
                }
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
}
- (void)createRoom {
    NEEduRoom *room = [[NEEduRoom alloc] init];
    room.nickName = self.nicknameView.text;
    room.roomName = [NSString stringWithFormat:@"%@的课堂",self.nicknameView.text];
    room.sceneType = self.lessonType;
    BOOL showChatroom = [[[NSUserDefaults standardUserDefaults] objectForKey:showChatroomKey] boolValue];
    NERoomConfig *config = [[NERoomConfig alloc] init];
    config.resource.chatroom = showChatroom;
    switch (room.sceneType) {
        case NEEduSceneType1V1: {
            room.configId = 5;
        }
            break;
        case NEEduSceneTypeSmall: {
            room.configId = 6;
        }
            break;
        case NEEduSceneTypeBig: {
            room.configId = 7;
        }
            break;
        case NEEduSceneTypeLive: {
            room.configId = 20;
            config.resource.live = YES;
        }
            break;
        default:
            break;
    }
    room.roomUuid = self.lessonIdView.text;
    room.config = config;
    if (self.lessonType == NEEduSceneTypeLive) {
        // 直播大班课流程
        __weak typeof(self)weakSelf = self;
        [[NEEduManager shared].roomService getRoom:room completion:^(NEEduRoomConfigResponse * _Nonnull result, NSError * _Nonnull error) {
            __strong typeof(self)strongSelf = weakSelf;
            strongSelf.view.userInteractionEnabled = YES;
            [strongSelf.view hideToastActivity];
            if (error) {
                [strongSelf.view makeToast:error.localizedDescription];
            }else {
                [strongSelf pushLiveVCWithRoomUuid:room.roomUuid];
            }
        }];
    }else {
        // 1v1、小班课、大班课流程
        __weak typeof(self)weakSelf = self;
        [[NEEduManager shared].roomService createRoom:room completion:^(NEEduCreateRoomRequest *result,NSError * _Nonnull error) {
            __strong typeof(self)strongSelf = weakSelf;
            [strongSelf.view hideToastActivity];
            strongSelf.view.userInteractionEnabled = YES;
            if (error) {
                [strongSelf.view makeToast:error.localizedDescription];
            }else {
                [strongSelf enterRoom:result];
            }
        }];
    }
}

- (void)enterRoom:(NEEduCreateRoomRequest *)resRoom {
    NEEduEnterRoomParam *param = [[NEEduEnterRoomParam alloc] init];
    param.roomUuid = resRoom.roomUuid;
    param.roomName = resRoom.roomName;
    param.sceneType = self.lessonType;
    param.userName = self.nicknameView.text;
    param.role = self.role;
    if (param.sceneType == NEEduSceneTypeBig) {
        param.autoSubscribeVideo = NO;
        param.autoSubscribeAudio = NO;
        if (param.role == NEEduRoleTypeStudent) {
            param.autoPublish = NO;
        }else {
            param.autoPublish = YES;
        }
    }else {
        param.autoPublish = YES;
        param.autoSubscribeVideo = YES;
        param.autoSubscribeAudio = YES;
    }
    __weak typeof(self)weakSelf = self;
    [[NEEduManager shared] enterClassroom:param completion:^(NSError * _Nonnull error, NEEduEnterRoomResponse * _Nonnull response) {
        __strong typeof(self)strongSelf = weakSelf;
        [strongSelf.view hideToastActivity];
        strongSelf.view.userInteractionEnabled = YES;
        if (error) {
            [strongSelf.view makeToast:error.localizedDescription];
        }else {
            if (response.room.rtcCid.length) {
                [[NSUserDefaults standardUserDefaults] setObject:response.room.rtcCid forKey:kLastRtcCid];
            }
            [strongSelf pushViewController];
        }
    }];
}

- (void)pushLiveVCWithRoomUuid:(NSString *)roomUuid {
    NEEduLiveRoomVC *roomVC = [[NEEduLiveRoomVC alloc] init];
    roomVC.roomUuid = roomUuid;
    roomVC.userName = self.nicknameView.text;
    roomVC.useFastLive = [[[NSUserDefaults standardUserDefaults] objectForKey:useFastLiveKey] boolValue];
    roomVC.modalPresentationStyle = UIModalPresentationFullScreen;
    [self presentViewController:roomVC animated:YES completion:nil];
}

- (void)pushViewController {
    UIViewController *roomVC;
    if (self.lessonType == NEEduSceneType1V1) {
        //1v1
        if (self.role == NEEduRoleTypeTeacher) {
            roomVC = [[NEEduOneMemberTeacherVC alloc] init];
        }else {
            roomVC = [[NEEduOneMemberVC alloc] init];
        }

    }else if(self.lessonType == NEEduSceneTypeSmall) {
        // 小班课
        if (self.role == NEEduRoleTypeTeacher) {
            roomVC = [[NEEduSmallClassTeacherVC alloc] init];
        }else {
            roomVC = [[NEEduSmallClassVC alloc] init];
        }

    }else if (self.lessonType == NEEduSceneTypeBig) {
        //大班课
        if (self.role == NEEduRoleTypeTeacher) {
            roomVC = [[NEEduBigClassTeacherVC alloc] init];
        }else {
            roomVC = [[NEEduBigClassStudentVC alloc] init];
        }
    }
    roomVC.modalPresentationStyle = UIModalPresentationFullScreen;
    [self presentViewController:roomVC animated:YES completion:nil];
    
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
    self.teacherRoleButton.hidden = [self.selectionView.title isEqualToString:@"直播大班课"];
    [self checkJoinButton];
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
            NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:self.tableview attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44 * self.lessonTypes.count];
            [self.view addConstraints:@[left,right,top]];
            [self.tableview addConstraint:height];
        }else {
            self.tableview.hidden = NO;
        }
    }else {
        self.tableview.hidden = YES;
    }
}
#pragma mark - 
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    return YES;
}
- (void)textFieldDidChange:(UITextField *)textField {
    if ([textField isEqual:self.lessonIdView.textField]) {
        textField.text =  [self numberOnly:textField.text];
        textField.text = textField.text.length >= 10?[textField.text substringToIndex:10]:textField.text;
    }else {
        textField.text = textField.text.length >= 20?[textField.text substringToIndex:20]:textField.text;
    }
    self.isLessonIdValide = [self isValidLessonId:self.lessonIdView.text];
    self.isNicknameValide = [self isValidNickname:self.nicknameView.text];
    [self checkJoinButton];
}

- (void)checkJoinButton {
    if (!self.currentRoleButton) {
        [self joinButtonEnable:NO];
        return;
    }
    if (!self.isLessonIdValide) {
        [self joinButtonEnable:NO];
        return;
    }
    if (!self.isNicknameValide) {
        [self joinButtonEnable:NO];
        return;
    }
    if ([self.selectionView.title isEqualToString:@"请选择课堂类型"]) {
        [self joinButtonEnable:NO];
        return;
    }
    [self joinButtonEnable:YES];
}

- (void)joinButtonEnable:(BOOL)enable {
    self.joinLessonBtn.enabled = enable;
    UIColor *color = enable ? [UIColor colorWithRed:81/255.0 green:116/255.0 blue:246/255.0 alpha:1.0] : [UIColor colorWithRed:144/255.0 green:166/255.0 blue:243/255.0 alpha:1.0];
    self.joinLessonBtn.backgroundColor = color;
}

- (void)settingButtonEvent:(UIButton *)button {
    UIStoryboard *setting = [UIStoryboard storyboardWithName:@"NESetting" bundle:nil];
    NESettingTableViewController *settingVC = [setting instantiateViewControllerWithIdentifier:@"setting"];
    [self.navigationController pushViewController:settingVC animated:YES];
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
- (UILabel *)infoLabel {
    if (!_infoLabel) {
        _infoLabel = [[UILabel alloc] init];
        _infoLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _infoLabel.font = [UIFont systemFontOfSize:12];
        _infoLabel.textColor = [UIColor lightGrayColor];
        _infoLabel.numberOfLines = 0;
        _infoLabel.text = @"本产品仅用于演示产品功能，课堂最长30分钟，不可商用";
    }
    return _infoLabel;
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
        [_joinLessonBtn setBackgroundColor:[UIColor colorWithRed:144/255.0 green:166/255.0 blue:243/255.0 alpha:1.0]];
        [_joinLessonBtn addTarget:self action:@selector(enterLessonEvent:) forControlEvents:UIControlEventTouchUpInside];
//        _joinLessonBtn.enabled = NO;
    }
    return _joinLessonBtn;
}

- (UIButton *)recordBtn {
    if (!_recordBtn) {
        _recordBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _recordBtn.translatesAutoresizingMaskIntoConstraints = NO;
        [_recordBtn setTitle:@"查看回放" forState:UIControlStateNormal];
        [_recordBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_recordBtn setBackgroundColor:[UIColor colorWithRed:81/255.0 green:116/255.0 blue:246/255.0 alpha:1.0]];
        [_recordBtn addTarget:self action:@selector(recordPlayEvent:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _recordBtn;
}

- (UIButton *)settingButton {
    if (!_settingButton) {
        _settingButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _settingButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_settingButton setImage:[UIImage imageNamed:@"enter_setting"] forState:UIControlStateNormal];
        [_settingButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_settingButton addTarget:self action:@selector(settingButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _settingButton;
}

- (UITableView *)tableview {
    if (!_tableview) {
        _tableview = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableview.translatesAutoresizingMaskIntoConstraints = NO;
        _tableview.delegate = self;
        _tableview.dataSource = self;
        _tableview.rowHeight = 44;
        _tableview.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableview.layer.cornerRadius = 2;
        _tableview.layer.borderColor = [UIColor colorWithRed:242/255.0 green:242/255.0 blue:242/255.0 alpha:1.0].CGColor;
        _tableview.layer.borderWidth = 1.0;
        _tableview.clipsToBounds = YES;
    }
    return _tableview;
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
