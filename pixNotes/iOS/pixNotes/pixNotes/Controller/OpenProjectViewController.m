//
//  OpenProjectViewController.m
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "OpenProjectViewController.h"
#import "Constants.h"


@implementation OpenProjectViewController


#pragma mark - UIViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(selectProjectAtProjectDirectory:)
                                                 name:NSNotificationSelectProject object:nil];
    [_projectTableViewController viewDidLoad];
    _projectTableViewController.editing = NO;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [_projectTableViewController viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [_projectTableViewController viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [_projectTableViewController viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [_projectTableViewController viewDidDisappear:animated];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSNotificationSelectProject object:nil];
    [_projectTableViewController viewDidUnload];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSNotificationSelectProject object:nil];
}

- (void)selectProjectAtProjectDirectory:(NSNotification *)notification
{
    NSString *projectDirectory = notification.object;
    [_delegate viewController:self loadProjectWithProjectDirectory:projectDirectory];
    [self back:nil];
}

- (IBAction)back:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}


#pragma mark - Rotation methods
// This method was deprecated in iOS 6
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

#pragma mark Orientation in iOS 6.0
// Apple replaced shouldAutorotateToInterfaceOrientation method with 02 other methods: supportedInterfaceOrientations, shouldAutorotate
- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskAll;
}

- (BOOL)shouldAutorotate
{
    return YES;
}

@end
