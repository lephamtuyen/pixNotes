//
//  CenterViewController.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "AbtractViewController.h"


@implementation AbtractViewController

- (id)initWithMainViewController:(MainViewController *)mainViewController
{
    self = [super init];
    
    if (self)
    {
        _delegate = mainViewController;
    }
    
    return self;
}

@end
