//
//  NESettingTableViewController.m
//  WisdomEducation
//
//  Created by 郭园园 on 2021/9/2.
//  Copyright © 2021 NetEase. All rights reserved.
//

#import "NESettingTableViewController.h"
#import "IMLoginVC.h"
#import "NEAppInfo.h"

@interface NESettingTableViewController ()
@property (weak, nonatomic) IBOutlet UISwitch *useChatroom;
@property (weak, nonatomic) IBOutlet UISwitch *useFastLive;
@property (weak, nonatomic) IBOutlet UILabel *versionLabel;
@property (weak, nonatomic) IBOutlet UISwitch *pushStream;

@end

@implementation NESettingTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"设置";
    self.tableView.tableFooterView = [UIView new];
    BOOL useChatroom = [[[NSUserDefaults standardUserDefaults] objectForKey:showChatroomKey] boolValue];
    BOOL useFastLive = [[[NSUserDefaults standardUserDefaults] objectForKey:useFastLiveKey] boolValue];
    BOOL pushStream =  [[[NSUserDefaults standardUserDefaults] objectForKey:pushStreamKey] boolValue];
    [self.useChatroom setOn:useChatroom];
    [self.useFastLive setOn:useFastLive];
    [self.pushStream setOn:pushStream];
    self.versionLabel.text = [NSString stringWithFormat:@"版本:%@ (%@)",[NEAppInfo appVersion],[NEAppInfo buildVersion]];
}
- (void)viewWillAppear:(BOOL)animated {
    [self.navigationController setNavigationBarHidden:NO];
}

#pragma mark - Event

- (IBAction)switchEvent:(UISwitch *)sender {
    [[NSUserDefaults standardUserDefaults] setObject:@(sender.isOn) forKey:showChatroomKey];
}

- (IBAction)userFastLive:(UISwitch *)sender {
    [[NSUserDefaults standardUserDefaults] setObject:@(sender.isOn) forKey:useFastLiveKey];
}
- (IBAction)pushStream:(UISwitch *)sender {
    [[NSUserDefaults standardUserDefaults] setObject:@(sender.isOn) forKey:pushStreamKey];
}

#pragma mark - Table view data source

//- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
//    return 1;
//}
//
//- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
//    return 1;
//}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row == 1) {
        IMLoginVC *vc = [[IMLoginVC alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}


/*
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:<#@"reuseIdentifier"#> forIndexPath:indexPath];
    
    // Configure the cell...
    
    return cell;
}
*/

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
