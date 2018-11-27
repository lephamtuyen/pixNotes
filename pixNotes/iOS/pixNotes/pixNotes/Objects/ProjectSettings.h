//
//  GridSettings.h
//  pixNotes
//
//  Created by Tuyen Le on 1/14/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ProjectSettings : NSObject <NSCoding>

@property (strong) NSString *projectName;
@property (assign) BOOL gridEnable;
@property (strong) UIColor *gridColor;
@property (assign) NSInteger gridStyle;

- (id)initWithProjectName:(NSString *)projectName;

@end
