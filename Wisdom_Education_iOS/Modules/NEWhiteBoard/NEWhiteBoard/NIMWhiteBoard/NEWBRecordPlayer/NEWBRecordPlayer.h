//
//  NEWBRecordPlayer.h
//  NEWhiteBoard
//
//  Created by 郭园园 on 2021/8/13.
//

#import <Foundation/Foundation.h>
#import "NEWBRecordPlayerParam.h"
#import "NEWBRecordInfo.h"

#import "NMCMessageHandlerDispatch.h"
NS_ASSUME_NONNULL_BEGIN

@protocol NEWBRecordPlayerDelegate <NSObject>

/// 白板资源加载完成
/// @param info 加载资源信息
- (void)onPreparedWithRecordInfo:(NEWBRecordInfo *)info;

/// 播放进度回调
/// @param time second
- (void)onPlayTime:(NSTimeInterval)time;

/// 播放完成回调
- (void)onPlayFinished;

@end
/// 回放地址
extern NSString *const NMCWhiteboardRecordUrl;
@interface NEWBRecordPlayer : NSObject
@property (nonatomic, weak) id<NEWBRecordPlayerDelegate> delegate;
@property (nonatomic, strong) WKWebView *webview;

/// 回放时长，单位秒
@property (nonatomic, assign) NSTimeInterval duration;
/// 初始化白板播放器
/// @param param 参数
- (instancetype)initPlayerWithContentView:(UIView *)view param:(NEWBRecordPlayerParam *)param;

/// 加载资源
- (void)prepareToPlay;

/// 播放
- (void)play;

/// 暂停
- (void)pause;

/// 停止
- (void)stop;

/// 跳转到指定时间
/// @param interval 相对于起始时间的偏移量，单位：ms
- (void)seekToTimeInterval:(NSInteger)interval;

/// 设置播放倍速，默认为1
/// @param speed 倍速值
- (void)setSpeed:(NSInteger)speed;

/// 设置观看视角
/// @param viewer 观看者的账号（登录时的account字段）
- (void)setViewer:(NSInteger)viewer;

/// 设置播放范围
/// @param startTime 起始时间
/// @param endTime 结束时间
- (void)setTimeRangeStartTime:(NSInteger)startTime endTime:(NSInteger)endTime;

@end

NS_ASSUME_NONNULL_END
