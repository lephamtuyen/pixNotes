//
//  OpenProjectViewController.h
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProjectTableViewController.h"
#import "MainViewController.h"
#import "ChangeProjectNameView.h"


@interface ManageProjectViewController : UIViewController <ChangeProjectNameViewDelegate>

@property (nonatomic, strong) IBOutlet ProjectTableViewController *projectTableViewController;
@property (nonatomic, strong) ChangeProjectNameView *changeProjectNameView;

@end
