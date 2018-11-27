//
//  ChangeProjectNameView.h
//  pixNotes
//
//  Created by Tuyen Le on 1/15/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@class ChangeProjectNameView;

@protocol ChangeProjectNameViewDelegate <NSObject>

- (void)closeChangeProjectNameView;

@end

@interface ChangeProjectNameView : UIView
{
    CGFloat _contentWidth;
    CGFloat _contentHeight;
    
    BOOL _keyboardVisible;
    
    NSString *_projectDirectory;
}

@property (nonatomic, strong) id<ChangeProjectNameViewDelegate>delegate;
@property (nonatomic, strong) UITextField *projectNameTextField;
@property (nonatomic, strong) UIView *tapView;

- (void)loadProjectDirectory:(NSString *)projectDirectory;
- (void)close;

@end
