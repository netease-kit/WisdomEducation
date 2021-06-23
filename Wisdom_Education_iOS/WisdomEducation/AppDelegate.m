//
//  AppDelegate.m
//  WisdomEducation
//
//  Created by Groot on 2021/4/21.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "AppDelegate.h"
//#import <EduUI/EduUI.h>
#import "EnterLessonViewController.h"
#import "ENavigationViewController.h"
#import <Bugly/Bugly.h>

@interface AppDelegate ()

@end

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    EnterLessonViewController *enterLessonVC = [[EnterLessonViewController alloc] init];
    ENavigationViewController *navgationViewController = [[ENavigationViewController alloc] initWithRootViewController:enterLessonVC];
    self.window.rootViewController = navgationViewController;
    [self.window makeKeyAndVisible];
    [Bugly startWithAppId:@"d35c7e8098"];
    return YES;
}


@end
