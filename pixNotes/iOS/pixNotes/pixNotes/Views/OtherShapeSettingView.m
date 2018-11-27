//
//  OtherSettingView.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 1/9/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "OtherShapeSettingView.h"
#import "Colors.h"
#import "Constants.h"

@implementation OtherShapeSettingView

- (void)initBorderSizeLabel
{
    _borderSizeLabel = [[UILabel alloc] init];
    [_borderSizeLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:20]];
    [_borderSizeLabel setBackgroundColor:[UIColor clearColor]];
    [_borderSizeLabel setTextColor:GRAY_TEXT_COLOR];
    _borderSizeLabel.text = @"Border Size";
    [self.contentView addSubview:_borderSizeLabel];
}

- (void)initSliderView
{
    _sliderView = [[UISlider alloc] init];
    [_sliderView setValue:1];
    [_sliderView setMinimumValue:0];
    [_sliderView setMaximumValue:2];
    [_sliderView addTarget:self action:@selector(valueChanged:) forControlEvents:UIControlEventValueChanged];
    UITapGestureRecognizer *sliderTapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(sliderTapped:)];
    [_sliderView addGestureRecognizer:sliderTapGesture];
    [_sliderView setMinimumTrackTintColor:GRAY_TEXT_COLOR];
    [_sliderView setMaximumTrackTintColor:GRAY_TEXT_COLOR];
    [self.contentView addSubview:_sliderView];
}

- (void)initBorderSizeButtons
{
    NSString *arrayTitle[3] = {@"Small", @"Medium", @"Larger"};
    UIControlContentHorizontalAlignment horizontalAlignment[3] = {
        UIControlContentHorizontalAlignmentLeft,
        UIControlContentHorizontalAlignmentCenter,
        UIControlContentHorizontalAlignmentRight
    };
    _borderSizeButtons = [[NSMutableArray alloc] init];
    _sizeBorderViews = [[NSMutableArray alloc] init];
    for (int i = 0; i < 3; i++)
    {
        UIButton *button = [[UIButton alloc] init];
        [button setTag:i];
        [button setBackgroundColor:[UIColor clearColor]];
        [button.titleLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:14]];
        [button setTitleColor:GRAY_TEXT_COLOR forState:UIControlStateNormal];
        [button setTitle:arrayTitle[i] forState:UIControlStateNormal];
        [button addTarget:self action:@selector(borderSize:) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:button];
        [_borderSizeButtons addObject:button];
        
        UIView *view = [[UIView alloc] init];
        view = [[UIView alloc] init];
        [view setBackgroundColor:[UIColor redColor]];
        [button addSubview:view];
        [_sizeBorderViews addObject:view];
        
        button.contentHorizontalAlignment = horizontalAlignment[i];
        button.contentVerticalAlignment = UIControlContentVerticalAlignmentTop;
    }
}

- (void)initColorLabel
{
    _colorLabel = [[UILabel alloc] init];
    [_colorLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:20]];
    [_colorLabel setBackgroundColor:[UIColor clearColor]];
    [_colorLabel setTextColor:GRAY_TEXT_COLOR];
    _colorLabel.text = @"Color";
    [self.contentView addSubview:_colorLabel];
}

- (void)initColorButtons
{
    NSString *imageList[6] = {@"red.png", @"yellow.png", @"blue.png", @"green.png", @"gray.png", @"white.png"};
    _colorButtons = [[NSMutableArray alloc] init];
    for (int i = 0; i < 6; i++)
    {
        UIButton *button = [[UIButton alloc] init];
        [button setTag:i];
        [button setImage:[UIImage imageNamed:imageList[i]] forState:UIControlStateNormal];
        [button setImage:[UIImage imageNamed:imageList[i]] forState:UIControlStateHighlighted];
        [button addTarget:self action:@selector(borderColor:) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:button];
        
        [_colorButtons addObject:button];
    }
}

