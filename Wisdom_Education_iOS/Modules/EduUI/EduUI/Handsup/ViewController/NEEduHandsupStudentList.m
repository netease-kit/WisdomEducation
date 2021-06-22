//
//  NEEduHandsupStudentList.m
//  EduUI
//
//  Created by Groot on 2021/6/3.
//

#import "NEEduHandsupStudentList.h"
#import "UIImage+NE.h"
#import "NEEduHandsupApplyCell.h"
#import <EduLogic/EduLogic.h>
#import "UIView+Toast.h"

@interface NEEduHandsupStudentList ()<UITableViewDelegate,UITableViewDataSource,NEEduHandsupApplyCellDelegate>
@property (nonatomic, strong) UIButton *backButton;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIView *topLine;
//@property (nonatomic, strong) NSArray<NEEduHttpUser *> *applyArray;
@end
static NSString *handsupCell = @"handsupCell";
@implementation NEEduHandsupStudentList

- (void)viewDidLoad {
    [super viewDidLoad];
//    [self loadData];
    [self setupSubviews];
    [self.tableView registerClass:[NEEduHandsupApplyCell class] forCellReuseIdentifier:handsupCell];
}
//- (void)loadData {
//    NSPredicate *predicate = [NSPredicate predicateWithBlock:^BOOL(NEEduHttpUser *  _Nullable evaluatedObject, NSDictionary<NSString *,id> * _Nullable bindings) {
//        return evaluatedObject.properties.avHandsUp.value = NEEduHandsupStateApply;
//    }];
//    self.applyArray = [self.applyStudents filteredArrayUsingPredicate:predicate];
//
//}
- (void)setupSubviews {
    self.view.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];

    [self.view addSubview:self.backButton];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:40];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:44];
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:self.backButton attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:48];
    [self.view addConstraints:@[left,top]];
    [self.backButton addConstraints:@[width,height]];
    
    [self.view addSubview:self.titleLabel];
    NSLayoutConstraint *titleLeft = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:80];
    NSLayoutConstraint *titleTop = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *titleHeight = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:48];
    NSLayoutConstraint *titleRight = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:-80];
    [self.view addConstraints:@[titleLeft,titleTop,titleRight]];
    [self.titleLabel addConstraint:titleHeight];
    
    [self.view addSubview:self.topLine];
    NSLayoutConstraint *lineLeft = [NSLayoutConstraint constraintWithItem:self.topLine attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *lineRight = [NSLayoutConstraint constraintWithItem:self.topLine attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *lineTop = [NSLayoutConstraint constraintWithItem:self.topLine attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.titleLabel attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *lineHeight = [NSLayoutConstraint constraintWithItem:self.topLine attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:1];
    [self.view addConstraints:@[lineLeft,lineRight,lineTop]];
    [self.topLine addConstraint:lineHeight];
    
    [self.view addSubview:self.tableView];
    NSLayoutConstraint *tableViewLeft = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:136];
    NSLayoutConstraint *tableViewRight = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:-136];
    NSLayoutConstraint *tableViewTop = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.topLine attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *tableViewBottom = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.view addConstraints:@[tableViewLeft,tableViewRight,tableViewTop,tableViewBottom]];

}
#pragma mark - NEEduHandsupApplyCellDelegate
- (void)agreeHansupApplyWithMember:(NEEduHttpUser *)member {
    //http 请求
    [[EduManager shared].userService handsupStateChange:NEEduHandsupStateTeaAccept userID:member.userUuid result:^(NSError * _Nonnull error) {
        if (!error) {
            [self.view makeToast:@"操作成功"];
            member.properties.avHandsUp.value = NEEduHandsupStateTeaAccept;
            [self.applyStudents removeObject:member];
            [self.tableView reloadData];
            if (self.delegate && [self.delegate respondsToSelector:@selector(didAgreeWithMember:)]) {
                [self.delegate didAgreeWithMember:member];
            }
        }else {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)disagreeHansupApplyWithMember:(NEEduHttpUser *)member {
    //http 请求
    [[EduManager shared].userService handsupStateChange:NEEduHandsupStateTeaReject userID:member.userUuid result:^(NSError * _Nonnull error) {
        if (!error) {
            member.properties.avHandsUp.value = NEEduHandsupStateTeaReject;
            [self.applyStudents removeObject:member];
            [self.tableView reloadData];
            if (self.delegate && [self.delegate respondsToSelector:@selector(didDisAgreeWithMember:)]) {
                [self.delegate didDisAgreeWithMember:member];
            }
        }else {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)backButton:(UIButton *)button {
    [self dismissViewControllerAnimated:YES completion:nil];
}
#pragma mark - UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.applyStudents.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NEEduHandsupApplyCell *cell = [tableView dequeueReusableCellWithIdentifier:handsupCell forIndexPath:indexPath];
    cell.delegate = self;
    cell.member = self.applyStudents[indexPath.row];
    return cell;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 52;
}

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc]initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.translatesAutoresizingMaskIntoConstraints = NO;
        _tableView.estimatedRowHeight = 83;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    }
    return _tableView;
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
- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.font = [UIFont systemFontOfSize:18];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.text = @"举手";
        _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
        _titleLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _titleLabel;
}
- (UIView *)topLine {
    if (!_topLine) {
        _topLine = [[UIView alloc] init];
        _topLine.backgroundColor = [UIColor colorWithRed:52/255.0 green:61/255.0 blue:73/255.0 alpha:1.0];
        _topLine.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _topLine;
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
