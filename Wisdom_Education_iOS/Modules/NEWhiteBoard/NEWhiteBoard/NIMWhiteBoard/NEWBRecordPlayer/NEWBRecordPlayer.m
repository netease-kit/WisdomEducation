//
//  NEWBRecordPlayer.m
//  NEWhiteBoard
//
//  Created by 郭园园 on 2021/8/13.
//

#import "NEWBRecordPlayer.h"
#import "NMCWebView.h"
#import <YYModel/YYModel.h>

NSString * const initPlayerAction = @"jsInitPlayer";
NSString * const playAction = @"jsPlay";
NSString * const pauseAction = @"jsPause";
NSString * const seekToAction = @"jsSeekTo";
NSString * const setSpeedAction = @"jsSetPlaySpeed";
NSString * const setViewerAction = @"jsSetViewer";
NSString * const setTimeRangeAction = @"jsSetTimeRange";
// https://yiyong-xedu-v2-static.netease.im/whiteboard-webview/g2/webview.record.html
NSString * const NMCWhiteboardRecordUrl = @"https://yiyong-xedu-v2-static.netease.im/whiteboard-webview/g2/webview.record.html";
// 私有化回放 测试地址
//NSString * const NMCWhiteboardRecordUrl = @"https://yunxinent-demo.netease.im/xedu/webview/g2/webview.record.html";

@interface NEWBRecordPlayer ()<NMCWhiteboardManagerDelegate,NEWBRecordPlayDelegate,WKUIDelegate,WKNavigationDelegate>
@property (nonatomic, strong) NEWBRecordPlayerParam *param;
@property (nonatomic, strong) UIView *contentView;
@end

@implementation NEWBRecordPlayer
- (instancetype)initPlayerWithContentView:(UIView *)view param:(NEWBRecordPlayerParam *)param {
    self = [super init];
    if (self) {
        self.param = param;
        self.contentView = view;
        // 1.add webView
        [self.contentView addSubview:self.webview];
        [NSLayoutConstraint activateConstraints:@[
            [self.webview.topAnchor constraintEqualToAnchor:self.contentView.topAnchor],
            [self.webview.leadingAnchor constraintEqualToAnchor:self.contentView.leadingAnchor],
            [self.webview.trailingAnchor constraintEqualToAnchor:self.contentView.trailingAnchor],
            [self.webview.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor],
        ]];
        NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:NMCWhiteboardRecordUrl]];
        [self.webview loadRequest:request];
        
        NMCMessageHandlerDispatch *dispatch = [NMCMessageHandlerDispatch sharedManager];
        dispatch.delegate = self;
        dispatch.recordDelegate = self;
    }
    return self;
}

- (void)prepareToPlay {
    NSDictionary *dic = [self.param yy_modelToJSONObject];
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:self.webview action:initPlayerAction param:dic];
}

- (void)play {
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:self.webview action:playAction param:nil];
}

- (void)pause {
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:self.webview action:pauseAction param:nil];
}
- (void)stop  {
    [self clearWebViewCache];
}
- (void)seekToTimeInterval:(NSInteger)interval {
    NSDictionary *param = @{@"time":@(interval)};
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:self.webview action:seekToAction param:param];
}

- (void)setSpeed:(NSInteger)speed {
    NSDictionary *param = @{@"speed":@(speed)};
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:self.webview action:setSpeedAction param:param];
}

- (void)setViewer:(NSInteger)viewer {
    if (viewer) {
        NSString *string = [NSString stringWithFormat:@"%ld",(long)viewer];
        NSDictionary *param = @{@"viewer":string};
        [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:self.webview action:setViewerAction param:param];
    }
}

- (void)setTimeRangeStartTime:(NSInteger)startTime endTime:(NSInteger)endTime {
    NSDictionary *param = @{
        @"startTime":@(startTime),
        @"endTime":@(endTime),
    };
    [[NMCMessageHandlerDispatch sharedManager] nativeCallWebWithWebView:self.webview action:setTimeRangeAction param:param];
}

#pragma mark - NMCWhiteboardManagerDelegate
- (void)onWebPageLoaded {
    NSLog(@"[WBrecord] onWebPageLoaded");
    // 2.load urls
    [self prepareToPlay];
    
}
- (void)onWebError:(NSInteger)code error:(NSString *)error {
    NSLog(@"[WBrecord] onWebError:%@",error);
}
- (void)onWebJsError:(NSString *)error {
    NSLog(@"[WBrecord] onWebJsError:%@",error);
}

- (void)onWebCreateWBFailed:(NSInteger)code error:(NSString *)error {
    NSLog(@"[WBrecord]:%s",__func__);
}


- (void)onWebCreateWBSucceed {
    NSLog(@"[WBrecord]:%s",__func__);
}


- (void)onWebJoinWBFailed:(NSInteger)code error:(NSString *)error {
    NSLog(@"[WBrecord]:%s",__func__);
}


- (void)onWebJoinWBSucceed {
    NSLog(@"[WBrecord]:%s",__func__);
}


- (void)onWebLeaveWB {
    NSLog(@"[WBrecord]:%s",__func__);
}


- (void)onWebLoginIMSucceed {
    NSLog(@"[WBrecord]:%s",__func__);
}

#pragma mark - NEWBRecordPlayDelegate
- (void)onPrepared:(NSDictionary *)dic {
    // 3.didLoadUrls
    NEWBRecordInfo *info = [NEWBRecordInfo yy_modelWithDictionary:dic];
    if (self.delegate && [self.delegate respondsToSelector:@selector(onPreparedWithRecordInfo:)]) {
        [self.delegate onPreparedWithRecordInfo:info];
    }
}

- (void)onPlayTime:(NSInteger)time {
    if (self.delegate &&[self.delegate respondsToSelector:@selector(onPlayTime:)]) {
        [self.delegate onPlayTime:time / 1000.0];
    }
}

- (void)onPlayFinished {
    NSLog(@"onPlayFinished");
    if (self.delegate && [self.delegate respondsToSelector:@selector(onPlayFinished)]) {
        [self.delegate onPlayFinished];
    }
}

- (void)onDurationChanged:(NSInteger)duration {
    self.duration = duration;
    NSLog(@"onDurationChanged:%d",duration);
}
#pragma mark - Private
- (UIViewController *)viewController {
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

#pragma mark - WKNavigationDelegate
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
#pragma mark - get
- (WKWebView *)webview {
    if (!_webview) {
        _webview = [[NMCWebView alloc] init];
        _webview.translatesAutoresizingMaskIntoConstraints = NO;
        _webview.UIDelegate = self;
        _webview.navigationDelegate = self;
        _webview.scrollView.scrollEnabled = NO;
        _webview.scrollView.bounces = NO;
    }
    return _webview;
}

//FIXME :临时使用切换URL的方式停止播放器的timer，等白班SDK开放destroy接口后替换
- (void)clearWebViewCache {
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:@"https://yunxin.163.com/"]];
    [self.webview loadRequest:request];

}

@end
