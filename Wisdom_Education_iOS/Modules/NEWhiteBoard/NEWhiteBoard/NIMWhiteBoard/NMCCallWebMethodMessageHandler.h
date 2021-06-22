//
//  NMCCallWebMethodMessageHandler.h
//  BlockFo
//
//  Created by taojinliang on 2019/5/29.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface NMCCallWebMethodMessageHandler : NSObject

+ (void)callWebMethodWithWebView:(WKWebView *)webview action:(NSString *)action param:(NSDictionary *)param;

@end

NS_ASSUME_NONNULL_END
