//
//  MainViewController.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>
#import "MainViewController.h"
#import "AbtractViewController.h"
#import "LeftViewController.h"
#import "RightViewController.h"
#import "EditImagesViewController.h"
#import "SendEmailViewController.h"
#import "SettingViewController.h"
#import "OpenProjectViewController.h"
#import "ManageProjectViewController.h"
#import "Constants.h"


@interface MainViewController()
{
    CGRect _centerPanelRestingFrame;
    CGPoint _locationBeforePan;
}

@end

@implementation MainViewController


#pragma mark - NSObject


- (id)init
{
    if (self = [super init])
    {
        [self baseInit];
    }
    return self;
}

- (void)baseInit
{
    [self initLeftPanel];
    [self initRightPanel];
    [self initCenterPanel];

    _leftFixedWidth = 256.0f;
    
    if (IS_IPAD())
    {
         _rightFixedWidth = 190.0f;
    }
    else
    {
         _rightFixedWidth = 128.0f;
    }
    _minimumMovePercentage = 0.05f;
    _maximumAnimationDuration = 0.2f;
}

#pragma mark - UIViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.view.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    
    _centerPanelContainer = [[UIView alloc] initWithFrame:self.view.bounds];
    _centerPanelContainer.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    _centerPanelRestingFrame = _centerPanelContainer.frame;
    
    _leftPanelContainer = [[UIView alloc] initWithFrame:self.view.bounds];
    _leftPanelContainer.hidden = YES;
    _leftPanelContainer.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleRightMargin;
    
    _rightPanelContainer = [[UIView alloc] initWithFrame:self.view.bounds];
    _rightPanelContainer.hidden = YES;
    _rightPanelContainer.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleLeftMargin;
    
    [self.view addSubview:_centerPanelContainer];
    [self.view addSubview:_leftPanelContainer];
    [self.view addSubview:_rightPanelContainer];
    
    self.state = SidePanelCenterVisible;

    _centerPanel.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    _centerPanel.view.frame = _centerPanelContainer.bounds;
    
    [_centerPanelContainer addSubview:_centerPanel.view];
    
    [self.view bringSubviewToFront:_centerPanelContainer];
    
    // Add pan gesture to button/label which loads Right Panel
    [self addPanGestureToView:((EditImagesViewController *)_centerPanel).loadImages];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    // ensure correct view dimensions
    [self layoutSideContainers];
    [self layoutSidePanels];
    _centerPanelContainer.frame = [self adjustCenterFrame];
    [self styleContainer:_centerPanelContainer animate:NO duration:0.0f];
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self adjustCenterFrame]; //Account for possible rotation while view appearing
}

- (void)viewDidUnload {
    [super viewDidUnload];
    self.tapView = nil;
    _centerPanelContainer = nil;
    _leftPanelContainer = nil;
    _rightPanelContainer = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation
{
    return YES;
}

#pragma mark Orientation in iOS 6.0
// Apple replaced shouldAutorotateToInterfaceOrientation method with 02 other methods: supportedInterfaceOrientations, shouldAutorotate
- (NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskAll;
}

- (BOOL)shouldAutorotate
{
    return YES;
}


- (void)willAnimateRotationToInterfaceOrientation:(__unused UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    _centerPanelContainer.frame = [self adjustCenterFrame];
    [self layoutSideContainers];
    [self layoutSidePanels];
    [self styleContainer:_centerPanelContainer animate:YES duration:duration];
}

#pragma mark - State

- (void)setState:(SidePanelState)state
{
    if (state != _state)
    {
        _state = state;
        switch (_state)
        {
            case SidePanelCenterVisible:
            {
                _visiblePanel = _centerPanel;
                _leftPanelContainer.userInteractionEnabled = NO;
                _rightPanelContainer.userInteractionEnabled = NO;
                break;
			}
            case SidePanelLeftVisible:
            {
                _visiblePanel = _leftPanel;
                _leftPanelContainer.userInteractionEnabled = YES;
                break;
			}
            case SidePanelRightVisible:
            {
                _visiblePanel = _rightPanel;
                _rightPanelContainer.userInteractionEnabled = YES;
                break;
			}
        }
        
        [self updateSlideButton];
    }
    
    
}

- (void)updateSlideButton
{
    switch (_state)
    {
        case SidePanelRightVisible:
        {
            ((EditImagesViewController *)_centerPanel).loadImages.image = [UIImage imageNamed:@"slide_back.png"];
            break;
        }
        default:
        {
            ((EditImagesViewController *)_centerPanel).loadImages.image = [UIImage imageNamed:@"slide_show.png"];
            break;
        }
    }
}

#pragma mark - Style

- (void)styleContainer:(UIView *)container animate:(BOOL)animate duration:(NSTimeInterval)duration
{
    UIBezierPath *shadowPath = [UIBezierPath bezierPathWithRoundedRect:container.bounds cornerRadius:0.0f];
    if (animate)
    {
        CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"shadowPath"];
        animation.fromValue = (id)container.layer.shadowPath;
        animation.toValue = (id)shadowPath.CGPath;
        animation.duration = duration;
        [container.layer addAnimation:animation forKey:@"shadowPath"];
    }
    container.layer.shadowPath = shadowPath.CGPath;
    container.layer.shadowColor = [UIColor blackColor].CGColor;
    container.layer.shadowRadius = 10.0f;
    container.layer.shadowOpacity = 0.75f;
    container.clipsToBounds = NO;
}

