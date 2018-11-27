//
//  DrawingView.m
//  pixNotes
//
//  Created by Tuyen Le on 11/19/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "DrawingView.h"
#import "DrawingShape.h"
#import "Settings.h"
#import "Rectangle.h"
#import "Circle.h"
#import "Blur.h"
#import "Text.h"
#import "FreeHand.h"
#import "Arrow.h"
#import "BlurView.h"
#import "UIImage+Blur.h"

@implementation DrawingView

- (void)drawCurrentShape:(CGRect)rect
{
    // Draw current drawing shape
    UIBezierPath *currentDrawPath = [_dataSource currentDrawingPath];
    if (currentDrawPath && CGRectIntersectsRect(rect,
                                                CGRectInset(currentDrawPath.bounds,
                                                            -(currentDrawPath.lineWidth + 1.0f),
                                                            -(currentDrawPath.lineWidth + 1.0f))))
    {
        [Settings.singleton.lineColor setStroke];
        [currentDrawPath stroke];
    }
}

- (void)drawSelectedBorderShape:(DrawingShape *)shape
{
    if ([shape isKindOfClass:Rectangle.class]
        || [shape isKindOfClass:Circle.class]
        || [shape isKindOfClass:Blur.class]
        || [shape isKindOfClass:Arrow.class]
        || [shape isKindOfClass:FreeHand.class])
    {
        for (NSInteger i = 0; i < shape.selectedBorder.count; i++)
        {
            [[UIColor blackColor] setStroke];
            [[UIColor whiteColor] setFill];
            [[shape.selectedBorder objectAtIndex:i] fill];
            [[shape.selectedBorder objectAtIndex:i] stroke];
        }
    }
}

- (void)drawShape:(DrawingShape *)shape shapeIndex:(NSInteger)shapeIndex selectedShapeIndex:(NSInteger)indexOfSelectedShape
{
    // Draw Arrow
    if ([shape isKindOfClass:Arrow.class])
    {
        [shape.lineColor setFill];
        [shape.path fill];
        [shape.lineColor setStroke];
        [shape.path stroke];
    }
    // Draw Blur
    else if ([shape isKindOfClass:Blur.class])
    {
        Blur *blurShape = (Blur *)shape;
        if (!blurShape.blurView)
        {
            blurShape.blurView = [[BlurView alloc] init];
            [blurShape changeImageOfBlurShape:[_dataSource getBlurImage]];
        }
        
        [self determineBackOrFront:blurShape shapeIndex:shapeIndex selectedShapeIndex:indexOfSelectedShape];
        
        [blurShape.blurView setFrame:blurShape.path.bounds];
    }
    // Draw Text
    else if ([shape isKindOfClass:Text.class])
    {
        [shape.lineColor setFill];
        [shape.path fill];
    }
    // Draw Other
    else
    {
        [shape.lineColor setStroke];
        [shape.path stroke];
    }
}

- (void)determineBackOrFront:(Blur *)blurShape shapeIndex:(NSInteger)shapeIndex selectedShapeIndex:(NSInteger)indexOfSelectedShape
{
    UIView *frontBlurView = [_dataSource getFrontBlurView];
    UIView *backBlurView = [_dataSource getBackBlurView];
    
    // Determine blur view added to front or back
    if (indexOfSelectedShape != NSNotFound && shapeIndex == indexOfSelectedShape)
    {
        if ([blurShape.blurView isDescendantOfView:backBlurView])
        {
            [blurShape.blurView removeFromSuperview];
        }
        if (![blurShape.blurView isDescendantOfView:frontBlurView])
        {
            [frontBlurView addSubview:blurShape.blurView];
        }
    }
    else
    {
        if ([blurShape.blurView isDescendantOfView:frontBlurView])
        {
            [blurShape.blurView removeFromSuperview];
        }
        if (![blurShape.blurView isDescendantOfView:backBlurView])
        {
            [backBlurView addSubview:blurShape.blurView];
        }
    }
}

- (void)drawRect:(CGRect)rect
{
    NSUInteger numberOfShapes = [_dataSource numberOfShapesInDrawingView];
    NSUInteger indexOfSelectedShape = NSNotFound;
    if ([_dataSource respondsToSelector:@selector(indexOfSelectedShapeInDrawingView)])
    {
        indexOfSelectedShape = [_dataSource indexOfSelectedShapeInDrawingView];
    }

    [self drawCurrentShape:rect];
    
    for (NSUInteger shapeIndex = 0; shapeIndex < numberOfShapes; shapeIndex++)
    {
        DrawingShape *shape = [_dataSource shapeAtIndex:shapeIndex];

        if (shape.visible && CGRectIntersectsRect(rect,
                                                  CGRectInset(shape.path.bounds,
                                                              -(shape.path.lineWidth + 1.0f),
                                                              -(shape.path.lineWidth + 1.0f))))
        {
            // Draw selected border shape
            if (shapeIndex == indexOfSelectedShape)
            {
                [self drawSelectedBorderShape:shape];
            }
            
            [self drawShape:shape shapeIndex:shapeIndex selectedShapeIndex:indexOfSelectedShape];
        }
    }
}

@end
