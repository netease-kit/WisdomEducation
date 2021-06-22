//
//  NEEduMembersVC.m
//  EduLogic
//
//  Created by Groot on 2021/5/27.
//

#import "NEEduMembersVC.h"
#import "UIImage+NE.h"
#import "NEEduMemberCell.h"
#import <EduLogic/EduLogic.h>
#import "UIView+Toast.h"
#import "UIView+NE.h"

@interface NEEduMembersVC ()<UITableViewDelegate,UITableViewDataSource,NEEduMemberCellDelegate>
@property (nonatomic, strong) UIButton *backButton;
@property (nonatomic, strong) UIButton *onlineButton;
@property (nonatomic, strong) UIButton *totalButton;
@property (nonatomic, strong) UIView *lineView;
@property (nonatomic, strong) UIView *lightLineView;
@property (nonatomic, strong) UIButton *currentButton;
@property (nonatomic, strong) NSLayoutConstraint *onlineWidth;
@property (nonatomic, strong) NSLayoutConstraint *totalLeft;
@property (nonatomic, strong) NSLayoutConstraint *lightLineLeft;
@property (nonatomic, strong) NSArray<NEEduMember *> *onlineArray;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSArray *currentArray;
@property (nonatomic, strong) UIButton *muteAudioButton;
@property (nonatomic, strong) UIButton *muteTextButton;
@end
static NSString *memberCellID = @"memberCellID";
@implementation NEEduMembersVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupSubviews];
    [self loadData];
}
- (void)viewWillAppear:(BOOL)animated {
    [self.tableView reloadData];
    [self.totalButton setTitle:[NSString stringWithFormat:@"课堂成员(%lu)",(unsigned long)self.members.count] forState:UIControlStateNormal];
    [self.onlineButton setTitle:[NSString stringWithFormat:@"连线成员(%lu)",(unsigned long)self.onlineArray.count] forState:UIControlStateNormal];
}

- (void)loadData {
    self.onlineArray = [self achiveOnlineArray];
    if ([EduManager shared].roomService.room.sceneType == NEEduSceneTypeBig) {
        self.currentButton = self.onlineButton;
        self.currentArray = self.onlineArray;
        self.onlineWidth.constant = 140;
        self.totalLeft.constant = 0;
        self.lightLineLeft.constant = 0;
        [self.onlineButton setTitle:[NSString stringWithFormat:@"连线成员(%lu)",(unsigned long)self.onlineArray.count] forState:UIControlStateNormal];
        self.currentArray = self.onlineArray;
    }else {
        self.currentButton = self.totalButton;
        self.onlineWidth.constant = 0;
        self.totalLeft.constant = 70;
        self.lightLineLeft.constant = 70;
        self.currentArray = self.members;
    }
    self.currentButton.selected = YES;
    [self.totalButton setTitle:[NSString stringWithFormat:@"课堂成员(%lu)",(unsigned long)self.members.count] forState:UIControlStateNormal];
    self.muteTextButton.selected = self.muteChat;
}

- (NSArray *)achiveOnlineArray {
    NSPredicate *predicate = [NSPredicate predicateWithBlock:^BOOL(NEEduMember *  _Nullable evaluatedObject, NSDictionary<NSString *,id> * _Nullable bindings) {
        return evaluatedObject.online;
    }];
    return [self.members filteredArrayUsingPredicate:predicate];
}