- (void)layoutSideContainers
{
    CGRect leftFrame = self.view.bounds;
    CGRect rightFrame = self.view.bounds;
    _leftPanelContainer.frame = leftFrame;
    _rightPanelContainer.frame = rightFrame;
    [self styleContainer:_leftPanelContainer animate:NO duration:0.0f];
    [self styleContainer:_rightPanelContainer animate:NO duration:0.0f];
}

- (void)layoutSidePanels
{
    if (_rightPanel.isViewLoaded)
    {
        CGRect frame = _rightPanelContainer.bounds;
        _rightPanel.view.frame = frame;
    }
    if (_leftPanel.isViewLoaded)
    {
        CGRect frame = _leftPanelContainer.bounds;
        _leftPanel.view.frame = frame;
    }
}

#pragma mark - Panels

- (void)initCenterPanel
{
    _centerPanel = [[EditImagesViewController alloc] initWithMainViewController:self];
    [self addChildViewController:_centerPanel];
    [_centerPanel didMoveToParentViewController:self];

    if (_state == SidePanelCenterVisible)
    {
        _visiblePanel = _centerPanel;
    }
}


- (void)initLeftPanel
{
    _leftPanel = [[LeftViewController alloc] initWithMainViewController:self];
    [self addChildViewController:_leftPanel];
    [_leftPanel didMoveToParentViewController:self];
}

- (void)initRightPanel
{
    _rightPanel = [[RightViewController alloc] initWithMainViewController:self];
    [self addChildViewController:_rightPanel];
    [_rightPanel didMoveToParentViewController:self];
}

#pragma mark - Gesture Recognizer Delegate

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
    if (gestureRecognizer.view == _tapView)
    {
        return YES;
    }
    else if (![self isOnTopLevelViewController:_centerPanel])
    {
        return NO;
    }
    else if ([gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]])
    {
        UIPanGestureRecognizer *pan = (UIPanGestureRecognizer *)gestureRecognizer;
        CGPoint translate = [pan translationInView:_centerPanelContainer];
        // Allow swipe right
        if (translate.x < 0)
        {
            return YES;
        }
    }
    
    return NO;
}

#pragma mark - Pan Gestures

- (void)addPanGestureToView:(UIView *)view
{
    UIPanGestureRecognizer *panGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePan:)];
    panGesture.delegate = self;
    panGesture.maximumNumberOfTouches = 1;
    panGesture.minimumNumberOfTouches = 1;
    [view addGestureRecognizer:panGesture];
}

- (void)wasDragged:(UIButton *)button withEvent:(UIEvent *)event
{
	// get the touch
	UITouch *touch = [[event touchesForView:button] anyObject];
    
	// get delta
	CGPoint previousLocation = [touch previousLocationInView:button];
	CGPoint location = [touch locationInView:button];
	CGFloat delta_x = location.x - previousLocation.x;
	CGFloat delta_y = location.y - previousLocation.y;
    
	// move button
	button.center = CGPointMake(button.center.x + delta_x,
                                button.center.y + delta_y);
}

