//
//  DrawingShape.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import "DrawingShape.h"
#import "NSString+UUID.h"
#import "UIBezierPath+Arrow.h"
#import "Settings.h"


@implementation DrawingShape


- (id)init
{
    self = [super init];
    if (self != nil)
    {
        _path = nil;
        _lineColor = Settings.singleton.lineColor;
        _tapTarget = nil;
        _selectedBorder = nil;
        _tapSelectedBorder = nil;
        _visible = YES;
        _followeeShapeID = nil;
        _drawingID = [NSString UUIDString];
        _deleted = NO;
    }
    return self;
}


- (id)initFromFolloweeShape:(DrawingShape *)drawingShape
{
    self = [super init];
    if (self != nil)
    {
        _deleted = NO;
        _drawingID = [NSString UUIDString];
        _path = [drawingShape.path copy];
        _tapTarget = [drawingShape.tapTarget copy];
        _selectedBorder = [[NSMutableArray alloc] init];

        for (UIBezierPath *path in drawingShape.selectedBorder)
        {
            [_selectedBorder addObject:[path copy]];
        }
        
        _tapSelectedBorder = [drawingShape.tapSelectedBorder copy];
        _lineColor = drawingShape.lineColor;
        _followeeShapeID = drawingShape.drawingID;
    }
    
    return self;
}


- (id)initWithCoder:(NSCoder *)decoder
{
    self = [super init];
    if (self)
    {
        [self decodeObjectWithCoder:decoder];
        
        [self addTapTarget];
        [self addSelectedBorder];
        [self addTapSelectedBorder];
    }
    
    return self;
}


- (void)decodeObjectWithCoder:(NSCoder *)decoder
{
    _deleted = [decoder decodeBoolForKey:@"deleted"];
    _path = [decoder decodeObjectForKey:@"path"];
    _lineColor = [decoder decodeObjectForKey:@"lineColor"];
    _visible = [decoder decodeBoolForKey:@"visible"];
    _followeeShapeID = [decoder decodeObjectForKey:@"followeeShape"];
    _drawingID = [decoder decodeObjectForKey:@"drawingID"];
}

- (void)encodeWithCoder:(NSCoder *)encoder
{
    [encoder encodeBool:_deleted forKey:@"deleted"];
    [encoder encodeObject:_path forKey:@"path"];
    [encoder encodeObject:_lineColor forKey:@"lineColor"];
    [encoder encodeBool:_visible forKey:@"visible"];
    [encoder encodeObject:_followeeShapeID forKey:@"followeeShape"];
    [encoder encodeObject:_drawingID forKey:@"drawingID"];
}


- (void)prepareToRemove
{
    
}


#pragma mark - Hit Shape Testing


- (BOOL)containsPoint:(CGPoint)point
{
    if (!_visible)
    {
        return NO;
    }
    
    if (_tapTarget.bounds.origin.x <= point.x
        && _tapTarget.bounds.origin.y <= point.y
        && _tapTarget.bounds.origin.x + _tapTarget.bounds.size.width >= point.x
        && _tapTarget.bounds.origin.y + _tapTarget.bounds.size.height >= point.y)
    {
        return YES;
    }
    
    return NO;
}


- (NSInteger)checkHitBorderShape:(CGPoint)point
{
    return NSNotFound;
}


#pragma mark - Bounds

- (CGRect)sumBounds
{
    if (_path == nil)
    {
        return CGRectZero;
    }
    
    return CGRectMake(_path.bounds.origin.x - 40.0, _path.bounds.origin.y - 40.0, _path.bounds.size.width + 80.0, _path.bounds.size.height + 80.0);
}


#pragma mark - Modifying Shapes

- (void)moveBy:(CGPoint)delta
{
    CGAffineTransform transform = CGAffineTransformMakeTranslation(delta.x, delta.y);
    [_path applyTransform:transform];
    [_tapTarget applyTransform:transform];
    [_tapSelectedBorder applyTransform:transform];
    for (NSInteger i = 0; i < _selectedBorder.count; i++)
    {
        UIBezierPath *path = [_selectedBorder objectAtIndex:i];
        [path applyTransform:transform];
    }
}

- (void)addTapTarget
{
    if (_path == nil)
    {
        _tapTarget = nil;
        return;
    }
    
    CGPathRef tapTargetPath = CGPathCreateCopyByStrokingPath(_path.CGPath,
                                                             NULL,
                                                             fmaxf(35.0f, _path.lineWidth),
                                                             _path.lineCapStyle,
                                                             _path.lineJoinStyle,
                                                             _path.miterLimit);
    
    if (tapTargetPath == NULL)
    {
        _tapTarget = nil;
        return;
    }
    
    _tapTarget = [UIBezierPath bezierPathWithCGPath:tapTargetPath];
    CGPathRelease(tapTargetPath);
}

- (void)addTapSelectedBorder
{

}

- (void)addSelectedBorder
{
}

@end
