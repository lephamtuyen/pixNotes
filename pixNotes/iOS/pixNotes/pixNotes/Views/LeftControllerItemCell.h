//
//  LeftControllerItemCell.h
//  pixNotes
//
//  Created by Tuyen Le on 11/18/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

#define CELL_NAME_NEW_PROJECT       @"New Session"
#define CELL_NAME_OPEN_PROJECTS     @"Open Session"
#define CELL_NAME_MANAGE_PROJECTS   @"Manage Sessions"
#define CELL_NAME_SETTINGS          @"Settings"

@interface LeftControllerItemCell : UITableViewCell
{
    NSString *_name;
}

@property(nonatomic, strong) IBOutlet UIImageView *icon;
@property(nonatomic, strong) IBOutlet UILabel *title;
@property(nonatomic, strong) IBOutlet UIImageView *separateline;

+ (UINib *)nib;
- (void)loadContentWithName:(NSDictionary *)name;

@end