- (void)handlePan:(UIGestureRecognizer *)sender
{
    if ([sender isKindOfClass:[UIPanGestureRecognizer class]])
    {
        UIPanGestureRecognizer *pan = (UIPanGestureRecognizer *)sender;
        
        if (pan.state == UIGestureRecognizerStateBegan)
        {
            _locationBeforePan = _centerPanelContainer.frame.origin;
        }
        
        CGPoint translate = [pan translationInView:_centerPanelContainer];
        CGRect frame = _centerPanelRestingFrame;
        frame.origin.x += roundf([self correctMovement:translate.x]);
        
        _centerPanelContainer.frame = frame;
        
        // if center panel has focus, make sure correct side panel is revealed
        if (_state == SidePanelCenterVisible)
        {
            if (frame.origin.x > 0.0f)
            {
                [self loadLeftPanel];
            }
            else if(frame.origin.x < 0.0f)
            {
                [self loadRightPanel];
            }
        }
        
        if (sender.state == UIGestureRecognizerStateEnded)
        {
            CGFloat deltaX =  frame.origin.x - _locationBeforePan.x;
            if ([self validateThreshold:deltaX])
            {
                [self completePan:deltaX];
            }
            else
            {
                [self undoPan];
            }
        }
        else if (sender.state == UIGestureRecognizerStateCancelled)
        {
            [self undoPan];
        }
    }
}

- (void)completePan:(CGFloat)deltaX
{
    switch (_state)
    {
        case SidePanelCenterVisible:
        {
            if (deltaX > 0.0f)
            {
                [self showLeftPanel:YES];
            }
            else
            {
                [self showRightPanel:YES];
            }
            break;
		}
        case SidePanelLeftVisible:
        {
            [self showCenterPanel:YES];
            break;
		}
        case SidePanelRightVisible:
        {
            [self showCenterPanel:YES];
            break;
		}
    }
}

- (void)undoPan
{
    switch (_state)
    {
        case SidePanelCenterVisible:
        {
            [self showCenterPanel:YES];
            break;
		}
        case SidePanelLeftVisible:
        {
            [self showLeftPanel:YES];
            break;
		}
        case SidePanelRightVisible:
        {
            [self showRightPanel:YES];
            break;
		}
    }
}

#pragma mark - Tap Gesture

- (void)setTapView:(UIView *)tapView
{
    if (tapView != _tapView)
    {
        [_tapView removeFromSuperview];
        _tapView = tapView;
        if (_tapView)
        {
            _tapView.frame = _centerPanelContainer.bounds;
            _tapView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
            [self addTapGestureToView:_tapView];
            [self addPanGestureToView:_tapView];
            [_centerPanelContainer addSubview:_tapView];
        }
    }
}

- (void)addTapGestureToView:(UIView *)view
{
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(centerPanelTapped:)];
    [view addGestureRecognizer:tapGesture];
}

- (void)centerPanelTapped:(__unused UIGestureRecognizer *)gesture
{
    [self showCenterPanel:YES];
}

#pragma mark - Internal Methods

- (CGFloat)correctMovement:(CGFloat)movement
{
    CGFloat position = _centerPanelRestingFrame.origin.x + movement;
    if (_state == SidePanelCenterVisible)
    {
        if ((position > 0.0f && !_leftPanel) || (position < 0.0f && !_rightPanel))
        {
            return 0.0f;
        }
        else if (position > self.leftVisibleWidth)
        {
            return self.leftVisibleWidth;
        }
        else if (position < -self.rightVisibleWidth)
        {
            return -self.rightVisibleWidth;
        }
    }
    else if (_state == SidePanelRightVisible)
    {
        if (position < -self.rightVisibleWidth)
        {
            return 0.0f;
        }
        else if (position > _rightPanelContainer.frame.origin.x)
        {
            return _rightPanelContainer.frame.origin.x - _centerPanelRestingFrame.origin.x;
        }
    }
    else if (_state == SidePanelLeftVisible)
    {
        if (position > self.leftVisibleWidth)
        {
            return 0.0f;
        }
        else if (position < _leftPanelContainer.frame.origin.x)
        {
            return _leftPanelContainer.frame.origin.x - _centerPanelRestingFrame.origin.x;
        }
    }
    return movement;
}

- (BOOL)validateThreshold:(CGFloat)movement
{
    CGFloat minimum = floorf(self.view.bounds.size.width * _minimumMovePercentage);
    switch (_state)
    {
        case SidePanelLeftVisible:
        {
            return movement <= -minimum;
		}
        case SidePanelCenterVisible:
        {
            return fabsf(movement) >= minimum;
		}
        case SidePanelRightVisible:
        {
            return movement >= minimum;
		}
    }
    return NO;
}

