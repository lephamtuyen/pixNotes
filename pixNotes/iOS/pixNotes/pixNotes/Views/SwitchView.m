//
//  SwitchView.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "SwitchView.h"
#import "Colors.h"

@implementation SwitchView


- (id)init
{
    self = [super init];
    if (self)
    {
        [self initSubviews];
        [self addGesture];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self)
    {
        [self initSubviews];
        [self addGesture];
    }
    return self;
}

- (void)setIsOn:(BOOL)isOn
{
    _isOn = isOn;
    
    if (_isOn)
    {
        [_onOffLabel setBackgroundColor:GREEN_TEXT_COLOR];
        _onOffLabel.text = @"ON";
        [UIView animateWithDuration:0.2
                         animations:^{
                             [_onOffLabel setFrame:CGRectMake(self.frame.size.width / 2 - 2, 2, self.frame.size.width / 2, self.frame.size.height - 4)];
                         }];
    }
    else
    {
        [_onOffLabel setBackgroundColor:[UIColor colorWithRed:206.0/255.0 green:80.0/255.0 blue:66.0/255.0 alpha:1.0]];
        _onOffLabel.text = @"OFF";
        [UIView animateWithDuration:0.2
                         animations:^{
                             [_onOffLabel setFrame:CGRectMake(2, 2, self.frame.size.width / 2, self.frame.size.height - 4)];
                         }];
    }
}

- (void)addGesture
{
    _tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(toggleOnOff:)];
    _tap.numberOfTapsRequired = 1;
    [self addGestureRecognizer:_tap];
}

- (void)initSubviews
{
    self.layer.cornerRadius = 7;
    self.layer.masksToBounds = YES;
    
    _onOffLabel = [[UILabel alloc] init];
    _onOffLabel.textColor = [UIColor whiteColor];
    _onOffLabel.layer.cornerRadius = 7;
    _onOffLabel.layer.masksToBounds = YES;
    _onOffLabel.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16];
    _onOffLabel.textAlignment = UITextAlignmentCenter;
    [self addSubview:_onOffLabel];
}

- (void)dealloc
{
    if (_tap)
    {
        [self removeGestureRecognizer:_tap];
        _tap = nil;
    }
}

- (void)toggleOnOff:(id)sender
{
    self.isOn = !_isOn;
    if (_delegate)
    {
        [_delegate toggleSwitch:_isOn sender:sender];
    }
}


@end