- (void)initSubviews
{
    [super initSubviews];
    
    [self initBorderSizeLabel];
    
    [self initSliderView];
    
    [self initBorderSizeButtons];
    
    [self initColorLabel];
    
    [self initColorButtons];
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    CGFloat _contentSize = [self getContentViewWidth];
    
    // Set frame of subviews
    [_borderSizeLabel setFrame:CGRectMake(_contentSize * 0.05, _contentSize * 0.05, _contentSize * 0.5, _contentSize * 0.125)];
    [_sliderView setFrame:CGRectMake(_borderSizeLabel.frame.origin.x,
                                     _borderSizeLabel.frame.origin.y + _borderSizeLabel.frame.size.height,
                                     _contentSize * 0.9, _contentSize * 0.125)];
    
    UIButton *sizeButton0 = [_borderSizeButtons objectAtIndex:0];
    UIButton *sizeButton1 = [_borderSizeButtons objectAtIndex:1];
    UIButton *sizeButton2 = [_borderSizeButtons objectAtIndex:2];
    [sizeButton0 setFrame:CGRectMake(_borderSizeLabel.frame.origin.x,
                                     _sliderView.frame.origin.y + _sliderView.frame.size.height,
                                     _contentSize * 0.25, _contentSize * 0.125)];
    [sizeButton1 setFrame:CGRectMake((_contentSize - sizeButton0.frame.size.width) / 2,
                                     sizeButton0.frame.origin.y,
                                     sizeButton0.frame.size.width, sizeButton0.frame.size.height)];
    [sizeButton2 setFrame:CGRectMake(_contentSize * 0.95 - sizeButton0.frame.size.width,
                                     sizeButton0.frame.origin.y,
                                     sizeButton0.frame.size.width, sizeButton0.frame.size.height)];
    
    for (NSInteger i = 0; i < 3; i++)
    {
        UIButton *button = [_borderSizeButtons objectAtIndex:i];
        UIView *view = [_sizeBorderViews objectAtIndex:i];
        [view setFrame:CGRectMake(0,
                                  button.frame.size.height * (0.9 - 0.1 * i),
                                  button.frame.size.width, button.frame.size.height * (0.1 + 0.1 * i))];
    }
    
    // Set frame of color buttons
    [_colorLabel setFrame:CGRectMake(_contentSize * 0.05, _contentSize * 0.45, _contentSize * 0.5, _contentSize * 0.125)];
    
    UIButton *button0 = [_colorButtons objectAtIndex:0];
    UIButton *button1 = [_colorButtons objectAtIndex:1];
    UIButton *button2 = [_colorButtons objectAtIndex:2];
    UIButton *button3 = [_colorButtons objectAtIndex:3];
    UIButton *button4 = [_colorButtons objectAtIndex:4];
    UIButton *button5 = [_colorButtons objectAtIndex:5];
    
    [button0 setFrame:CGRectMake(_contentSize * 0.08,
                                 _colorLabel.frame.origin.y + _colorLabel.frame.size.height,
                                 _contentSize * 0.15, _contentSize * 0.15)];
    [button1 setFrame:CGRectMake(button0.frame.origin.x + button0.frame.size.width + _contentSize * 0.08,
                                 button0.frame.origin.y,
                                 button0.frame.size.width, button0.frame.size.height)];
    [button2 setFrame:CGRectMake(button1.frame.origin.x + button1.frame.size.width + _contentSize * 0.08,
                                 button0.frame.origin.y,
                                 button0.frame.size.width, button0.frame.size.height)];
    [button3 setFrame:CGRectMake(button2.frame.origin.x + button2.frame.size.width + _contentSize * 0.08,
                                 button0.frame.origin.y,
                                 button0.frame.size.width, button0.frame.size.height)];
    [button4 setFrame:CGRectMake(button0.frame.origin.x,
                                 button0.frame.origin.y + _contentSize * 0.2,
                                 button0.frame.size.width, button0.frame.size.height)];
    [button5 setFrame:CGRectMake(button1.frame.origin.x,
                                 button4.frame.origin.y,
                                 button0.frame.size.width, button0.frame.size.height)];

    NSInteger indexColor = [self getCurrentIndexColor];
    [self focusOnColorButton:indexColor];
    [self refreshThumbColorAndSizeBorderColorWithColorIndex:indexColor];
}

- (void)loadLineWidth:(CGFloat)lineWidth color:(UIColor *)color
{
    _lineWidth = lineWidth;
    _lineColor = color;
    
    NSInteger indexColor = [self getCurrentIndexColor];
    [self focusOnColorButton:indexColor];
    [self refreshThumbColorAndSizeBorderColorWithColorIndex:indexColor];
    
    NSInteger indexLineWidth = [self getCurrentIndexLineWidth];
    [_sliderView setValue:indexLineWidth];
}

- (void)closeSettingViewAndApplyNewChanges
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(settingView:choosenLineWidth:choosenColor:)])
    {
        [self.delegate settingView:self choosenLineWidth:_lineWidth choosenColor:_lineColor];
    }
}

- (void)changeLineWidth:(NSInteger)index
{
    if(index == 0)
    {
        _lineWidth = SMALL_BORDER;
    }
    else if(index == 1)
    {
        _lineWidth = MEDIUM_BORDER;
    }
    else if (index == 2)
    {
        _lineWidth = LARGER_BORDER;
    }
    else
    {
        _lineWidth = SMALL_BORDER;
    }
}


