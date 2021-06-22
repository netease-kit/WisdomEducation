//
//  NMCWhiteboardManager.m
//  BlockFo
//
//  Created by taojinliang on 2019/5/30.
//  Copyright © 2019 BlockFo. All rights reserved.
//

#import "NMCWhiteboardManager.h"
#import "NMCWebView.h"
#import "NMCMessageHandlerDispatch.h"
#import "NMCWebViewHeader.h"

//#ifdef debug
NSString * const NMCWhiteboardURL = @"https://meeting.163.com/whiteboard/stable/3.1.0/webview.html";
//#else
//NSString * const NMCWhiteboardURL = @"https://app.yunxin.163.com/webdemo/whiteboard/webview.html";
//#endif

//NSString * const NMCWhiteboardURL = @"https://app.yunxin.163.com/webdemo/whiteboard/webview_vconsole.html";
@interface NMCWhiteboardManager()<WKNavigationDelegate,WKUIDelegate>
@property(nonatomic, strong) NMCWebView *webview;
@end

@implementation NMCWhiteboardManager

+ (instancetype)sharedManager {
    static NMCWhiteboardManager *manager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        manager = [[NMCWhiteboardManager alloc] init];
    });
    return manager;
}

- (void)setDelegate:(id<NMCWhiteboardManagerDelegate>)delegate
{
    _delegate = delegate;
    [NMCMessageHandlerDispatch sharedManager].delegate = delegate;
}

#pragma mark - Publish API
- (WKWebView *)createWebViewFrame:(CGRect)frame
{
    if (!_webview) {
        _webview = [[NMCWebView alloc] initWithFrame:frame];
        _webview.UIDelegate = self;
        _webview.navigationDelegate = self;
        _webview.scrollView.scrollEnabled = NO;
        _webview.scrollView.bounces = NO;
    }
    return _webview;
}

- (void)callWebLoginIM:(NMCWebLoginParam *)loginParam
{
    NSMutableDictionary *param = [NSMutableDictionary dictionary];
    // 白板房间名称 为 唯一值，业务层维护，用来创建、加入房间
    [param setObject:loginParam.channelName forKey:@"channelName"];
    // 开启 web 调试日志
    [param setObject:@(loginParam.debug) forKey:@"debug"];
    // IM 账号体系 appKey
    [param setObject:loginParam.appKey forKey:@"appKey"];
    // IM 账号体系 account
    [param setObject:loginParam.account forKey:@"account"];
    [param setObject:loginParam.nickname forKey:@"nickname"];
    // IM 账号体系 密码
    [param setObject:loginParam.token forKey:@"token"];
    // 是否服务端录制
    [param setObject:@(loginParam.record) forKey:@"record"];
    // ownerAccout 参数，owner 账号
    [param setObject:loginParam.ownerAccount forKey:@"ownerAccount"];
    // platform 参数
    [param setObject:@"ios" forKey:@"platform"];

    [param setObject:@(loginParam.height) forKey:@"height"];
    [param setObject:@(loginParam.width) forKey:@"width"];
    
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:_webview action:NMCMethodActionWebLoginIM param:param];
}

- (void)callWebLogoutIM
{
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:_webview action:NMCMethodActionWebLogout param:[NSMutableDictionary dictionary]];
}

- (void)callEnableDraw:(BOOL)enable
{
    [self performJSSelector:@"enableDraw" withObject:@"drawPlugin" parameter:@{@"param1":@(enable)}];
}

