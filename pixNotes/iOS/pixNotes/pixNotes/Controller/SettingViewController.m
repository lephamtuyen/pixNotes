//
//  SettingViewController.m
//  pixNotes
//
//  Created by Tuyen Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import "SettingViewController.h"
#import "MainViewController.h"
#import "Colors.h"
#import "Constants.h"


@implementation SettingViewController

#pragma mark - View lifecycle


- (void)viewDidLoad
{
	[super viewDidLoad];
    
    _autoDetectEnable = Settings.singleton.autoDetectEnable;
    
    _gridEnableSwitchView.delegate = self;
    _autoDetectEnableSwitchView.delegate = self;
    
    // Read projectName
    NSString *projectSettingsPath = [_projectDirectory stringByAppendingPathComponent:FileProjectSettingsFile];
    _projectSettings = [NSKeyedUnarchiver unarchiveObjectWithFile:projectSettingsPath];
    
    _gridColorIndex = [self getIndexOfColor:_projectSettings.gridColor];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    _gridEnableSwitchView.isOn = _projectSettings.gridEnable;
    _autoDetectEnableSwitchView.isOn = _autoDetectEnable;
    
    [self updateGridEnableStatus];
    [self updateGridStyleStatus];
    [self updateGridColorStatus];
    
    if (IS_IPAD())
    {
        [_backBtn setHidden:YES];
    }
}


- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    if (IS_IPAD())
    {
        if(!_tapBehideGesture)
        {
            _tapBehideGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapBehindDetected:)];
            [_tapBehideGesture setNumberOfTapsRequired:1];
            _tapBehideGesture.delegate = self;
            [_tapBehideGesture setCancelsTouchesInView:NO]; //So the user can still interact with controls in the modal view
        }
        
        [self.view.window addGestureRecognizer:_tapBehideGesture];
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    if (IS_IPAD())
    {
        [self.view.window removeGestureRecognizer:_tapBehideGesture];
    }
}

- (void)tapBehindDetected:(UITapGestureRecognizer *)sender
{
    if (sender.state == UIGestureRecognizerStateEnded)
    {
        CGPoint location = [sender locationInView:nil]; //Passing nil gives us coordinates in the window
        
        //Convert tap location into the local view's coordinate system. If outside, dismiss the view.
        if (![self.view pointInside:[self.view convertPoint:location fromView:self.view.window] withEvent:nil])
        {
            Settings.singleton.autoDetectEnable = _autoDetectEnable;
            Settings.singleton.gridEnable = _projectSettings.gridEnable;
            Settings.singleton.gridStyle = _projectSettings.gridStyle;
            Settings.singleton.gridColor = _projectSettings.gridColor;
            
            //Save project name
            NSString *projectSettingsPath = [_projectDirectory stringByAppendingPathComponent:FileProjectSettingsFile];
            [NSKeyedArchiver archiveRootObject:_projectSettings toFile:projectSettingsPath];
            
            [_delegate refreshGridView];
            
            [self.navigationController popViewControllerAnimated:YES];
            [self dismissModalViewControllerAnimated:YES];
        }
    }
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
    return YES;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    _gridEnableSwitchView.delegate = nil;
    _gridEnableSwitchView = nil;
}

- (void)dealloc
{
    _gridEnableSwitchView.delegate = nil;
    _gridEnableSwitchView = nil;
}


- (IBAction)back:(id)sender
{
    Settings.singleton.autoDetectEnable = _autoDetectEnable;
    Settings.singleton.gridEnable = _projectSettings.gridEnable;
    Settings.singleton.gridStyle = _projectSettings.gridStyle;
    Settings.singleton.gridColor = _projectSettings.gridColor;
    
    //Save project name
    NSString *projectSettingsPath = [_projectDirectory stringByAppendingPathComponent:FileProjectSettingsFile];
    [NSKeyedArchiver archiveRootObject:_projectSettings toFile:projectSettingsPath];
    
    [self.navigationController popViewControllerAnimated:YES];
    [self dismissModalViewControllerAnimated:YES];
}


#pragma SwitchViewDelegate

- (void)toggleSwitch:(BOOL)enable sender:(id)sender
{
    UITapGestureRecognizer *gesture = (UITapGestureRecognizer *)sender;
    if (gesture.view.tag == 0)
    {
        if (_autoDetectEnable != enable)
        {
            _autoDetectEnable = enable;
        }
    }
    else if (gesture.view.tag == 1)
    {
        if (_projectSettings.gridEnable != enable)
        {
            _projectSettings.gridEnable = enable;
            
            [self updateGridEnableStatus];
        }
    }
}


