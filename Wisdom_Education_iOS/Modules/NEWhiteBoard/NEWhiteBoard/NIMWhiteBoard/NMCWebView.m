//
//  NMCWebView.m
//  BlockFo
//
//  Created by taojinliang on 2019/5/29.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#import "NMCWebView.h"
#import "NMCCallNativeMethodMessageHandler.h"
#import "NMCWebViewHeader.h"

@implementation NMCWebView

- (instancetype)initWithFrame:(CGRect)frame{
    WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
//    config.allowsInlineMediaPlayback = YES;
//    config.mediaTypesRequiringUserActionForPlayback = WKAudiovisualMediaTypeNone;
//    config.requiresUserActionForMediaPlayback = NO;
//    config.mediaPlaybackRequiresUserAction = NO;
    
    //3.视频播放相关
   {
       if ([config respondsToSelector:@selector(setAllowsInlineMediaPlayback:)]) {
           [config setAllowsInlineMediaPlayback:YES];
       }
       
       //视频播放
       if (@available(iOS 10.0, *)) {
           if ([config respondsToSelector:@selector(setMediaTypesRequiringUserActionForPlayback:)]){
               [config setMediaTypesRequiringUserActionForPlayback:WKAudiovisualMediaTypeNone];
           }
       } else if (@available(iOS 9.0, *)) {
           if ( [config respondsToSelector:@selector(setRequiresUserActionForMediaPlayback:)]) {
               [config setRequiresUserActionForMediaPlayback:NO];
           }
       } else {
           if ( [config respondsToSelector:@selector(setMediaPlaybackRequiresUserAction:)]) {
               [config setMediaPlaybackRequiresUserAction:NO];
           }
       }
   }
    
    self = [super initWithFrame:frame configuration:config];
    if(self){
        [self config];
    }
    return self;
}

#pragma mark - Configuration
- (void)config {
    //0.UI
    {
        self.backgroundColor = [UIColor clearColor];
        self.scrollView.backgroundColor = [UIColor clearColor];
    }
    
    //1.注入脚本
    {
        NSString *bundlePath = [[NSBundle bundleForClass:self.class] pathForResource:@"NMCWebView" ofType:@"bundle"];
        
        NSString *scriptPath = [NSString stringWithFormat:@"%@/%@",bundlePath, @"NMCJSBridge.js"];
        
        NSString *bridgeJSString = [[NSString alloc] initWithContentsOfFile:scriptPath encoding:NSUTF8StringEncoding error:NULL];
        
        WKUserScript *userScript = [[WKUserScript alloc] initWithSource:bridgeJSString injectionTime:WKUserScriptInjectionTimeAtDocumentStart forMainFrameOnly:NO];
        
        [self.configuration.userContentController addUserScript:userScript];
    }
    
    //2.指定MessageHandler
    {
        [self.configuration.userContentController addScriptMessageHandler:[[NMCCallNativeMethodMessageHandler alloc] init] name:NMCNativeMethodMessage];
    }
    
    
    if (@available(iOS 11.0, *)) {
        //http://guokelide.com/2018/03/13/%E7%AC%AC3%E7%AF%87-Wkwebview%E9%80%82%E9%85%8DiPhoneX%E8%B8%A9%E5%9D%91%E8%AE%B0/
        self.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    }
    
//    NSLog(@"---> before config : %lu, %d", (unsigned long)self.configuration.mediaTypesRequiringUserActionForPlayback, self.configuration.allowsInlineMediaPlayback);

   
    
//    NSLog(@"---> after config : %lu, %d", (unsigned long)self.configuration.mediaTypesRequiringUserActionForPlayback, self.configuration.allowsInlineMediaPlayback);
}


- (void)dealloc{
    //清除handler
    [self.configuration.userContentController removeScriptMessageHandlerForName:NMCNativeMethodMessage];
    
    //清除UserScript
    [self.configuration.userContentController removeAllUserScripts];
    
    //停止加载
    [self stopLoading];

    //清空相关delegate
    [super setUIDelegate:nil];
    [super setNavigationDelegate:nil];
}
@end
