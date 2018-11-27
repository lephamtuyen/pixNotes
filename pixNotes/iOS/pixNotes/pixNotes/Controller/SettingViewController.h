//
//  SettingViewController.h
//  pixNotes
//
//  Created by Tuyen Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Settings.h"
#import "SwitchView.h"
#import "ProjectSettings.h"
#import "MainViewController.h"

@interface SettingViewController : UIViewController <SwitchViewDelegate, UIGestureRecognizerDelegate>
{
    NSInteger _gridColorIndex;
    
    UITapGestureRecognizer *_tapBehideGesture;
}

@property (nonatomic, strong) id<MainViewControllerDelegate>delegate;
@property (nonatomic, assign) BOOL autoDetectEnable;
@property (nonatomic, strong) ProjectSettings *projectSettings;

@property (nonatomic, strong) IBOutlet UIButton *backBtn;
@property (nonatomic, strong) IBOutlet SwitchView *gridEnableSwitchView;
@property (nonatomic, strong) IBOutlet SwitchView *autoDetectEnableSwitchView;
@property (nonatomic, strong) IBOutlet UIView *contentGridView;

@property (nonatomic, strong) IBOutlet UIButton *redSmall;
@property (nonatomic, strong) IBOutlet UIButton *redLarge;
@property (nonatomic, strong) IBOutlet UIButton *yellowSmall;
@property (nonatomic, strong) IBOutlet UIButton *yellowLarge;
@property (nonatomic, strong) IBOutlet UIButton *blueSmall;
@property (nonatomic, strong) IBOutlet UIButton *blueLarge;
@property (nonatomic, strong) IBOutlet UIButton *greenSmall;
@property (nonatomic, strong) IBOutlet UIButton *greenLarge;
@property (nonatomic, strong) IBOutlet UIButton *graySmall;
@property (nonatomic, strong) IBOutlet UIButton *grayLarge;
@property (nonatomic, strong) IBOutlet UIButton *whiteSmall;
@property (nonatomic, strong) IBOutlet UIButton *whiteLarge;

@property (nonatomic, strong) IBOutlet UIImageView *indicator1;
@property (nonatomic, strong) IBOutlet UIImageView *indicator2;
@property (nonatomic, strong) IBOutlet UIImageView *indicator3;
@property (nonatomic, strong) IBOutlet UIImageView *indicator4;

@property (nonatomic, strong) NSString *projectDirectory;

- (IBAction)back:(id)sender;

- (IBAction)gridStylePressed:(id)sender;
- (IBAction)gridColorSmallPressed:(id)sender;
@end
