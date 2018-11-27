//
//  SettingView.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/20/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>

@class SettingView;

@protocol SettingViewViewDelegate <NSObject>

- (void)settingView:(SettingView *)view choosenFontSize:(CGFloat)fontSize choosenColor:(UIColor *)lineColor choosenText:(NSString *)text;
- (void)settingView:(SettingView *)view choosenLineWidth:(CGFloat)lineWidth choosenColor:(UIColor *)lineColor;
- (void)settingView:(SettingView *)view choosenBlurFactor:(CGFloat)blurFactor;
- (void)closeSettingView;

@end
@interface SettingView : UIView


@property (nonatomic, strong) id<SettingViewViewDelegate>delegate;
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIView *tapView;

- (void)initSubviews;
- (CGFloat)getContentViewWidth;
- (CGFloat)getContentViewHeight;

- (void)closeSettingViewAndApplyNewChanges; // Abstract method

@end
