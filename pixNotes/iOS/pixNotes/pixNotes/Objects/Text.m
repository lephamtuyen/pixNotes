//
//  Text.m
//  pixNotes
//
//  Created by Tuyen Le on 12/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "Text.h"
#import "Constants.h"
#import "Settings.h"

@implementation Text

- (id)init
{
    self = [super init];
    if (self != nil)
    {
        _fontSize = Settings.singleton.fontSize;
    }
    return self;
}

- (id)initFromFolloweeShape:(Text *)drawingShape
{
    self = [super initFromFolloweeShape:drawingShape];
    if (self != nil)
    {
        _text = drawingShape.text;
        _fontSize = drawingShape.fontSize;
    }
    
    return self;
}


- (void)decodeObjectWithCoder:(NSCoder *)decoder
{
    [super decodeObjectWithCoder:decoder];
    
    _text = [decoder decodeObjectForKey:@"text"];
    _fontSize = [decoder decodeFloatForKey:@"fontSize"];
}


- (void)encodeWithCoder:(NSCoder *)encoder
{
    [super encodeWithCoder:encoder];
    
    [encoder encodeObject:_text forKey:@"text"];
    [encoder encodeFloat:_fontSize forKey:@"fontSize"];
}

- (BOOL)containsPoint:(CGPoint)point
{
    if (!self.visible)
    {
        return NO;
    }
    
    return [self.tapTarget containsPoint:point];
}

- (void)addSelectedBorder
{
    if (self.path == nil)
    {
        self.selectedBorder = nil;
        return;
    }
    
    self.selectedBorder = [[NSMutableArray alloc] init];
    
    CGRect bound = self.path.bounds;
    bound.origin.x -= 10.0f;
    bound.origin.y -= 10.0f;
    bound.size.width += 20.0f;
    bound.size.height += 20.0f;
    UIBezierPath *path = [UIBezierPath bezierPathWithRect:bound];
    [self.selectedBorder addObject:path];
}

@end
