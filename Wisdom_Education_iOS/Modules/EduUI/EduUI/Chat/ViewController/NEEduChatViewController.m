//
//  NEEduChatViewController.m
//  EduUI
//
//  Created by Groot on 2021/5/24.
//

#import "NEEduChatViewController.h"
#import "UIImage+NE.h"
#import "NEEduChatInputView.h"
#import <EduLogic/EduLogic.h>
#import "UIView+Toast.h"
#import "NEEduChatLeftCell.h"
#import "NEEduChatRightCell.h"
#import <EduLogic/EduLogic.h>
#import "NSString+NE.h"
#import "NEEduChatTimeCell.h"
@interface NEEduChatViewController ()<UITableViewDelegate,UITableViewDataSource,NEEduIMChatDelegate,NEEduChatBaseCellDelegate>
@property (nonatomic, strong) UIButton *backButton;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIView *topLine;
@property (nonatomic, strong) UITableView *tableView;

@property (nonatomic, strong) UIView *bottomLine;
@property (nonatomic, strong) NEEduChatInputView *inputView;
@property (nonatomic, strong) NSLayoutConstraint *inputBottom;
@property (nonatomic, strong) NSDate *lastMsgDate;
@end

static NSString *leftCell = @"leftCellID";
static NSString *rightCell = @"rightCellID";
static NSString *timeCell = @"timeCellID";

@implementation NEEduChatViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    self.titleLabel.text = @"聊天室";
    [self setupSubviews];
    [self.inputView updateUIWithMute:self.muteChat];
    [self.tableView registerClass:[NEEduChatLeftCell class] forCellReuseIdentifier:leftCell];
    [self.tableView registerClass:[NEEduChatRightCell class] forCellReuseIdentifier:rightCell];
    [self.tableView registerClass:[NEEduChatTimeCell class] forCellReuseIdentifier:timeCell];

