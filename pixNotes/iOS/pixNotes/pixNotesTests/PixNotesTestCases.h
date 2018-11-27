//
//  pixNotesTestCases.h
//  pixNotes
//
//  Created by Tuyen Le on 12/11/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <SenTestingKit/SenTestingKit.h>
#import <OCMock/OCMock.h>

@interface PixNotesTestCases : SenTestCase
{
    NSMutableArray *_mocksToVerify;
}

/// Returns the URL for a resource that's been added to the test target.
- (NSURL *)URLForResource:(NSString *)name withExtension:(NSString *)extension;

/// Calls +[OCMockObject mockForClass:] and adds the mock and call -verify on it during -tearDown
- (id)autoVerifiedMockForClass:(Class)aClass;
/// C.f. -autoVerifiedMockForClass:
- (id)autoVerifiedPartialMockForObject:(id)object;

/// Calls -verify on the mock during -tearDown
- (void)verifyDuringTearDown:(id)mock;

@end
