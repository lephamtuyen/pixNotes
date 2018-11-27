//
//  LeftViewControllerTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "EditImagesViewController.h"
#import "AppDelegate.h"
#import "LeftViewController.h"
#import "LeftControllerItemCell.h"
#import "SendEmailViewController.h"
#import "SettingViewController.h"
#import "OpenProjectViewController.h"
#import "ManageProjectViewController.h"
#import "Constants.h"

@interface LeftViewControllerTestCases : PixNotesTestCases
{
    AppDelegate *_appDelegate;
    EditImagesViewController *_editPictureViewController;
    LeftViewController *_leftViewController;
}
@end

@implementation LeftViewControllerTestCases

- (void)setUp
{
    _appDelegate = [[UIApplication sharedApplication] delegate];
    _editPictureViewController = (EditImagesViewController *)_appDelegate.viewController.centerPanel;
    _leftViewController = (LeftViewController *)_appDelegate.viewController.leftPanel;
}

- (void)tearDown
{
    [super tearDown];
}

// LeftViewController must not nil
- (void)testLeftViewControllerMustNotNil
{
    STAssertNotNil(_leftViewController, @"LeftViewController nil");
}

// Delegate Of LeftViewController is MainViewController
- (void)testDelegateOfLeftViewControllerIsMainViewController
{
    STAssertEqualObjects(_leftViewController.delegate, _appDelegate.viewController, @"delegate of LeftViewController is not MainViewController");
}

- (void)testControllerSetsUpCellCorrectly
{
    // Trigger view did load:
    UIView *view = _leftViewController.view;
    STAssertNotNil(view, @"");
    
    STAssertNotNil(_leftViewController.tableView, @"");
    
    LeftControllerItemCell *cell = (LeftControllerItemCell *)[_leftViewController tableView:_leftViewController.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
    STAssertNotNil(cell, @"");
    STAssertEqualObjects(CELL_NAME_NEW_PROJECT, cell.title.text, @"Should have set label");
    
    cell = (LeftControllerItemCell *)[_leftViewController tableView:_leftViewController.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0]];
    STAssertNotNil(cell, @"");
    STAssertEqualObjects(CELL_NAME_OPEN_PROJECTS, cell.title.text, @"Should have set label");
    
    cell = (LeftControllerItemCell *)[_leftViewController tableView:_leftViewController.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:2 inSection:0]];
    STAssertNotNil(cell, @"");
    STAssertEqualObjects(CELL_NAME_MANAGE_PROJECTS, cell.title.text, @"Should have set label");
    
    cell = (LeftControllerItemCell *)[_leftViewController tableView:_leftViewController.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:3 inSection:0]];
    STAssertNotNil(cell, @"");
    STAssertEqualObjects(CELL_NAME_SETTINGS, cell.title.text, @"Should have set label");
}

- (void)testSimulateTapOnCell
{
    // Trigger view did load:
    UIView *view = _leftViewController.view;
    STAssertNotNil(view, @"");
    
    // New project
    [_leftViewController tableView:_leftViewController.tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
    STAssertTrue([_appDelegate.viewController.view.subviews containsObject:_appDelegate.viewController.createNewProjectView], @"");
    
    // Open project
    [_leftViewController tableView:_leftViewController.tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:0]];
    STAssertEqualObjects([_appDelegate.viewController.navigationController.topViewController class], [OpenProjectViewController class], @"");
    
    // Manage project
    [_leftViewController tableView:_leftViewController.tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:2 inSection:0]];
    STAssertEqualObjects([_appDelegate.viewController.navigationController.topViewController class], [ManageProjectViewController class], @"");
    
    // New project
    [_leftViewController tableView:_leftViewController.tableView didSelectRowAtIndexPath:[NSIndexPath indexPathForRow:3 inSection:0]];
    if (IS_IPAD())
    {
        STAssertEqualObjects([_appDelegate.viewController.presentedViewController class], [SettingViewController class], @"");
    }
    else
    {
        STAssertEqualObjects([_appDelegate.viewController.navigationController.topViewController class], [SettingViewController class], @"");
    }
    
}

@end
