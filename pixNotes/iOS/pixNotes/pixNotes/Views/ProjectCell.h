//
//  ProjectCell.h
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ProjectCell : UITableViewCell

@property(nonatomic, strong) IBOutlet UILabel *title;
@property(nonatomic, strong) IBOutlet UIImageView *arrow;
@property(nonatomic, strong) IBOutlet UIImageView *separateline;

+ (UINib *)nib;
- (void)loadContentWithName:(NSString *)name;

@end
