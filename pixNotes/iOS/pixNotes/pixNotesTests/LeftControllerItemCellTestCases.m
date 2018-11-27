//
//  LeftControllerItemCellTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "LeftControllerItemCell.h"
#import "Colors.h"

@interface LeftControllerItemCellTestCases : PixNotesTestCases
{
    LeftControllerItemCell *_cell;
}
@end

@implementation LeftControllerItemCellTestCases

- (void)setUp
{
    _cell = [[LeftControllerItemCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
}

- (void)tearDown
{
    _cell = nil;
    [super tearDown];
}

- (void)testNibLoading
{
    UINib *nib = [LeftControllerItemCell nib];
    STAssertNotNil(nib, @"");
    
    NSArray *a = [nib instantiateWithOwner:nil options:@{}];
    STAssertEquals([a count], (NSUInteger) 1, @"");
    LeftControllerItemCell *cell = a[0];
    STAssertTrue([cell isMemberOfClass:[LeftControllerItemCell class]], @"");
    
    // Check that outlets are set up correctly:
    STAssertNotNil(cell.icon, @"");
    STAssertNotNil(cell.title, @"");
    STAssertNotNil(cell.separateline, @"");
}

// Default highlighted with dark gray color
- (void)testDefaultBackgroundColor
{
    [_cell setHighlighted:NO animated:YES];
    STAssertEqualObjects(_cell.backgroundColor, BACKGROUND_MENU_VIEW, @"Not match default background color");
}


// Selected a cell => highlighted with dark gray color
- (void)testHighLightWithDarkGrayColorWhenSelectedCell
{
    [_cell setHighlighted:YES animated:YES];
    STAssertEqualObjects(_cell.backgroundColor, [UIColor darkGrayColor], @"Not change background color when highlighted");
}

@end
