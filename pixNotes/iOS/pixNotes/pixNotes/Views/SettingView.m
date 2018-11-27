//
//  SettingView.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/20/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "SettingView.h"
#import "Colors.h"
#import "Constants.h"


@implementation SettingView

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
    [self setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.5]];
    
    _tapView = [[UIView alloc] init];
    [_tapView setBackgroundColor:[UIColor clearColor]];
    [self addSubview:_tapView];
    UITapGestureRecognizer *tapOutside = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapOutside:)];
    [_tapView addGestureRecognizer:tapOutside];
    
    _contentView = [[UIView alloc] init];
    _contentView.layer.cornerRadius = 10;
    _contentView.layer.masksToBounds = YES;
    [_contentView setBackgroundColor:BACKGROUND_POPUP_VIEW];
    [self addSubview:_contentView];
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    CGFloat contentSizeWidth = [self getContentViewWidth];
    CGFloat contentSizeHeight = [self getContentViewHeight];
    
    [_tapView setFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
    [_contentView setFrame:CGRectMake((self.frame.size.width - contentSizeWidth) / 2,
                                      (self.frame.size.height - contentSizeHeight) / 2,
                                      contentSizeWidth, contentSizeHeight)];
}

- (void)handleTapOutside:(UITapGestureRecognizer *)recognizer
{
    CGPoint tapLocation = [recognizer locationInView:self];
    if( tapLocation.x < _contentView.frame.origin.x
       || tapLocation.x > _contentView.frame.origin.x + _contentView.frame.size.width
       || tapLocation.y < _contentView.frame.origin.y
       || tapLocation.y > _contentView.frame.origin.y + _contentView.frame.size.height )
    {
        [self closeSettingViewAndApplyNewChanges];
    }
}

- (void)closeSettingViewAndApplyNewChanges
{
    
}


- (CGFloat)getContentViewWidth
{
    if (IS_IPAD())
    {
        return 350;
    }

    // Iphone
    return 250;
}

- (CGFloat)getContentViewHeight
{
    return [self getContentViewWidth];
}

@end
