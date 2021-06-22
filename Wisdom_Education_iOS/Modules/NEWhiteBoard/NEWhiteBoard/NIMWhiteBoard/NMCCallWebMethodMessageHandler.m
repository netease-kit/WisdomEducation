//
//  NMCCallWebMethodMessageHandler.m
//  BlockFo
//
//  Created by taojinliang on 2019/5/29.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#import "NMCCallWebMethodMessageHandler.h"
#import "NMCWebViewHeader.h"


@implementation NMCCallWebMethodMessageHandler

+ (void)callWebMethodWithWebView:(WKWebView *)webview action:(NSString *)action param:(NSDictionary *)param
{
    if ( !action || action.length == 0 ) {
        return;
    }
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:action forKey:NMCMethodAction];
    if (param && param.count > 0) {
        [dict setObject:param forKey:NMCMethodParam];
    }
    
    NSString *bridgeObj = [NMCCallWebMethodMessageHandler jsonStringWithData:dict];
    NSString *callWebString = [NSString stringWithFormat:@"window.WebJSBridge('%@')", bridgeObj];
    NSLog(@"[webview] native call web ---> action : %@, param : %@", action, param);
    if ([[NSThread currentThread] isMainThread]) {
        [webview evaluateJavaScript:callWebString completionHandler:^(id _Nullable obj, NSError * _Nullable error) {
            if (error) {
                NSLog(@"[webview] error = %@",error);
            }
        }];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [webview evaluateJavaScript:callWebString completionHandler:^(id _Nullable obj, NSError * _Nullable error) {
                if (error) {
                    NSLog(@"[webview] error = %@",error);
                }
            }];
        });
    }
}

+ (NSString *)jsonStringWithData:(NSDictionary *)data {
    NSString *messageJSON = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:data options:0 error:NULL] encoding:NSUTF8StringEncoding];;
//    NSLog(@"messageJSON = %@",messageJSON);
//    messageJSON = [messageJSON stringByReplacingOccurrencesOfString:@"\\" withString:@"\\\\"];
//    messageJSON = [messageJSON stringByReplacingOccurrencesOfString:@"\"" withString:@"\\\""];
//    messageJSON = [messageJSON stringByReplacingOccurrencesOfString:@"\'" withString:@"\\\'"];
//    messageJSON = [messageJSON stringByReplacingOccurrencesOfString:@"\n" withString:@"\\n"];
//    messageJSON = [messageJSON stringByReplacingOccurrencesOfString:@"\r" withString:@"\\r"];
//    messageJSON = [messageJSON stringByReplacingOccurrencesOfString:@"\f" withString:@"\\f"];
//    messageJSON = [messageJSON stringByReplacingOccurrencesOfString:@"\u2028" withString:@"\\u2028"];
//    messageJSON = [messageJSON stringByReplacingOccurrencesOfString:@"\u2029" withString:@"\\u2029"];
//    NSLog(@"messageJSON = %@",messageJSON);

    return messageJSON;
}
@end
