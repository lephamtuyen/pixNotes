//
//  GridView.m
//  pixNotes
//
//  Created by Tuyen Le on 11/26/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "GridView.h"
#import "Settings.h"
#import "Constants.h"

@implementation GridView


- (void)drawRect:(CGRect)rect
{
    [self drawGrid:Settings.singleton.gridEnable existedDrawing:[_dataSource isExistedDrawing]];
}

- (void)drawGrid:(BOOL)gridEnable existedDrawing:(BOOL)isExistedDrawing
{
    if (gridEnable && isExistedDrawing)
    {
        [Settings.singleton.gridColor setStroke];
        if (Settings.singleton.gridStyle == GRID_STYLE_DIAGONAL)
        {
            [self drawGridDiagonal];
        }
        else if (Settings.singleton.gridStyle == GRID_STYLE_COLUMN)
        {
            [self drawGridColumn];
        }
        else if (Settings.singleton.gridStyle == GRID_STYLE_ROW)
        {
            [self drawGridRow];
        }
        else if (Settings.singleton.gridStyle == GRID_STYLE_SQUARE)
        {
            [self drawGridColumn];
            [self drawGridRow];
        }
        else
        {
            [self drawGridDiagonal];
        }
    }
}

- (void)drawGridDiagonal
{
    CGFloat offset = sqrtf((self.frame.size.width * self.frame.size.width + self.frame.size.height * self.frame.size.height)) / 40;
    CGFloat duongCheo = sqrtf((self.frame.size.width * self.frame.size.width + self.frame.size.height * self.frame.size.height));
    for (int i = 1; i < 40; i++)
    {
        UIBezierPath *path = [UIBezierPath bezierPath];
        [path moveToPoint:CGPointMake(0, i * offset * duongCheo / self.frame.size.height)];
        [path addLineToPoint:CGPointMake(i * offset * duongCheo / self.frame.size.width, 0)];
        path.lineWidth = GRID_LINE_WIDTH;
        [path stroke];
    }
}

- (void)drawGridColumn
{
    CGFloat offset = self.frame.size.width / 20;
    for (int i = 1; i < 20; i++)
    {
        UIBezierPath *path = [UIBezierPath bezierPath];
        [path moveToPoint:CGPointMake(i * offset, 0)];
        [path addLineToPoint:CGPointMake(i * offset, self.frame.size.height)];
        path.lineWidth = GRID_LINE_WIDTH;
        [path stroke];
    }
}


- (void)drawGridRow
{
    CGFloat offset = self.frame.size.height / 20;
    for (int i = 1; i < 20; i++)
    {
        UIBezierPath *path = [UIBezierPath bezierPath];
        [path moveToPoint:CGPointMake(0, i * offset)];
        [path addLineToPoint:CGPointMake(self.frame.size.width, i * offset)];
        path.lineWidth = GRID_LINE_WIDTH;
        [path stroke];
    }
}

@end
