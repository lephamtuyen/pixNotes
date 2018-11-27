//
//  GridSettings.m
//  pixNotes
//
//  Created by Tuyen Le on 1/14/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "ProjectSettings.h"
#import "Constants.h"
#import "Colors.h"

@implementation ProjectSettings

- (id)initWithProjectName:(NSString *)projectName
{
    self = [super init];
    
    if (self)
    {
        _projectName = projectName;
        _gridEnable = NO;
        _gridStyle = GRID_STYLE_DIAGONAL;
        _gridColor = GRAY_COLOR;
    }
    
    return self;
}

- (void)encodeWithCoder:(NSCoder *)encoder; {
    
    [encoder encodeObject:_projectName forKey:@"projectName"];
    [encoder encodeBool:_gridEnable forKey:@"gridEnable"];
    [encoder encodeInteger:_gridStyle forKey:@"gridStyle"];
    [encoder encodeObject:_gridColor forKey:@"gridColor"];
}

- (id)initWithCoder:(NSCoder *)decoder {
    
    self = [super init];
    
    if (self)
    {
        _projectName = [decoder decodeObjectForKey:@"projectName"];
        _gridEnable = [decoder decodeBoolForKey:@"gridEnable"];
        _gridStyle = [decoder decodeIntegerForKey:@"gridStyle"];
        _gridColor = [decoder decodeObjectForKey:@"gridColor"];
    }
    
    return self;
}

@end
