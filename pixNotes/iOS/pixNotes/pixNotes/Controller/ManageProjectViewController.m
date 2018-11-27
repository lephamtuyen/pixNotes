//
//  OpenProjectViewController.m
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "ManageProjectViewController.h"
#import "Constants.h"


@implementation ManageProjectViewController


#pragma mark - UIViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(selectProjectAtProjectDirectory:)
                                                 name:NSNotificationSelectProject object:nil];
    [_projectTableViewController viewDidLoad];
    _projectTableViewController.editing = YES;
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
    [_projectTableViewController viewDidUnload];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSNotificationSelectProject object:nil];
}

- (void)dealloc
{
    _projectTableViewController = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSNotificationSelectProject object:nil];
}

- (void)selectProjectAtProjectDirectory:(NSNotification *)notification
{
    [self showChangeProjectNameView:notification.object];
}

- (void)showChangeProjectNameView:(NSString *)projectDirectory
{
    if (!_changeProjectNameView)
    {
        _changeProjectNameView = [[ChangeProjectNameView alloc] init];
        _changeProjectNameView.autoresizingMask = (UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth);
        _changeProjectNameView.delegate = self;
    }
    
    [_changeProjectNameView setFrame:self.view.frame];
    [_changeProjectNameView loadProjectDirectory:projectDirectory];
    [self.view addSubview:_changeProjectNameView];
}

- (void)closeChangeProjectNameViewFromSuperview
{
    if (_changeProjectNameView)
    {
        [_changeProjectNameView close];
        _changeProjectNameView = nil;
    }
}

- (IBAction)back:(id)sender
{
    // Avoid crash in iOS7
    [_projectTableViewController.tableView reloadData];
    
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma -mark ChangeProjectNameViewDelegate

- (void)closeChangeProjectNameView
{
    [_projectTableViewController setupTableView];
    [_projectTableViewController.tableView reloadData];
    
    [self closeChangeProjectNameViewFromSuperview];
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
