//
//  ProjectCell.m
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "ProjectCell.h"
#import "Colors.h"

@implementation ProjectCell

+ (UINib *)nib
{
    return [UINib nibWithNibName:@"ProjectCell" bundle:nil];
}

- (void)loadContentWithName:(NSString *)name
{
    _title.text = name;
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated
{
    if (highlighted)
    {
        _title.textColor = GREEN_TEXT_COLOR;
    }
    else
    {
        _title.textColor = [UIColor whiteColor];
    }
    
    _arrow.highlighted = highlighted;
}

@end
