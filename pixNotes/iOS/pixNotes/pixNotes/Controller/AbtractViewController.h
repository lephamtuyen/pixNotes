//
//  CenterViewController.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MainViewController.h"


@interface AbtractViewController : UIViewController

@property (nonatomic, strong) id<MainViewControllerDelegate>delegate;

- (id)initWithMainViewController:(MainViewController *)mainViewController;

@end
