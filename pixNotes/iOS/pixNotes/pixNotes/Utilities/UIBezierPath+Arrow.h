//
//  UIBezierPath+Arrow.h
//  pixNotes
//
//  Created by Tuyen Le on 12/4/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>
#define kArrowPointCount 7

@interface UIBezierPath (Arrow)

+ (UIBezierPath *)dqd_bezierPathWithArrowFromPoint:(CGPoint)startPoint
                                           toPoint:(CGPoint)endPoint
                                         tailWidth:(CGFloat)tailWidth
                                         headWidth:(CGFloat)headWidth
                                        headLength:(CGFloat)headLength;
+ (CGAffineTransform)dqd_transformForStartPoint:(CGPoint)startPoint
                                       endPoint:(CGPoint)endPoint
                                         length:(CGFloat)length;
@end
