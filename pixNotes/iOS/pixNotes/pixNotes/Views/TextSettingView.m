//
//  TextSettingView.m
//  pixNotes
//
//  Created by Tuyen Le on 1/21/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "TextSettingView.h"
#import "Colors.h"
#import "Constants.h"

@implementation TextSettingView

- (id)init
{
    self = [super init];
    
    if (self)
    {
        _keyboardVisible = NO;
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide) name:UIKeyboardWillHideNotification object:nil];
        
        UITapGestureRecognizer *tapToDismissKeyboard = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self
                                       action:@selector(dismissKeyboard)];
        tapToDismissKeyboard.delegate = self;
        [self.contentView addGestureRecognizer:tapToDismissKeyboard];
    }
    
    return self;
}

- (void)initTextField
{
    _textField = [[UITextField alloc] init];
    _textField.placeholder = @"Text";
    _textField.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16];
    _textField.layer.cornerRadius = 5;
    _textField.layer.masksToBounds = YES;
    _textField.backgroundColor = [UIColor whiteColor];
    [_textField setClearButtonMode:UITextFieldViewModeAlways];
    [_textField setAutocapitalizationType:UITextAutocapitalizationTypeNone];
    [_textField setAutocorrectionType:UITextAutocorrectionTypeNo];
    [_textField setContentVerticalAlignment:UIControlContentVerticalAlignmentCenter];
    UIView *leftPaddingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 5, 28)];
    _textField.leftView = leftPaddingView;
    _textField.leftViewMode = UITextFieldViewModeAlways;
    [self.contentView addSubview:_textField];
}

- (void)initFontSizeLabel
{
    _fontSizeLabel = [[UILabel alloc] init];
    [_fontSizeLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:20]];
    [_fontSizeLabel setBackgroundColor:[UIColor clearColor]];
    [_fontSizeLabel setTextColor:GRAY_TEXT_COLOR];
    _fontSizeLabel.text = @"Font Size";
    [self.contentView addSubview:_fontSizeLabel];
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

- (void)initFontSizeButton
{
    NSString *arrayTitle[3] = {@"Small", @"Medium", @"Larger"};
    UIControlContentHorizontalAlignment horizontalAlignment[3] = {
        UIControlContentHorizontalAlignmentLeft,
        UIControlContentHorizontalAlignmentCenter,
        UIControlContentHorizontalAlignmentRight
    };
    _fontSizeButtons = [[NSMutableArray alloc] init];
    _sizeFontViews = [[NSMutableArray alloc] init];
    for (int i = 0; i < 3; i++)
    {
        UIButton *button = [[UIButton alloc] init];
        [button setTag:i];
        [button setBackgroundColor:[UIColor clearColor]];
        [button.titleLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:14]];
        [button setTitleColor:GRAY_TEXT_COLOR forState:UIControlStateNormal];
        [button setTitle:arrayTitle[i] forState:UIControlStateNormal];
        [button addTarget:self action:@selector(fontSize:) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:button];
        [_fontSizeButtons addObject:button];
        
        UIView *view = [[UIView alloc] init];
        view = [[UIView alloc] init];
        [view setBackgroundColor:[UIColor redColor]];
        [button addSubview:view];
        [_sizeFontViews addObject:view];
        
        button.contentHorizontalAlignment = horizontalAlignment[i];
        button.contentVerticalAlignment = UIControlContentVerticalAlignmentTop;
    }
}

- (void)initFontColorLabel
{
    _colorLabel = [[UILabel alloc] init];
    [_colorLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:20]];
    [_colorLabel setBackgroundColor:[UIColor clearColor]];
    [_colorLabel setTextColor:GRAY_TEXT_COLOR];
    _colorLabel.text = @"Color";
    [self.contentView addSubview:_colorLabel];
}