- (void)setWhiteboardColor:(NSString *)color
{
    [self performJSSelector:@"setColor" withObject:@"drawPlugin" parameter:@{@"param1":color}];
}
//- (void)setTools:(NSArray *)tools position:(ToolViewPosition)position {
//    [self performJSSelector:@"setContainerOptions" withObject:@"toolCollection" parameter:@{@"param1":color}];
//
//}
- (void)hiddenTools:(BOOL)hidden {
    if (hidden) {
        [self performJSSelector:@"setVisibility" withObject:@"toolCollection" parameter:@{@"params":@[@{@"topRight":@{@"visible":@YES}, @"bottomRight":@{@"visible":@NO}, @"topLeft":@{@"visible":@NO}}]}];
    }else {
        [self performJSSelector:@"setVisibility" withObject:@"toolCollection" parameter:@{@"params":@[@{@"topRight":@{@"visible":@YES}, @"bottomRight":@{@"visible":@YES}, @"topLeft":@{@"visible":@YES}}]}];
    }
}
- (void)setupWhiteboardTools:(NSArray <NMCTool *> *)tools {
    NSArray *toolArray = [tools yy_modelToJSONObject];
    NSLog(@"toolArray:%@",toolArray);
    [self performJSSelector:@"setContainerOptions" withObject:@"toolCollection" parameter:@{@"param1":toolArray}];
}
- (void)performJSSelector:(NSString *)selector withObject:(NSString *)object parameter:(NSDictionary *)parameter {
    NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithDictionary:parameter];
    [dic setObject:object forKey:@"target"];
    [dic setObject:selector forKey:@"action"];
    NSLog(@"callJS: selector:%@ object:%@ parameter:%@",selector,object,dic);
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:_webview action:NMCMethodActionCall param:dic];
}

- (void)clearWebViewCache {
    if ([[[UIDevice currentDevice]systemVersion]intValue ] >= 9.0) {
         NSArray * types =@[WKWebsiteDataTypeMemoryCache,WKWebsiteDataTypeDiskCache]; // 9.0之后才有的
         NSSet *websiteDataTypes = [NSSet setWithArray:types];
         NSDate *dateFrom = [NSDate dateWithTimeIntervalSince1970:0];
         [[WKWebsiteDataStore defaultDataStore] removeDataOfTypes:websiteDataTypes modifiedSince:dateFrom completionHandler:^{}];
     }else{
         NSString *libraryPath = [NSSearchPathForDirectoriesInDomains(NSLibraryDirectory,NSUserDomainMask,YES) objectAtIndex:0];
         NSString *cookiesFolderPath = [libraryPath stringByAppendingString:@"/Cookies"];
         NSLog(@"%@", cookiesFolderPath);
         NSError *errors;
         [[NSFileManager defaultManager] removeItemAtPath:cookiesFolderPath error:&errors];
     }
}
#pragma mark - Private
- (UIViewController *)viewController{
    for (UIView* next = [_webview superview]; next; next = next.superview){
        UIResponder* nextResponder = [next nextResponder];
        if ([nextResponder isKindOfClass:[UIViewController class]]) {
            return (UIViewController*)nextResponder;
        }
    }
    return nil;
}

#pragma mark - WKUIDelegate

- (nullable WKWebView *)webView:(WKWebView *)webView createWebViewWithConfiguration:(WKWebViewConfiguration *)configuration forNavigationAction:(WKNavigationAction *)navigationAction windowFeatures:(WKWindowFeatures *)windowFeatures
{
    return webView;
}

- (void)webViewDidClose:(WKWebView *)webView
{
    
}

// 提示框
- (void)webView:(WKWebView *)webView runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(void))completionHandler
{
    UIViewController *vc = [self viewController];
    if (vc && vc.isViewLoaded && _webview && [_webview superview]){
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:message ? message : @"" preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            completionHandler();
        }]];
        [vc presentViewController:alert animated:YES completion:NULL];
    }else{
        completionHandler();
    }
}

// 确认框
- (void)webView:(WKWebView *)webView runJavaScriptConfirmPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(BOOL result))completionHandler
{
    UIViewController *vc = [self viewController];
    if (vc && vc.isViewLoaded && _webview && [_webview superview]){
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:message ? message : @"" preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            completionHandler(YES);
        }]];
        [alert addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
            completionHandler(NO);
        }]];
        [vc presentViewController:alert animated:YES completion:NULL];
    }else{
        completionHandler(NO);
    }
}

