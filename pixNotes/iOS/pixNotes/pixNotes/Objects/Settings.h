//
//  ToolSettings.h
//  pixNotes
//
//  Created by Tuyen Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Settings : NSObject

@property (assign) CGFloat lineWidth;
@property (assign) CGFloat fontSize;
@property (strong) UIColor *lineColor;
@property (assign) BOOL gridEnable;
@property (assign) BOOL autoDetectEnable;
@property (assign) BOOL drawEnable;
@property (assign) NSInteger gridStyle;
@property (strong) UIColor *gridColor;
@property (assign) CGFloat blurFactor;
@property (assign) BOOL tutorialEnable;

+ (Settings *)singleton;

@end
