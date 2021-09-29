//
//  NEEduLiveMembersVC.m
//  EduUI
//
//  Created by 郭园园 on 2021/9/16.
//

#import "NEEduLiveMembersVC.h"
#import "NELiveMemberCell.h"
#import "UIImage+NE.h"
#import <EduLogic/EduLogic.h>
#import "NEEduMembersHeadView.h"

static NSString *const cellID = @"NELiveMemberCellID";
@interface NEEduLiveMembersVC ()<UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate>
@property (nonatomic, strong) UIButton *backButton;
@property (nonatomic, strong) UIButton *onlineButton;
@property (nonatomic, strong) UIView *lineView;
@property (nonatomic, strong) NSLayoutConstraint *onlineWidth;
@property (nonatomic, strong) NSLayoutConstraint *totalLeft;
@property (nonatomic, strong) NSLayoutConstraint *lightLineLeft;
@property (nonatomic, strong) NSMutableArray <NIMChatroomMember *>*members;
@property (nonatomic, strong) NEEduMembersHeadView *searchView;

@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSArray<NIMChatroomMember *> *searchArray;
@property (nonatomic, assign) bool isSearching;
@end

@implementation NEEduLiveMembersVC

- (void)addMember:(NIMChatroomMember *)member {
    // 过滤老师
    if (member.type == NIMChatroomMemberTypeManager || member.type == NIMChatroomMemberTypeCreator) {
        return;
    }
    for (NIMChatroomMember *existMember in self.members) {
        if ([existMember.userId isEqualToString:member.userId]) {
            return;
        }
    }
    [self.members addObject:member];
    if (self.isSearching) {
        self.searchArray = [self searchMembsersWithName:self.searchView.textField.text];
    }
    [self.tableView reloadData];
}

- (void)removeMember:(NIMChatroomMember *)member {
    NIMChatroomMember *target;
    for (NIMChatroomMember *existMember in self.members) {
        if ([existMember.userId isEqualToString:member.userId]) {
            target = existMember;
            break;
        }
    }
    [self.members removeObject:target];
    if (self.isSearching) {
        self.searchArray = [self searchMembsersWithName:self.searchView.textField.text];
    }
    [self.tableView reloadData];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupSubviews];
    [self getChatMemebers];
}

- (void)getChatMemebers {
    __weak typeof(self) weakSelf = self;
    [[NEEduManager shared].imService getChatroomMembers:self.room.properties.chatRoom.chatRoomId result:^(NSError * _Nonnull error, NSArray<NIMChatroomMember *> * _Nullable members) {
        __strong typeof(self) strongSelf = weakSelf;
        NSLog(@"error:%@ members:%@",error,members);
        if (error) {
            return;
        }
        strongSelf.members = [NSMutableArray array];
        for (NIMChatroomMember *member in members) {
            // 过滤老师
            if (member.type != NIMChatroomMemberTypeManager && member.type != NIMChatroomMemberTypeCreator) {
                [strongSelf.members addObject:member];
            }
        }
        [strongSelf.tableView reloadData];
    }];
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
    if (@available(iOS 11.0, *)) {
        [NSLayoutConstraint activateConstraints:@[
            [self.onlineButton.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor constant:0],
            [self.onlineButton.centerXAnchor constraintEqualToAnchor:self.view.centerXAnchor constant:0],
            [self.onlineButton.heightAnchor constraintEqualToConstant:48],
            [self.onlineButton.widthAnchor constraintEqualToConstant:140],
        ]];
    }else {
        [NSLayoutConstraint activateConstraints:@[
            [self.onlineButton.topAnchor constraintEqualToAnchor:self.view.topAnchor constant:0],
            [self.onlineButton.centerXAnchor constraintEqualToAnchor:self.view.centerXAnchor constant:0],
            [self.onlineButton.heightAnchor constraintEqualToConstant:48],
            [self.onlineButton.widthAnchor constraintEqualToConstant:140],
        ]];
    }
        
    [self.view addSubview:self.lineView];
    NSLayoutConstraint *lineLeft = [NSLayoutConstraint constraintWithItem:self.lineView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *lineRight = [NSLayoutConstraint constraintWithItem:self.lineView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *lineTop = [NSLayoutConstraint constraintWithItem:self.lineView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.onlineButton attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *lineHeight = [NSLayoutConstraint constraintWithItem:self.lineView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:1];
    [self.view addConstraints:@[lineLeft,lineRight,lineTop]];
    [self.lineView addConstraint:lineHeight];
    
    [self.view addSubview:self.searchView];
    NSLayoutConstraint *searchViewtop = [NSLayoutConstraint constraintWithItem:self.searchView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.lineView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *searchViewLeft = [NSLayoutConstraint constraintWithItem:self.searchView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1.0 constant:100];
    NSLayoutConstraint *searchViewRight = [NSLayoutConstraint constraintWithItem:self.searchView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:-100];
    NSLayoutConstraint *searchViewHeight = [NSLayoutConstraint constraintWithItem:self.searchView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:48];
    [self.view addConstraints:@[searchViewtop,searchViewLeft,searchViewRight]];
    [self.searchView addConstraint:searchViewHeight];
    
    [self.view addSubview:self.tableView];
    NSLayoutConstraint *tableTop = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.lineView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:48];
    NSLayoutConstraint *tableLeft = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1.0 constant:130];
    NSLayoutConstraint *tableRight = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:- 130];
    NSLayoutConstraint *tableBottom = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeBottom multiplier:1.0 constant:- 68];
    [self.view addConstraints:@[tableTop,tableLeft,tableRight,tableBottom]];
    [self.tableView registerClass:[NELiveMemberCell class] forCellReuseIdentifier:cellID];
}

