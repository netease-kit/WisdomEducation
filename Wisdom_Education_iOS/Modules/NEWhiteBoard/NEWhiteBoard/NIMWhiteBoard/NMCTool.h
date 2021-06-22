//
//  NMCTool.h
//  NEWhiteBoard
//
//  Created by Groot on 2021/4/30.
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

- (instancetype)initWithName:(WhiteboardItemName)name;

@end

@interface NMCTool : NSObject
@property (nonatomic , copy) NSArray<WhiteboardItem *>   * items;
//
@property (nonatomic , assign) ToolViewPosition toolposition;
@property (nonatomic , copy) NSString *         position;

@end

NS_ASSUME_NONNULL_END
