//
//  DrawingView.h
//  pixNotes
//
//  Created by Tuyen Le on 11/19/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DrawingShape.h"

@protocol DrawingViewDataSource;

@interface DrawingView : UIView
{
    IBOutlet id <DrawingViewDataSource> _dataSource;
}


@end


@protocol DrawingViewDataSource <NSObject>

@required
- (NSUInteger)numberOfShapesInDrawingView;
- (DrawingShape *)shapeAtIndex:(NSUInteger)shapeIndex;
- (UIBezierPath *)pathForShapeAtIndex:(NSUInteger)shapeIndex;
- (UIColor *)lineColorForShapeAtIndex:(NSUInteger)shapeIndex;
- (BOOL)visibleForShapeAtIndex:(NSUInteger)shapeIndex;
- (UIImage *)getBlurImage;
- (UIView *)getBackBlurView;
- (UIView *)getFrontBlurView;
- (UIBezierPath *)currentDrawingPath;
@optional
- (NSUInteger)indexOfSelectedShapeInDrawingView;

@end
