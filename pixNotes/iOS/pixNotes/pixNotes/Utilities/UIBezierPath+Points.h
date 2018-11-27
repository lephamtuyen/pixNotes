//
//  UIBezierPath+Points.h
//  pixNotes
//
//  Created by Tuyen Le on 1/22/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UIBezierPath (Points)
@property (nonatomic, readonly) NSArray *points;
@property (nonatomic, readonly) NSArray *bezierElements;
@property (nonatomic, readonly) CGFloat length;

- (NSArray *) pointPercentArray;
- (CGPoint) pointAtPercent: (CGFloat) percent withSlope: (CGPoint *) slope;
+ (UIBezierPath *) pathWithPoints: (NSArray *) points;
+ (UIBezierPath *) pathWithElements: (NSArray *) elements;

@end
