//
//  NMCCallNativeMethodMessageHandler.m
//  BlockFo
//
//  Created by taojinliang on 2019/5/29.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#import "NMCCallNativeMethodMessageHandler.h"
#import "NMCWebViewHeader.h"
#import "NMCMessageHandlerDispatch.h"

@implementation NMCCallNativeMethodMessageHandler

#pragma mark - WKScriptMessageHandler
- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    //获取到js脚本传过来的参数
    NSMutableDictionary *params = [[NSMutableDictionary alloc] initWithDictionary:message.body];
    NSString *action = params[NMCMethodAction];
    NSDictionary *param = params[NMCMethodParam];
    [[NMCMessageHandlerDispatch sharedManager] webCallNativeWithWebView:message.webView action:action param:param];
}
@end