- (void)user:(NSString *)userID online:(BOOL)online {
    for (NEEduMember *memberIn in self.members) {
        if ([memberIn.userID isEqualToString:userID]) {
            memberIn.online = online;
            break;
        }
    }
    self.onlineArray = [self achiveOnlineArray];
    if ([self.currentButton isEqual:self.onlineButton]) {
        self.currentArray = self.onlineArray;
    }
    if (self.presentingViewController) {
        [self.tableView reloadData];
        [self.onlineButton setTitle:[NSString stringWithFormat:@"连线成员(%lu)",(unsigned long)self.onlineArray.count] forState:UIControlStateNormal];
    }
}
- (void)memberIn:(NEEduMember *)member {
    [self.members addObject:member];
    if (self.presentingViewController) {
        [self.tableView reloadData];
        [self.totalButton setTitle:[NSString stringWithFormat:@"课堂成员(%lu)",(unsigned long)self.members.count] forState:UIControlStateNormal];
    }
}
- (void)memberOut:(NSString *)userID {
    NEEduMember *removeMember;
    for (int i = 0; i < self.members.count ; i ++) {
        NEEduMember *memberExist = self.members[i];
        if ([memberExist.userID isEqualToString:userID]) {
            removeMember = memberExist;
            break;
        }
    }
    if (removeMember) {
        [self.members removeObject:removeMember];
    }
    if (removeMember.online) {
        //上台学生离开
        self.onlineArray = [self achiveOnlineArray];
    }
    if ([self.currentButton isEqual:self.onlineButton]) {
        self.currentArray = self.onlineArray;
    }
    if (self.presentingViewController) {
        [self.tableView reloadData];
        [self.totalButton setTitle:[NSString stringWithFormat:@"课堂成员(%lu)",(unsigned long)self.members.count] forState:UIControlStateNormal];
        [self.onlineButton setTitle:[NSString stringWithFormat:@"连线成员(%lu)",(unsigned long)self.onlineArray.count] forState:UIControlStateNormal];
    }
}

