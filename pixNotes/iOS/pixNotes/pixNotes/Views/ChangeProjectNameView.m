//
//  ChangeProjectNameView.m
//  pixNotes
//
//  Created by Tuyen Le on 1/15/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "ChangeProjectNameView.h"
#import "ProjectSettings.h"
#import "Constants.h"

@implementation ChangeProjectNameView

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
    
    _projectNameTextField = [[UITextField alloc] init];
    _projectNameTextField.placeholder = @"Project Name";
    _projectNameTextField.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16];
    _projectNameTextField.layer.cornerRadius = 5;
    _projectNameTextField.layer.masksToBounds = YES;
    _projectNameTextField.backgroundColor = [UIColor whiteColor];
    [_projectNameTextField setClearButtonMode:UITextFieldViewModeAlways];
    [_projectNameTextField setAutocapitalizationType:UITextAutocapitalizationTypeWords];
    [_projectNameTextField setAutocorrectionType:UITextAutocorrectionTypeNo];
    [_projectNameTextField setContentVerticalAlignment:UIControlContentVerticalAlignmentCenter];
    _projectNameTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    UIView *leftPaddingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 5, 28)];
    _projectNameTextField.leftView = leftPaddingView;
    _projectNameTextField.leftViewMode = UITextFieldViewModeAlways;
    [_projectNameTextField setReturnKeyType:UIReturnKeyDone];
    [_projectNameTextField addTarget:self
                       action:@selector(textFieldFinished:)
             forControlEvents:UIControlEventEditingDidEndOnExit];
    
    [self addSubview:_projectNameTextField];
    [_projectNameTextField becomeFirstResponder];
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    _contentWidth = 240;
    _contentHeight = 28;
    
    [_tapView setFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
    [_projectNameTextField setFrame:CGRectMake((self.frame.size.width - _contentWidth) / 2,
                                               (self.frame.size.height - _contentHeight) / 2,
                                               _contentWidth, _contentHeight)];
}


- (void)loadProjectDirectory:(NSString *)projectDirectory
{
    _projectDirectory = projectDirectory;
    
    // Read projectName
    NSString *projectFileNamePath = [_projectDirectory stringByAppendingPathComponent:FileProjectSettingsFile];
    ProjectSettings *projectSettings = [NSKeyedUnarchiver unarchiveObjectWithFile:projectFileNamePath];
    
    _projectNameTextField.text = projectSettings.projectName;
}

- (void)handleTapOutside:(UITapGestureRecognizer *)recognizer
{
    CGPoint tapLocation = [recognizer locationInView:self];
    if( tapLocation.x < _projectNameTextField.frame.origin.x
       || tapLocation.x > _projectNameTextField.frame.origin.x + _projectNameTextField.frame.size.width
       || tapLocation.y < _projectNameTextField.frame.origin.y
       || tapLocation.y > _projectNameTextField.frame.origin.y + _projectNameTextField.frame.size.height )
    {
        if (!_projectNameTextField.text || [_projectNameTextField.text isEqualToString:@""])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                            message:@"No Project Name"
                                                           delegate:nil
                                                  cancelButtonTitle:@"OK"
                                                  otherButtonTitles:nil];
            [alert show];
            return;
        }
        
        [self updateNewProjectName];
        [_delegate closeChangeProjectNameView];
    }
}

- (void)textFieldFinished:(id)sender
{
    [self updateNewProjectName];
    [_delegate closeChangeProjectNameView];
}

- (void)updateNewProjectName
{
    //Save project name
    NSString *projectSettingsPath = [_projectDirectory stringByAppendingPathComponent:FileProjectSettingsFile];
    ProjectSettings *projectSettings = [NSKeyedUnarchiver unarchiveObjectWithFile:projectSettingsPath];
    projectSettings.projectName = [_projectNameTextField.text isEqualToString:@""] ? _projectNameTextField.placeholder : _projectNameTextField.text;
    [NSKeyedArchiver archiveRootObject:projectSettings toFile:projectSettingsPath];
}

#pragma mark - Handle keyboard

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
    
    CGRect newFrame = [_projectNameTextField frame];
    
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
        
        newFrame.origin.y -= keyboardHeight - (self.frame.size.height - newFrame.size.height - newFrame.origin.y) + 10;
        [_projectNameTextField setFrame:newFrame];
        
        [UIView commitAnimations];
    }
    
    _keyboardVisible = YES;
}

-(void)keyboardWillHide
{
    CGRect newFrame = [_projectNameTextField frame];
    newFrame.origin.y = (self.frame.size.height - _contentHeight) / 2;
    [_projectNameTextField setFrame:newFrame];
    
    _keyboardVisible = NO;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)close
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self removeFromSuperview];
}

@end
