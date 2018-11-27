//
//  SettingViewControllerTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "SettingViewController.h"
#import "SwitchView.h"
#import "Constants.h"
#import "Colors.h"
#import "Settings.h"

@interface SettingViewControllerTestCases : PixNotesTestCases
{
    SettingViewController *_settingViewController;
    UIView *_settingView;
}
@end

@implementation SettingViewControllerTestCases

- (void)setUp
{
    [super setUp];
    _settingViewController = [[SettingViewController alloc] init];
    _settingView = _settingViewController.view;
    if (_settingViewController.projectSettings == nil)
    {
        _settingViewController.projectSettings = [[ProjectSettings alloc] initWithProjectName:@"Test"];
    }
}

- (void)tearDown
{
    _settingViewController = nil;
    _settingView = nil;
    [super tearDown];
}

- (void)testInitSettingViewController
{
    STAssertNotNil(_settingViewController, @"SettingViewController nil");

    STAssertNotNil(_settingView, @"");
    STAssertNotNil(_settingViewController.autoDetectEnableSwitchView, @"");
    STAssertNotNil(_settingViewController.gridEnableSwitchView, @"");
    STAssertNotNil(_settingViewController.contentGridView, @"");
    STAssertNotNil(_settingViewController.indicator1, @"");
    STAssertNotNil(_settingViewController.indicator2, @"");
    STAssertNotNil(_settingViewController.indicator3, @"");
    STAssertNotNil(_settingViewController.indicator4, @"");
    STAssertNotNil(_settingViewController.redLarge, @"");
    STAssertNotNil(_settingViewController.redSmall, @"");
    STAssertNotNil(_settingViewController.yellowLarge, @"");
    STAssertNotNil(_settingViewController.yellowSmall, @"");
    STAssertNotNil(_settingViewController.greenLarge, @"");
    STAssertNotNil(_settingViewController.greenSmall, @"");
    STAssertNotNil(_settingViewController.blueLarge, @"");
    STAssertNotNil(_settingViewController.blueSmall, @"");
    STAssertNotNil(_settingViewController.grayLarge, @"");
    STAssertNotNil(_settingViewController.graySmall, @"");
    STAssertNotNil(_settingViewController.whiteLarge, @"");
    STAssertNotNil(_settingViewController.whiteSmall, @"");
}

- (void)testSwitchOffAndHiddenGridContentView
{
    _settingViewController.gridEnableSwitchView.isOn = YES;
    _settingViewController.projectSettings.gridEnable = YES;
    [_settingViewController.gridEnableSwitchView toggleOnOff:[_settingViewController.gridEnableSwitchView.gestureRecognizers firstObject]];
    STAssertTrue(_settingViewController.contentGridView.hidden, @"Not hidden grid content view");
    
    [_settingViewController.gridEnableSwitchView toggleOnOff:[_settingViewController.gridEnableSwitchView.gestureRecognizers firstObject]];
    STAssertFalse(_settingViewController.contentGridView.hidden, @"Not show grid content view");
}

// Not save to setting if cancel
- (void)testCancel
{
    _settingViewController.projectSettings.gridEnable = !Settings.singleton.gridEnable;
    NSInteger i = 0;
    while (_settingViewController.projectSettings.gridStyle == Settings.singleton.gridEnable)
    {
        _settingViewController.projectSettings.gridStyle = i;
        i++;
    }
    
    NSArray *color = [NSArray arrayWithObjects:RED_COLOR, GREEN_COLOR, BLUE_COLOR, YELLOW_COLOR, GRAY_COLOR, WHITE_COLOR, nil];
    i = 0;
    while ([_settingViewController.projectSettings.gridColor isEqual:Settings.singleton.gridColor])
    {
        _settingViewController.projectSettings.gridColor = [color objectAtIndex:i];
        i++;
    }
    
    [_settingViewController back:nil];
    
    STAssertEqualObjects(Settings.singleton.gridColor, _settingViewController.projectSettings.gridColor, @"Not set grid color");
    STAssertEquals(Settings.singleton.gridStyle, _settingViewController.projectSettings.gridStyle, @"Not set grid color");
    
    if (_settingViewController.projectSettings.gridEnable)
    {
        STAssertTrue(Settings.singleton.gridEnable, @"Not set grid color");
    }
    else
    {
        STAssertFalse(Settings.singleton.gridEnable, @"Not set grid color");
    }
}

- (void)testStatusOfStyleGridViewWhenPressedGridStyleButton
{
    // Press grid style square
    [_settingViewController gridStylePressed:[_settingViewController.indicator4.superview viewWithTag:3]];
    STAssertTrue(_settingViewController.indicator1.hidden, @"");
    STAssertTrue(_settingViewController.indicator2.hidden, @"");
    STAssertTrue(_settingViewController.indicator3.hidden, @"");
    STAssertFalse(_settingViewController.indicator4.hidden, @"");
}

- (void)testStatusOfGridColorViewWhenPressedGridColorButton
{
    // Press grid color white =>
    // - all of large color button are hidden except whiteLarge
    // - all of small color button are shown except white small
    [_settingViewController gridColorSmallPressed:_settingViewController.whiteSmall];
    STAssertTrue(_settingViewController.whiteSmall.hidden, @"");
    STAssertFalse(_settingViewController.whiteLarge.hidden, @"");
    STAssertTrue(_settingViewController.redLarge.hidden, @"");
    STAssertFalse(_settingViewController.redSmall.hidden, @"");
    STAssertFalse(_settingViewController.greenSmall.hidden, @"");
    STAssertTrue(_settingViewController.greenLarge.hidden, @"");
    STAssertTrue(_settingViewController.grayLarge.hidden, @"");
    STAssertFalse(_settingViewController.graySmall.hidden, @"");
    STAssertTrue(_settingViewController.yellowLarge.hidden, @"");
    STAssertFalse(_settingViewController.yellowSmall.hidden, @"");
    STAssertTrue(_settingViewController.blueLarge.hidden, @"");
    STAssertFalse(_settingViewController.blueSmall.hidden, @"");
}

@end
