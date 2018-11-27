//
//  LeftControllerItemCell.m
//  pixNotes
//
//  Created by Tuyen Le on 11/18/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "LeftControllerItemCell.h"
#import "Colors.h"

@implementation LeftControllerItemCell


+ (UINib *)nib
{
    return [UINib nibWithNibName:@"LeftControllerItemCell" bundle:nil];
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated
{
    if (highlighted)
    {
        self.backgroundColor = [UIColor darkGrayColor];
    }
    else
    {
        self.backgroundColor = BACKGROUND_MENU_VIEW;
    }
}

- (void)loadContentWithName:(NSDictionary *)name
{
    _name = [[name allKeys] objectAtIndex:0];
    if ([_name isEqualToString:CELL_NAME_NEW_PROJECT])
    {
        _icon.image = [UIImage imageNamed:@"new_project.png"];
    }
    else if ([_name isEqualToString:CELL_NAME_OPEN_PROJECTS])
    {
        _icon.image = [UIImage imageNamed:@"open_project.png"];
    }
    else if ([_name isEqualToString:CELL_NAME_MANAGE_PROJECTS])
    {
        _icon.image = [UIImage imageNamed:@"manage_project.png"];
    }
    else if ([_name isEqualToString:CELL_NAME_SETTINGS])
    {
        _icon.image = [UIImage imageNamed:@"setting.png"];
    }

    _title.text = _name;

    if (![[name objectForKey:_name] boolValue])
    {
        self.userInteractionEnabled = NO;
        _title.textColor = [UIColor grayColor];
    }
    else
    {
        self.userInteractionEnabled = YES;
        _title.textColor = GRAY_TEXT_COLOR;
    }
    
    [self setHighlighted:NO animated:NO];
}

@end
