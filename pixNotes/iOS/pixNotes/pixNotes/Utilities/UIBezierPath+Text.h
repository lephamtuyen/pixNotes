//
//  UIBezierPath+Text.h
//  pixNotes
//
//  Created by Tuyen Le on 12/20/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreText/CoreText.h>


@interface UIBezierPath (Text)

+ (UIBezierPath *)drawText:(NSString*)textToDraw atPoint:(CGPoint)textPoint fontSize:(CGFloat)fontSize;

@end
