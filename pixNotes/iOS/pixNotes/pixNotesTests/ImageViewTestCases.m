//
//  ImageViewTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "ImageView.h"

@interface ImageViewTestCases : PixNotesTestCases
{
    ImageView *_imageView;
}
@end

@implementation ImageViewTestCases

- (void)setUp
{
    [super setUp];
    _imageView = [[ImageView alloc] init];
}

- (void)tearDown
{
    _imageView = nil;
    [super tearDown];
}


// Check Image View is created or not.
- (void)testImageViewNotNil
{
    STAssertNotNil(_imageView, @"Cannot alloc ImageView");
    STAssertNotNil(_imageView.imageView, @"");
    STAssertNotNil(_imageView.imageViewIndicator, @"");
    STAssertNotNil(_imageView.borderTop, @"");
    STAssertNotNil(_imageView.borderLeft, @"");
    STAssertNotNil(_imageView.borderRight, @"");
    STAssertNotNil(_imageView.borderBottom, @"");
}

@end
