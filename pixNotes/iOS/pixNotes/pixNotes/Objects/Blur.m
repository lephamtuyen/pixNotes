//
//  Blur.m
//  pixNotes
//
//  Created by Tuyen Le on 12/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "Blur.h"
#import "Settings.h"
#import "UIImage+Blur.h"

@implementation Blur


- (id)init
{
    self = [super init];
    if (self != nil)
    {
        _blurView = [[BlurView alloc] init];
        _blurFactor = Settings.singleton.blurFactor;
    }
    return self;
}


- (void)decodeObjectWithCoder:(NSCoder *)decoder
{
    [super decodeObjectWithCoder:decoder];
    
    _blurFactor = [decoder decodeFloatForKey:@"blurFactor"];
}

- (void)encodeWithCoder:(NSCoder *)encoder
{
    [super encodeWithCoder:encoder];
    
    [encoder encodeFloat:_blurFactor forKey:@"blurFactor"];
}


- (void)setVisible:(BOOL)visible
{
    [super setVisible:visible];
    
    _blurView.hidden = !visible;
}


- (id)initFromFolloweeShape:(Blur *)drawingShape
{
    self = [super initFromFolloweeShape:drawingShape];
    if (self != nil)
    {
        _blurView = [[BlurView alloc] initWithFrame:drawingShape.blurView.frame];
        _blurView.blurImageView.image = drawingShape.blurView.blurImageView.image;
        [drawingShape.blurView.superview addSubview:self.blurView];
        
        _blurFactor = drawingShape.blurFactor;
    }
    
    return self;
}

- (NSInteger)checkHitBorderShape:(CGPoint)point
{
    if (!self.visible)
    {
        return NSNotFound;
    }
    
    if ([self.tapSelectedBorder containsPoint:point])
    {
        CGFloat deltaX = point.x - self.path.bounds.origin.x;
        CGFloat deltaY = point.y - self.path.bounds.origin.y;
        if (deltaX < self.path.bounds.size.width / 5)
        {
            if (deltaY < self.path.bounds.size.height / 5)
            {
                return 0;
            }
            else if (deltaY < 4 * self.path.bounds.size.height / 5)
            {
                return 7;
            }
            return 6;
        }
        else if (deltaX < 4 * self.path.bounds.size.width / 5)
        {
            if (deltaY < self.path.bounds.size.height / 5)
            {
                return 1;
            }
            else if (deltaY > 4 * self.path.bounds.size.height / 5)
            {
                return 5;
            }
        }
        else
        {
            if (deltaY < self.path.bounds.size.height / 5)
            {
                return 2;
            }
            else if (deltaY < 4 * self.path.bounds.size.height / 5)
            {
                return 3;
            }
            return 4;
        }
    }
    
    return NSNotFound;
}


- (void)addTapSelectedBorder
{
    if (self.selectedBorder == nil)
    {
        self.tapSelectedBorder = nil;
        return;
    }
    
    CGRect bound = self.path.bounds;
    UIBezierPath *path = [UIBezierPath bezierPathWithRect:bound];
    path.lineWidth = self.path.lineWidth;
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
    
    CGRect rects[8] = {
        CGRectMake(CGRectGetMinX(self.path.bounds) - SELECTED_BORDER_WIDTH - self.path.lineWidth / 2,
                   CGRectGetMinY(self.path.bounds) - SELECTED_BORDER_WIDTH - self.path.lineWidth / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(CGRectGetMidX(self.path.bounds) - SELECTED_BORDER_WIDTH / 2,
                   CGRectGetMinY(self.path.bounds) - SELECTED_BORDER_WIDTH - self.path.lineWidth / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(CGRectGetMaxX(self.path.bounds) + self.path.lineWidth / 2,
                   CGRectGetMinY(self.path.bounds) - SELECTED_BORDER_WIDTH - self.path.lineWidth / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(CGRectGetMaxX(self.path.bounds) + self.path.lineWidth / 2,
                   CGRectGetMidY(self.path.bounds) - SELECTED_BORDER_WIDTH / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(CGRectGetMaxX(self.path.bounds) + self.path.lineWidth / 2,
                   CGRectGetMaxY(self.path.bounds) + self.path.lineWidth / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(CGRectGetMidX(self.path.bounds) - SELECTED_BORDER_WIDTH / 2,
                   CGRectGetMaxY(self.path.bounds) + self.path.lineWidth / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(CGRectGetMinX(self.path.bounds) - SELECTED_BORDER_WIDTH - self.path.lineWidth / 2,
                   CGRectGetMaxY(self.path.bounds) + self.path.lineWidth / 2,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH),
        CGRectMake(CGRectGetMinX(self.path.bounds) - SELECTED_BORDER_WIDTH - self.path.lineWidth / 2,
                   self.path.bounds.origin.y + self.path.bounds.size.height / 2 - 5.0f,
                   SELECTED_BORDER_WIDTH, SELECTED_BORDER_WIDTH)
    };
    
    for (int i = 0; i < 8; i++)
    {
        UIBezierPath *path = [UIBezierPath bezierPathWithOvalInRect:rects[i]];
        path.lineWidth = 2;
        [self.selectedBorder addObject:path];
    }
}

- (void)changeImageOfBlurShape:(UIImage *)blurImage
{
    if (!blurImage)
    {
        return;
    }
    
    CGImageRef image = CGImageCreateWithImageInRect(blurImage.CGImage, self.path.bounds);
    UIImage *cropedImage = [UIImage imageWithCGImage:image];
    CGImageRelease(image);
    _blurView.blurImageView.image = [cropedImage boxblurImageWithBlur:_blurFactor];
}


- (void)prepareToRemove
{
    [_blurView removeFromSuperview];
}


@end
