//
//  AppDelegate.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "AppDelegate.h"

#import "MainViewController.h"

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [[UIApplication sharedApplication] setStatusBarHidden:YES];
    
    _window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
	
	_viewController = [[MainViewController alloc] init];

	_window.rootViewController = [[UINavigationController alloc] initWithRootViewController:_viewController];
    [[_viewController navigationController] setNavigationBarHidden:YES animated:NO];
    [_window makeKeyAndVisible];
    return YES;
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    [_viewController saveCurrentImageWhenEnterBackgroud];
}

@end
