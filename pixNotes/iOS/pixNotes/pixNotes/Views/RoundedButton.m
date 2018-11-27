//
//  RoundedButton.m
//  pixNotes
//
//  Created by Tuyen Le on 2/20/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "RoundedButton.h"

@implementation RoundedButton

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self initView];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self)
    {
        [self initView];
    }
    
    return self;
}

- (id)init
{
    self = [super init];
    if (self)
    {
        [self initView];
    }
    
    return self;
}

- (void)initView
{
    self.layer.cornerRadius = 5;
    self.layer.masksToBounds = YES;
}

@end
