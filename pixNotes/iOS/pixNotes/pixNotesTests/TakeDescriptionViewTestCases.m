//
//  TakeDescriptionTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "TakeDescriptionView.h"
#import "EditImagesViewController.h"
#import "AppDelegate.h"

@interface TakeDescriptionViewTestCases : PixNotesTestCases
{
    AppDelegate *_appDelegate;
    EditImagesViewController *_editPictureViewController;
    TakeDescriptionView *_takeDescriptionView;
}
@end

@implementation TakeDescriptionViewTestCases

- (void)setUp
{
    _appDelegate = [[UIApplication sharedApplication] delegate];
    _editPictureViewController = (EditImagesViewController *)_appDelegate.viewController.centerPanel;
}

- (void)tearDown
{
    if (_takeDescriptionView)
    {
        [_takeDescriptionView.delegate closeTakeDescriptionView];
    }
    
    [super tearDown];
}

// Shown take description view after pressed TakeNote button
- (void)testTakeDescriptionViewIsShownWhenPressesTakeNoteBtn
{
    _editPictureViewController.allowHandleTouch = YES;
    [_editPictureViewController takeDescriptionActionTapped:nil];
    _takeDescriptionView = _editPictureViewController.takeDescriptionView;
    STAssertTrue([_editPictureViewController.view.subviews containsObject:_takeDescriptionView], @"Not show take description view");
}

// Not show take description view if pressed into description label without text
- (void)testNotShowTakeDescriptionViewWhenPressedDescriptionLabelWithoutText
{
    _editPictureViewController.descriptionLabel.text = @"";
    [_editPictureViewController takeDescriptionLabelTapped];
    _takeDescriptionView = _editPictureViewController.takeDescriptionView;
    STAssertFalse([_editPictureViewController.view.subviews containsObject:_takeDescriptionView], @"Should not show take description view if pressed into description label without text");
}

// Show take description view if pressed into description label having text
- (void)testShowTakeDescriptionViewWhenPressedDescriptionLabelHavingText
{
    _editPictureViewController.descriptionLabel.text = @"Text";
    _editPictureViewController.allowHandleTouch = YES;
    [_editPictureViewController takeDescriptionLabelTapped];
    _takeDescriptionView = _editPictureViewController.takeDescriptionView;
    STAssertTrue([_editPictureViewController.view.subviews containsObject:_takeDescriptionView], @"Should Show take description view if pressed into description label having text");
}

//// Close take description view if pressed outside its contentView
//- (void)testCloseTakeDescriptionViewWhenPressedOutsideItsContentView
//{
//    _editPictureViewController.allowHandleTouch = YES;
//    [_editPictureViewController takeDescriptionActionTapped:nil];
//    _takeDescriptionView = _editPictureViewController.takeDescriptionView;
//    CGRect frame = _takeDescriptionView.contentView.frame;
//    
//    // Point outside take description view
//    CGPoint point = CGPointMake(frame.origin.x - 1, frame.origin.y - 1);
//    
//    [_takeDescriptionView pointInside:point withEvent:nil];
//    STAssertFalse([_editPictureViewController.view.subviews containsObject:_takeDescriptionView], @"Don't close take description view when pressed outside its contentView");
//}

//// Don't close take description view if pressed inside its contentView
//- (void)testDontCloseTakeDescriptionViewWhenPressedInsideItsContentView
//{
//    _editPictureViewController.allowHandleTouch = YES;
//    [_editPictureViewController takeDescriptionActionTapped:nil];
//    _takeDescriptionView = _editPictureViewController.takeDescriptionView;
//    CGRect frame = _takeDescriptionView.contentView.frame;
//    
//    // Point outside take description view
//    CGPoint point = CGPointMake(frame.origin.x, frame.origin.y);
//    
//    [_takeDescriptionView pointInside:point withEvent:nil];
//    STAssertTrue([_editPictureViewController.view.subviews containsObject:_takeDescriptionView], @"Should not close take description view when pressed inside its contentView");
//}


// Show current description in take description view
- (void)testShowCurrentDescriptionInTakeDescriptionView
{
    _editPictureViewController.descriptionLabel.text = @"Text";
    _editPictureViewController.allowHandleTouch = YES;
    [_editPictureViewController takeDescriptionLabelTapped];
    _takeDescriptionView = _editPictureViewController.takeDescriptionView;
    STAssertEqualObjects(_takeDescriptionView.textView.text, _editPictureViewController.descriptionLabel.text, @"Mismatch between text in description label and textview in take description view");
}


//// Not save description if pressed outside content view of take description view
//- (void)testNotSaveDescriptionWhenPressesOutsideContentViewOfTakeDescriptionView
//{
//    _editPictureViewController.descriptionLabel.text = @"Text (before)";
//    [_editPictureViewController takeDescriptionLabelTapped];
//    _takeDescriptionView = _editPictureViewController.takeDescriptionView;
//    _takeDescriptionView.textView.text = @"Text (after)";
//    
//    CGRect frame = _takeDescriptionView.contentView.frame;
//    
//    // Point outside take description view
//    CGPoint point = CGPointMake(frame.origin.x - 1, frame.origin.y - 1);
//    
//    [_takeDescriptionView pointInside:point withEvent:nil];
//
//    STAssertEqualObjects(_editPictureViewController.descriptionLabel.text, @"Text (before)", @"Should not save description if pressed outside content view of take description view");
//}

// Save description if pressed into save button of take description view
- (void)testSaveDescriptionWhenPressesSaveButtonOfTakeDescriptionView
{
    // Pre-condition
    _editPictureViewController.imageDirectory = @"test";
    _editPictureViewController.descriptionLabel.text = @"Text (before)";
    _editPictureViewController.allowHandleTouch = YES;
    [_editPictureViewController takeDescriptionLabelTapped];
    _takeDescriptionView = _editPictureViewController.takeDescriptionView;
    _takeDescriptionView.textView.text = @"Text (after)";

    [_takeDescriptionView saveDescription];

    STAssertEqualObjects(_editPictureViewController.descriptionLabel.text, @"Text (after)", @"Mismatch between text in description label and textview in take description view");
}

@end
