//
//  MainViewControllerTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "AppDelegate.h"
#import "MainViewController.h"
#import "EditImagesViewController.h"

@interface MainViewControllerTestCases : PixNotesTestCases
{
    AppDelegate *_appDelegate;
    MainViewController *_mainViewController;
    EditImagesViewController *_editImagesViewController;
}

@end

@implementation MainViewControllerTestCases

- (void)setUp
{
    [super setUp];
    _appDelegate = [[UIApplication sharedApplication] delegate];
    _mainViewController = _appDelegate.viewController;
    _editImagesViewController = (EditImagesViewController *)_appDelegate.viewController.centerPanel;
}

- (void)tearDown
{
    // Put teardown code here. This method is called after the invocation of each test method in the class. 
    [super tearDown];
}

- (void)testInitializeMainViewController
{
    STAssertNotNil(_mainViewController, @"");
    
    STAssertNotNil(_mainViewController.view, @"");
    STAssertNotNil(_mainViewController.navigationController, @"");
    STAssertNotNil(_mainViewController.leftPanel, @"");
    STAssertNotNil(_mainViewController.centerPanel, @"");
    
    // Visible view is of central view
    STAssertEqualObjects(_mainViewController.visiblePanel, _mainViewController.centerPanel, @"");
    STAssertEquals(_mainViewController.state, SidePanelCenterVisible, @"");
}

- (void)testShowHideLeftViewControllerWhenPressedMenuButton
{
    [_editImagesViewController showHideLeftControllerPressed:nil];
    
    STAssertEqualObjects(_mainViewController.visiblePanel, _mainViewController.leftPanel, @"");
    STAssertEquals(_mainViewController.state, SidePanelLeftVisible, @"");
    
    [_editImagesViewController showHideLeftControllerPressed:nil];
    
    // Visible view is of central view
    STAssertEqualObjects(_mainViewController.visiblePanel, _mainViewController.centerPanel, @"");
    STAssertEquals(_mainViewController.state, SidePanelCenterVisible, @"");
}

@end
