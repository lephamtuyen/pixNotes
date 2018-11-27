//
//  SettingsTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "Settings.h"
#import "Constants.h"
#import "Colors.h"

@interface SettingsTestCases : PixNotesTestCases

@end

@implementation SettingsTestCases

- (void)setUp
{
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown
{
    // Put teardown code here. This method is called after the invocation of each test method in the class. 
    [super tearDown];
}

- (void)testInitialize
{
    Settings *a = Settings.singleton;
    Settings *b = Settings.singleton;
    
    STAssertEqualObjects(a, b, @"Singleton work is not correctly");
}

- (void)testLineWidthBorder
{
    Settings.singleton.lineWidth = LARGER_BORDER;
    STAssertEquals(Settings.singleton.lineWidth, LARGER_BORDER, @"Set border size incorrect");
}

- (void)testFontSize
{
    Settings.singleton.fontSize = LARGER_FONT;
    STAssertEquals(Settings.singleton.fontSize, LARGER_FONT, @"");
}

- (void)testLineColor
{
    Settings.singleton.lineColor = BLUE_COLOR;
    STAssertEqualObjects(Settings.singleton.lineColor, BLUE_COLOR, @"Not set to default border");
}

- (void)testGridEnable
{
    Settings.singleton.gridEnable = YES;
    STAssertTrue(Settings.singleton.gridEnable, @"Not disable Grid");
    
    Settings.singleton.gridEnable = NO;
    STAssertFalse(Settings.singleton.gridEnable, @"Not set Grid enable");
}

- (void)testAutoDetectEnable
{
    Settings.singleton.autoDetectEnable = YES;
    STAssertTrue(Settings.singleton.autoDetectEnable, @"");
    
    Settings.singleton.autoDetectEnable = NO;
    STAssertFalse(Settings.singleton.autoDetectEnable, @"");
}

- (void)testGridStyle
{
    Settings.singleton.gridStyle = GRID_STYLE_SQUARE;
    STAssertEquals(Settings.singleton.gridStyle, GRID_STYLE_SQUARE, @"Set grid style incorrect");
}

- (void)testGridColor
{
    Settings.singleton.gridColor = WHITE_COLOR;
    STAssertEqualObjects(Settings.singleton.gridColor, WHITE_COLOR, @"Set grid color incorrect");
}

- (void)testBlurFactor
{
    Settings.singleton.blurFactor = STANDARD_BLUR;
    STAssertEquals(Settings.singleton.blurFactor, STANDARD_BLUR, @"");
}

@end
