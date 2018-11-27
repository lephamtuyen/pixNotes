//
//  SendEmailViewControllerTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "SendEmailViewController.h"
#import "Settings.h"

@interface SendEmailViewControllerTestCases : PixNotesTestCases
{
    SendEmailViewController *_sendEmailViewController;
    UIView *_sendEmailView;
}
@end

@implementation SendEmailViewControllerTestCases

- (void)setUp
{
    [super setUp];
    _sendEmailViewController = [[SendEmailViewController alloc] init];
    _sendEmailView = _sendEmailViewController.view;
}

- (void)tearDown
{
    _sendEmailViewController = nil;
    _sendEmailView = nil;
    [super tearDown];
}

- (void)testInitSendEmailViewController
{
    STAssertNotNil(_sendEmailViewController, @"SendEmailViewController nil");
    
    STAssertNotNil(_sendEmailView, @"");
    STAssertNotNil(_sendEmailViewController.switchView, @"");
    STAssertNotNil(_sendEmailViewController.pdfImage, @"");
    STAssertNotNil(_sendEmailViewController.wordImage, @"");
}

- (void)testStatusOfStyleGridViewWhenPressedGridStyleButton
{
    // Press word export button
    [_sendEmailViewController formatButtonPressed:[_sendEmailViewController.wordImage.superview viewWithTag:1]];
    STAssertTrue(_sendEmailViewController.pdfImage.hidden, @"");
    STAssertFalse(_sendEmailViewController.wordImage.hidden, @"");
}

@end
