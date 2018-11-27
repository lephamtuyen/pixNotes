//
//  UINavigationController+Orientation.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/1/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "UINavigationController+Orientation.h"
#import "Settings.h"

@implementation UINavigationController (Orientation)

#pragma mark - Rotation methods
// This method was deprecated in iOS 6
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Only support portrait for tutorial view
    if (!Settings.singleton.tutorialEnable)
    {
        if (UIInterfaceOrientationIsPortrait(interfaceOrientation))
        {
            return YES;
        }
        else
        {
            return NO;
        }
    }
    
    return [self.topViewController shouldAutorotateToInterfaceOrientation:interfaceOrientation];
}

#pragma mark Orientation in iOS 6.0
// Apple replaced shouldAutorotateToInterfaceOrientation method with 02 other methods: supportedInterfaceOrientations, shouldAutorotate
- (NSUInteger)supportedInterfaceOrientations
{
    return [self.topViewController supportedInterfaceOrientations];
}

- (BOOL)shouldAutorotate
{
    // Only support portrait for tutorial view
    return Settings.singleton.tutorialEnable && [self.topViewController shouldAutorotate];
}

@end
