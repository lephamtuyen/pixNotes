//
//  PDFPrintTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "PDFPrint.h"

@interface PDFPrintTestCases : PixNotesTestCases

@end

@implementation PDFPrintTestCases

- (void)setUp
{
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown
{
    // Put teardown code here. This method is called after the invocation of each test method in the class. 
    [super tearDown];
}

- (void)testInitialize
{
    PDFPrint *pdf = [[PDFPrint alloc] initWithIncludeSystemEnvironment:YES];
    STAssertNotNil(pdf, @"pdf nil");
    STAssertTrue(pdf.isInclude, @"not set is include");
}

- (void)testGeneratePdfData
{
    NSData *pdfData = [[[PDFPrint alloc] initWithIncludeSystemEnvironment:YES] generatePdfDataWithImagesList:nil];
    STAssertNotNil(pdfData, @"pdf data nil");
}

@end
