//
//  ViewController.m
//  WisdomEducation
//
//  Created by Groot on 2021/4/21.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "ViewController.h"
#import <EduLogic/EduLogic.h>
@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor redColor];
//    [self initialRtc];
//    [self join];
    
}
//- (void)initialRtc {
//    NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
//    // 打开推流,回调摄像头采集数据
//    NSDictionary *params = @{
//        kNERtcKeyPublishSelfStreamEnabled: @YES,    // 打开推流
//        kNERtcKeyVideoCaptureObserverEnabled: @YES  // 将摄像头采集的数据回调给用户
//    };
//    [coreEngine setClientRole:kNERtcClientRoleBroadcaster];
//    [coreEngine setParameters:params];
//    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
//    context.engineDelegate = self;
//    context.appKey = @"4e67e6aca27aaf3db54d17089b24274e";
//    int res = [coreEngine setupEngineWithContext:context];
////    YXAlogInfo(@"观众NERtc初始化设置 NERtcEngine, res: %d", res);
//    // 启用本地音/视频
//    [coreEngine enableLocalAudio:YES];
//    [coreEngine enableLocalVideo:YES];
//}
//- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
//    NSLog(@"touch");
//    [[NERtcEngine sharedEngine] leaveChannel];
//    [self.navigationController popViewControllerAnimated:YES];
//}
//- (void)join {
//    long uid = random() % 100;
//    NSLog(@"uid:%d",uid);
//    [[NERtcEngine sharedEngine] joinChannelWithToken:@"388d2964144dec1fd6cda17abe4da3eab9c7cde6" channelName:@"112" myUid:uid completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
//        NSLog(@"join error:%@",error);
//    }];
//}
- (void)dealloc
{
    NSLog(@"%@ %s",[self class],__func__);
}

@end
