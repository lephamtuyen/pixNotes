//
//  UIImage+Blur.h
//  pixNotes
//
//  Created by Tuyen Le on 12/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <Accelerate/Accelerate.h>

@interface UIImage (Blur)

-(UIImage *)boxblurImageWithBlur:(CGFloat)blur;

@end
