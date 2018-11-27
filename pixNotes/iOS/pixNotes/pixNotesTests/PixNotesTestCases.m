//
//  pixNotesTestCases.m
//  pixNotes
//
//  Created by Tuyen Le on 12/11/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"

@implementation PixNotesTestCases

- (void)setUp
{
    [super setUp];
}

- (void)tearDown
{
    for (id mock in _mocksToVerify)
    {
        [mock verify];
    }
    _mocksToVerify = nil;
    [super tearDown];
}

- (NSURL *)URLForResource:(NSString *)name withExtension:(NSString *)extension
{
    NSBundle *bundle = [NSBundle bundleForClass:[PixNotesTestCases class]];
    return [bundle URLForResource:name withExtension:extension];
}

- (id)autoVerifiedMockForClass:(Class)aClass
{
    id mock = [OCMockObject mockForClass:aClass];
    [self verifyDuringTearDown:mock];
    return mock;
}

- (id)autoVerifiedPartialMockForObject:(id)object
{
    id mock = [OCMockObject partialMockForObject:object];
    [self verifyDuringTearDown:mock];
    return mock;
}

- (void)verifyDuringTearDown:(id)mock
{
    if (_mocksToVerify == nil)
    {
        _mocksToVerify = [NSMutableArray array];
    }
    
    [_mocksToVerify addObject:mock];
}

@end
