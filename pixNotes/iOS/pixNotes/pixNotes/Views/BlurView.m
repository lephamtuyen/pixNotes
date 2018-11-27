//
//  BlurView.m
//  pixNotes
//
//  Created by Tuyen Le on 12/27/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "BlurView.h"

@implementation BlurView

- (id)init
{
    self = [super init];
    if (self)
    {
        [self setBackgroundColor:[UIColor clearColor]];
        [self initSubviews];
    }
    return self;
}


- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self setBackgroundColor:[UIColor clearColor]];
        [self initSubviews];
    }
    return self;
}

- (void)initSubviews
{
    _blurImageView = [[UIImageView alloc] init];
    [_blurImageView setBackgroundColor:[UIColor clearColor]];
    [self addSubview:_blurImageView];
}


- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    CGRect blurImageFrame = CGRectMake(0, 0, _blurImageView.image.size.width, _blurImageView.image.size.height);
    if (frame.origin.x < 0)
    {
        blurImageFrame.origin.x = -frame.origin.x;
    }
    if (frame.origin.y < 0)
    {
        blurImageFrame.origin.y = -frame.origin.y;
    }
    
    [_blurImageView setFrame:blurImageFrame];
}


@end