- (void)setupSubviews {
    self.view.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    [self.view addSubview:self.backButton];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:40];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:48];
    [self.view addConstraints:@[left,top]];
    [self.backButton addConstraints:@[width,height]];
    
    [self.view addSubview:self.onlineButton];
    NSLayoutConstraint *onlineLeft = [NSLayoutConstraint constraintWithItem:self.onlineButton attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeCenterX multiplier:1.0 constant:- 140];
    NSLayoutConstraint *onlineTop = [NSLayoutConstraint constraintWithItem:self.onlineButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    self.onlineWidth = [NSLayoutConstraint constraintWithItem:self.onlineButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:140];
    NSLayoutConstraint *onlineHeight = [NSLayoutConstraint constraintWithItem:self.onlineButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:48];
    [self.view addConstraints:@[onlineLeft,onlineTop]];
    [self.onlineButton addConstraints:@[self.onlineWidth,onlineHeight]];
    
    [self.view addSubview:self.totalButton];
    self.totalLeft = [NSLayoutConstraint constraintWithItem:self.totalButton attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.onlineButton attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *totalTop = [NSLayoutConstraint constraintWithItem:self.totalButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *totalWidth = [NSLayoutConstraint constraintWithItem:self.totalButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:140];
    NSLayoutConstraint *totalHeight = [NSLayoutConstraint constraintWithItem:self.totalButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:48];
    [self.view addConstraints:@[self.totalLeft,totalTop]];
    [self.totalButton addConstraints:@[totalWidth,totalHeight]];
    
    [self.view addSubview:self.lineView];
    NSLayoutConstraint *lineLeft = [NSLayoutConstraint constraintWithItem:self.lineView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *lineRight = [NSLayoutConstraint constraintWithItem:self.lineView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *lineTop = [NSLayoutConstraint constraintWithItem:self.lineView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.totalButton attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *lineHeight = [NSLayoutConstraint constraintWithItem:self.lineView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:1];
    [self.view addConstraints:@[lineLeft,lineRight,lineTop]];
    [self.lineView addConstraint:lineHeight];
    
    [self.view addSubview:self.lightLineView];
    self.lightLineLeft = [NSLayoutConstraint constraintWithItem:self.lightLineView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.onlineButton attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *lightLineTop = [NSLayoutConstraint constraintWithItem:self.lightLineView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.lineView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *lightLineHeight = [NSLayoutConstraint constraintWithItem:self.lightLineView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:1];
    NSLayoutConstraint *lightLineWidth = [NSLayoutConstraint constraintWithItem:self.lightLineView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:140];
    [self.view addConstraints:@[self.lightLineLeft,lightLineTop]];
    [self.lightLineView addConstraints:@[lightLineHeight,lightLineWidth]];

    [self.view addSubview:self.tableView];
    NSLayoutConstraint *tableTop = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:49];
    NSLayoutConstraint *tableLeft = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1.0 constant:130];
    NSLayoutConstraint *tableRight = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:- 130];
    NSLayoutConstraint *tableBottom = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeBottom multiplier:1.0 constant:- 68];
    [self.view addConstraints:@[tableTop,tableLeft,tableRight,tableBottom]];
    [self.tableView registerClass:[NEEduMemberCell class] forCellReuseIdentifier:memberCellID];
    
    if ([EduManager shared].localUser.roleType == NEEduRoleTypeTeacher) {
        [self.view addSubview:self.muteAudioButton];
        NSLayoutConstraint *muteAudioTop = [NSLayoutConstraint constraintWithItem:self.muteAudioButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.tableView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
        NSLayoutConstraint *muteAudioLeft = [NSLayoutConstraint constraintWithItem:self.muteAudioButton attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.tableView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
        NSLayoutConstraint *muteAudioW = [NSLayoutConstraint constraintWithItem:self.muteAudioButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:120];
        NSLayoutConstraint *muteAudioH = [NSLayoutConstraint constraintWithItem:self.muteAudioButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];

        [self.view addConstraints:@[muteAudioTop,muteAudioLeft]];
        [self.muteAudioButton addConstraints:@[muteAudioW,muteAudioH]];
        
        [self.view addSubview:self.muteTextButton];
        NSLayoutConstraint *muteTextTop = [NSLayoutConstraint constraintWithItem:self.muteTextButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.tableView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:12];
        NSLayoutConstraint *muteTextLeft = [NSLayoutConstraint constraintWithItem:self.muteTextButton attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.muteAudioButton attribute:NSLayoutAttributeRight multiplier:1.0 constant:20];
        NSLayoutConstraint *muteTextW = [NSLayoutConstraint constraintWithItem:self.muteTextButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:150];
        NSLayoutConstraint *muteTextH = [NSLayoutConstraint constraintWithItem:self.muteTextButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];
        [self.view addConstraints:@[muteTextTop,muteTextLeft]];
        [self.muteTextButton addConstraints:@[muteTextW,muteTextH]];
    }
}

- (void)backButton:(UIButton *)button {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)topButton:(UIButton *)button {
    if ([self.currentButton isEqual:button]) {
        return;
    }
    self.currentButton.selected = !self.currentButton.selected;
    button.selected = !button.selected;
    self.currentButton = button;
    if ([self.currentButton isEqual:self.onlineButton]) {
        self.currentArray = self.onlineArray;
    }else {
        self.currentArray = self.members;
    }
    self.lightLineLeft.constant = [self.currentButton isEqual:self.onlineButton] ? 0 : 140;
    [self reloadData];
}
- (void)reloadData {
    [self.tableView reloadData];
}
#pragma mark -
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.currentArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NEEduMemberCell *cell = [tableView dequeueReusableCellWithIdentifier:memberCellID forIndexPath:indexPath];
    NEEduMember *member = self.currentArray[indexPath.row];
    member.isInAllList = [self.currentButton isEqual:self.onlineButton]?NO:YES;
    cell.member = member;
    cell.delegate = self;
    return cell;
}

#pragma mark - NEEduMemberCellDelegate
- (void)didSeletedAudio:(BOOL)isSelected member:(NEEduMember *)member {
    if ([member.userID isEqualToString:[EduManager shared].localUser.userUuid]) {
        __weak typeof(self) weakSelf = self;
        [[EduManager shared].userService localUserAudioEnable:!isSelected result:^(NSError * _Nonnull error) {
            if (error) {
                [weakSelf.view makeToast:error.localizedDescription];
            }else {
                [weakSelf.view makeToast:@"操作成功"];
            }
        }];
    }else {
        __weak typeof(self) weakSelf = self;
        [[EduManager shared].userService remoteUserAudioEnable:!isSelected userID:member.userID result:^(NSError * _Nonnull error) {
            if (error) {
                [weakSelf.view makeToast:error.localizedDescription];
            }else {
                [weakSelf.view makeToast:@"操作成功"];
            }
        }];
    }
}
- (void)didSeletedVideo:(BOOL)isSelected member:(NEEduMember *)member {
    if ([member.userID isEqualToString:[EduManager shared].localUser.userUuid]) {
        [[EduManager shared].userService localUserVideoEnable:!isSelected result:^(NSError * _Nonnull error) {
        }];
    }else {
        [[EduManager shared].userService remoteUserVideoEnable:!isSelected userID:member.userID result:^(NSError * _Nonnull error) {
        }];
    }
}
- (void)cell:(NEEduMemberCell *)cell didSeletedMore:(BOOL)isSelected member:(NEEduMember *)member {
    [self showAlertViewWithMember:member onCell:cell];
}
- (void)showAlertViewWithMember:(NEEduMember *)member onCell:(NEEduMemberCell *)cell {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:@"更多操作" preferredStyle:UIAlertControllerStyleActionSheet];
    NSString *title = member.whiteboardEnable ? @"取消白板权限":@"授予白板权限";
    NSString *shareTitle = member.shareScreenEnable ? @"取消共享权限":@"授予共享权限";
    __weak typeof(self)weakSelf = self;
    UIAlertAction *action = [UIAlertAction actionWithTitle:title style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        if ([EduManager shared].profile.snapshot.room.states.step.value != NEEduLessonStateClassIn) {
            [weakSelf.view makeToast:@"还未开始上课"];
            return;
        }
        //授予/取消白板权限
        [[EduManager shared].userService whiteboardDrawable:!member.whiteboardEnable userID:member.userID result:^(NSError * _Nonnull error) {
            if (error) {
                [weakSelf.view makeToast:error.localizedDescription];
            }
        }];
    }];
    UIAlertAction *actionSecond = [UIAlertAction actionWithTitle:shareTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        //授予/取消共享权限
        if ([EduManager shared].profile.snapshot.room.states.step.value != NEEduLessonStateClassIn) {
            [weakSelf.view makeToast:@"还未开始上课"];
            return;
        }
//        __weak typeof(self)weakSelf = self;
        [[EduManager shared].userService screenShareAuthorization:!member.shareScreenEnable userID:member.userID result:^(NSError * _Nonnull error) {
            if (error) {
                [weakSelf.view makeToast:error.localizedDescription];
            }
        }];
    }];
    [alert addAction:action];
    [alert addAction:actionSecond];
    
    //如果是老师且在台上
    if ([EduManager shared].localUser.roleType == NEEduRoleTypeTeacher && member.online) {
        UIAlertAction *actionHandsup = [UIAlertAction actionWithTitle:@"请他下台" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [weakSelf.view showAlertViewOnVC:weakSelf withTitle:@"请他下台" subTitle:@"结束该学生的上台动作，同时回收他的屏幕共享、白板权限" confirm:^{
                //老师操作下台
//                __weak typeof(self)weakSelf = self;
                [[EduManager shared].userService handsupStateChange:NEEduHandsupStateTeaOffStage userID:member.userID result:^(NSError * _Nonnull error) {
                    if (!error) {
                        [weakSelf.view makeToast:@"操作成功"];
                    }else {
                        [weakSelf.view makeToast:error.localizedDescription];
                    }
                }];
            }];
        }];
        [alert addAction:actionHandsup];
    }else {
        
    }
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    }];
    [alert addAction:cancelAction];

    UIPopoverPresentationController *popover = alert.popoverPresentationController;
    if (popover) {
        popover.sourceView = cell.moreButton;
        popover.permittedArrowDirections = UIPopoverArrowDirectionAny;
    }
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)muteAudioButtonClick:(UIButton *)button {
    __weak typeof(self)weakSelf = self;
    [[EduManager shared].roomService muteAll:YES completion:^(NSError * _Nonnull error, NEEduPropertyItem * _Nonnull item) {
        if (error) {
            [weakSelf.view makeToast:error.localizedDescription];
        }else {
            [weakSelf.view makeToast:@"操作成功"];
        }
    }];
}
- (void)muteTextButtonClick:(UIButton *)button {
    button.selected = !button.selected;
    __weak typeof(self)weakSelf = self;
    [[EduManager shared].roomService muteAllText:button.selected completion:^(NSError * _Nonnull error, NEEduPropertyItem * _Nonnull item) {
        if (error) {
            [weakSelf.view makeToast:error.localizedDescription];
        }else {
            [weakSelf.view makeToast:@"操作成功"];
        }
    }];
}

