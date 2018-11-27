//
//  TakeDescriptionView.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/21/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "TakeDescriptionView.h"
#import "Colors.h"

#define CONTENT_WIDTH           250
#define CONTENT_WIDTH_IPAD      500
#define CONTENT_HEIGHT          150
#define CONTENT_HEIGHT_IPAD     300

#define SAVE_IMAGE_WIDTH        30
#define MARGIN                  20

@implementation TakeDescriptionView

- (id)init
{
    self = [super init];
    if (self)
    {
        [self initSubviews];
        
        _keyboardVisible = NO;
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide) name:UIKeyboardWillHideNotification object:nil];
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
    [_contentView setBackgroundColor:BACKGROUND_POPUP_VIEW];
    _contentView.layer.cornerRadius = 10;
    _contentView.layer.masksToBounds = YES;
    [self addSubview:_contentView];
    
    _textView = [[UITextView alloc] init];
    [_textView setBackgroundColor:[UIColor clearColor]];
    _textView.textColor = GRAY_TEXT_COLOR;
    _textView.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16];
    [_contentView addSubview:_textView];
    
    _saveBtn = [[UIButton alloc] init];
    [_saveBtn setBackgroundColor:[UIColor clearColor]];
    [_saveBtn setImage:[UIImage imageNamed:@"OK.png"] forState:UIControlStateNormal];
    [_saveBtn addTarget:self action:@selector(saveDescription) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:_saveBtn];
}


- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)
    {
        _contentWidth = CONTENT_WIDTH;
        _contentHeight = CONTENT_HEIGHT;
    }
    else
    {
        _contentWidth = CONTENT_WIDTH_IPAD;
        _contentHeight = CONTENT_HEIGHT_IPAD;
    }
    
    [_tapView setFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
    [_contentView setFrame:CGRectMake((self.frame.size.width - _contentWidth) / 2, 80, _contentWidth, _contentHeight)];
    [_textView setFrame:CGRectMake(10, 10, _contentWidth - MARGIN, _contentHeight - MARGIN)];
    [_saveBtn setFrame:CGRectMake(_contentWidth - SAVE_IMAGE_WIDTH - 10, _contentHeight - SAVE_IMAGE_WIDTH - 10, SAVE_IMAGE_WIDTH, SAVE_IMAGE_WIDTH)];
}


- (void)saveDescription
{
    if (_delegate && [_delegate respondsToSelector:@selector(takeDescriptionView:description:)])
    {
        [_delegate takeDescriptionView:self description:_textView.text];
    }
}

- (void)loadDescription:(NSString *)description
{
    _textView.text = description;
    
    [_textView becomeFirstResponder];
}

- (void)handleTapOutside:(UITapGestureRecognizer *)recognizer
{
    CGPoint tapLocation = [recognizer locationInView:self];
    if( tapLocation.x < _contentView.frame.origin.x
       || tapLocation.x > _contentView.frame.origin.x + _contentView.frame.size.width
       || tapLocation.y < _contentView.frame.origin.y
       || tapLocation.y > _contentView.frame.origin.y + _contentView.frame.size.height )
    {
        [_delegate closeTakeDescriptionView];
    }
}

- (CGFloat)getHeightOfKeyboard:(CGRect)rect
{
    if (UIInterfaceOrientationIsLandscape([[UIApplication sharedApplication] statusBarOrientation]))
    {
        return rect.size.width;
    }

    // Portrait
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
    
    CGRect newFrame = [_contentView frame];
    
    if (self.frame.size.height - keyboardHeight < newFrame.origin.y + newFrame.size.height)
    {
        // Get animation info from userInfo
        NSTimeInterval animationDuration;
        UIViewAnimationCurve animationCurve;
        
        [[[aNotification userInfo] objectForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&animationCurve];
        [[[aNotification userInfo] objectForKey:UIKeyboardAnimationDurationUserInfoKey] getValue:&animationDuration];
        
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:animationDuration];
        [UIView setAnimationCurve:animationCurve];
        
        newFrame.origin.y -= keyboardHeight - (self.frame.size.height - newFrame.size.height - newFrame.origin.y);
        [_contentView setFrame:newFrame];
        
        [UIView commitAnimations];
    }
    
    _keyboardVisible = YES;
}

-(void)keyboardWillHide
{
    CGRect newFrame = [_contentView frame];
    newFrame.origin.y = (self.frame.size.height - _contentHeight) / 2;
    [_contentView setFrame:newFrame];
    
    _keyboardVisible = NO;
}

-(void)textViewDidEndEditing:(UITextView *)textView
{
    [textView resignFirstResponder];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