- (BOOL)isOnTopLevelViewController:(UIViewController *)root
{
    if ([root isKindOfClass:[UINavigationController class]])
    {
        UINavigationController *nav = (UINavigationController *)root;
        return [nav.viewControllers count] == 1;
    }
    else if ([root isKindOfClass:[UITabBarController class]])
    {
        UITabBarController *tab = (UITabBarController *)root;
        return [self isOnTopLevelViewController:tab.selectedViewController];
    }
    return root != nil;
}

#pragma mark - Loading Panels

- (void)loadLeftPanel
{
    _rightPanelContainer.hidden = YES;
    if (_leftPanelContainer.hidden && _leftPanel)
    {
        if (!_leftPanel.view.superview)
        {
            [self layoutSidePanels];
            _leftPanel.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
            [_leftPanelContainer addSubview:_leftPanel.view];
        }
        
        _leftPanelContainer.hidden = NO;
    }
}

- (void)loadRightPanel
{
    _leftPanelContainer.hidden = YES;
    if (_rightPanelContainer.hidden && _rightPanel)
    {
        if (!_rightPanel.view.superview)
        {
            [self layoutSidePanels];
            _rightPanel.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
            [_rightPanelContainer addSubview:_rightPanel.view];
        }
        
        _rightPanelContainer.hidden = NO;
    }
}

#pragma mark - Animation

- (CGFloat)calculatedDuration
{
    CGFloat remaining = fabsf(_centerPanelContainer.frame.origin.x - _centerPanelRestingFrame.origin.x);
    CGFloat max = _locationBeforePan.x == _centerPanelRestingFrame.origin.x ? remaining : fabsf(_locationBeforePan.x - _centerPanelRestingFrame.origin.x);
    return max > 0.0f ? _maximumAnimationDuration * (remaining / max) : _maximumAnimationDuration;
}

- (void)animateCenterPanelWithCompletion:(void (^)(BOOL finished))completion
{
    CGFloat duration = [self calculatedDuration];
    [UIView animateWithDuration:duration delay:0.0f options:UIViewAnimationOptionCurveLinear|UIViewAnimationOptionLayoutSubviews animations:^{
        _centerPanelContainer.frame = _centerPanelRestingFrame;
        [self styleContainer:_centerPanelContainer animate:YES duration:duration];
    } completion:^(BOOL finished) {
        if (completion)
        {
            completion(finished);
        }
    }];
}

#pragma mark - Panel Sizing

- (CGRect)adjustCenterFrame
{
    CGRect frame = self.view.bounds;
    switch (_state)
    {
        case SidePanelCenterVisible:
        {
            frame.origin.x = 0.0f;
            break;
		}
        case SidePanelLeftVisible:
        {
            frame.origin.x = self.leftVisibleWidth;
            break;
		}
        case SidePanelRightVisible:
        {
            frame.origin.x = -self.rightVisibleWidth;
            break;
		}
    }
    
    _centerPanelRestingFrame = frame;
    return _centerPanelRestingFrame;
}

- (CGFloat)leftVisibleWidth
{
    return _leftFixedWidth ? _leftFixedWidth : floorf(self.view.bounds.size.width * _leftGapPercentage);
}

- (CGFloat)rightVisibleWidth
{
    return _rightFixedWidth ? _rightFixedWidth : floorf(self.view.bounds.size.width * _rightGapPercentage);
}

#pragma mark - Showing Panels

- (void)showLeftPanel:(BOOL)animated
{
    self.state = SidePanelLeftVisible;
    [self loadLeftPanel];
    
    [self adjustCenterFrame];
    
    if (animated)
    {
        [self animateCenterPanelWithCompletion:nil];
    }
    else
    {
        _centerPanelContainer.frame = _centerPanelRestingFrame;
        [self styleContainer:_centerPanelContainer animate:NO duration:0.0f];
    }
    
    self.tapView = [[UIView alloc] init];
}

- (void)showRightPanel:(BOOL)animated
{
    self.state = SidePanelRightVisible;
    [self loadRightPanel];
    
    [self adjustCenterFrame];
    
    if (animated)
    {
        [self animateCenterPanelWithCompletion:nil];
    }
    else
    {
        _centerPanelContainer.frame = _centerPanelRestingFrame;
        [self styleContainer:_centerPanelContainer animate:NO duration:0.0f];
    }
    
    self.tapView = [[UIView alloc] init];
}