- (void)initColorButton
{
    NSString *imageList[6] = {@"red.png", @"yellow.png", @"blue.png", @"green.png", @"gray.png", @"white.png"};
    _colorButtons = [[NSMutableArray alloc] init];
    for (int i = 0; i < 6; i++)
    {
        UIButton *button = [[UIButton alloc] init];
        [button setTag:i];
        [button setImage:[UIImage imageNamed:imageList[i]] forState:UIControlStateNormal];
        [button setImage:[UIImage imageNamed:imageList[i]] forState:UIControlStateHighlighted];
        [button addTarget:self action:@selector(textColor:) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:button];
        
        [_colorButtons addObject:button];
    }
}

- (void)initSubviews
{
    [super initSubviews];
    
    [self initTextField];
    
    [self initFontSizeLabel];
    
    [self initSliderView];
    
    [self initFontSizeButton];
    
    [self initFontColorLabel];
    
    [self initColorButton];
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    // Get content width of content view
    CGFloat _contentSize = [self getContentViewWidth];
    
    // Set frame of subviews
    [_textField setFrame:CGRectMake(_contentSize * 0.05, _contentSize * 0.05, _contentSize * 0.9, 28)];
    [_fontSizeLabel setFrame:CGRectMake(_textField.frame.origin.x,
                                        _textField.frame.origin.y + _textField.frame.size.height + 5,
                                        _contentSize * 0.5, _contentSize * 0.125)];
    [_sliderView setFrame:CGRectMake(_fontSizeLabel.frame.origin.x,
                                     _fontSizeLabel.frame.origin.y + _fontSizeLabel.frame.size.height,
                                     _contentSize * 0.9, _contentSize * 0.125)];
    
    UIButton *sizeButton0 = [_fontSizeButtons objectAtIndex:0];
    UIButton *sizeButton1 = [_fontSizeButtons objectAtIndex:1];
    UIButton *sizeButton2 = [_fontSizeButtons objectAtIndex:2];
    [sizeButton0 setFrame:CGRectMake(_fontSizeLabel.frame.origin.x,
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
        UIButton *button = [_fontSizeButtons objectAtIndex:i];
        UIView *view = [_sizeFontViews objectAtIndex:i];
        [view setFrame:CGRectMake(0,
                                  button.frame.size.height * (0.9 - 0.1 * i),
                                  button.frame.size.width, button.frame.size.height * (0.1 + 0.1 * i))];
    }
    
    [_colorLabel setFrame:CGRectMake(_contentSize * 0.05,
                                     sizeButton0.frame.origin.y + sizeButton0.frame.size.height + 5,
                                     _contentSize * 0.5, _contentSize * 0.125)];
    
    // Set frame of circles button
    
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

- (void)loadFontSize:(CGFloat)fontSize color:(UIColor *)color text:(NSString *)text
{
    _fontSize = fontSize;
    _textColor = color;
    _textField.text = text;
    
    NSInteger indexColor = [self getCurrentIndexColor];
    [self focusOnColorButton:indexColor];
    [self refreshThumbColorAndSizeBorderColorWithColorIndex:indexColor];
    
    NSInteger indexLineWidth = [self getCurrentIndexFontSize];
    [_sliderView setValue:indexLineWidth];
}

- (void)closeSettingViewAndApplyNewChanges
{
    if (!_textField.text || [_textField.text isEqualToString:@""])
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                        message:@"Don't allow empty text"
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
        return;
    }
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(settingView:choosenFontSize:choosenColor:choosenText:)])
    {
        [self.delegate settingView:self choosenFontSize:_fontSize choosenColor:_textColor choosenText:_textField.text];
    }
}

- (void)changeFontSize:(NSInteger)index
{
    if(index == 0)
    {
        _fontSize = SMALL_FONT;
    }
    else if(index == 1)
    {
        _fontSize = MEDIUM_FONT;
    }
    else if (index == 2)
    {
        _fontSize = LARGER_FONT;
    }
    else
    {
        _fontSize = MEDIUM_FONT;
    }
}