- (void)changeLineColor:(NSInteger)index
{
    if(index == 0)
    {
        _lineColor = RED_COLOR;
    }
    else if(index == 1)
    {
        _lineColor = YELLOW_COLOR;
    }
    else if (index == 2)
    {
        _lineColor = BLUE_COLOR;
    }
    else if (index == 3)
    {
        _lineColor = GREEN_COLOR;
    }
    else if (index == 4)
    {
        _lineColor = GRAY_COLOR;
    }
    else if (index == 5)
    {
        _lineColor = WHITE_COLOR;
    }
    else
    {
        _lineColor = RED_COLOR;
    }
}

- (NSInteger)getCurrentIndexColor
{
    if([_lineColor isEqual:RED_COLOR])
    {
        return 0;
    }
    else if([_lineColor isEqual:YELLOW_COLOR])
    {
        return 1;
    }
    else if([_lineColor isEqual:BLUE_COLOR])
    {
        return 2;
    }
    else if([_lineColor isEqual:GREEN_COLOR])
    {
        return 3;
    }
    else if([_lineColor isEqual:GRAY_COLOR])
    {
        return 4;
    }
    else if([_lineColor isEqual:WHITE_COLOR])
    {
        return 5;
    }

    // Default is RED_COLOR
    return 0;
}


- (NSInteger)getCurrentIndexLineWidth
{
    if(_lineWidth == SMALL_BORDER)
    {
        return 0;
    }
    else if(_lineWidth == MEDIUM_BORDER)
    {
        return 1;
    }
    else if(_lineWidth == LARGER_BORDER)
    {
        return 2;
    }

    // Default id SMALL BORDER
    return 0;
}


#pragma mark Button action


- (void)borderSize:(id)sender
{
    UIButton *button = (UIButton *)sender;
    _sliderView.value = button.tag;
    
    [self changeLineWidth:button.tag];
}

- (void)borderColor:(id)sender
{
    UIButton *button = (UIButton *)sender;
    
    NSInteger previousIndex = [self getCurrentIndexColor];
    if (button.tag != previousIndex)
    {
        [self unFocusPreviousColorButton:previousIndex];
        [self focusOnColorButton:button.tag];
        [self changeLineColor:button.tag];
        [self refreshThumbColorAndSizeBorderColorWithColorIndex:button.tag];
    }
}

- (void)refreshThumbColorAndSizeBorderColorWithColorIndex:(NSInteger)index
{
    NSString *imageList[6] = {@"thumb_red.png", @"thumb_yellow.png", @"thumb_blue.png", @"thumb_green.png", @"thumb_gray.png", @"thumb_white.png"};
    [_sliderView setThumbImage:[UIImage imageNamed:imageList[index]] forState:UIControlStateNormal];
    [_sliderView setThumbImage:[UIImage imageNamed:imageList[index]] forState:UIControlStateHighlighted];
    
    for (NSInteger i = 0; i < 3; i++)
    {
        [[_sizeBorderViews objectAtIndex:i] setBackgroundColor:_lineColor];
    }
}

- (void)focusOnColorButton:(NSInteger)index
{
    UIButton *button = [_colorButtons objectAtIndex:index];
    CGFloat _contentSize = [self getContentViewWidth];
    CGRect frame = button.frame;
    frame.origin.x -= _contentSize * 0.025;
    frame.origin.y -= _contentSize * 0.025;
    frame.size.width = 0.2 * _contentSize;
    frame.size.height = 0.2 * _contentSize;
    [button setFrame:frame];
}


- (void)unFocusPreviousColorButton:(NSInteger)index
{
    UIButton *button = [_colorButtons objectAtIndex:index];
    CGFloat _contentSize = [self getContentViewWidth];
    CGRect frame = button.frame;
    frame.origin.x += _contentSize * 0.025;
    frame.origin.y += _contentSize * 0.025;
    frame.size.width = 0.15 * _contentSize;
    frame.size.height = 0.15 * _contentSize;
    [button setFrame:frame];
}

#pragma mark pan slider

- (void)valueChanged:(UISlider*)sender
{
    NSUInteger index = (NSUInteger)(sender.value + 0.5); // Round the number.
    [sender setValue:index animated:NO];
    
    [self changeLineWidth:index];
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
    
    NSUInteger index = (NSUInteger)(value + 0.5); // Round the number.
    [slider setValue:index animated:NO];
    
    [self changeLineWidth:index];
}

@end
