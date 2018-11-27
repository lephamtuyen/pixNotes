//
//  BlurSettingView.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 1/9/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "SettingView.h"

@interface BlurSettingView : SettingView

@property (nonatomic, strong) UILabel *blurLabel;
@property (nonatomic, assign) CGFloat blurFactor;
@property (nonatomic, strong) UISlider *sliderView;

- (void)loadBlurFactor:(CGFloat)blurFactor;

@end
