//
//  ImportPictureViewTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "AppDelegate.h"
#import "EditImagesViewController.h"

@interface ImportImagesViewTestCases : PixNotesTestCases
{
    AppDelegate *_appDelegate;
    EditImagesViewController *_editImageViewController;
}
@end

@implementation ImportImagesViewTestCases

- (void)setUp
{
    [super setUp];
    _appDelegate = [[UIApplication sharedApplication] delegate];
    _editImageViewController = (EditImagesViewController *)_appDelegate.viewController.centerPanel;
}

- (void)tearDown
{
    [super tearDown];
}

// Tap on a image
- (void)testTapOnScrollView
{
    
}

@end
