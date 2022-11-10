//
//  NMCTool.h
//  NEWhiteBoard
//
//  Created by Groot on 2021/4/30.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import <YYModel/YYModel.h>

NS_ASSUME_NONNULL_BEGIN

typedef enum : NSUInteger {
    ToolViewPositionTopLeft,
    ToolViewPositionTopRight,
    ToolViewPositionBottomLeft,
    ToolViewPositionBottomRight,
} ToolViewPosition;

typedef enum : NSUInteger {
    WhiteboardItemNameSelection,
    WhiteboardItemNamePen,
    WhiteboardItemNameShape,
    WhiteboardItemNameZoomLevel,
    WhiteboardItemNameEraser,
    WhiteboardItemNameClear,
    WhiteboardItemNameUndo,
    WhiteboardItemNameRedo,
    WhiteboardItemNameZoomIn,
    WhiteboardItemNameZoomOut,
    WhiteboardItemNameFitToConent,
    WhiteboardItemNameFitToDoc,
    WhiteboardItemNamePan,
    WhiteboardItemNameVisionLock,
    WhiteboardItemNamePageBoardInfo,
    WhiteboardItemNamePreview,
    WhiteboardItemNameMore,
    WhiteboardItemNameUpload,
    WhiteboardItemNameLog,
} WhiteboardItemName;

extern const NSString * WhiteboardPositionTopRight;
extern const NSString * WhiteboardPositionBottomRight;
extern const NSString * WhiteboardPositionTopLeft;
@interface WhiteboardItem :NSObject
@property (nonatomic , copy) NSString              * hint;
@property (nonatomic , copy) NSString              * stack;
@property (nonatomic , copy) NSString              * tool;
//预览页面位置
@property (nonatomic, strong) NSString             *previewSliderPosition;
//
@property (nonatomic , assign, readonly) WhiteboardItemName  itemName;
@property (nonatomic , copy) NSArray             <WhiteboardItem *> * subItems;

@property(nonatomic, assign) BOOL supportPptToH5;
@property(nonatomic, assign) BOOL supportDocToPic;
@property(nonatomic, assign) BOOL supportUploadMedia;
@property(nonatomic, assign) BOOL supportTransMedia;

- (instancetype)initWithName:(WhiteboardItemName)name;

@end

@interface NMCTool : NSObject
@property (nonatomic , copy) NSArray<WhiteboardItem *>   * items;
//
@property (nonatomic , assign) ToolViewPosition toolposition;
@property (nonatomic , copy) NSString *         position;

@end

NS_ASSUME_NONNULL_END
