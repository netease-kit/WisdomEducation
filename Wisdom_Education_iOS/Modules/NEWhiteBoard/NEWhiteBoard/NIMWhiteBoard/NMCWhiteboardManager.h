//
//  NMCWhiteboardManager.h
//  BlockFo
//
//  Created by taojinliang on 2019/5/30.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>
#import "NMCWebLoginParam.h"
#import "NMCWhiteboardManagerProtocol.h"
#import "NMCTool.h"

NS_ASSUME_NONNULL_BEGIN


extern NSString *const NMCWhiteboardURL;

@interface NMCWhiteboardManager : NSObject

@property(nonatomic, weak) id<NMCWhiteboardManagerDelegate> delegate;
/// 配置文件 参数读取开关
@property (nonatomic, assign, getter=isConfigRead) BOOL configRead;
+ (instancetype)sharedManager;

- (WKWebView *)createWebViewFrame:(CGRect)frame;

/**
 调用web登录，再页面加载之后

 @param loginParam 登录参数
 */
- (void)callWebJoinRoom:(NMCWebLoginParam *)loginParam;

/// 发送Auth信息

- (void)sendAuthNonce:(NSString *)nonce curTime:(NSString *)curTime checksum:(NSString *)checksum;
/**
 调用web退出登录，当不再使用白板之后
 */
- (void)callWebLogoutIM;

/**
 设置白板是否可以绘制

 @param enable 是否可用
 */
- (void)callEnableDraw:(BOOL)enable;
/**
 设置 视频上传配置
 
 @param presetId 转码模板id
 */
- (void)setAppConfigWithPresetId:(NSNumber *)presetId;
/**
 设置白板颜色

 @param color 白板颜色
 */
- (void)setWhiteboardColor:(NSString *)color;

/// 展示/隐藏白板工具栏
/// @param hidden 默认为 NO
- (void)hiddenTools:(BOOL)hidden;

- (void)setupWhiteboardTools:(NSArray <NMCTool *> *)tools;
//- (void)setTools:(NSArray *)tools position:(ToolViewPosition)position;
/**
 清理webview缓存

 @param color 白板颜色
 */
- (void)clearWebViewCache;
@end

NS_ASSUME_NONNULL_END