// 输入框
- (void)webView:(WKWebView *)webView runJavaScriptTextInputPanelWithPrompt:(NSString *)prompt defaultText:(nullable NSString *)defaultText initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(NSString * _Nullable result))completionHandler
{
    UIViewController *vc = [self viewController];
    if (vc && vc.isViewLoaded && _webview && [_webview superview]){
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:prompt ? prompt : @"" preferredStyle:UIAlertControllerStyleAlert];
        [alert addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
            textField.textColor = [UIColor blackColor];
            textField.placeholder = defaultText ? defaultText : @"";
        }];
        [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            completionHandler([[alert.textFields lastObject] text]);
        }]];
        [alert addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
            completionHandler(nil);
        }]];
        [vc presentViewController:alert animated:YES completion:NULL];
    }else{
        completionHandler(nil);
    }
}

//ios 10
//- (BOOL)webView:(WKWebView *)webView shouldPreviewElement:(WKPreviewElementInfo *)elementInfo
//{
//
//}
//
//- (nullable UIViewController *)webView:(WKWebView *)webView previewingViewControllerForElement:(WKPreviewElementInfo *)elementInfo defaultActions:(NSArray<id <WKPreviewActionItem>> *)previewActions
//{
//
//}
//
//- (void)webView:(WKWebView *)webView commitPreviewingViewController:(UIViewController *)previewingViewController
//{
//
//}
#pragma mark - WKNavigationDelegate
//发送请求之前决定是否跳转
- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler
{
    NSString *requestString = navigationAction.request.URL.absoluteString;
    NSLog(@"[WK] decidePolicyForNavigationAction %@",requestString);
    decisionHandler(WKNavigationActionPolicyAllow);
}

- (void)webView:(WKWebView *)webView didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition disposition, NSURLCredential * _Nullable credential))completionHandler {
    completionHandler(NSURLSessionAuthChallengeUseCredential, nil);
}

//在收到响应后，决定是否跳转(表示当客户端收到服务器的响应头，根据response相关信息，可以决定这次跳转是否可以继续进行。
- (void)webView:(WKWebView *)webView decidePolicyForNavigationResponse:(WKNavigationResponse *)navigationResponse decisionHandler:(void (^)(WKNavigationResponsePolicy))decisionHandler
{
    NSURLResponse *response = navigationResponse.response;
    NSLog(@"[WK] decidePolicyForNavigationResponse %@",response);
    decisionHandler(WKNavigationResponsePolicyAllow);
}

//页面开始加载时调用
- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(null_unspecified WKNavigation *)navigation
{
    NSLog(@"[WK] didStartProvisionalNavigation");
}

//接收到服务器跳转请求之后调用(接收服务器重定向时)
- (void)webView:(WKWebView *)webView didReceiveServerRedirectForProvisionalNavigation:(null_unspecified WKNavigation *)navigation
{
    
}

//加载失败时调用(加载内容时发生错误时)
- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(null_unspecified WKNavigation *)navigation withError:(NSError *)error
{
    NSLog(@"[WK] didFailProvisionalNavigation %@",error);
}

//当内容开始返回时调用
- (void)webView:(WKWebView *)webView didCommitNavigation:(null_unspecified WKNavigation *)navigation
{
    NSLog(@"[WK] didCommitNavigation");
}

//页面加载完成之后调用
- (void)webView:(WKWebView *)webView didFinishNavigation:(null_unspecified WKNavigation *)navigation
{
    NSLog(@"[WK] didFinishNavigation");
}

//导航期间发生错误时调用
- (void)webView:(WKWebView *)webView didFailNavigation:(null_unspecified WKNavigation *)navigation withError:(NSError *)error
{
    NSLog(@"[WK] didFailNavigation %@",error);
}

//- (void)webView:(WKWebView *)webView didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition disposition, NSURLCredential * _Nullable credential))completionHandler
//{
//
//}

//iOS9.0以上异常终止时调用
- (void)webViewWebContentProcessDidTerminate:(WKWebView *)webView
{
    NSLog(@"[WK] webViewWebContentProcessDidTerminate");
    [webView reload];
}
@end
