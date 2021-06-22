//
//  EBaseViewController.m
//  NEEducation
//
//  Created by Netease on 2021/1/18.
//

#import "NEBaseViewController.h"

@interface NEBaseViewController ()

@end

@implementation NEBaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

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
