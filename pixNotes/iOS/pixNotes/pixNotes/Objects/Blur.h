//
//  Blur.h
//  pixNotes
//
//  Created by Tuyen Le on 12/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <CoreImage/CoreImage.h>
#import "DrawingShape.h"
#import "BlurView.h"

@interface Blur : DrawingShape

@property (nonatomic, strong) BlurView *blurView;
@property (nonatomic, assign) CGFloat blurFactor;
@property (nonatomic, strong) UIImage *blurImage;

- (void)changeImageOfBlurShape:(UIImage *)blurImage;
@end
