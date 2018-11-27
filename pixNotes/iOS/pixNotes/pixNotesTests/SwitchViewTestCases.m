//
//  pixNotesTests.m
//  pixNotesTests
//
//  Created by Tuyen Le on 12/6/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "SwitchView.h"

@interface SwitchViewTestCases : PixNotesTestCases
{
    SwitchView *_switchView;
}
@end

@implementation SwitchViewTestCases

- (void)setUp
{
    _switchView = [[SwitchView alloc] init];
}

- (void)tearDown
{
    _switchView = nil;
    [super tearDown];
}

// Check SwitchView has alloc o=or not
- (void)testSwitchViewInitNotNil
{
    STAssertNotNil(_switchView, @"Cannot alloc SwitchView");
}

// Check status of switch has changed
- (void)testSwitchViewChangeStatus
{
    _switchView.isOn = YES;
    STAssertEqualObjects(_switchView.onOffLabel.text, @"ON", @"Not change status");
}

// Check status of switch has changed
- (void)testSwitchViewPressed
{
    BOOL currentOnOff = _switchView.isOn;
    [_switchView toggleOnOff:nil];
    if (currentOnOff)
    {
        STAssertFalse(_switchView.isOn, @"Not change status after pressed switch");
    }
    else
    {
        STAssertTrue(_switchView.isOn, @"Not change status after pressed switch");
    }
}

@end
