//
//  UIViewController+SidePanel.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MainViewController;

/* This optional category provides a convenience method for finding the current
 side panel controller that your view controller belongs to. It is similar to the
 Apple provided "navigationController" and "tabBarController" methods.
 */
@interface UIViewController (SidePanel)

// The nearest ancestor in the view controller hierarchy that is a side panel controller.
@property (nonatomic, weak, readonly) MainViewController *sidePanelController;

@end
