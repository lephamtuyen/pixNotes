//
//  ImageView.h
//  pixNotes
//
//  Created by Tuyen Le on 11/27/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>


@interface ImageView : UIView

@property (nonatomic, strong) UIView *borderLeft;
@property (nonatomic, strong) UIView *borderRight;
@property (nonatomic, strong) UIView *borderTop;
@property (nonatomic, strong) UIView *borderBottom;

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UIImageView *imageViewIndicator;

@property (nonatomic, assign) BOOL highlightBorder;

@property (nonatomic, assign) CGFloat indicatorWidth;
@property (nonatomic, assign) CGFloat indicatorHeight;

- (void)setImageViewWithImage:(UIImage *)image;

@end
