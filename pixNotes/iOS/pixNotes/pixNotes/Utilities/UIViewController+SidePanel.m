//
//  UIViewController+SidePanel.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "UIViewController+SidePanel.h"

#import "MainViewController.h"

@implementation UIViewController (SidePanel)

- (MainViewController *)sidePanelController
{
    UIViewController *iter = self.parentViewController;
    while (iter)
    {
        if ([iter isKindOfClass:[MainViewController class]])
        {
            return (MainViewController *)iter;
        }
        else if (iter.parentViewController && iter.parentViewController != iter)
        {
            iter = iter.parentViewController;
        }
        else
        {
            iter = nil;
        }
    }
    return nil;
}

@end
