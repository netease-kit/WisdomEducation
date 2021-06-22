//
//  NMCTool.m
//  NEWhiteBoard
//
//  Created by Groot on 2021/4/30.
//

#import "NMCTool.h"
NSString * WhiteboardPositionTopRight = @"topRight";
NSString * WhiteboardPositionBottomRight = @"bottomRight";
NSString * WhiteboardPositionTopLeft = @"topLeft";

@interface WhiteboardItem ()
@property (nonatomic , assign, readwrite) WhiteboardItemName  itemName;
@end

@implementation WhiteboardItem

- (instancetype)initWithName:(WhiteboardItemName)name
{
    self = [super init];
    if (self) {
        self.tool = [self nameFromItemName:name];
        self.stack = @"horizontal";
    }
    return self;
}
- (NSString *)nameFromItemName:(WhiteboardItemName)itemName {
    NSString *name = @"";
    switch (itemName) {
        case WhiteboardItemNameSelection:
            name = @"select";
            self.hint = @"选择";
            break;
        case WhiteboardItemNamePen:
            name = @"pen";
            self.hint = @"画笔";
            break;
        case WhiteboardItemNameShape:
            name = @"shape";
            self.hint = @"图形";
            break;
        case WhiteboardItemNameEraser:
            name = @"element-eraser";
            self.hint = @"橡皮擦";
            break;
        case WhiteboardItemNameClear:
            name = @"clear";
            self.hint = @"清除";
            break;
        case WhiteboardItemNameUndo:
            name = @"undo";
            self.hint = @"上一步";
            break;
        case WhiteboardItemNameRedo:
            name = @"redo";
            self.hint = @"下一步";
            break;
        case WhiteboardItemNameZoomIn:
            name = @"zoomIn";
            self.hint = @"缩小";
            break;
        case WhiteboardItemNameZoomOut:
            name = @"zoomOut";
            self.hint = @"放大";
            break;
        case WhiteboardItemNameMore:
            name = @"multiInOne";
            self.hint = @"更多";
            break;
        case WhiteboardItemNameZoomLevel:
            name = @"zoomLevel";
            self.hint = @"zoomLevel";
            break;
        case WhiteboardItemNameFitToConent:
            name = @"fitToContent";
            self.hint = @"fitToContent";
            break;
        case WhiteboardItemNameFitToDoc:
            name = @"fitToDoc";
            self.hint = @"fitToDoc";
            break;
        case WhiteboardItemNamePan:
            name = @"pan";
            self.hint = @"pan";
            break;
        case WhiteboardItemNameVisionLock:
            name = @"visionLock";
            self.hint = @"visionLock";
            break;
        case WhiteboardItemNamePageBoardInfo:
            name = @"pageBoardInfo";
            self.hint = @"pageBoardInfo";
            break;
        case WhiteboardItemNamePreview:
            name = @"preview";
            self.hint = @"preview";
            self.previewSliderPosition = @"right";
            break;
        default:
            break;
    }
    return name;
}
+ (nullable NSArray<NSString *> *)modelPropertyBlacklist {
    return @[@"itemName"];
}
@end

@implementation NMCTool

- (void)setToolPosition:(ToolViewPosition)toolPosition {
    switch (toolPosition) {
        case ToolViewPositionTopLeft:
            self.position = @"topLeft";
            break;
        case ToolViewPositionTopRight:
            self.position = @"topRight";
            break;
        case ToolViewPositionBottomLeft:
            self.position = @"bottomLeft";
            break;
        case ToolViewPositionBottomRight:
            self.position = @"bottomRight";
            break;
        default:
            self.position = @"bottomRight";
            break;
    }
}

+ (nullable NSArray<NSString *> *)modelPropertyBlacklist {
    return @[@"toolposition"];
}
@end