- (UIButton *)backButton {
    if (!_backButton) {
        _backButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_backButton setImage:[UIImage ne_imageNamed:@"room_down"] forState:UIControlStateNormal];
        [_backButton addTarget:self action:@selector(backButton:) forControlEvents:UIControlEventTouchUpInside];
        _backButton.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _backButton;
}

- (UIButton *)onlineButton {
    if (!_onlineButton) {
        _onlineButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_onlineButton setTitle:@"连线成员" forState:UIControlStateNormal];
        _onlineButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_onlineButton addTarget:self action:@selector(topButton:) forControlEvents:UIControlEventTouchUpInside];
        [_onlineButton setTitleColor:[UIColor colorWithRed:74/255.0 green:86/255.0 blue:101/255.0 alpha:1.0] forState:UIControlStateNormal];
        [_onlineButton setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    }
    return _onlineButton;
}
- (UIButton *)totalButton {
    if (!_totalButton) {
        _totalButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_totalButton setTitle:@"课堂成员" forState:UIControlStateNormal];
        [_totalButton addTarget:self action:@selector(topButton:) forControlEvents:UIControlEventTouchUpInside];
        _totalButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_totalButton setTitleColor:[UIColor colorWithRed:74/255.0 green:86/255.0 blue:101/255.0 alpha:1.0] forState:UIControlStateNormal];
        [_totalButton setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    }
    return _totalButton;
}
- (UIView *)lineView {
    if (!_lineView) {
        _lineView = [[UIView alloc] init];
        _lineView.translatesAutoresizingMaskIntoConstraints = NO;
        _lineView.backgroundColor = [UIColor colorWithRed:52/255.0 green:61/255.0 blue:73/255.0 alpha:1.0];
    }
    return _lineView;
}
- (UIView *)lightLineView {
    if (!_lightLineView) {
        _lightLineView = [[UIView alloc] init];
        _lightLineView.translatesAutoresizingMaskIntoConstraints = NO;
        _lightLineView.backgroundColor = [UIColor whiteColor];
    }
    return _lightLineView;
}
- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView.translatesAutoresizingMaskIntoConstraints = NO;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.rowHeight = 52;
        _tableView.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    }
    return _tableView;
}
- (UIButton *)muteAudioButton {
    if (!_muteAudioButton) {
        _muteAudioButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _muteAudioButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_muteAudioButton setTitle:@"全体静音" forState:UIControlStateNormal];
        [_muteAudioButton setImage:[UIImage ne_imageNamed:@"member_mute_all"] forState:UIControlStateNormal];
        [_muteAudioButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _muteAudioButton.titleEdgeInsets = UIEdgeInsetsMake(0, 10, 0, 0);
        _muteAudioButton.titleLabel.font = [UIFont systemFontOfSize:16];
        [_muteAudioButton addTarget:self action:@selector(muteAudioButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _muteAudioButton;
}
- (UIButton *)muteTextButton {
    if (!_muteTextButton) {
        _muteTextButton = [UIButton buttonWithType:UIButtonTypeCustom];
        _muteTextButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_muteTextButton setTitle:@"聊天室全体禁言" forState:UIControlStateNormal];
        [_muteTextButton setTitle:@"聊天室解除禁言" forState:UIControlStateSelected];
        [_muteTextButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _muteTextButton.titleLabel.font = [UIFont systemFontOfSize:16];
        _muteTextButton.titleEdgeInsets = UIEdgeInsetsMake(0, 10, 0, 0);
        [_muteTextButton setImage:[UIImage ne_imageNamed:@"room_mute_text"] forState:UIControlStateNormal];
        [_muteTextButton setImage:[UIImage ne_imageNamed:@"room_mute_text_select"] forState:UIControlStateSelected];
        [_muteTextButton addTarget:self action:@selector(muteTextButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _muteTextButton;
}
#pragma mark - Orientations
-(BOOL)shouldAutorotate {
    return NO;
}
- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
     return UIInterfaceOrientationMaskLandscapeRight;
}
- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return UIInterfaceOrientationLandscapeRight;
}

@end
