//
//  OtherSettingView.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 1/9/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "SettingView.h"

@interface OtherShapeSettingView : SettingView
{
    UILabel *_colorLabel;
}

@property (nonatomic, strong) UISlider *sliderView;
@property (nonatomic, strong) UILabel *borderSizeLabel;

@property (nonatomic, strong) UIColor *lineColor;
@property (nonatomic, assign) CGFloat lineWidth;
@property (nonatomic, strong) NSMutableArray *colorButtons;
@property (nonatomic, strong) NSMutableArray *sizeBorderViews;
@property (nonatomic, strong) NSMutableArray *borderSizeButtons;

- (void)loadLineWidth:(CGFloat)lineWidth color:(UIColor *)color;

- (void)borderColor:(id)sender;
- (void)borderSize:(id)sender;
- (void)valueChanged:(UISlider*)sender;

@end
