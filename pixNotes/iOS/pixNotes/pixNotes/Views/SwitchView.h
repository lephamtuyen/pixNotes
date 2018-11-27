//
//  SwitchView.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SwitchViewDelegate

- (void)toggleSwitch:(BOOL)enable sender:(id)sender;

@end

@interface SwitchView : UIView
{
    UITapGestureRecognizer *_tap;
}

@property (nonatomic, strong) id<SwitchViewDelegate>delegate;
@property (nonatomic, assign) BOOL isOn;
@property (nonatomic, strong) UILabel *onOffLabel;

- (void)toggleOnOff:(id)sender;
@end