- (void)showCenterPanel:(BOOL)animated
{
    self.state = SidePanelCenterVisible;
    
    [self adjustCenterFrame];
    
    if (animated)
    {
        [self animateCenterPanelWithCompletion:^(__unused BOOL finished) {
            _leftPanelContainer.hidden = YES;
            _rightPanelContainer.hidden = YES;
        }];
    }
    else
    {
        _centerPanelContainer.frame = _centerPanelRestingFrame;
        [self styleContainer:_centerPanelContainer animate:NO duration:0.0f];
        _leftPanelContainer.hidden = YES;
        _rightPanelContainer.hidden = YES;
    }
    
    self.tapView = nil;
}

#pragma mark - New project popup

- (void)showNewProjectViewPopup
{
    if (!_createNewProjectView)
    {
        _createNewProjectView = [[NewProjectView alloc] init];
        _createNewProjectView.autoresizingMask = (UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth);
        _createNewProjectView.delegate = self;
    }
    
    [_createNewProjectView setFrame:self.view.frame];
    [self.view addSubview:_createNewProjectView];
    
}

- (void)closeNewProjectViewPopupFromSuperView
{
    if (_createNewProjectView)
    {
        [_createNewProjectView removeFromSuperview];
        _createNewProjectView = nil;
    }
}


#pragma mark - Show Import Image Controller

- (void)showImportImageControllerWithDelegate:(id)delegate
{
    AGImagePickerController *imagePicker = [[AGImagePickerController alloc] initWithDelegate:delegate];
    imagePicker.maximumNumberOfPhotosToBeSelected = 20;
    
    [self presentModalViewController:imagePicker animated:YES];
}

- (void)showCameraToCaptureImageWithDelegate:(id)delegate
{
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
    {
        UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
        
        imagePicker.delegate = delegate;
        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        [self presentModalViewController:imagePicker animated:YES];
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                        message:@"No camera on this device"
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
    }
}

#pragma mark - NewProjectView delegate

- (void)showImportImageController:(NewProjectView *)view
{
    [self showImportImageControllerWithDelegate:view];
}


- (void)showCameraToCaptureImage:(NewProjectView *)view
{
    [self showCameraToCaptureImageWithDelegate:view];
}

- (void)closeCameraController
{
    [self dismissModalViewControllerAnimated:YES];
}


- (void)closeNewProjectViewPopup
{
    [self closeNewProjectViewPopupFromSuperView];
}

- (void)showCentralPanel
{
    [self showCenterPanel:YES];
}

- (void)loadProjectWithProjectDirectory:(NSString *)projectDirectory
{
    [((EditImagesViewController *)self.centerPanel) loadFirstImageWithProjectDirectory:projectDirectory];
}

#pragma mark - MainViewController Delegate

- (void)refreshGridView
{
    [((EditImagesViewController *)self.centerPanel).gridView setNeedsDisplay];
}

- (void)updateButtonsState
{
    [((EditImagesViewController *)self.centerPanel) updateStateOfButtons];
}

- (void)updateCatchImagewithImageDirectory:(NSString *)imageDirectory
{
    UIImage *cacheImage = [((RightViewController *)_rightPanel) imageWithKey:imageDirectory];
    if (cacheImage)
    {
        [((RightViewController *)_rightPanel) updateCacheWithImageDirectory:imageDirectory];
    }
}

- (void)viewController:(UIViewController *)controller loadProjectWithProjectDirectory:(NSString *)projectDirectory
{
    [self loadProjectWithProjectDirectory:projectDirectory];
}

- (void)removeCurrentDrawing:(NSInteger)removeImageIndex
{
    [((EditImagesViewController *)self.centerPanel) removeCurrentDrawing:removeImageIndex];
}

- (NSString *)getCurrentProjectDirectory
{
    return ((EditImagesViewController *)self.centerPanel).projectDirectory;
}

- (void)loadImageWithImageDirectory:(NSString *)imageDirectory andShowCentralPanel:(BOOL)isShowCentral
{
    [((EditImagesViewController *)self.centerPanel) loadImageWithImageDirectory:imageDirectory];
    
    if (isShowCentral)
    {
        [self showCenterPanel:YES];
    }
}

- (void)viewController:(UIViewController *)controller closeCameraController:(id)sender
{
    [self closeCameraController];
}