- (void)changeLineColor:(NSInteger)index
{
    if(index == 0)
    {
        _textColor = RED_COLOR;
    }
    else if(index == 1)
    {
        _textColor = YELLOW_COLOR;
    }
    else if (index == 2)
    {
        _textColor = BLUE_COLOR;
    }
    else if (index == 3)
    {
        _textColor = GREEN_COLOR;
    }
    else if (index == 4)
    {
        _textColor = GRAY_COLOR;
    }
    else if (index == 5)
    {
        _textColor = WHITE_COLOR;
    }
    else
    {
        _textColor = RED_COLOR;
    }
}

- (NSInteger)getCurrentIndexColor
{
    if([_textColor isEqual:RED_COLOR])
    {
        return 0;
    }
    else if([_textColor isEqual:YELLOW_COLOR])
    {
        return 1;
    }
    else if([_textColor isEqual:BLUE_COLOR])
    {
        return 2;
    }
    else if([_textColor isEqual:GREEN_COLOR])
    {
        return 3;
    }
    else if([_textColor isEqual:GRAY_COLOR])
    {
        return 4;
    }
    else if([_textColor isEqual:WHITE_COLOR])
    {
        return 5;
    }
    
    // Default is red color
    return 0;
}


- (NSInteger)getCurrentIndexFontSize
{
    if(_fontSize == SMALL_FONT)
    {
        return 0;
    }
    else if(_fontSize == MEDIUM_FONT)
    {
        return 1;
    }
    else if(_fontSize == LARGER_FONT)
    {
        return 2;
    }
    
    // Default is medium size
    return 1;
}


#pragma mark Button action


- (void)fontSize:(id)sender
{
    UIButton *button = (UIButton *)sender;
    _sliderView.value = button.tag;
    
    [self changeFontSize:button.tag];
}

- (void)textColor:(id)sender
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
        [[_sizeFontViews objectAtIndex:i] setBackgroundColor:_textColor];
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
    
    [self changeFontSize:index];
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
    
    [self changeFontSize:index];
}

- (CGFloat)getContentViewHeight
{
    if (IS_IPAD())
    {
        return 375;
    }

    // iPhone
    return 275;
}

#pragma mark - Handle keyboard

- (CGFloat)getHeightOfKeyboard:(CGRect)rect
{
    if UIInterfaceOrientationIsLandscape([[UIApplication sharedApplication] statusBarOrientation])
    {
        return rect.size.width;
    }
    
    // Portrait (Adapt with convention of CI)
    return rect.size.height;
}

-(void)keyboardWillShow:(NSNotification *)aNotification
{
    if (_keyboardVisible)
    {
        return;
    }
    
    CGRect keyboardFrame = [[[aNotification userInfo]
                             objectForKey:UIKeyboardFrameBeginUserInfoKey]
                            CGRectValue];
    CGFloat keyboardHeight = [self getHeightOfKeyboard:keyboardFrame];
    
    CGRect newFrame = [self.contentView frame];
    
    if (self.frame.size.height - keyboardHeight < _textField.frame.origin.y + _textField.frame.size.height)
    {
        // Get animation info from userInfo
        NSTimeInterval animationDuration;
        UIViewAnimationCurve animationCurve;
        
        [[[aNotification userInfo] objectForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&animationCurve];
        [[[aNotification userInfo] objectForKey:UIKeyboardAnimationDurationUserInfoKey] getValue:&animationDuration];
        
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:animationDuration];
        [UIView setAnimationCurve:animationCurve];
        
        newFrame.origin.y -= keyboardHeight - (self.frame.size.height - newFrame.size.height - newFrame.origin.y) + 10;
        [self.contentView setFrame:newFrame];
        
        [UIView commitAnimations];
    }
    
    _keyboardVisible = YES;
}

-(void)keyboardWillHide
{
    CGRect newFrame = [self.contentView frame];
    newFrame.origin.y = (self.frame.size.height - [self getContentViewHeight]) / 2;
    [self.contentView setFrame:newFrame];
    
    _keyboardVisible = NO;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void)dismissKeyboard
{
    [_textField resignFirstResponder];
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch {
    if ([touch.view isKindOfClass:[UIControl class]])
    {
        // we touched a button, slider, or other UIControl
        // ignore the touch
        return NO;
    }
    
    // handle the touch
    return YES;
}

@end
