//
//  NEWBRecordPlayerParam.h
//  NEWhiteBoard
//
//  Created by 郭园园 on 2021/8/13.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NEWBRecordPlayerParam : NSObject
@property (nonatomic, strong) NSArray<NSString *> *urls;

/// 控制条，默认“toolbar”，toolbar为webview.record.html中工具栏的domId, 若不填，则不会渲染控制条。此时应该由客户端将播放进度通过jsSeekTo同步给webview
@property (nonatomic, strong) NSString *controlContainerId;

/// 请参考SDK接口中WhiteboardSDK.getInstance中的drawPluginParams
@property (nonatomic, strong) NSString *drawPluginParams;

@end

NS_ASSUME_NONNULL_END
