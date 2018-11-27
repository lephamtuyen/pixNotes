//
//  TakeDescriptionView.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/21/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@class TakeDescriptionView;

@protocol TakeDescriptionViewDelegate <NSObject>

- (void)takeDescriptionView:(TakeDescriptionView *)view description:(NSString *)descrition;
- (void)closeTakeDescriptionView;

@end


@interface TakeDescriptionView : UIView <UITextViewDelegate>
{
    CGFloat _contentWidth;
    CGFloat _contentHeight;
    
    BOOL _keyboardVisible;
}

@property (nonatomic, strong) id<TakeDescriptionViewDelegate>delegate;
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UITextView *textView;
@property (nonatomic, strong) UIButton *saveBtn;
@property (nonatomic, strong) UIView *tapView;

- (void)loadDescription:(NSString *)description;
- (void)saveDescription;

@end
