//
//  TextSettingView.h
//  pixNotes
//
//  Created by Tuyen Le on 1/21/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "SettingView.h"

@interface TextSettingView : SettingView <UIGestureRecognizerDelegate>
{
    UILabel *_colorLabel;
    
    BOOL _keyboardVisible;
}

@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UISlider *sliderView;
@property (nonatomic, strong) UILabel *fontSizeLabel;

@property (nonatomic, strong) UIColor *textColor;
@property (nonatomic, assign) CGFloat fontSize;
@property (nonatomic, strong) NSMutableArray *colorButtons;
@property (nonatomic, strong) NSMutableArray *sizeFontViews;
@property (nonatomic, strong) NSMutableArray *fontSizeButtons;

- (void)loadFontSize:(CGFloat)fontSize color:(UIColor *)color text:(NSString *)text;

- (void)textColor:(id)sender;
- (void)fontSize:(id)sender;
- (void)valueChanged:(UISlider*)sender;

@end
