//
//  MainViewController.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "NewProjectView.h"

@class AbtractViewController;


typedef enum _SidePanelState
{
    SidePanelCenterVisible = 1,
    SidePanelLeftVisible,
    SidePanelRightVisible
} SidePanelState;

@protocol MainViewControllerDelegate <NSObject>

- (void)viewController:(UIViewController *)controller showNewProjectPopupPressed:(id)sender;
- (void)viewController:(UIViewController *)controller   showHideRightControllerPressed:(NSString *)currentImageDirectory
                                                        currentProjectDirectory:(NSString *)currentProjectDirectory
                                                        delayWhenShow:(BOOL)isDelay;
- (void)viewController:(UIViewController *)controller showHideLeftControllerPressed:(id)sender;
- (void)viewController:(UIViewController *)controller choosenControllerIndex:(NSInteger)index;
- (void)viewController:(UIViewController *)controller showImportImageControllerPressed:(id)sender;
- (void)viewController:(UIViewController *)controller showCameraToCaptureImageControllerPressed:(id)sender;
- (void)viewController:(UIViewController *)controller closeCameraController:(id)sender;
- (void)viewController:(UIViewController *)controller loadProjectWithProjectDirectory:(NSString *)projectDirectory;
- (void)removeCurrentDrawing:(NSInteger)removeImageIndex;
- (void)loadImageWithImageDirectory:(NSString *)imageDirectory andShowCentralPanel:(BOOL)isShowCentral;
- (NSString *)getCurrentProjectDirectory;
- (void)updateCatchImagewithImageDirectory:(NSString *)imageDirectory;
- (void)updateButtonsState;
- (void)refreshGridView;

@end


@interface MainViewController : UIViewController<UIGestureRecognizerDelegate, MainViewControllerDelegate, NewProjectViewDelegate>

#pragma mark - Usage

// set the panels
@property (nonatomic, strong) UIViewController *leftPanel;   // optional
@property (nonatomic, strong) UIViewController *centerPanel; // required
@property (nonatomic, strong) UIViewController *rightPanel;  // optional


#pragma mark - Look & Feel

// size the left panel based on % of total screen width
@property (nonatomic) CGFloat leftGapPercentage;

// size the left panel based on this fixed size. overrides leftGapPercentage
@property (nonatomic) CGFloat leftFixedWidth;

// the visible width of the left panel
@property (nonatomic, readonly) CGFloat leftVisibleWidth;

// size the right panel based on % of total screen width
@property (nonatomic) CGFloat rightGapPercentage;

// size the right panel based on this fixed size. overrides rightGapPercentage
@property (nonatomic) CGFloat rightFixedWidth;

// the visible width of the right panel
@property (nonatomic, readonly) CGFloat rightVisibleWidth;

// by default applies a black shadow to the container. override in sublcass to change
- (void)styleContainer:(UIView *)container animate:(BOOL)animate duration:(NSTimeInterval)duration;

#pragma mark - Animation

// the minimum % of total screen width the centerPanel.view must move for panGesture to succeed
@property (nonatomic) CGFloat minimumMovePercentage;

// the maximum time panel opening/closing should take. Actual time may be less if panGesture has already moved the view.
@property (nonatomic) CGFloat maximumAnimationDuration;


#pragma mark - Nuts & Bolts

// Current state of panels. Use KVO to monitor state changes
@property (nonatomic, readonly) SidePanelState state;

// The currently visible panel
@property (nonatomic, weak, readonly) UIViewController *visiblePanel;

// Containers for the panels.
@property (nonatomic, strong, readonly) UIView *leftPanelContainer;
@property (nonatomic, strong, readonly) UIView *rightPanelContainer;
@property (nonatomic, strong, readonly) UIView *centerPanelContainer;

@property (nonatomic, strong) UIView *tapView;
@property (nonatomic, strong) NewProjectView *createNewProjectView;

- (void)saveCurrentImageWhenEnterBackgroud;
@end
