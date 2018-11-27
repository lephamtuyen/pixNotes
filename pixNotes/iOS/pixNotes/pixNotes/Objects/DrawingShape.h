//
//  DrawingShape.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import <Foundation/Foundation.h>


#define MAX_CORDINATE_VALUE 10000
#define RECT_RECOGNIZED_AREA_WIDTH 30
#define SELECTED_BORDER_WIDTH 10.0f

@interface DrawingShape : NSObject <NSCoding>

@property (strong) NSString *drawingID;
@property (strong) NSString *followeeShapeID;
@property (assign) BOOL deleted;
@property (assign) BOOL visible;
@property (strong) UIBezierPath *path;
@property (strong) UIBezierPath *tapTarget;

@property (strong) UIBezierPath *tapSelectedBorder;
@property (strong) NSMutableArray *selectedBorder;

@property (strong) UIColor *lineColor;

- (void)prepareToRemove;
- (void)decodeObjectWithCoder:(NSCoder *)decoder;
- (id)initFromFolloweeShape:(DrawingShape *)drawingShape;
- (CGRect)sumBounds;
- (void)addTapTarget;
- (void)addSelectedBorder;
- (void)addTapSelectedBorder;
- (BOOL)containsPoint:(CGPoint)point;
- (NSInteger)checkHitBorderShape:(CGPoint)point;
- (void)moveBy:(CGPoint)delta;

@end