//
//  NMCMessageHandlerDispatch.m
//  BlockFo
//
//  Created by taojinliang on 2019/5/30.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#import "NMCMessageHandlerDispatch.h"
#import "NMCCallWebMethodMessageHandler.h"
#import "NMCWebViewHeader.h"


@implementation NMCMessageHandlerDispatch

+ (instancetype)sharedManager {
    static NMCMessageHandlerDispatch *manager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[NMCMessageHandlerDispatch alloc] init];
    });
    return manager;
}

- (void)nativeCallWebWithWebView:(WKWebView *)webview action:(NSString *)action param:(NSDictionary *)param
{
    [NMCCallWebMethodMessageHandler callWebMethodWithWebView:webview action:action param:param];
}

- (void)webCallNativeWithWebView:(WKWebView *)webview action:(NSString *)action param:(NSDictionary *)param
{
    
    if ([action isEqualToString: NMCMethodActionWebPageLoaded]){
        if (_delegate && [_delegate respondsToSelector:@selector(onWebPageLoaded)]) {
            [_delegate onWebPageLoaded];
        }
    }else if ([action isEqualToString: NMCMethodActionWebLoginSucceed]){
        if (_delegate && [_delegate respondsToSelector:@selector(onWebLoginIMSucceed)]) {
            [_delegate onWebLoginIMSucceed];
        }
    }else if ([action isEqualToString: NMCMethodActionWebCreateWBSucceed]){
        if (_delegate && [_delegate respondsToSelector:@selector(onWebCreateWBSucceed)]) {
            [_delegate onWebCreateWBSucceed];
        }
    }else if ([action isEqualToString: NMCMethodActionWebJoinWBSucceed]){
        if (_delegate && [_delegate respondsToSelector:@selector(onWebJoinWBSucceed)]) {
            [_delegate onWebJoinWBSucceed];
        }
    }else if ([action isEqualToString: NMCMethodActionWebLoginIMFailed]) {
        if (_delegate && [_delegate respondsToSelector:@selector(onWebLoginIMFailed:error:)]) {
            [_delegate onWebLoginIMFailed:[param[NMCMethodParamCode] integerValue] error:param[NMCMethodParamMsg]];
        }
    }else if ([action isEqualToString: NMCMethodActionWebJoinWBFailed]) {
        if (_delegate && [_delegate respondsToSelector:@selector(onWebLoginIMFailed:error:)]) {
            [_delegate onWebLoginIMFailed:[param[NMCMethodParamCode] integerValue] error:param[NMCMethodParamMsg]];
        }
    }else if ([action isEqualToString: NMCMethodActionWebCreateWBFailed]) {
        if (_delegate && [_delegate respondsToSelector:@selector(onWebCreateWBFailed:error:)]) {
            [_delegate onWebCreateWBFailed:[param[NMCMethodParamCode] integerValue] error:param[NMCMethodParamMsg]];
        }
    }else if ([action isEqualToString: NMCMethodActionWebLeaveWB]) {
        if (_delegate && [_delegate respondsToSelector:@selector(onWebLeaveWB)]) {
            [_delegate onWebLeaveWB];
        }
    }else if ([action isEqualToString: NMCMethodActionWebError]) {
        if (_delegate && [_delegate respondsToSelector:@selector(onWebError:error:)]) {
            [_delegate onWebError:[param[NMCMethodParamCode] integerValue] error:param[NMCMethodParamMsg]];
        }
    }else if ([action isEqualToString: NMCMethodActionWebJSError]) {
        if (_delegate && [_delegate respondsToSelector:@selector(onWebJsError:)]) {
            [_delegate onWebJsError:param[NMCMethodParamMsg]];
        }
    }else if ([action isEqualToString:NMCMethodActionWebLog]) {
        NSString *logType = [param objectForKey:@"type"];
        if ([logType isEqualToString:@"error"]) {
            NSLog(@"webLog:%@",param[@"msg"]);
        }
    }
    
}

@end