- (void)backButtonEvent:(UIButton *)button {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)topButton:(UIButton *)button {
    
}

- (NSArray *)searchMembsersWithName:(NSString *)name {
    NSMutableArray *array = [NSMutableArray array];
    for (NIMChatroomMember *user in self.members) {
        if ([user.roomNickname localizedStandardContainsString:name]) {
            [array addObject:user];
        }
    }
    return array;
}

- (void)searchButtonEvent:(UIButton *)button {
    if (self.searchView.textField.text.length) {
        self.isSearching = YES;
        self.searchArray = [self searchMembsersWithName:self.searchView.textField.text];
    }else {
        self.isSearching = NO;
        self.searchArray = [NSArray array];
    }
    [self.tableView reloadData];
    [self.searchView.textField resignFirstResponder];
}

- (UIButton *)backButton {
    if (!_backButton) {
        _backButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_backButton setImage:[UIImage ne_imageNamed:@"room_down"] forState:UIControlStateNormal];
        [_backButton addTarget:self action:@selector(backButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
        _backButton.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _backButton;
}

- (UIButton *)onlineButton {
    if (!_onlineButton) {
        _onlineButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_onlineButton setTitle:@"课堂成员" forState:UIControlStateNormal];
        _onlineButton.translatesAutoresizingMaskIntoConstraints = NO;
        [_onlineButton addTarget:self action:@selector(topButton:) forControlEvents:UIControlEventTouchUpInside];
        [_onlineButton setTitleColor:[UIColor colorWithRed:74/255.0 green:86/255.0 blue:101/255.0 alpha:1.0] forState:UIControlStateNormal];
        [_onlineButton setTitleColor:[UIColor whiteColor] forState:UIControlStateSelected];
    }
    return _onlineButton;
}

- (UIView *)lineView {
    if (!_lineView) {
        _lineView = [[UIView alloc] init];
        _lineView.translatesAutoresizingMaskIntoConstraints = NO;
        _lineView.backgroundColor = [UIColor colorWithRed:52/255.0 green:61/255.0 blue:73/255.0 alpha:1.0];
    }
    return _lineView;
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

- (NEEduMembersHeadView *)searchView {
    if (!_searchView) {
        _searchView = [[NEEduMembersHeadView alloc] init];
        [_searchView.searchButton addTarget:self action:@selector(searchButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
        _searchView.textField.delegate = self;
    }
    return _searchView;
}
#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (self.isSearching) {
        return self.searchArray.count;
    } else {
        return self.members.count;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NELiveMemberCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID forIndexPath:indexPath];
    if (self.isSearching) {
        NIMChatroomMember *member = self.searchArray[indexPath.row];
        cell.nameLabel.text = member.roomNickname;
    } else {
        NIMChatroomMember *member = self.members[indexPath.row];
        cell.nameLabel.text = member.roomNickname;
    }
    return cell;
}

#pragma mark - UITextFieldDelegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if (textField.text.length) {
        self.isSearching = YES;
        self.searchArray = [self searchMembsersWithName:textField.text];
    } else {
        self.isSearching = NO;
        self.searchArray = self.members;
    }
    [self.tableView reloadData];
    [textField resignFirstResponder];
    return YES;
}

- (BOOL)textFieldShouldClear:(UITextField *)textField {
    self.isSearching = NO;
    self.searchArray = [NSArray array];
    [self.tableView reloadData];
    return YES;
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
