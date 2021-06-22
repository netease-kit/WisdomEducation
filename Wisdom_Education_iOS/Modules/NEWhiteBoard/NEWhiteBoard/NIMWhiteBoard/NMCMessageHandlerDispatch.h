//
//  NMCMessageHandlerDispatch.h
//  BlockFo
//
//  Created by taojinliang on 2019/5/30.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>
#import "NMCWhiteboardManagerProtocol.h"

NS_ASSUME_NONNULL_BEGIN

@interface NMCMessageHandlerDispatch : NSObject
@property(nonatomic, weak) id<NMCWhiteboardManagerDelegate> delegate;
+ (instancetype)sharedManager;
- (void)nativeCallWebWithWebView:(WKWebView *)webview action:(NSString *)action param:(NSDictionary *)param;
- (void)webCallNativeWithWebView:(WKWebView *)webview action:(NSString *)action param:(NSDictionary *)param;
@end

NS_ASSUME_NONNULL_END
