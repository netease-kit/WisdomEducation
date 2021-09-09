//
//  NMCWebViewHeader.h
//  BlockFo
//
//  Created by taojinliang on 2019/5/29.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#ifndef NMCWebViewHeader_h
#define NMCWebViewHeader_h

#define NMCNativeMethodMessage @"NMCNativeMethodMessage"
#define NMCMethodAction @"action"
#define NMCMethodParam @"param"

#define NMCMethodParamCode @"code"
#define NMCMethodParamMsg @"msg"
#define NMCMethodParamEventName @"eventName"


//IM登录
#define NMCMethodActionWebJoin @"jsJoinWB"
// 发送授权信息
#define NMCMethodActionSendAuth @"jsSendAuth"

//设置白板是否可以绘制
#define NMCMethodActionEnableDraw @"jsEnableDraw"
//自定义调用JS方法
#define NMCMethodActionCall @"jsDirectCall"
//退出web
#define NMCMethodActionWebLogout @"jsLeaveWB"
//页面加载完成
#define NMCMethodActionWebPageLoaded @"webPageLoaded"
// web获取Auth
#define NMCMethodActionWebGetAuth @"webGetAuth"
//创建房间成功
#define NMCMethodActionWebCreateWBSucceed @"webCreateWBSucceed"
//加入房间成功
#define NMCMethodActionWebJoinWBSucceed @"webJoinWBSucceed"
//加入房间失败
#define NMCMethodActionWebJoinWBFailed @"webJoinWBFailed"
//创建房间失败
#define NMCMethodActionWebCreateWBFailed @"webCreateWBFailed"

//一般是由于Native调用了jsLogoutIMAndLeaveWB，WebView随之退出IM及白板信令，然后发送此消息给客户端
#define NMCMethodActionWebLeaveWB @"webLeaveWB"

//WebView中发生了网络异常
#define NMCMethodActionWebError @"webError"
//WebView抛出Js错误。客户端可以根据此消息调试
#define NMCMethodActionWebJSError @"webJsError"

#define NMCMethodActionWebLog @"webLog"

#endif /* NMCWebViewHeader_h */