//    [EduManager shared].imService.chatDelegate = self;
    [self addNotification];
}
- (void)loadData {
    
}
- (void)updateMuteChat:(BOOL)muteChat {
    self.muteChat = muteChat;
    if ([EduManager shared].localUser.roleType != NEEduRoleTypeTeacher) {
        [self.inputView updateUIWithMute:muteChat];
    }
}
- (void)setupSubviews {
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
    [self.view addSubview:self.bottomLine];
    [self.view addSubview:self.inputView];
    
    NSLayoutConstraint *tableViewLeft = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *tableViewRight = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *tableViewTop = [NSLayoutConstraint constraintWithItem:self.tableView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.topLine attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.view addConstraints:@[tableViewLeft,tableViewRight,tableViewTop]];

    NSLayoutConstraint *bottomLineLeft = [NSLayoutConstraint constraintWithItem:self.bottomLine attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *bottomLineRight = [NSLayoutConstraint constraintWithItem:self.bottomLine attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTrailing multiplier:1.0 constant:0];
    NSLayoutConstraint *bottomLineTop = [NSLayoutConstraint constraintWithItem:self.bottomLine attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.tableView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *bottomLineBottom = [NSLayoutConstraint constraintWithItem:self.bottomLine attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.inputView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *bottomLineHeight = [NSLayoutConstraint constraintWithItem:self.bottomLine attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:1];
    [self.view addConstraints:@[bottomLineLeft,bottomLineRight,bottomLineTop,bottomLineBottom]];
    [self.bottomLine addConstraint:bottomLineHeight];
    
    NSLayoutConstraint *inputLeft = [NSLayoutConstraint constraintWithItem:self.inputView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *inputRight = [NSLayoutConstraint constraintWithItem:self.inputView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    self.inputBottom = [NSLayoutConstraint constraintWithItem:self.inputView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    NSLayoutConstraint *inputHeight = [NSLayoutConstraint constraintWithItem:self.inputView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:80];
    [self.view addConstraints:@[inputLeft,inputRight,self.inputBottom]];
    [self.inputView addConstraint:inputHeight];

}
- (void)addNotification {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHidden:) name:UIKeyboardWillHideNotification object:nil];
}
- (void)removeNotification {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (void)keyboardWillShow:(NSNotification *)notification {
    CGRect frame = [[[notification userInfo] objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    float bottom = frame.size.height;
    double duration = [[notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    self.inputBottom.constant = - bottom;
    [UIView animateWithDuration:duration animations:^{
        [self.view layoutIfNeeded];
    }];
}

- (void)keyboardWillHidden:(NSNotification *)notification {
    double duration = [[notification.userInfo objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    self.inputBottom.constant = 0;
    [UIView animateWithDuration:duration animations:^{
        [self.view layoutIfNeeded];
    }];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.view endEditing:YES];
}
- (void)reloadTableViewToBottom:(BOOL)bottom {
    [self.tableView reloadData];
    if (bottom && self.messages.count > 0) {
        [self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:[self.messages count] - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }
}

#pragma mark - NEEduChatBaseCellDelegate
- (void)chatCell:(NEEduChatBaseCell *)cell didLongPressMessage:(NEEduChatMessage *)message {
    UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"" message:@"更多操作" preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction *copyAction = [UIAlertAction actionWithTitle:@"复制" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        if (message.content) {
            UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
            pasteboard.string = message.content;
            [self.view makeToast:@"复制成功"];
        }
    }];
    
    [alertVC addAction:copyAction];
    
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    }];
    [alertVC addAction:cancelAction];
    
    if (alertVC.popoverPresentationController) {
        alertVC.popoverPresentationController.sourceView = cell.contentLabel;
        alertVC.popoverPresentationController.permittedArrowDirections = UIPopoverArrowDirectionAny;
    }
    [self presentViewController:alertVC animated:YES completion:nil];
}
#pragma mark -
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.messages.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NEEduChatMessage *message = self.messages[indexPath.row];
    if (message.type == NIMMessageTypeText) {
        NSString *cellID = message.myself ? rightCell : leftCell;
        NEEduChatBaseCell *cell = [tableView dequeueReusableCellWithIdentifier:cellID forIndexPath:indexPath];
        cell.model = message;
        cell.delegate = self;
        return cell;
    }else {
        NEEduChatBaseCell *cell = [tableView dequeueReusableCellWithIdentifier:timeCell forIndexPath:indexPath];
        cell.model = message;
        return cell;
    }
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    NEEduChatMessage *message = self.messages[indexPath.row];
    if (message.type == NIMMessageTypeText) {
        return message.textSize.height + 43 + 20;
    }else {
        return message.textSize.height + 20;
    }
}

#pragma mark - Event
- (void)sendButtonEvent:(UIButton *)button {
    if (self.inputView.textField.text.length <= 0) {
        return;
    }
    NSString *string = [self.inputView.textField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    if (string.length <= 0) {
        return;
    }
    NSError *error = nil;
    [[EduManager shared].imService sendChatroomTextMessage:self.inputView.textField.text error:&error];
    self.inputView.textField.text = @"";
    [self.inputView.textField resignFirstResponder];
}
- (BOOL)isValidLessonId:(NSString *)lessonId {
    if (lessonId.length > 11 ) {
        return NO;
    }
    NSString *string = [lessonId stringByTrimmingCharactersInSet:[NSCharacterSet decimalDigitCharacterSet]];
    string = [string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    return string.length ? NO : YES;
}

- (void)backButton:(UIButton *)button {
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc]initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.translatesAutoresizingMaskIntoConstraints = NO;
        _tableView.estimatedRowHeight = 0;
        _tableView.estimatedSectionFooterHeight = 0;
        _tableView.estimatedSectionHeaderHeight = 0;
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
        _titleLabel.text = @"聊天室";
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
- (UIView *)bottomLine {
    if (!_bottomLine) {
        _bottomLine = [[UIView alloc] init];
        _bottomLine.backgroundColor = [UIColor colorWithRed:52/255.0 green:61/255.0 blue:73/255.0 alpha:1.0];
        _bottomLine.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _bottomLine;
}

- (NEEduChatInputView *)inputView {
    if (!_inputView) {
        _inputView = [[NEEduChatInputView alloc] init];
        [_inputView.sendButton addTarget:self action:@selector(sendButtonEvent:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _inputView;
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
- (void)dealloc
{
    [self removeNotification];
}
@end