- (void)updateGridEnableStatus
{
    if (_projectSettings.gridEnable)
    {
        [_contentGridView setHidden:NO];
    }
    else
    {
        [_contentGridView setHidden:YES];
    }
}

- (void)updateGridStyleStatus
{
    NSArray *gridStyle = [NSArray arrayWithObjects:_indicator1, _indicator2, _indicator3, _indicator4, nil];
    
    for (int i = 0; i < 4; i++)
    {
        if (i == _projectSettings.gridStyle)
        {
            [[gridStyle objectAtIndex:i] setHidden:NO];
        }
        else
        {
            [[gridStyle objectAtIndex:i] setHidden:YES];
        }
    }
}


- (void)updateGridColorStatus
{
    NSArray *colorSmall = [NSArray arrayWithObjects:_redSmall, _yellowSmall, _blueSmall, _greenSmall, _graySmall, _whiteSmall, nil];
    NSArray *colorLarge = [NSArray arrayWithObjects:_redLarge, _yellowLarge, _blueLarge, _greenLarge, _grayLarge, _whiteLarge, nil];
    
    for (int i = 0; i < 6; i++)
    {
        if (_gridColorIndex == i)
        {
            [[colorSmall objectAtIndex:i] setHidden:YES];
            [[colorLarge objectAtIndex:i] setHidden:NO];
        }
        else
        {
            [[colorSmall objectAtIndex:i] setHidden:NO];
            [[colorLarge objectAtIndex:i] setHidden:YES];
        }
    }
}


- (IBAction)gridStylePressed:(id)sender
{
    UIButton *button = (UIButton *)sender;
    
    _projectSettings.gridStyle = button.tag;
    
    [self updateGridStyleStatus];
}


- (IBAction)gridColorSmallPressed:(id)sender
{
    UIButton *button = (UIButton *)sender;
    _gridColorIndex = button.tag;
    if (button.tag == 0)
    {
        _projectSettings.gridColor = RED_COLOR;
    }
    else if (button.tag == 1)
    {
        _projectSettings.gridColor = YELLOW_COLOR;
    }
    else if (button.tag == 2)
    {
        _projectSettings.gridColor = BLUE_COLOR;
    }
    else if (button.tag == 3)
    {
        _projectSettings.gridColor = GREEN_COLOR;
    }
    else if (button.tag == 4)
    {
        _projectSettings.gridColor = GRAY_COLOR;
    }
    else if (button.tag == 5)
    {
        _projectSettings.gridColor = WHITE_COLOR;
    }
    else
    {
        _projectSettings.gridColor = GRAY_COLOR;
        _gridColorIndex = 4;
    }
    
    [self updateGridColorStatus];
}

- (int)getIndexOfColor:(UIColor *)color
{
    if([color isEqual:RED_COLOR])
    {
        return 0;
    }
    else if([color isEqual:YELLOW_COLOR])
    {
        return 1;
    }
    else if([color isEqual:BLUE_COLOR])
    {
        return 2;
    }
    else if([color isEqual:GREEN_COLOR])
    {
        return 3;
    }
    else if([color isEqual:GRAY_COLOR])
    {
        return 4;
    }
    else if([color isEqual:WHITE_COLOR])
    {
        return 5;
    }

    // Default
    return 4;
}


#pragma mark - Rotation methods
// This method was deprecated in iOS 6
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if (IS_IPAD())
    {
        return YES;
    }

    return (interfaceOrientation == UIInterfaceOrientationPortrait || interfaceOrientation == UIInterfaceOrientationPortraitUpsideDown);
}

#pragma mark Orientation in iOS 6.0
// Apple replaced shouldAutorotateToInterfaceOrientation method with 02 other methods: supportedInterfaceOrientations, shouldAutorotate
- (NSUInteger)supportedInterfaceOrientations
{
    if (IS_IPAD())
    {
        return UIInterfaceOrientationMaskAll;
    }

    return UIInterfaceOrientationPortrait | UIInterfaceOrientationPortraitUpsideDown;
}

- (BOOL)shouldAutorotate
{
    if (IS_IPAD())
    {
        return YES;
    }

    return NO;
}


@end
