//
//  Arrow.m
//  pixNotes
//
//  Created by Tuyen Le on 12/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "Arrow.h"
#import "UIBezierPath+Arrow.h"

@implementation Arrow

- (id)initFromFolloweeShape:(Arrow *)drawingShape
{
    self = [super initFromFolloweeShape:drawingShape];
    if (self != nil)
    {
        self.tail = drawingShape.tail;
        self.head = drawingShape.head;
    }
    
    return self;
}


- (void)decodeObjectWithCoder:(NSCoder *)decoder
{
    [super decodeObjectWithCoder:decoder];
    
    _tail.x = [decoder decodeFloatForKey:@"start.x"];
    _tail.y = [decoder decodeFloatForKey:@"start.y"];
    _head.x = [decoder decodeFloatForKey:@"end.x"];
    _head.y = [decoder decodeFloatForKey:@"end.y"];
}

- (void)encodeWithCoder:(NSCoder *)encoder
{
    [super encodeWithCoder:encoder];
    
    [encoder encodeFloat:_tail.x forKey:@"start.x"];
    [encoder encodeFloat:_tail.y forKey:@"start.y"];
    [encoder encodeFloat:_head.x forKey:@"end.x"];
    [encoder encodeFloat:_head.y forKey:@"end.y"];
}


- (BOOL)containsPoint:(CGPoint)point
{
    if (!self.visible)
    {
        return NO;
    }
    
    return [self.tapTarget containsPoint:point];
}

- (NSInteger)checkHitBorderShape:(CGPoint)point
{
    if (!self.visible)
    {
        return NSNotFound;
    }
    
    CGFloat length = hypotf(_head.x - _tail.x, _head.y - _tail.y);
    CGFloat pointVsStart = hypotf(point.x - _tail.x, point.y - _tail.y);
    CGFloat pointVsEnd = hypotf(point.x - _head.x, point.y - _head.y);
    
    if ((pointVsStart / length) < 0.2)
    {
        return 0;
    }
    else if ((pointVsEnd / length) < 0.2)
    {
        return 1;
    }

    return NSNotFound;
}


- (void)moveBy:(CGPoint)delta
{
    [super moveBy:delta];
    
    CGAffineTransform transform = CGAffineTransformMakeTranslation(delta.x, delta.y);
    _tail = CGPointApplyAffineTransform(_tail, transform);
    _head = CGPointApplyAffineTransform(_head, transform);
}


- (void)addTapSelectedBorder
{
    if (self.selectedBorder == nil)
    {
        self.tapSelectedBorder = nil;
        return;
    }
    
    CGFloat length = hypotf(_head.x - _tail.x, _head.y - _tail.y);
    CGAffineTransform transform = [UIBezierPath dqd_transformForStartPoint:_tail
                                                                  endPoint:_head
                                                                    length:length];
    
    UIBezierPath *path = [UIBezierPath bezierPathWithRect:CGRectMake(0, - 2 * self.path.lineWidth, length, 4 * self.path.lineWidth)];
    path.lineWidth = self.path.lineWidth;
    [path applyTransform:transform];
    
    CGPathRef tapBorderTargetPath = CGPathCreateCopyByStrokingPath(path.CGPath,
                                                                   NULL,
                                                                   fmaxf(70.0f, path.lineWidth),
                                                                   path.lineCapStyle,
                                                                   path.lineJoinStyle,
                                                                   path.miterLimit);
    
    self.tapSelectedBorder = [UIBezierPath bezierPathWithCGPath:tapBorderTargetPath];
    
    CGPathRelease(tapBorderTargetPath);
}


- (void)addSelectedBorder
{
    if (self.path == nil)
    {
        self.selectedBorder = nil;
        return;
    }
    
    self.selectedBorder = [[NSMutableArray alloc] init];
    
    CGFloat length = hypotf(_head.x - _tail.x, _head.y - _tail.y);
    CGRect rects[6] = {
        CGRectMake(- SELECTED_BORDER_WIDTH - self.path.lineWidth,
                   - SELECTED_BORDER_WIDTH - 2 * self.path.lineWidth - SELECTED_BORDER_WIDTH / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(length  + self.path.lineWidth,
                   - SELECTED_BORDER_WIDTH - 2 * self.path.lineWidth - SELECTED_BORDER_WIDTH / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(length  + self.path.lineWidth,
                   - SELECTED_BORDER_WIDTH / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(length  + self.path.lineWidth,
                   2 * self.path.lineWidth + SELECTED_BORDER_WIDTH / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(- SELECTED_BORDER_WIDTH - self.path.lineWidth,
                   2 * self.path.lineWidth + SELECTED_BORDER_WIDTH / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(- SELECTED_BORDER_WIDTH - self.path.lineWidth,
                   - SELECTED_BORDER_WIDTH / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH)
    };
    
    CGAffineTransform transform = [UIBezierPath dqd_transformForStartPoint:_tail
                                                                  endPoint:_head
                                                                    length:length];
    
    for (int i = 0; i < 6; i++)
    {
        UIBezierPath *path = [UIBezierPath bezierPathWithOvalInRect:rects[i]];
        path.lineWidth = 2;
        [self.selectedBorder addObject:path];
        [path applyTransform:transform];
    }
}

- (void)computeHeadAndTailOfArrowWithPointsArray:(NSArray *)points
{
    CGPoint top = CGPointZero;
    float cos = -10;
    CGPoint begin = [[points firstObject] CGPointValue];
    CGPoint end = [[points lastObject] CGPointValue];
    for (NSInteger i = 1; i < points.count - 1; i++)
    {
        float tichvohuong = (begin.y - [points[i] CGPointValue].y) * (end.y - [points[i] CGPointValue].y) + (begin.x - [points[i] CGPointValue].x) * (end.x - [points[i] CGPointValue].x);
        float doloncuaA = sqrtf(powf(begin.y - [points[i] CGPointValue].y, 2) + powf(begin.x - [points[i] CGPointValue].x, 2));
        float doloncuaB = sqrtf(powf(end.y - [points[i] CGPointValue].y, 2) + powf(end.x - [points[i] CGPointValue].x, 2));
        float cosi = tichvohuong / (doloncuaA * doloncuaB);
        
        if (cos < cosi)
        {
            cos = cosi;
            top = [points[i] CGPointValue];
        }
    }
    
    CGFloat beginvscorner = sqrtf(powf(begin.x - top.x, 2) + powf(begin.y - top.y, 2));
    CGFloat endvscorner = sqrtf(powf(end.x - top.x, 2) + powf(end.y - top.y, 2));
    
    if (beginvscorner > endvscorner)
    {
        _tail = begin;
    }
    else
    {
        _tail = end;
    }
    _head = top;
}

@end
