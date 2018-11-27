//
//  EditPictureViewController.h
//  pixNotes
//
//  Created by Tuyen Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import <CoreText/CoreText.h>
#import "AbtractViewController.h"
#import "SettingView.h"
#import "DrawingView.h"
#import "TakeDescriptionView.h"
#import "GridView.h"
#import "GLGestureRecognizer.h"
#import "GLGestureRecognizer+JSONTemplates.h"
#import "AGImagePickerController.h"
#import "RecordAudioView.h"
#import "ProjectTableViewController.h"


@interface EditImagesViewController : AbtractViewController <SettingViewViewDelegate, TakeDescriptionViewDelegate, UIAlertViewDelegate, RecordAudioViewDelegate, UIScrollViewDelegate>
{
    BOOL _allowZoomImage;
    
    // Determine focus on shape or not
    BOOL _isFocusOnOtherShape;
    
    // Only use when shape is blur, circle, rectangle
    NSInteger _selectedBorderIndex;
    
    // Improve drawing by Bezier path
    uint _ctr;
    CGPoint _pts[5];
    
    // New path draw
    UIBezierPath *_newPath;
    
    
    // Temp Point for handle gestures
    CGPoint _tempPointForHandleGestures;
    
    // Only for iPad
    UIPopoverController *_sendEmailPopup;
}

@property (nonatomic, strong) IBOutlet UILabel *appTitle;
@property (nonatomic, strong) IBOutlet UIButton *undoBtn;
@property (nonatomic, strong) IBOutlet UIButton *redoBtn;
@property (nonatomic, strong) IBOutlet UIButton *takeDescriptionBtn;
@property (nonatomic, strong) IBOutlet UIButton *allowDrawingBtn;
@property (nonatomic, strong) IBOutlet UIImageView *loadImages;
@property (nonatomic, strong) IBOutlet UIButton *shareBtn;
@property (nonatomic, strong) IBOutlet UIButton *menuBtn;
@property (nonatomic, strong) IBOutlet UIButton *addNewBtn;
@property (nonatomic, strong) IBOutlet UIButton *recordAudioBtn;

@property (nonatomic, strong) IBOutlet UIView *workSpaceView;
@property (nonatomic, strong) IBOutlet UIView *workingImageView;

@property (nonatomic, strong) IBOutlet UIImageView *imageView;
@property (nonatomic, strong) IBOutlet UIView *deleteView;
@property (nonatomic, strong) IBOutlet UIView *footerView;
@property (nonatomic, strong) IBOutlet UIImageView *deleteImageIndicator;
@property (nonatomic, strong) IBOutlet GridView *gridView;
@property (nonatomic, strong) IBOutlet UIView *backBlurView;
@property (nonatomic, strong) IBOutlet DrawingView *drawingView;
@property (nonatomic, strong) IBOutlet UIView *frontBlurView;
@property (nonatomic, strong) IBOutlet UIImageView *backgroundImageView;

@property (nonatomic, strong) IBOutlet UIView *tutorialView;
@property (nonatomic, strong) IBOutlet UIView *projectTableView;
@property (nonatomic, strong) IBOutlet UIView *noProjectView;
@property (nonatomic, strong) IBOutlet UIScrollView *scrollView;
@property (nonatomic, strong) IBOutlet UIButton *skipBtn;
@property (nonatomic, strong) IBOutlet UIPageControl *pageControl;
@property (nonatomic, strong) NSArray *pageImages;
@property (nonatomic, strong) NSMutableArray *pageViews;

// Blur image
@property (nonatomic, strong) UIImage *blurImage;
@property (nonatomic, assign) CGFloat originScale;

// Allow touch or not
@property (nonatomic, assign) BOOL allowHandleTouch;
@property (nonatomic, assign) BOOL allowDrawing;

// Setting View
@property (nonatomic, strong) SettingView *settingView;

// Take Description View
@property (nonatomic, strong) TakeDescriptionView *takeDescriptionView;
@property (nonatomic, strong) NSString *description;

// Record audio View
@property (nonatomic, strong) RecordAudioView *recordAudioView;

// Drawing Shape
@property (nonatomic, assign) NSUInteger selectedShapeIndex;
@property (nonatomic, strong) NSMutableArray *shapes;

// Current drawing shape index
@property (nonatomic, assign) int activeShapeIndex;

// Handle undo/redo
@property (nonatomic, assign) int undoStackCount;

// Identity of current drawing
@property (nonatomic, strong) NSString *imageDirectory;
@property (nonatomic, strong) NSString *projectDirectory;

// Object use in detect shape algorithm
@property (nonatomic, strong) GLGestureRecognizer *shapeRecognizer;

// Point array use in detect shape algorithm
@property (nonatomic, strong) NSMutableArray *pointsArray;

// Gestures
@property (nonatomic, strong) UITapGestureRecognizer *tap;

@property (nonatomic, strong) UITapGestureRecognizer *oneTapHitShapeRecognizer;
@property (nonatomic, strong) UITapGestureRecognizer *doubleTapTwoTouchScaleToOriginRecognizer;
@property (nonatomic, strong) UITapGestureRecognizer *doubleTapDrawTextRecognizer;
@property (nonatomic, strong) UIPanGestureRecognizer *drawShapeRecognizer;
@property (nonatomic, strong) UIPanGestureRecognizer *moveDrawingRecognizer;
@property (nonatomic, strong) UIPinchGestureRecognizer *zoomDrawingRecognizer;
@property (nonatomic, strong) UILongPressGestureRecognizer *showSettingViewRecognizer;

@property (nonatomic, strong) IBOutlet ProjectTableViewController *projectTableViewController;

// IBAction
- (IBAction)takeDescriptionActionTapped:(id)sender;
- (IBAction)showHideLeftControllerPressed:(id)sender;

// Gesture recognition action
- (void)takeDescriptionLabelTapped;

// Update button state
- (void)updateStateOfButtons;

// Setup view background
- (void)setupViewBackground;

// File handling - Load / Save / Delete drawings
- (void)initializeDrawingImageWithImageDirectory:(NSString *)drawingID;
- (void)loadTemplateToRecognizeShape;
- (void)initializeShapes;
- (DrawingShape *)createShapeWithShapeType:(Class)shapeClass;
- (DrawingShape *)createShapeFromFolloweeShape:(DrawingShape *)followeeShape;
- (void)updateCurrentShapeIndex;
- (void)removeShapeBehindActiveShape;
- (DrawingShape *)drawingShapeAtIndex:(int)index;
- (DrawingShape *)drawingShapeWithUUID:(NSString *)uuid;
- (DrawingShape *)selectedShape;
- (DrawingShape *)activeShape;
- (void)initializePointsArray;
- (void)addPointToPointsArray:(CGPoint)point;

//Load drawing
- (void)loadImageWithImageDirectory:(NSString *)drawingID;
- (void)removeCurrentDrawing:(NSInteger)removeImageIndex;

- (void)loadFirstImageWithProjectDirectory:(NSString *)projectDirectory;

// Add gesture
- (void)addGesture;

// Shapes handling
- (UIImage *)blurImageChangeNew:(BOOL)isNew;

- (void)saveCurrentImageWhenLeaveController;
- (void)saveDrawingToDirectory:(NSString*)imageDirectory saveFlatCopy:(BOOL)saveFlatCopy;

- (void)showSettingView;
@end
