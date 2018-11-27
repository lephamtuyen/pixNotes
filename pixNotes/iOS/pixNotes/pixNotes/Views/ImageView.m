//
//  ImageView.m
//  pixNotes
//
//  Created by Tuyen Le on 11/27/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "ImageView.h"
#import "Colors.h"

#define BORDER_WIDTH 2

@implementation ImageView

- (id)init
{
    self = [super init];
    if (self)
    {
        [self initSubviews];
    }
    return self;
}

- (void)initSubviews
{
    _borderLeft = [[UIView alloc] init];
    [_borderLeft setBackgroundColor:BORDER_COLOR];
    _borderLeft.layer.cornerRadius = 2;
    _borderLeft.layer.masksToBounds = YES;
    [self addSubview:_borderLeft];
    
    _borderRight = [[UIView alloc] init];
    [_borderRight setBackgroundColor:BORDER_COLOR];
    _borderRight.layer.cornerRadius = 2;
    _borderRight.layer.masksToBounds = YES;
    [self addSubview:_borderRight];
    
    _borderTop = [[UIView alloc] init];
    [_borderTop setBackgroundColor:BORDER_COLOR];
    _borderTop.layer.cornerRadius = 2;
    _borderTop.layer.masksToBounds = YES;
    [self addSubview:_borderTop];
    
    _borderBottom = [[UIView alloc] init];
    [_borderBottom setBackgroundColor:BORDER_COLOR];
    _borderBottom.layer.cornerRadius = 2;
    _borderBottom.layer.masksToBounds = YES;
    [self addSubview:_borderBottom];
    
    _imageView = [[UIImageView alloc] init];
    [self addSubview:_imageView];
    
    _imageViewIndicator = [[UIImageView alloc] init];
    [self addSubview:_imageViewIndicator];
    _imageViewIndicator.autoresizingMask = UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleRightMargin;
    _imageViewIndicator.hidden = YES;
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];

    [self layoutImageView];
}

- (void)setImageViewWithImage:(UIImage *)image
{
    _imageView.image = image;
    
    [self layoutImageView];
}

- (void)setHighlightBorder:(BOOL)highlightBorder
{
    _highlightBorder = highlightBorder;
    
    if (_highlightBorder)
    {
        [_borderLeft setBackgroundColor:BACKGROUND_GREEN_COLOR];
        [_borderRight setBackgroundColor:BACKGROUND_GREEN_COLOR];
        [_borderBottom setBackgroundColor:BACKGROUND_GREEN_COLOR];
        [_borderTop setBackgroundColor:BACKGROUND_GREEN_COLOR];
    }
    else
    {
        [_borderLeft setBackgroundColor:BORDER_COLOR];
        [_borderRight setBackgroundColor:BORDER_COLOR];
        [_borderBottom setBackgroundColor:BORDER_COLOR];
        [_borderTop setBackgroundColor:BORDER_COLOR];
    }
    
    [self layoutBorderFrame:_highlightBorder];
}

- (void)layoutImageView
{
    if (_imageView.image)
    {
        CGFloat imageFactor = _imageView.image.size.width / _imageView.image.size.height;
        CGFloat viewFactor = self.frame.size.width / self.frame.size.height;
        if (imageFactor > viewFactor)
        {
            [_imageView setFrame:CGRectMake(2 * BORDER_WIDTH,
                                            (self.frame.size.height - self.frame.size.width / imageFactor) / 2 + 2 * BORDER_WIDTH,
                                            self.frame.size.width - 4 * BORDER_WIDTH,
                                            self.frame.size.width / imageFactor - 4 * BORDER_WIDTH)];
        }
        else
        {
            [_imageView setFrame:CGRectMake((self.frame.size.width - self.frame.size.height * imageFactor) / 2 + 2 * BORDER_WIDTH,
                                            2 * BORDER_WIDTH,
                                            self.frame.size.height * imageFactor - 4 * BORDER_WIDTH,
                                            self.frame.size.height - 4 * BORDER_WIDTH)];
        }
        
        [_imageViewIndicator setFrame:CGRectMake(_imageView.frame.origin.x + _imageView.frame.size.width - 2 * _indicatorWidth / 3,
                                                 _imageView.frame.origin.y + _imageView.frame.size.height - 2 * _indicatorHeight / 3,
                                                 _indicatorWidth, _indicatorHeight)];
        
        [self layoutBorderFrame:_highlightBorder];
        
    }
}

- (void)layoutBorderFrame:(BOOL)isHighLight
{
    CGFloat borderWidth = isHighLight ? 2 * BORDER_WIDTH : BORDER_WIDTH;
    [_borderTop setFrame:CGRectMake(_imageView.frame.origin.x - borderWidth,
                                    _imageView.frame.origin.y - borderWidth,
                                    _imageView.frame.size.width + 2 * borderWidth, borderWidth)];
    [_borderRight setFrame:CGRectMake(_imageView.frame.origin.x + _imageView.frame.size.width,
                                      _borderTop.frame.origin.y, borderWidth,
                                      _imageView.frame.size.height + 2 * borderWidth)];
    [_borderBottom setFrame:CGRectMake(_borderTop.frame.origin.x,
                                       _imageView.frame.origin.y + _imageView.frame.size.height,
                                       _borderTop.frame.size.width, borderWidth)];
    [_borderLeft setFrame:CGRectMake(_borderTop.frame.origin.x,
                                     _borderTop.frame.origin.y,
                                     borderWidth, _borderRight.frame.size.height)];
}

- (void)dealloc
{
    _imageViewIndicator = nil;
    _imageView = nil;
}

@end