- (void)viewController:(UIViewController *)controller showImportImageControllerPressed:(id)sender
{
    [self showImportImageControllerWithDelegate:controller];
}

- (void)viewController:(UIViewController *)controller showCameraToCaptureImageControllerPressed:(id)sender
{
    [self showCameraToCaptureImageWithDelegate:controller];
}

- (void)viewController:(UIViewController *)controller showNewProjectPopupPressed:(id)sender
{
    [self showNewProjectViewPopup];
}

- (void)viewController:(UIViewController *)controller showHideLeftControllerPressed:(id)sender
{
    if (_state == SidePanelLeftVisible)
    {
        [self showCenterPanel:YES];
    }
    else if (_state == SidePanelCenterVisible)
    {
        [((LeftViewController *)_leftPanel) setupTableView];
        [((LeftViewController *)_leftPanel).tableView reloadData];
        [self showLeftPanel:YES];
    }
}

- (void)viewController:(UIViewController *)controller
showHideRightControllerPressed:(NSString *)currentImageDirectory
currentProjectDirectory:(NSString *)currentProjectDirectory
delayWhenShow:(BOOL)isDelay
{
    if (_state == SidePanelRightVisible)
    {
        [self showCenterPanel:YES];
    }
    else if (_state == SidePanelCenterVisible || _state == SidePanelLeftVisible)
    {
        if (!_rightPanel.isViewLoaded)
        {
            [_rightPanel view];
            [((RightViewController *)_rightPanel) adjustSubviewFrame];
        }
        
        [((EditImagesViewController *)self.centerPanel) saveDrawingToDirectory:currentImageDirectory saveFlatCopy:YES];
        ((RightViewController *)_rightPanel).activeImageDirectory = currentImageDirectory;
        ((RightViewController *)_rightPanel).activeProjectDirectory = currentProjectDirectory;

        if (_state == SidePanelLeftVisible)
        {
            [self showCenterPanel:YES];
        }
        
        // Delay when show right panel (smooth animation)
        if (isDelay)
        {
            [self performSelector:@selector(showRightPanel:) withObject:[NSNumber numberWithBool:YES] afterDelay:1];
        }
        else
        {
            [self showRightPanel:YES];
        }
    }
}

- (void)viewController:(UIViewController *)controller choosenControllerIndex:(NSInteger)index
{
    if (index == 0)
    {
        [self showNewProjectViewPopup];
    }
    else if (index == 1)
    {
        OpenProjectViewController *controller = [[OpenProjectViewController alloc] init];
        controller.delegate = self;
        [self.navigationController pushViewController:controller animated:YES];
        [self showCenterPanel:NO];
    }
    else if (index == 2)
    {
        ManageProjectViewController *controller = [[ManageProjectViewController alloc] init];
        [self.navigationController pushViewController:controller animated:YES];
        [self showCenterPanel:NO];
    }
    else if (index == 3)
    {
        SettingViewController *controller = [[SettingViewController alloc] init];
        controller.projectDirectory = ((EditImagesViewController *)self.centerPanel).projectDirectory;
        controller.delegate = self;

        if (IS_IPAD())
        {
            controller.modalPresentationStyle = UIModalPresentationFormSheet;
            [self presentModalViewController:controller animated:YES];
            
            CGRect frame;
            CGFloat width = 320;
            CGFloat height = 480;
            CGFloat windowWidth = 768;
            CGFloat windowHeight = 1024;
            if (UIInterfaceOrientationIsPortrait([[UIApplication sharedApplication] statusBarOrientation]))
            {
                frame.origin.x = (windowWidth - width) / 2;
                frame.origin.y = (windowHeight - height) / 2;
                frame.size.width = width;
                frame.size.height = height;
            }
            else
            {
                frame.origin.x = (windowHeight - width) / 2;
                frame.origin.y = (windowWidth - height) / 2;
                frame.size.width = width;
                frame.size.height = height;
            }
            
            controller.view.superview.frame = frame;
        }
        else
        {
            if (UIInterfaceOrientationIsLandscape([[UIApplication sharedApplication] statusBarOrientation]))
            {
                [self presentModalViewController:controller animated:YES];
            }
            else
            {
                [self.navigationController pushViewController:controller animated:YES];
            }
            [self showCenterPanel:NO];
        }
    }
}

- (void)saveCurrentImageWhenEnterBackgroud
{
    [((EditImagesViewController *)self.centerPanel) saveCurrentImageWhenLeaveController];
}

@end
