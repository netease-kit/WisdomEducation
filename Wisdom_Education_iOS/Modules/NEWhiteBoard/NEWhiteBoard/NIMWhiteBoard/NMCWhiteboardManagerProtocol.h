//
//  NMCWhiteboardManagerProtocol.h
//  BlockFo
//
//  Created by taojinliang on 2019/5/30.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#ifndef NMCWhiteboardManagerProtocol_h
#define NMCWhiteboardManagerProtocol_h

#import "NMCWebLoginParam.h"

@protocol NMCWhiteboardManagerDelegate <NSObject>

/**
 web页面加载完成
 */
- (void)onWebPageLoaded;

/**
 web登录IM成功,可以忽略
 */
- (void)onWebLoginIMSucceed;

/**
 web创建白板房间成功,可以忽略
 */
- (void)onWebCreateWBSucceed;

/**
 web加入白板房间成功,可以忽略
 */
- (void)onWebJoinWBSucceed;

/**
 提示web登录IM的错误及原因
 
 @param code 错误码
 @param error 具体错误信息
 */
- (void)onWebLoginIMFailed:(NSInteger)code error:(NSString *)error;

/**
 提示web加入白板房间的错误及原因
 
 @param code 错误码
 @param error 具体错误信息
 */
- (void)onWebJoinWBFailed:(NSInteger)code error:(NSString *)error;

/**
 提示web创建白板房间的错误及原因
 
 @param code 错误码
 @param error 具体错误信息
 */
- (void)onWebCreateWBFailed:(NSInteger)code error:(NSString *)error;

/**
 web离开白板房间
 */
- (void)onWebLeaveWB;

/**
 web发生了网络异常
 
 @param code 错误码
 @param error 具体错误信息
 */
- (void)onWebError:(NSInteger)code error:(NSString *)error;

/**
 web抛出Js错误
 
 @param error 具体错误信息
 */
- (void)onWebJsError:(NSString *)error;

@end

#endif /* NMCWhiteboardManagerProtocol_h */
