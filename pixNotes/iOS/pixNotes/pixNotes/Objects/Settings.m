//
//  ToolSettings.m
//  pixNotes
//
//  Created by Tuyen Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import "Settings.h"
#import "Colors.h"
#import "Constants.h"

@implementation Settings


+ (Settings *)singleton
{
    static Settings *singleton = nil;
    if (singleton == nil)
    {
        singleton = [[self alloc] init];
    }
    return singleton;
}


- (CGFloat)lineWidth
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"lineWidth"])
    {
        return [[NSUserDefaults standardUserDefaults] floatForKey:@"lineWidth"];
    }

    // Default
    return SMALL_BORDER;
}


- (void)setLineWidth:(CGFloat)lineWidth
{
    [[NSUserDefaults standardUserDefaults] setFloat:lineWidth forKey:@"lineWidth"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (CGFloat)fontSize
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"fontSize"])
    {
        return [[NSUserDefaults standardUserDefaults] floatForKey:@"fontSize"];
    }

    return MEDIUM_FONT;
}


- (void)setFontSize:(CGFloat)fontSize
{
    [[NSUserDefaults standardUserDefaults] setFloat:fontSize forKey:@"fontSize"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}


- (CGFloat)blurFactor
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"blurFactor"])
    {
        return [[NSUserDefaults standardUserDefaults] floatForKey:@"blurFactor"];
    }

    return STANDARD_BLUR;
}


- (void)setBlurFactor:(CGFloat)blurFactor
{
    [[NSUserDefaults standardUserDefaults] setFloat:blurFactor forKey:@"blurFactor"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (NSInteger)gridStyle
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"gridStyle"])
    {
        return [[NSUserDefaults standardUserDefaults] integerForKey:@"gridStyle"];
    }

    return GRID_STYLE_DIAGONAL;
}


- (void)setGridStyle:(NSInteger)gridStyle
{
    [[NSUserDefaults standardUserDefaults] setInteger:gridStyle forKey:@"gridStyle"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}


- (BOOL)gridEnable
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"gridEnable"])
    {
        return [[NSUserDefaults standardUserDefaults] boolForKey:@"gridEnable"];
    }

    return NO;
}


- (void)setGridEnable:(BOOL)gridEnable
{
    [[NSUserDefaults standardUserDefaults] setInteger:gridEnable forKey:@"gridEnable"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}


- (BOOL)autoDetectEnable
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"autoDetectEnable"])
    {
        return [[NSUserDefaults standardUserDefaults] boolForKey:@"autoDetectEnable"];
    }

    return YES;
}


- (void)setAutoDetectEnable:(BOOL)autoDetectEnable
{
    [[NSUserDefaults standardUserDefaults] setInteger:autoDetectEnable forKey:@"autoDetectEnable"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}


- (BOOL)drawEnable
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"drawEnable"])
    {
        return [[NSUserDefaults standardUserDefaults] boolForKey:@"drawEnable"];
    }

    return YES;
}


- (void)setDrawEnable:(BOOL)drawEnable
{
    [[NSUserDefaults standardUserDefaults] setInteger:drawEnable forKey:@"drawEnable"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}


- (UIColor *)lineColor
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"lineColorAlpha"])
    {
        return [UIColor colorWithRed:[[NSUserDefaults standardUserDefaults] floatForKey:@"lineColorRed"]
                               green:[[NSUserDefaults standardUserDefaults] floatForKey:@"lineColorGreen"]
                                blue:[[NSUserDefaults standardUserDefaults] floatForKey:@"lineColorBlue"]
                               alpha:[[NSUserDefaults standardUserDefaults] floatForKey:@"lineColorAlpha"]];
    }
    
    return RED_COLOR;
}


- (void)setLineColor:(UIColor *)color
{
    CGFloat red = 0.0, green = 0.0, blue = 0.0, alpha =0.0;
    [color getRed:&red green:&green blue:&blue alpha:&alpha];
    
    [[NSUserDefaults standardUserDefaults] setFloat:red forKey:@"lineColorRed"];
    [[NSUserDefaults standardUserDefaults] setFloat:green forKey:@"lineColorGreen"];
    [[NSUserDefaults standardUserDefaults] setFloat:blue forKey:@"lineColorBlue"];
    [[NSUserDefaults standardUserDefaults] setFloat:alpha forKey:@"lineColorAlpha"];
}


- (UIColor *)gridColor
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"gridColorAlpha"])
    {
        return [UIColor colorWithRed:[[NSUserDefaults standardUserDefaults] floatForKey:@"gridColorRed"]
                               green:[[NSUserDefaults standardUserDefaults] floatForKey:@"gridColorGreen"]
                                blue:[[NSUserDefaults standardUserDefaults] floatForKey:@"gridColorBlue"]
                               alpha:[[NSUserDefaults standardUserDefaults] floatForKey:@"gridColorAlpha"]];
    }
    
    return GRAY_COLOR;
}


- (void)setGridColor:(UIColor *)gridColor
{
    CGFloat red = 0.0, green = 0.0, blue = 0.0, alpha =0.0;
    [gridColor getRed:&red green:&green blue:&blue alpha:&alpha];
    
    [[NSUserDefaults standardUserDefaults] setFloat:red forKey:@"gridColorRed"];
    [[NSUserDefaults standardUserDefaults] setFloat:green forKey:@"gridColorGreen"];
    [[NSUserDefaults standardUserDefaults] setFloat:blue forKey:@"gridColorBlue"];
    [[NSUserDefaults standardUserDefaults] setFloat:alpha forKey:@"gridColorAlpha"];
}


- (BOOL)tutorialEnable
{
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"tutorialEnable"])
    {
        return [[NSUserDefaults standardUserDefaults] boolForKey:@"tutorialEnable"];
    }
    
    return NO;
}


- (void)setTutorialEnable:(BOOL)tutorialEnable
{
    [[NSUserDefaults standardUserDefaults] setInteger:tutorialEnable forKey:@"tutorialEnable"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

@end
