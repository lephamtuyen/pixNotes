//
//  GLGestureRecognizer.h
//
//  Created by Adam Preble on 4/28/09.  adam@giraffelab.com
//

#import <Foundation/Foundation.h>

#define kSamplePoints (16)

@interface GLGestureRecognizer : NSObject

@property (nonatomic, strong) NSDictionary *templates;
@property (nonatomic, strong) NSArray *touchPoints;

- (void)findBestMatchScore:(NSMutableArray **)outScore;

@end
