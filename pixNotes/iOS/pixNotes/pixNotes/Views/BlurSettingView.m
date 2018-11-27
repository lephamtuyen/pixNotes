//
//  BlurSettingView.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 1/9/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "BlurSettingView.h"
#import "Colors.h"


@implementation BlurSettingView


- (void)initSubviews
{
    [super initSubviews];
    
    _blurLabel = [[UILabel alloc] init];
    [_blurLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:20]];
    [_blurLabel setBackgroundColor:[UIColor clearColor]];
    [_blurLabel setTextColor:GRAY_TEXT_COLOR];
    _blurLabel.text = @"Opacity";
    [self.contentView addSubview:_blurLabel];
    
    _sliderView = [[UISlider alloc] init];
    [_sliderView setValue:0.5f];
    [_sliderView setMinimumValue:0.0f];
    [_sliderView setMaximumValue:1.0f];
    [_sliderView addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
    UITapGestureRecognizer *sliderTapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(sliderTapped:)];
    [_sliderView setThumbImage:[UIImage imageNamed:@"thumb_yellow.png"] forState:UIControlStateNormal];
    [_sliderView setThumbImage:[UIImage imageNamed:@"thumb_yellow.png"] forState:UIControlStateHighlighted];
    [_sliderView addGestureRecognizer:sliderTapGesture];
    [_sliderView setMinimumTrackTintColor:GRAY_TEXT_COLOR];
    [_sliderView setMaximumTrackTintColor:GRAY_TEXT_COLOR];
    [self.contentView addSubview:_sliderView];
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    CGFloat _contentSizeWidth = [self getContentViewWidth];
    [_blurLabel setFrame:CGRectMake(_contentSizeWidth * 0.05, _contentSizeWidth * 0.05, _contentSizeWidth * 0.5, _contentSizeWidth * 0.125)];
    [_sliderView setFrame:CGRectMake(_blurLabel.frame.origin.x, _contentSizeWidth / 6, _contentSizeWidth * 0.9, _contentSizeWidth * 0.125)];
}

- (void)closeSettingViewAndApplyNewChanges
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(closeSettingView)])
    {
        [self.delegate closeSettingView];
    }
}

- (void)loadBlurFactor:(CGFloat)blurFactor
{
    [_sliderView setValue:blurFactor];
}


#pragma mark pan slider

- (void)valueChanged:(UISlider*)sender
{
    [self.delegate settingView:self choosenBlurFactor:sender.value];
}


#pragma mark tap slider

- (void)sliderTapped:(UIGestureRecognizer *)gesture
{
    UISlider* slider = (UISlider*)gesture.view;
    
    // Tap on thumb
    if (slider.highlighted)
    {
        return;
    }
    
    CGPoint point = [gesture locationInView:slider];
    CGFloat percentage = point.x / slider.bounds.size.width;
    CGFloat delta = percentage * (slider.maximumValue - slider.minimumValue);
    CGFloat value = slider.minimumValue + delta;
    
    [slider setValue:value animated:YES];
    [self.delegate settingView:self choosenBlurFactor:value];
}

- (CGFloat)getContentViewHeight
{
    return [self getContentViewWidth] / 3;
}

@end
