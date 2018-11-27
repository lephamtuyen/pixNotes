//
//  EditPictureViewController.m
//  pixNotes
//
//  Created by Tuyen Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import "EditImagesViewController.h"
#import "MainViewController.h"
#import "NSString+UUID.h"
#import "Settings.h"
#import "Constants.h"
#import "UIImage+FixOrientation.h"
#import "DrawingShape.h"
#import "Utilities.h"
#import "UIImage+Resize.h"
#import "UIBezierPath+Arrow.h"
#import "UIBezierpath+Text.h"
#import "Text.h"
#import "Blur.h"
#import "Rectangle.h"
#import "Circle.h"
#import "FreeHand.h"
#import "Arrow.h"
#import "UIImage+Blur.h"
#import "OtherShapeSettingView.h"
#import "BlurSettingView.h"
#import "SendEmailViewController.h"
#import "ProjectSettings.h"
#import "OpenProjectViewController.h"
#import "TextSettingView.h"
#import "UIBezierPath+Points.h"
#import "Colors.h"


@implementation EditImagesViewController

#pragma mark - View lifecycle

- (void)viewDidLoad
{
	[super viewDidLoad];
    
    [_projectTableViewController viewDidLoad];
    _projectTableViewController.editing = NO;

    [self initializeShapes];
    
    [self loadTemplateToRecognizeShape];
    
    [self resetUndoStack];
    
    [self addGesture];
    
    [self setupTutorialView];
    
    [self setupViewBackground];
    
    [self updateStateOfButtons];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(deleteProjectAtProjectDirectory:) name:NSNotificationDeleteProject object:nil];
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    
    [_projectTableViewController viewDidUnload];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSNotificationDeleteProject object:nil];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSNotificationDeleteProject object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSNotificationSelectProject object:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(selectProjectAtProjectDirectory:) name:NSNotificationSelectProject object:nil];
    
    [_projectTableViewController viewWillAppear:animated];
    
    // Scale to origin of image
    [self scaleImageFrameWithAnimation:NO];
    
    // Redraw grid view if having any changes in setting
    [_gridView setNeedsDisplay];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NSNotificationSelectProject object:nil];
    
    [_projectTableViewController viewWillDisappear:animated];
    
    [self saveCurrentImageWhenLeaveController];
}


- (void)saveCurrentImageWhenLeaveController
{
    // Save current drawing if leave this controller
    if (_imageDirectory)
    {
        [self saveDrawingToDirectory:_imageDirectory saveFlatCopy:YES];
    }
}

#pragma mark - File handling - Load / Save / Delete drawings


- (void)initializeDrawingImageWithImageDirectory:(NSString *)imageDirectory
{
    // Get drawing ID.
    _imageDirectory = imageDirectory;
    if (_imageDirectory)
    {
        _projectDirectory = [imageDirectory stringByDeletingLastPathComponent];
    }

    if (_imageDirectory)
    {
        [self loadDrawingImageFromImageDirectory:_imageDirectory];
        _allowHandleTouch = YES;
    }
    else
    {
        // Not allow touch if we dont have drawing in drawing view
        _allowHandleTouch = NO;
    }
}


- (void)loadDrawingImageFromImageDirectory:(NSString *)imageDirectory
{
    // Load drawing shapes from file
    [self loadDrawingShapesFromFiles:imageDirectory];
    
    // Load drawing description from file
    [self loadDrawingDescriptionFromFile:imageDirectory];
}


- (void)loadDrawingShapesFromFiles:(NSString *)imageDirectory
{
    // Load original image from files
    NSString *originImageName = [imageDirectory stringByAppendingPathComponent:FileOriginDrawing];
    [_workingImageView setTransform:CGAffineTransformMake(1, 0, 0, 1, 0, 0)];
    _imageView.image = [UIImage imageWithData:[NSData dataWithContentsOfFile:originImageName]];
    [_workingImageView setFrame:CGRectMake(0, 0, _imageView.image.size.width, _imageView.image.size.height)];
    
    // Load shapes from file
    NSString *shapesFileName = [imageDirectory stringByAppendingPathComponent:FileShapesFile];
    _shapes = [[NSKeyedUnarchiver unarchiveObjectWithFile:shapesFileName] mutableCopy];
    
    // Update _activeShapeIndex. Active Shape is the last shape which is visible on drawing view
    // Update _undoStackCount
    _activeShapeIndex = -1;
    if (_shapes == nil)
    {
        _shapes = [[NSMutableArray alloc] init];
    }
    else
    {
        for (int i = (int)_shapes.count - 1; i >= 0; i--)
        {
            DrawingShape *shape = [_shapes objectAtIndex:i];
            if (shape.visible == YES)
            {
                _activeShapeIndex = i;
                break;
            }
        }
        
        if (_activeShapeIndex != -1)
        {
            _undoStackCount = _activeShapeIndex + 1;
        }
    }
}

// Load description from file and update to description label
- (void)loadDrawingDescriptionFromFile:(NSString *)imageDirectory
{
    NSString *textFilePath = [imageDirectory stringByAppendingPathComponent:FileDescriptionFile];
    _description = [NSString stringWithContentsOfURL:[NSURL fileURLWithPath:textFilePath] encoding:NSUTF8StringEncoding error:nil];
}

// Save current drawing to file.
- (void)saveDrawingToDirectory:(NSString *)imageDirectory saveFlatCopy:(BOOL)saveFlatCopy
{
    if (!imageDirectory)
    {
        return;
    }
    
    NSFileManager *fileManager = [NSFileManager defaultManager];

    //Remove old file
    [fileManager removeItemAtPath:[imageDirectory stringByAppendingString:FileShapesFile] error:nil];
    [fileManager removeItemAtPath:[imageDirectory stringByAppendingString:FileDescriptionFile] error:nil];
    
    [self saveDrawingShapes:imageDirectory];
    [self saveDrawingDescription:imageDirectory];
    
    // Save flat image which combines original image and shapes
    if (saveFlatCopy)
    {
        [fileManager removeItemAtPath:[imageDirectory stringByAppendingString:FileFlatDrawing] error:nil];
        [self saveFlatDrawing:[imageDirectory stringByAppendingPathComponent:FileFlatDrawing]];
        
        // Update image cache of ImportImagesView. It help ImportImagesView show immediately changes from current drawing
        [self.delegate updateCatchImagewithImageDirectory:imageDirectory];
    }
}

// Save all shapes to file.
- (void)saveDrawingShapes:(NSString *)imageDirectory
{
    NSString *layersFileName = [imageDirectory stringByAppendingPathComponent:FileShapesFile];
    [NSKeyedArchiver archiveRootObject:_shapes toFile:layersFileName];
}


// Save description to file.
- (void)saveDrawingDescription:(NSString *)imageDirectory
{
    NSString *textFilePath = [imageDirectory stringByAppendingPathComponent:FileDescriptionFile];
    [_description writeToFile:textFilePath atomically:YES encoding:NSUTF8StringEncoding error:nil];
}

- (void)saveFlatDrawing:(NSString *)imageFileName
{
    // Create a image from original image and drawing shape.
    UIImage *flatImage = [self getFlattenedImageOfDrawing];
    
    NSData *photoData = UIImagePNGRepresentation(flatImage);
    [photoData writeToFile:imageFileName atomically:YES];
}


- (UIImage*)getFlattenedImageOfDrawing
{
    // Create a new bitmap image context
    UIGraphicsBeginImageContext(_imageView.image.size);

    [_imageView.image drawInRect:CGRectMake(0, 0, _imageView.image.size.width, _imageView.image.size.height) blendMode:kCGBlendModeNormal alpha:1.0];
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    [_backBlurView.layer renderInContext:context];
    [_drawingView.layer renderInContext:context];
    [_frontBlurView.layer renderInContext:context];

    // Get a UIImage from the image context
    UIImage *flatImage = UIGraphicsGetImageFromCurrentImageContext();
    
    // Clean up drawing environment
    UIGraphicsEndImageContext();
    
    return flatImage;
}


#pragma mark - Shapes handling


- (UIImage *)blurImageChangeNew:(BOOL)isNew
{
    if (!_imageDirectory)
    {
        _blurImage = nil;
        return nil;
    }
    
    if (!_blurImage || isNew)
    {
        UIGraphicsBeginImageContext(_imageView.frame.size);
        CGContextRef context = UIGraphicsGetCurrentContext();
        CGContextTranslateCTM(context, 0.0f, 0.0f);
        [_imageView.layer renderInContext:context];
        UIImage *snapshot = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        
        _blurImage = snapshot;
    }
    
    return _blurImage;
}


- (void)loadTemplateToRecognizeShape
{
    // Load template for detect shape (Apply 1$ algorithm)
    // Now, every shape use 6 templates
    if (!_shapeRecognizer)
    {
        _shapeRecognizer = [[GLGestureRecognizer alloc] init];
        NSData *jsonData = [NSData dataWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"Gestures" ofType:@"json"]];
        BOOL isSuccess;
        NSError *error;
        isSuccess = [_shapeRecognizer loadTemplatesFromJsonData:jsonData error:&error];
        if (!isSuccess)
        {
            NSLog(@"Error loading template for detect shape: %@", error);
            return;
        }
        
        NSLog(@"loading template for detect shape successful");
    }
}


- (void)initializeShapes
{
    self.selectedShapeIndex = NSNotFound;
    
    if (!_shapes)
    {
        _shapes = [[NSMutableArray alloc] init];
    }
    [_shapes removeAllObjects];
}


- (DrawingShape *)createShapeWithShapeType:(Class)shapeClass
{
    [self removeShapeBehindActiveShape];
    
    // Add new shape with linewith and line color from setting
    NSLog(@"Add new shape with linewith and line color from setting");
    DrawingShape *newShape = [[shapeClass alloc] init];
    [_shapes addObject:newShape];
    
    [self updateCurrentShapeIndex];
    
    return newShape;
}


- (DrawingShape *)createShapeFromFolloweeShape:(DrawingShape *)followeeShape
{
    [self removeShapeBehindActiveShape];
    
    // Add new shape with linewith and line color from setting
    DrawingShape *newShape = [[followeeShape.class alloc] initFromFolloweeShape:followeeShape];
    [_shapes addObject:newShape];

    [self updateCurrentShapeIndex];
    
    return newShape;
}


- (void)updateCurrentShapeIndex
{
    if (!_shapes)
    {
        return;
    }
    
    _activeShapeIndex = (int)_shapes.count - 1;
}


- (void)removeShapeBehindActiveShape
{
    int lastShapeIndex = (int)_shapes.count - 1;
    
    // In case user undoes some steps (_activeShapeIndex < lastShapeIndex), we must remove shapes from _activeShapeIndex to lastShapeIndex
    if (_activeShapeIndex < lastShapeIndex)
    {
        NSLog(@"Remove shapes behind _activeShapeIndex");
        for (NSInteger i = _activeShapeIndex + 1; i < _shapes.count; i++)
        {
            DrawingShape *shape = [_shapes objectAtIndex:i];
            [shape prepareToRemove];
        }
        [_shapes removeObjectsInRange:NSMakeRange(_activeShapeIndex + 1, _shapes.count - _activeShapeIndex - 1)];
    }
}


// Change current selected index
- (void)setSelectedShapeIndex:(NSUInteger)selectedShapeIndex
{
    _selectedShapeIndex = selectedShapeIndex;
    
    [_drawingView setNeedsDisplay];
    
    // Update button state after focus on another shape
    [self updateStateOfButtons];
}

// Get shape at index
- (DrawingShape *)drawingShapeAtIndex:(int)index
{
    int lastIndex = (int)_shapes.count - 1;
    if (index > lastIndex || index < 0)
    {
        return nil;
    }
    return [_shapes objectAtIndex:index];
}

// Get shape with id
- (DrawingShape *)drawingShapeWithUUID:(NSString *)uuid
{
    for (DrawingShape *shape in _shapes)
    {
        if ([shape.drawingID isEqualToString:uuid])
        {
            return shape;
        }
    }
    
    return nil;
}

// Get current selected shape
- (DrawingShape *)selectedShape
{
    if (_shapes == nil || _shapes.count == 0
        || _selectedBorderIndex < 0 || _selectedShapeIndex == NSNotFound || (_selectedShapeIndex != NSNotFound && _selectedShapeIndex >= _shapes.count))
    {
        return nil;
    }
    return [_shapes objectAtIndex:_selectedShapeIndex];
}

// Get current active shape
// ActiveShape is often last shape in shape list. However, when user undo, active shape will be a previous shapes
- (DrawingShape *)activeShape
{
    if (_shapes == nil || _shapes.count == 0
        || _activeShapeIndex < 0 || _activeShapeIndex >= _shapes.count)
    {
        return nil;
    }
    return [_shapes objectAtIndex:_activeShapeIndex];
}

- (void)initializePointsArray
{
    _pointsArray = [[NSMutableArray alloc] init];
}

- (void)addPointToPointsArray:(CGPoint)point
{
    if (_pointsArray)
    {
        [_pointsArray addObject:[NSValue valueWithCGPoint:point]];
    }
}

- (void)addNewShapePathToShape:(DrawingShape *)shape lineWidth:(CGFloat)lineWidth inRect:(CGRect)rect
{
    if ([shape isKindOfClass:Blur.class] || [shape isKindOfClass:Rectangle.class])
    {
        shape.path = [UIBezierPath bezierPathWithRect:rect];
    }
    else if ([shape isKindOfClass:Circle.class])
    {
        shape.path = [UIBezierPath bezierPathWithOvalInRect:rect];
    }
    else if ([shape isKindOfClass:Arrow.class])
    {
        Arrow *arrow = (Arrow *)shape;
        shape.path = [UIBezierPath dqd_bezierPathWithArrowFromPoint:arrow.tail toPoint:arrow.head tailWidth:lineWidth / 2 headWidth:lineWidth * 4 headLength:lineWidth * 2];
    }
    else if ([shape isKindOfClass:FreeHand.class])
    {
        shape.path = [UIBezierPath pathWithPoints:_pointsArray];
        ((FreeHand *)shape).pointArray = _pointsArray;
    }
    
    shape.path.lineWidth = lineWidth;
}


- (void)createNewShapeWithPath:(UIBezierPath *)path andPointsArray:(NSMutableArray *)pointsArray
{
    FreeHand *freehandShape = (FreeHand *)[self createShapeWithShapeType:FreeHand.class];
    freehandShape.path = path;
    freehandShape.pointArray = pointsArray;
    NSLog(@"FREEHAND MATCH");
}


- (void)recognizeHandDrawShape:(UIBezierPath *)path drawingPoints:(NSMutableArray *)points
{
    if (Settings.singleton.autoDetectEnable)
    {
        NSMutableArray *scores = [[NSMutableArray alloc] init]; // Smallest value is the best
        _shapeRecognizer.touchPoints = points;
        [_shapeRecognizer findBestMatchScore:&scores];
        
        float minScore = INFINITY;
        int index = -1;
        
        for (NSInteger i = 0; i < scores.count; i++)
        {
            if (minScore > [[scores objectAtIndex:i] floatValue])
            {
                minScore = [[scores objectAtIndex:i] floatValue];
                index = roundf(i * 5 / scores.count);
            }
        }
        
        switch (index)
        {
            case 0:
            {
                Blur *newShape = (Blur *)[self createShapeWithShapeType:Blur.class];
                [self addNewShapePathToShape:newShape lineWidth:Settings.singleton.lineWidth inRect:path.bounds];
                newShape.blurFactor = Settings.singleton.blurFactor;
                [newShape changeImageOfBlurShape:[self blurImageChangeNew:NO]];
                
                NSLog(@"BLUR MATCH");
                break;
            }
            case 1:
            {
                if (minScore > 0.25)
                {
                    [self createNewShapeWithPath:path andPointsArray:points];
                    return;
                }
                
                Circle *newShape = (Circle *)[self createShapeWithShapeType:Circle.class];
                [self addNewShapePathToShape:newShape lineWidth:Settings.singleton.lineWidth inRect:path.bounds];
                
                NSLog(@"CIRCLE MATCH");
                break;
            }
            case 3:
            {
                if (minScore > 0.25)
                {
                    [self createNewShapeWithPath:path andPointsArray:points];
                    return;
                }
                
                Arrow *newShape = (Arrow *)[self createShapeWithShapeType:Arrow.class];
                [newShape computeHeadAndTailOfArrowWithPointsArray:points];
                [self addNewShapePathToShape:newShape lineWidth:Settings.singleton.lineWidth inRect:path.bounds];
                
                NSLog(@"ARROW MATCH");
                break;
            }
            case 4:
            {
                if (minScore > 0.2)
                {
                    [self createNewShapeWithPath:path andPointsArray:points];
                    return;
                }
                
                Rectangle *newShape = (Rectangle *)[self createShapeWithShapeType:Rectangle.class];
                [self addNewShapePathToShape:newShape lineWidth:Settings.singleton.lineWidth inRect:path.bounds];
                
                NSLog(@"RECTANGLE MATCH");
                break;
            }
                
            default:
                [self createNewShapeWithPath:path andPointsArray:points];
                break;
            
        }
    }
    else
    {
        [self createNewShapeWithPath:path andPointsArray:points];
    }
}


- (void)unselectSelectedShape
{
    if (_selectedShapeIndex != NSNotFound)
    {
        self.selectedShapeIndex = NSNotFound;
    }
}

#pragma mark - UIAlertViewDelegate handle


- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 0)
    {
        return;
    }
    
    if ([alertView textFieldAtIndex:0].text && ![[alertView textFieldAtIndex:0].text isEqualToString:@""])
    {
        [self createShapeWithShapeType:Text.class];
        Text *textShape = (Text *)self.activeShape;
        textShape.text = [alertView textFieldAtIndex:0].text;
        
        self.activeShape.path = [UIBezierPath drawText:textShape.text atPoint:_tempPointForHandleGestures fontSize:Settings.singleton.fontSize];
        
        [self.activeShape addTapTarget];
        [self.activeShape addSelectedBorder];
        [self.activeShape addTapSelectedBorder];
        [self addDrawingToUndoStack];
        
        self.selectedShapeIndex = _activeShapeIndex;
    }
}


#pragma mark - Add gesture function.


- (void)addGesture
{
    // Gesture tap on shape => Determine user tap on shape or not
    if (!_tap)
    {
        _tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(showHideRightControllerPressed:)];
        _tap.numberOfTapsRequired = 1;
        [_loadImages addGestureRecognizer:_tap];
    }
    
    // Gesture tap on shape => Determine user tap on shape or not
    if (!_oneTapHitShapeRecognizer)
    {
        _oneTapHitShapeRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapDetected:)];
        _oneTapHitShapeRecognizer.numberOfTapsRequired = 1;
        [_workingImageView addGestureRecognizer:_oneTapHitShapeRecognizer];
    }
    
    // Gesture double tap on drawing view => Draw text to drawing view
    if (!_doubleTapDrawTextRecognizer)
    {
        _doubleTapDrawTextRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapDetected:)];
        _doubleTapDrawTextRecognizer.numberOfTapsRequired = 2;
        [_workingImageView addGestureRecognizer:_doubleTapDrawTextRecognizer];
    }
    
    // Gesture double tap with 2 finger => scale drawing view to original
    if (!_doubleTapTwoTouchScaleToOriginRecognizer)
    {
        _doubleTapTwoTouchScaleToOriginRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapDetected:)];
        _doubleTapTwoTouchScaleToOriginRecognizer.numberOfTapsRequired = 2;
        _doubleTapTwoTouchScaleToOriginRecognizer.numberOfTouchesRequired = 2;
        [_workingImageView addGestureRecognizer:_doubleTapTwoTouchScaleToOriginRecognizer];
    }
    
    // Gesture pan on drawing view to draw shape
    if (!_drawShapeRecognizer)
    {
        _drawShapeRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(drawDetected:)];
        [_workingImageView addGestureRecognizer:_drawShapeRecognizer];
        _drawShapeRecognizer.maximumNumberOfTouches = 1;
        _drawShapeRecognizer.minimumNumberOfTouches = 1;
    }
    
    // Gesture to move drawing
    if (!_moveDrawingRecognizer)
    {
        _moveDrawingRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(moveDetected:)];
        [_workingImageView addGestureRecognizer:_moveDrawingRecognizer];
        _moveDrawingRecognizer.maximumNumberOfTouches = 2;
        _moveDrawingRecognizer.minimumNumberOfTouches = 2;
    }
    
    // Gesture to zoom in/out drawing
    if (!_zoomDrawingRecognizer)
    {
        _zoomDrawingRecognizer = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(zoomDetected:)];
        [_workingImageView addGestureRecognizer:_zoomDrawingRecognizer];
    }
    
    // Gesture to zoom in/out drawing
    if (!_showSettingViewRecognizer)
    {
        _showSettingViewRecognizer = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(showSettingViewDetected:)];
        _showSettingViewRecognizer.minimumPressDuration = 1.0;
        [_workingImageView addGestureRecognizer:_showSettingViewRecognizer];
    }
}


#pragma mark - Touch handling utility function


- (CGRect)newFrameFromShape:(DrawingShape *)shape withTranslateX:(CGFloat)deltaX withTranslateY:(CGFloat)deltaY
{
    CGRect newFrame = shape.path.bounds;
    
    if ([shape isKindOfClass:Rectangle.class]
        || [shape isKindOfClass:Blur.class]
        || [shape isKindOfClass:Circle.class]
        || [shape isKindOfClass:FreeHand.class])
    {
        // Top left
        if (_selectedBorderIndex == 0)
        {
            newFrame.origin.x += deltaX;
            newFrame.size.width -= deltaX;
            newFrame.origin.y += deltaY;
            newFrame.size.height -= deltaY;
        }
        // Middle top
        else if (_selectedBorderIndex == 1)
        {
            newFrame.origin.y += deltaY;
            newFrame.size.height -= deltaY;
        }
        // Top right
        else if (_selectedBorderIndex == 2)
        {
            newFrame.size.width += deltaX;
            newFrame.origin.y += deltaY;
            newFrame.size.height -= deltaY;
        }
        // Middle right
        else if (_selectedBorderIndex == 3)
        {
            newFrame.size.width += deltaX;
        }
        // Bottom right
        else if (_selectedBorderIndex == 4)
        {
            newFrame.size.width += deltaX;
            newFrame.size.height += deltaY;
        }
        // Middle bottom
        else if (_selectedBorderIndex == 5)
        {
            newFrame.size.height += deltaY;
        }
        // Bottom left
        else if (_selectedBorderIndex == 6)
        {
            newFrame.origin.x += deltaX;
            newFrame.size.width -= deltaX;
            newFrame.size.height += deltaY;
        }
        // Middle left
        else if (_selectedBorderIndex == 7)
        {
            newFrame.origin.x += deltaX;
            newFrame.size.width -= deltaX;
        }
        
        // Prevent negative size of rect
        if (newFrame.size.width <= 1.0)
        {
            newFrame.size.width = 1.0;
        }
        if (newFrame.size.height <= 1.0)
        {
            newFrame.size.height = 1.0;
        }
        
        if ([shape isKindOfClass:FreeHand.class])
        {
            FreeHand *freeHand = (FreeHand *)shape;
            // Resize free hand shape
            [self initializePointsArray];
            
            for (NSInteger i = 0; i < freeHand.pointArray.count; i++)
            {
                CGPoint point = [[freeHand.pointArray objectAtIndex:i] CGPointValue];
                point.x = newFrame.origin.x + newFrame.size.width * (point.x - freeHand.path.bounds.origin.x) / freeHand.path.bounds.size.width;
                point.y = newFrame.origin.y + newFrame.size.height * (point.y - freeHand.path.bounds.origin.y) / freeHand.path.bounds.size.height;
                [self addPointToPointsArray:point];
            }
        }
    }
    else if ([shape isKindOfClass:Arrow.class])
    {
        Arrow *arrow = (Arrow *)shape;
        
        if (_selectedBorderIndex == 0)
        {
            CGPoint newStart = CGPointMake(arrow.tail.x + deltaX, arrow.tail.y + deltaY);
            arrow.tail = newStart;
        }
        else if (_selectedBorderIndex == 1)
        {
            CGPoint newEnd = CGPointMake(arrow.head.x + deltaX, arrow.head.y + deltaY);
            arrow.head = newEnd;
        }
    }

    return newFrame;
}

- (NSUInteger)checkHitShape:(CGPoint)point
{
    __block NSUInteger hitShapeIndex = NSNotFound;
    [_shapes enumerateObjectsWithOptions:NSEnumerationReverse usingBlock:^(id shape, NSUInteger idx, BOOL *stop)
     {
         if ([shape containsPoint:point])
         {
             hitShapeIndex = idx;
             *stop = YES;
         }
     }];
    
    return hitShapeIndex;
}


/**
 Scale and rotation transforms are applied relative to the layer's anchor point this method moves a gesture recognizer's view's anchor point between the user's fingers.
 */
- (void)adjustAnchorPointForGestureRecognizer:(UIGestureRecognizer *)gestureRecognizer
{
    if (gestureRecognizer.state == UIGestureRecognizerStateBegan)
    {
        UIView *piece = gestureRecognizer.view;
        CGPoint locationInView = [gestureRecognizer locationInView:piece];
        CGPoint locationInSuperview = [gestureRecognizer locationInView:piece.superview];
        
        piece.layer.anchorPoint = CGPointMake(locationInView.x / piece.bounds.size.width, locationInView.y / piece.bounds.size.height);
        piece.center = locationInSuperview;
    }
}

#pragma mark - Pinch/scale handling

- (void)showSettingViewDetected:(UILongPressGestureRecognizer *)recognizer
{
    if (_allowHandleTouch && Settings.singleton.drawEnable)
    {
        CGPoint tapLocation = [recognizer locationInView:_drawingView];
        
        NSUInteger selectedIndex = [self checkHitShape:tapLocation];
        
        if (selectedIndex == NSNotFound)
        {
            // If we have a selectedShape now, check to hit its border or not
            if (_selectedShapeIndex == NSNotFound)
            {
                self.selectedShapeIndex = NSNotFound;
            }
            else
            {
                NSUInteger selectedBorderIndex;
                selectedBorderIndex = [self.selectedShape checkHitBorderShape:tapLocation];
                
                // If not hit border shapes, update selectedShape
                if (selectedBorderIndex == NSNotFound)
                {
                    self.selectedShapeIndex = NSNotFound;
                }
            }
        }
        else
        {
            if (_selectedShapeIndex != selectedIndex)
            {
                self.selectedShapeIndex = selectedIndex;
            }
        }
        
        if (recognizer.state == UIGestureRecognizerStateBegan)
        {
            [self showSettingView];
        }
    }
}


#pragma mark - Pinch/scale handling

- (void)zoomDetected:(UIPinchGestureRecognizer *)zoomRecognizer
{
    if (_allowHandleTouch)
    {
        if (zoomRecognizer.state == UIGestureRecognizerStateBegan)
        {
            // Don't allow to scale if scale factor increase/decrease suddenly
            if (ABS(zoomRecognizer.scale - 1.0f) > 0.2f)
            {
                _allowZoomImage = NO;
            }
            else
            {
                _allowZoomImage = YES;
                [self adjustAnchorPointForGestureRecognizer:zoomRecognizer];
            }
        }
        
        if (_allowZoomImage)
        {
            if (zoomRecognizer.state == UIGestureRecognizerStateBegan ||
                zoomRecognizer.state == UIGestureRecognizerStateChanged)
            {
                CGFloat currentScale = [[_workingImageView.layer valueForKeyPath:@"transform.scale"] floatValue];
                if (currentScale > _originScale * 4 && [zoomRecognizer scale] > 1)
                {
                    return;
                }
                
                _workingImageView.transform = CGAffineTransformScale([[zoomRecognizer view] transform], [zoomRecognizer scale], [zoomRecognizer scale]);
                [zoomRecognizer setScale:1];
            }
            else if (zoomRecognizer.state == UIGestureRecognizerStateEnded)
            {
                [self completeMoveImage];
            }
        }
    }
}


#pragma mark - Tap to description label handling

- (void)takeDescriptionLabelTapped
{
    if (_allowHandleTouch && ![_description isEqualToString:@""])
    {
        [self showTakeDescriptionView];
    }
}


#pragma mark - Tap on drawing view handling


- (void)tapDetected:(UITapGestureRecognizer *)tapRecognizer
{
    if (_allowHandleTouch && Settings.singleton.drawEnable)
    {
        CGPoint tapLocation = [tapRecognizer locationInView:_drawingView];
        
        if (tapRecognizer.numberOfTouchesRequired == 1)
        {
            // Single tap => check to hit shape or not
            if (tapRecognizer.numberOfTapsRequired == 1)
            {
                NSUInteger selectedIndex = [self checkHitShape:tapLocation];
                
                if (selectedIndex == NSNotFound)
                {
                    // If we have a selectedShape now, check to hit its border or not
                    if (_selectedShapeIndex == NSNotFound)
                    {
                        self.selectedShapeIndex = NSNotFound;
                    }
                    else
                    {
                        NSUInteger selectedBorderIndex = [self.selectedShape checkHitBorderShape:tapLocation];
                        
                        // If not hit border shapes, update selectedShape
                        if (selectedBorderIndex == NSNotFound)
                        {
                            self.selectedShapeIndex = NSNotFound;
                        }
                    }
                }
                else
                {
                    if (_selectedShapeIndex != selectedIndex)
                    {
                        self.selectedShapeIndex = selectedIndex;
                    }
                }
            }
            // Double tap => drawing text
            else if (tapRecognizer.numberOfTapsRequired == 2)
            {
                if (_selectedShapeIndex == NSNotFound || (![self.selectedShape containsPoint:tapLocation] && [self.selectedShape checkHitBorderShape:tapLocation] == NSNotFound))
                {
                    [self unselectSelectedShape];
                    
                    _tempPointForHandleGestures = tapLocation;
                    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Add Text" message:@"" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"OK", nil];
                    alertView.alertViewStyle = UIAlertViewStylePlainTextInput;
                    [alertView show];
                }
            }
        }
        // Double touch => scale to original drawing
        else if (tapRecognizer.numberOfTouchesRequired == 2)
        {
            [self scaleImageFrameWithAnimation:YES];
        }
    }
}

#pragma mark - Pan on drawing view to move image handling


- (void)moveDetected:(UIPanGestureRecognizer *)moveRecognizer
{
    if (_allowHandleTouch)
    {
        UIView *piece = [moveRecognizer view];
        
        [self adjustAnchorPointForGestureRecognizer:moveRecognizer];
        
        if ([moveRecognizer state] == UIGestureRecognizerStateBegan || [moveRecognizer state] == UIGestureRecognizerStateChanged) {
            CGPoint translation = [moveRecognizer translationInView:[piece superview]];
            
            [piece setCenter:CGPointMake([piece center].x + translation.x, [piece center].y + translation.y)];
            [moveRecognizer setTranslation:CGPointZero inView:[piece superview]];
        }
        else if ([moveRecognizer state] == UIGestureRecognizerStateEnded)
        {
            [self completeMoveImage];
        }
    }
}


#pragma mark - Pan on drawing view to draw/zoom/move shape handling


- (void)drawDetected:(UIPanGestureRecognizer *)drawRecognizer
{
    if (_allowHandleTouch && Settings.singleton.drawEnable)
    {
        CGPoint tapLocation = [drawRecognizer locationInView:_drawingView];
        switch (drawRecognizer.state)
        {
            case UIGestureRecognizerStateBegan:
            {
                NSUInteger selectedIndex = [self checkHitShape:tapLocation];
                
                // Not hit any shape
                if (selectedIndex == NSNotFound)
                {
                    // Not hit current selected shape => draw new shape
                    if (_selectedShapeIndex == NSNotFound)
                    {
                        // Create new path
                        _newPath = [UIBezierPath bezierPath];
                        _newPath.lineWidth = Settings.singleton.lineWidth;
                        
                        [self initializePointsArray];
                        [self addPointToPointsArray:tapLocation];
                        
                        _ctr = 0;
                        _pts[0] = tapLocation;
                        _isFocusOnOtherShape = NO;
                        self.selectedShapeIndex = NSNotFound;
                    }
                    // Check hit border of current selected shape
                    else
                    {
                        _selectedBorderIndex = [self.selectedShape checkHitBorderShape:tapLocation];
                        
                        // Not hit border of current selected shape => draw new shape
                        if (_selectedBorderIndex == NSNotFound)
                        {
                            // Create new path
                            _newPath = [UIBezierPath bezierPath];
                            _newPath.lineWidth = Settings.singleton.lineWidth;
                            
                            [self initializePointsArray];
                            [self addPointToPointsArray:tapLocation];
                            
                            _ctr = 0;
                            _pts[0] = tapLocation;
                            _isFocusOnOtherShape = NO;
                            self.selectedShapeIndex = NSNotFound;
                        }
                        // Import properties of old shape (followee) to new shape (follower)
                        else
                        {
                            // Import properties of old shape (followee) to new shape (follower)
                            DrawingShape *newShape = [self createShapeFromFolloweeShape:self.selectedShape];
                            
                            self.selectedShape.visible = NO;
                            newShape.visible = YES;
                            
                            self.selectedShapeIndex = _activeShapeIndex;
                            _isFocusOnOtherShape = YES;
                        }
                    }
                }
                // Hit the shape
                else
                {
                    DrawingShape *followeeShape;
                    if (_selectedShapeIndex != NSNotFound && _selectedShapeIndex != selectedIndex && ([self.selectedShape containsPoint:tapLocation] || [self.selectedShape checkHitBorderShape:tapLocation] != NSNotFound))
                    {
                        followeeShape = self.selectedShape;
                    }
                    else
                    {
                        followeeShape = [self drawingShapeAtIndex:(int)selectedIndex];
                    }

                    // Import properties of old shape (followee) to new shape (follower)
                    DrawingShape *newShape = [self createShapeFromFolloweeShape:followeeShape];
                    
                    followeeShape.visible = NO;
                    newShape.visible = YES;
                    
                    _selectedBorderIndex = [newShape checkHitBorderShape:tapLocation];
                    
                    self.selectedShapeIndex = _activeShapeIndex;
                    _isFocusOnOtherShape = YES;
                }
                
                _tempPointForHandleGestures = CGPointMake(0, 0);
                
                break;
            }
            case UIGestureRecognizerStateChanged:
            {
                CGPoint translation = [drawRecognizer translationInView:_drawingView];
                CGFloat deltaX = translation.x - _tempPointForHandleGestures.x;
                CGFloat deltaY = translation.y - _tempPointForHandleGestures.y;
                
                // Fonus on the shape => implement move/zoom shape
                if (_isFocusOnOtherShape)
                {
                    // Move shape
                    if (_selectedBorderIndex == NSNotFound)
                    {
                        CGRect originalBounds = self.activeShape.sumBounds;
                        CGRect newBounds = CGRectApplyAffineTransform(originalBounds, CGAffineTransformMakeTranslation(translation.x, translation.y));
                        CGRect rectToRedraw = CGRectUnion(originalBounds, newBounds);
                        
                        [self.activeShape moveBy:translation];
                        
                        [_drawingView setNeedsDisplayInRect:rectToRedraw];
                        [drawRecognizer setTranslation:CGPointZero inView:_drawingView];
                    }
                    // Implement resize shape
                    else
                    {
                        // Now just for blur, rectangle, circle. arrow
                        if (![self.activeShape isKindOfClass:Text.class])
                        {
                            CGRect originalBounds = self.activeShape.sumBounds;
                            CGRect rect = [self newFrameFromShape:self.activeShape withTranslateX:deltaX withTranslateY:deltaY];
                            
                            [self addNewShapePathToShape:self.activeShape lineWidth:self.activeShape.path.lineWidth inRect:rect];
                            [self.activeShape addTapTarget];
                            [self.activeShape addSelectedBorder];
                            [self.activeShape addTapSelectedBorder];
                            
                            CGRect rectToRedraw = CGRectUnion(originalBounds, self.activeShape.sumBounds);
                            [_drawingView setNeedsDisplayInRect:rectToRedraw];
                        }
                    }
                    
                    // Change blur image instantly
                    if ([self.activeShape isKindOfClass:Blur.class])
                    {
                        Blur *blur = (Blur *)self.activeShape;
                        [blur changeImageOfBlurShape:[self blurImageChangeNew:NO]];
                    }
                }
                // Draw free hand shape. Apply algorithm to smooth draw line
                else
                {
                    _ctr++;
                    _pts[_ctr] = tapLocation;
                    [self addPointToPointsArray:tapLocation];
                    
                    if (_ctr == 4)
                    {
                        // Smooth drawing
                        _pts[3] = CGPointMake((_pts[2].x + _pts[4].x)/2.0, (_pts[2].y + _pts[4].y)/2.0);
                        
                        [_newPath moveToPoint:_pts[0]];
                        [_newPath addCurveToPoint:_pts[3] controlPoint1:_pts[1] controlPoint2:_pts[2]];
                        
                        [_drawingView setNeedsDisplayInRect:CGRectMake(_newPath.bounds.origin.x - 20.0, _newPath.bounds.origin.y - 20.0, _newPath.bounds.size.width + 40.0, _newPath.bounds.size.height + 40.0)];
                        _pts[0] = _pts[3];
                        _pts[1] = _pts[4];
                        _ctr = 1;
                    }
                }
                
                _tempPointForHandleGestures = translation;
                break;
            }
            case UIGestureRecognizerStateEnded:
            {
                if (_isFocusOnOtherShape)
                {
                    if ([self.activeShape isKindOfClass:Blur.class])
                    {
                        Blur *blur = (Blur *)self.activeShape;
                        [blur changeImageOfBlurShape:[self blurImageChangeNew:NO]];
                    }
                    
                    // Delete shape step (only invisible)
                    if ([drawRecognizer locationInView:self.view].y < 44 && _selectedBorderIndex == NSNotFound)
                    {
                        [self deleteDrawingStep];
                    }
                }
                else
                {
                    // Remove shape if shape doesn't have point
                    if (!_pointsArray || _pointsArray.count < 2)
                    {
                        self.selectedShapeIndex = NSNotFound;
                        _isFocusOnOtherShape = NO;
                        _selectedBorderIndex = NSNotFound;
                        break;
                    }
                    
                    // Draw shape again if number of point in shape less than 5 (Smooth line algorithm is not draw if number of point < 5)
                    if (_pointsArray.count < 5)
                    {
                        [_newPath moveToPoint:[_pointsArray[0] CGPointValue]];
                        for (NSInteger i = 1; i < _pointsArray.count; i++)
                        {
                            [_newPath addLineToPoint:[_pointsArray[i] CGPointValue]];
                        }
                    }
                    
                    // Detect shape and re-draw new shape.
                    [self recognizeHandDrawShape:_newPath drawingPoints:_pointsArray];
                    
                    [self.activeShape addTapTarget];
                    [self.activeShape addSelectedBorder];
                    [self.activeShape addTapSelectedBorder];
                    _ctr = 0;
                }
                
                _newPath = nil;
                [self addDrawingToUndoStack];
                self.selectedShapeIndex = _activeShapeIndex;
                
                _isFocusOnOtherShape = NO;
                _selectedBorderIndex = NSNotFound;
                break;
            }
            default:
                break;
        }
        
        [self showHideDeleteView:drawRecognizer];
    }
}


#pragma mark - Undo/Redo handling


- (void)removeActiveShape
{
    [_shapes removeLastObject];
    _activeShapeIndex = _shapes.count - 1;
}

- (void)resetUndoStack
{
    _undoStackCount = 0;
    NSLog(@"Reset undo stack");
}


// add the current drawing to the undo stack
- (void)addDrawingToUndoStack
{
    _undoStackCount++;
    NSLog(@"Undo stack count: %d", _undoStackCount);
}

// remove the current drawing to the undo stack
- (void)removeDrawingToUndoStack
{
    _undoStackCount--;
    NSLog(@"Undo stack count: %d", _undoStackCount);
}


- (void)deleteDrawingStep
{
    // Rollback position.
    DrawingShape *followeeShape = [self drawingShapeWithUUID:self.activeShape.followeeShapeID];
    self.activeShape.path = [followeeShape.path copy];
    self.activeShape.tapTarget = [followeeShape.tapTarget copy];
    self.activeShape.selectedBorder = [[NSMutableArray alloc] init];
    
    for (UIBezierPath *path in followeeShape.selectedBorder)
    {
        [self.activeShape.selectedBorder addObject:[path copy]];
    }
    self.activeShape.tapSelectedBorder = [followeeShape.tapSelectedBorder copy];
    
    // Invi shape
    self.activeShape.deleted = YES;
    self.activeShape.visible = NO;
}


- (void)undoDrawingStep
{
    if (_undoStackCount > 0)
    {
        DrawingShape *currentActiveShape = self.activeShape;
        if (currentActiveShape)
        {
            [self removeDrawingToUndoStack];

            [self unselectSelectedShape];
            
            currentActiveShape.visible = NO;
            
            _activeShapeIndex--;
            
            DrawingShape *followeeShape = [self drawingShapeWithUUID:currentActiveShape.followeeShapeID];
            if (followeeShape)
            {
                followeeShape.visible = YES;
            }
            else
            {
                NSLog(@"Not found followee object");
            }
            
            [_drawingView setNeedsDisplay];
        }
        else
        {
            NSLog(@"Undo action access invalid index");
        }
    }
    else
    {
        NSLog(@"Nothing undo");
    }
}


- (void)redoDrawingStep
{
    int lastIndex = (int)_shapes.count - 1;
    if (_activeShapeIndex < lastIndex)
    {
        _activeShapeIndex++;
        DrawingShape *nextActiveShape = self.activeShape;
        if (nextActiveShape)
        {
            [self unselectSelectedShape];
            
            if (!nextActiveShape.deleted)
            {
                nextActiveShape.visible = YES;
            }
            
            [self addDrawingToUndoStack];
            
            DrawingShape *currentActiveShape = [self drawingShapeWithUUID:nextActiveShape.followeeShapeID];
            if (currentActiveShape)
            {
                currentActiveShape.visible = NO;
            }
            else
            {
                NSLog(@"Not found followee object");
            }
            
            [_drawingView setNeedsDisplay];
        }
        else
        {
            NSLog(@"Redo action access invalid index");
        }
    }
    else
    {
        NSLog(@"Nothing redo");
    }
}


#pragma mark - setupViewBackground


- (void)setupViewBackground
{
    if (_imageDirectory)
    {
        _projectTableView.hidden = YES;
        _workingImageView.hidden = NO;
        _footerView.hidden = NO;
        
        UIImage *backgroundImage = [UIImage imageNamed:@"bg_caro.png"];
        [_backgroundImageView setBackgroundColor:[UIColor colorWithPatternImage:backgroundImage]];
    }
    else
    {
        if (_projectDirectory)
        {
            _projectTableView.hidden = YES;
            _workingImageView.hidden = NO;
            _footerView.hidden = NO;
            
            UIImage *backgroundImage = [UIImage imageNamed:@"bg_caro.png"];
            [_backgroundImageView setBackgroundColor:[UIColor colorWithPatternImage:backgroundImage]];
        }
        else
        {
            _projectTableView.hidden = NO;
            _workingImageView.hidden = YES;
            _footerView.hidden = YES;

            [_backgroundImageView setBackgroundColor:BACKGROUND_MENU_VIEW];
        }
        
        
        
        if (_imageView.image != nil)
        {
            _imageView.image = nil;
        }
    }
    
    // App title
    [_appTitle setHidden:_projectTableView.hidden && _tutorialView.hidden];
}


#pragma mark - IBActions


- (IBAction)recordActionTapped:(id)sender
{
    [self unselectSelectedShape];
    [self showRecordAudioView];
}

- (IBAction)newProjectActionTapped:(id)sender
{
    [self.delegate viewController:self showNewProjectPopupPressed:sender];
}

- (IBAction)undoActionTapped:(id)sender
{
    [self undoDrawingStep];
    [self updateStateOfButtons];
}

- (IBAction)redoActionTapped:(id)sender
{
    [self redoDrawingStep];
    [self updateStateOfButtons];
}

- (IBAction)allowDrawingBtnTapped:(id)sender
{
    [self unselectSelectedShape];
    Settings.singleton.drawEnable = !Settings.singleton.drawEnable;
    [self updateStateOfButtons];
}


- (IBAction)takeDescriptionActionTapped:(id)sender
{
    [self unselectSelectedShape];
    [self showTakeDescriptionView];
}


- (IBAction)showHideLeftControllerPressed:(id)sender
{
    [self unselectSelectedShape];
    if ([self.delegate respondsToSelector:@selector(viewController:showHideLeftControllerPressed:)])
    {
        [self.delegate viewController:self showHideLeftControllerPressed:sender];
    }
}


- (IBAction)showHideRightControllerPressed:(id)sender
{
    [self unselectSelectedShape];
    if ([self.delegate respondsToSelector:@selector(viewController:showHideRightControllerPressed:currentProjectDirectory:delayWhenShow:)])
    {
        if (sender)
        {
            [self.delegate viewController:self showHideRightControllerPressed:_imageDirectory currentProjectDirectory:_projectDirectory delayWhenShow:NO];
        }
        else
        {
            [self.delegate viewController:self showHideRightControllerPressed:_imageDirectory currentProjectDirectory:_projectDirectory delayWhenShow:YES];
        }
    }
}

- (IBAction)shareBtnActionTapped:(id)sender
{
    [self saveCurrentImageWhenLeaveController];
    
    SendEmailViewController *controller = [[SendEmailViewController alloc] init];
    controller.projectDirectory = _projectDirectory;
    controller.currentImageDirectory = _imageDirectory;
    
    if (IS_IPAD())
    {
        [controller setContentSizeForViewInPopover:CGSizeMake(320, 480)];
		_sendEmailPopup = [[UIPopoverController alloc] initWithContentViewController:controller];
		[_sendEmailPopup presentPopoverFromRect:[_footerView convertRect:_shareBtn.frame toView:self.view] inView:self.view permittedArrowDirections:UIPopoverArrowDirectionDown animated:YES];
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
    }
}


- (IBAction)skipBtnActionTapped:(id)sender
{
    Settings.singleton.tutorialEnable = YES;
    [self setupTutorialView];
}

#pragma mark - Alerts, Sheets, HUDs

- (void)showRecordAudioView
{
    if (!_recordAudioView)
    {
        _recordAudioView = [[RecordAudioView alloc] init];
        _recordAudioView.autoresizingMask = (UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth);
        _recordAudioView.delegate = self;
    }
    
    [_recordAudioView setFrame:self.view.frame];
    [_recordAudioView loadImageDirectory:_imageDirectory];
    [self.view addSubview:_recordAudioView];
}

- (void)showTakeDescriptionView
{
    if (!_takeDescriptionView)
    {
        _takeDescriptionView = [[TakeDescriptionView alloc] init];
        _takeDescriptionView.autoresizingMask = (UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth);
        _takeDescriptionView.delegate = self;
    }
    
    [_takeDescriptionView setFrame:self.view.frame];
    [_takeDescriptionView loadDescription:_description];
    [self.view addSubview:_takeDescriptionView];
}


- (void)showSettingView
{
    if (_selectedShapeIndex != NSNotFound)
    {
        if (!_settingView)
        {
            if ([self.selectedShape isKindOfClass:Blur.class])
            {
                _settingView = [[BlurSettingView alloc] init];
                [(BlurSettingView *)_settingView loadBlurFactor:((Blur *)self.selectedShape).blurFactor];
            }
            else if ([self.selectedShape isKindOfClass:Text.class])
            {
                _settingView = [[TextSettingView alloc] init];
                [(TextSettingView *)_settingView loadFontSize:((Text *)self.selectedShape).fontSize color:self.selectedShape.lineColor text:((Text *)self.selectedShape).text];
            }
            else
            {
                _settingView = [[OtherShapeSettingView alloc] init];
                [(OtherShapeSettingView *)_settingView loadLineWidth:self.selectedShape.path.lineWidth color:self.selectedShape.lineColor];
            }
        }
        
        _settingView.autoresizingMask = (UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth);
        _settingView.delegate = self;
        [_settingView setFrame:self.view.frame];
        [self.view addSubview:_settingView];
    }
}


- (void)removeTakeDescriptionView
{
    if (_takeDescriptionView)
    {
        [_takeDescriptionView removeFromSuperview];
        _takeDescriptionView = nil;
    }
}

- (void)removeRecordAudioView
{
    if (_recordAudioView)
    {
        [_recordAudioView removeFromSuperview];
        _recordAudioView = nil;
    }
}

- (void)removeSettingView
{
    if (_settingView)
    {
        [_settingView removeFromSuperview];
        _settingView = nil;
    }
}


#pragma mark - TakeDescriptionView delegate


- (void)takeDescriptionView:(TakeDescriptionView *)view description:(NSString *)description
{
    [self removeTakeDescriptionView];
    _description = description ? description : @"";
    [self updateStateOfButtons];
}

- (void)closeTakeDescriptionView
{
    [self removeTakeDescriptionView];
}


- (void)closeRecordAudioView
{
    [self removeRecordAudioView];
    [self updateStateOfButtons];
}

#pragma mark - SettingView delegate


- (void)settingView:(SettingView *)view choosenBlurFactor:(CGFloat)blurFactor
{
    if (_selectedShapeIndex != NSNotFound)
    {
        Settings.singleton.blurFactor = blurFactor;
        ((Blur *)self.selectedShape).blurFactor = blurFactor;
        [(Blur *)self.selectedShape changeImageOfBlurShape:[self blurImageChangeNew:NO]];
    }
}

- (void)settingView:(SettingView *)view choosenFontSize:(CGFloat)fontSize choosenColor:(UIColor *)lineColor choosenText:(NSString *)text
{
    if (_selectedShapeIndex != NSNotFound)
    {
        Text *textShape = (Text *)self.selectedShape;
        textShape.text = text;
        textShape.fontSize = fontSize;

        self.selectedShape.path = [UIBezierPath drawText:textShape.text atPoint:CGPointMake(self.selectedShape.path.bounds.origin.x, self.selectedShape.path.bounds.origin.y) fontSize:fontSize];
        
        Settings.singleton.fontSize = fontSize;
        self.selectedShape.lineColor = lineColor;
        
        [self.selectedShape addTapTarget];
        [self.selectedShape addSelectedBorder];
        [self.selectedShape addTapSelectedBorder];
        
        [_drawingView setNeedsDisplay];
    }
    
    [self removeSettingView];
}

- (void)settingView:(SettingView *)view choosenLineWidth:(CGFloat)lineWidth choosenColor:(UIColor *)lineColor
{
    // Change line Color and line width of seelected shape and of setting as well
    if (_selectedShapeIndex != NSNotFound)
    {
        // In case, Shape is arrow, need a new path with another fontsize
        if ([self.selectedShape isKindOfClass:Arrow.class] && self.selectedShape.path.lineWidth != lineWidth)
        {
            Arrow *arrow = (Arrow *)self.selectedShape;
            self.selectedShape.path = [UIBezierPath dqd_bezierPathWithArrowFromPoint:arrow.tail toPoint:arrow.head tailWidth:lineWidth / 2 headWidth:lineWidth * 4 headLength:lineWidth * 2];
        }
        
        Settings.singleton.lineColor = lineColor;
        Settings.singleton.lineWidth = lineWidth;

        self.selectedShape.path.lineWidth = lineWidth;
        self.selectedShape.lineColor = lineColor;
        
        [self.selectedShape addTapTarget];
        [self.selectedShape addSelectedBorder];
        [self.selectedShape addTapSelectedBorder];

        [_drawingView setNeedsDisplay];
    }
    
    [self removeSettingView];
}

- (void)closeSettingView
{
    [self removeSettingView];
}


- (void)loadFirstImageWithProjectDirectory:(NSString *)projectDirectory
{
    NSArray *imageListFromProject = [Utilities getImagesFromProjectDirectory:projectDirectory];

    if (imageListFromProject.count == 0)
    {
        _projectDirectory = projectDirectory;
        [self loadImageWithImageDirectory:nil];
    }
    else
    {
        [self loadImageWithImageDirectory:[imageListFromProject objectAtIndex:0]];
    }

    [self showHideRightControllerPressed:nil];
}


- (void)loadImageWithImageDirectory:(NSString *)imageDirectory
{
    // No changes if selected drawing and current drawing are the same
    if (_imageDirectory && [_imageDirectory isEqualToString:imageDirectory])
    {
        return;
    }

    // Save current drawing
    if (_imageDirectory)
    {
        [self saveDrawingToDirectory:_imageDirectory saveFlatCopy:YES];
    }

    // Don't show tutorial anymore
    Settings.singleton.tutorialEnable = YES;
    
    // Load new drawing which is chosen
    [self resetUndoStack];
    [self initializeShapes];
    [self initializeDrawingImageWithImageDirectory:imageDirectory];
    [self changeProjectSettings];
    [self updateStateOfButtons];
    [self setupTutorialView];
    [self setupViewBackground];
    [self scaleImageFrameWithAnimation:NO];
    [self removeBlurSubView];
    [self blurImageChangeNew:YES];
    
    [_drawingView setNeedsDisplay];
    [_gridView setNeedsDisplay];
}

- (void)changeProjectSettings
{
    if (_projectDirectory)
    {
        NSString *projectSettingsPath = [_projectDirectory stringByAppendingPathComponent:FileProjectSettingsFile];
        ProjectSettings *projectSettings = [NSKeyedUnarchiver unarchiveObjectWithFile:projectSettingsPath];
        Settings.singleton.gridEnable = projectSettings.gridEnable;
        Settings.singleton.gridColor = projectSettings.gridColor;
        Settings.singleton.gridStyle = projectSettings.gridStyle;
    }
}

- (void)removeCurrentDrawing:(NSInteger)removeImageIndex
{
    [self resetUndoStack];
    [self initializeShapes];
    [self initializeDrawingImageWithImageDirectory:[Utilities getImageDirectoryFromProjectDirectory:_projectDirectory nextToIndex:removeImageIndex]];
    [self updateStateOfButtons];
    [self setupViewBackground];
    [self scaleImageFrameWithAnimation:NO];
    [self removeBlurSubView];
    [self blurImageChangeNew:YES];
    
    [_drawingView setNeedsDisplay];
    [_gridView setNeedsDisplay];
}


- (void)removeBlurSubView
{
    for (UIView *view in _frontBlurView.subviews)
    {
        [view removeFromSuperview];
    }
    for (UIView *view in _backBlurView.subviews)
    {
        [view removeFromSuperview];
    }
}


#pragma mark - DrawingViewDataSource

- (DrawingShape *)shapeAtIndex:(NSUInteger)shapeIndex
{
    return [_shapes objectAtIndex:shapeIndex];
}


- (NSUInteger)numberOfShapesInDrawingView
{
    return [_shapes count];
}

- (UIBezierPath *)pathForShapeAtIndex:(NSUInteger)shapeIndex
{
    DrawingShape *shape = [_shapes objectAtIndex:shapeIndex];
    return shape.path;
}

- (UIColor *)lineColorForShapeAtIndex:(NSUInteger)shapeIndex
{
    DrawingShape *shape = [_shapes objectAtIndex:shapeIndex];
    return shape.lineColor;
}


- (BOOL)visibleForShapeAtIndex:(NSUInteger)shapeIndex
{
    DrawingShape *shape = [_shapes objectAtIndex:shapeIndex];
    return shape.visible;
}


- (NSUInteger)indexOfSelectedShapeInDrawingView
{
    return _selectedShapeIndex;
}


- (UIBezierPath *)currentDrawingPath
{
    return _newPath;
}


- (UIImage *)getBlurImage
{
    return [self blurImageChangeNew:NO];
}


- (UIView *)getBackBlurView
{
    return _backBlurView;
}


- (UIView *)getFrontBlurView
{
    return _frontBlurView;
}


#pragma mark - Update button state

- (void)updateStateOfButtons
{
    // Disable undo button when _undoStackCount <= 0
    if (_undoStackCount <= 0)
    {
        [_undoBtn setEnabled:NO];
    }
    else
    {
        [_undoBtn setEnabled:YES];
    }
    
    // Disable redo button when _activeShapeIndex is last shape index
    int lastIndex = (int)_shapes.count - 1;
    if (_activeShapeIndex >= lastIndex)
    {
        [_redoBtn setEnabled:NO];
    }
    else
    {
        [_redoBtn setEnabled:YES];
    }
    
    // Hidden take description button when no drawing in
    if (_imageDirectory)
    {
        [_takeDescriptionBtn setEnabled:YES];
        if (_description == nil || [_description isEqualToString:@""])
        {
            [_takeDescriptionBtn setImage:[UIImage imageNamed:@"add_description.png"] forState:UIControlStateNormal];
        }
        else
        {
            [_takeDescriptionBtn setImage:[UIImage imageNamed:@"edit_description.png"] forState:UIControlStateNormal];
        }
        
        [_recordAudioBtn setEnabled:YES];
        
        if ([[NSFileManager defaultManager] fileExistsAtPath:[_imageDirectory stringByAppendingPathComponent:FileAudioFile]])
        {
            [_recordAudioBtn setImage:[UIImage imageNamed:@"listen_sound.png"] forState:UIControlStateNormal];
        }
        else
        {
            [_recordAudioBtn setImage:[UIImage imageNamed:@"add_voice.png"] forState:UIControlStateNormal];
        }
    }
    else
    {
        _description = @"";
        
        [_takeDescriptionBtn setEnabled:NO];
        [_recordAudioBtn setEnabled:NO];
    }
    
    if (_projectDirectory)
    {
        [_loadImages setHidden:NO];
        [_menuBtn setHidden:NO];
        [_shareBtn setHidden:NO];
        [_allowDrawingBtn setHidden:NO];
        [_undoBtn setHidden:NO];
        [_redoBtn setHidden:NO];
        
        [_addNewBtn setHidden:YES];
        
        NSArray *imageListFromProject = [Utilities getImagesFromProjectDirectory:_projectDirectory];
        if (imageListFromProject.count == 0)
        {
            [_shareBtn setEnabled:NO];
        }
        else
        {
            [_shareBtn setEnabled:YES];
        }
    }
    else
    {
        [_loadImages setHidden:YES];
        [_menuBtn setHidden:YES];
        [_shareBtn setHidden:YES];
        [_allowDrawingBtn setHidden:YES];
        [_undoBtn setHidden:YES];
        [_redoBtn setHidden:YES];
        
        [_addNewBtn setHidden:NO];
    }
    
    
    if (Settings.singleton.drawEnable)
    {
        [_allowDrawingBtn setImage:[UIImage imageNamed:@"unlock.png"] forState:UIControlStateNormal];
    }
    else
    {
        [_allowDrawingBtn setImage:[UIImage imageNamed:@"lock.png"] forState:UIControlStateNormal];
    }
}


- (void)showHideDeleteView:(UIPanGestureRecognizer *)recognizer
{
    CGPoint currentPosition = [recognizer locationInView:self.view];
    if (currentPosition.y < 44)
    {
        _deleteImageIndicator.highlighted = YES;
    }
    else
    {
        _deleteImageIndicator.highlighted = NO;
    }
    
    if (_isFocusOnOtherShape && _selectedBorderIndex == NSNotFound)
    {
        [_deleteView setHidden:NO];
    }
    else
    {
        [_deleteView setHidden:YES];
    }
}


#pragma mark - Adjust layout when rotate or disappear

- (void)scaleImageFrameWithAnimation:(BOOL)animated
{
    if (_imageDirectory && _imageView.image != nil)
    {
        CGFloat imageFactor = _imageView.image.size.width / _imageView.image.size.height;
        CGFloat containerFactor = _workSpaceView.frame.size.width / _workSpaceView.frame.size.height;
        CGFloat targetScale;
        if (imageFactor > containerFactor)
        {
            targetScale = _workSpaceView.frame.size.width / _imageView.image.size.width;
        }
        else
        {
            targetScale = _workSpaceView.frame.size.height / _imageView.image.size.height;
        }
        
        // Scale image view to fit screen
        if (animated)
        {
            [UIView animateWithDuration:0.2
                             animations:^{
                                 [_workingImageView setTransform:CGAffineTransformMake(targetScale, 0, 0, targetScale, 0, 0)];
                                 _workingImageView.layer.anchorPoint = CGPointMake(0.5,0.5);
                                 _workingImageView.center = CGPointMake(_workSpaceView.frame.size.width / 2, _workSpaceView.frame.size.height / 2);
                                 
                                 _originScale = [[_workingImageView.layer valueForKeyPath:@"transform.scale"] floatValue];
                             }];
        }
        else
        {
            [_workingImageView setTransform:CGAffineTransformMake(targetScale, 0, 0, targetScale, 0, 0)];
            _workingImageView.layer.anchorPoint = CGPointMake(0.5,0.5);
            _workingImageView.center = CGPointMake(_workSpaceView.frame.size.width / 2, _workSpaceView.frame.size.height / 2);
            
            _originScale = [[_workingImageView.layer valueForKeyPath:@"transform.scale"] floatValue];
        }
        
    }
}

- (void)completeMoveImage
{
    if (_imageDirectory && _imageView.image != nil)
    {
        CGFloat currentScale = [[_workingImageView.layer valueForKeyPath:@"transform.scale"] floatValue];
        if (currentScale < _originScale)
        {
            [self scaleImageFrameWithAnimation:YES];
        }
        else
        {
            CGRect newFrame = _workingImageView.frame;
            if (_workingImageView.frame.size.width > _workSpaceView.frame.size.width && _workingImageView.frame.size.height > _workSpaceView.frame.size.height)
            {
                if (_workingImageView.frame.origin.x > 0)
                {
                    newFrame.origin.x = 0;
                }
                else if (_workingImageView.frame.origin.x + _workingImageView.frame.size.width < _workSpaceView.frame.size.width)
                {
                    newFrame.origin.x += _workSpaceView.frame.size.width - (_workingImageView.frame.origin.x + _workingImageView.frame.size.width);
                }
                if (_workingImageView.frame.origin.y > 0)
                {
                    newFrame.origin.y = 0;
                }
                else if (_workingImageView.frame.origin.y + _workingImageView.frame.size.height < _workSpaceView.frame.size.height)
                {
                    newFrame.origin.y += _workSpaceView.frame.size.height - (_workingImageView.frame.origin.y + _workingImageView.frame.size.height);
                }
            }
            else
            {
                if (_workingImageView.frame.origin.x > 0)
                {
                    if (_workingImageView.frame.size.width > _workSpaceView.frame.size.width)
                    {
                        newFrame.origin.x = 0;
                    }
                    else
                    {
                        newFrame.origin.x = (_workSpaceView.frame.size.width - _workingImageView.frame.size.width) / 2;
                    }
                }
                else if (_workingImageView.frame.origin.x + _workingImageView.frame.size.width < _workSpaceView.frame.size.width)
                {
                    if (_workingImageView.frame.size.width > _workSpaceView.frame.size.width)
                    {
                        newFrame.origin.x += _workSpaceView.frame.size.width - (_workingImageView.frame.origin.x + _workingImageView.frame.size.width);
                    }
                    else
                    {
                        newFrame.origin.x += _workSpaceView.frame.size.width / 2 - _workingImageView.frame.origin.x - _workingImageView.frame.size.width / 2;
                    }
                }
                if (_workingImageView.frame.origin.y > 0)
                {
                    if (_workingImageView.frame.size.height > _workSpaceView.frame.size.height)
                    {
                        newFrame.origin.y = 0;
                    }
                    else
                    {
                        newFrame.origin.y = (_workSpaceView.frame.size.height - _workingImageView.frame.size.height) / 2;
                    }
                }
                else if (_workingImageView.frame.origin.y + _workingImageView.frame.size.height < _workSpaceView.frame.size.height)
                {
                    if (_workingImageView.frame.size.height > _workSpaceView.frame.size.height)
                    {
                        newFrame.origin.y += _workSpaceView.frame.size.height - (_workingImageView.frame.origin.y + _workingImageView.frame.size.height);
                    }
                    else
                    {
                        newFrame.origin.y += _workSpaceView.frame.size.height / 2 - _workingImageView.frame.origin.y - _workingImageView.frame.size.height / 2;
                    }
                }
            }
            
            [UIView animateWithDuration:0.2
                             animations:^{
                                 [_workingImageView setFrame:newFrame];
                             }];
        }
    }
}

- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    // Scale image to original one when rotate
    [self scaleImageFrameWithAnimation:YES];
    
    if (IS_IPAD())
    {
        if ([_sendEmailPopup isPopoverVisible])
        {
            [_sendEmailPopup presentPopoverFromRect:[_footerView convertRect:_shareBtn.frame toView:self.view] inView:self.view permittedArrowDirections:UIPopoverArrowDirectionDown animated:YES];
        }
    }
}


#pragma mark - GridViewDataSource delegate

- (BOOL)isExistedDrawing
{
    return (_imageDirectory != nil);
}


#pragma mark - NSNotification handle

- (void)deleteProjectAtProjectDirectory:(NSNotification *)notification
{
    NSString *projectDirectory = notification.object;

    if (_projectDirectory && [_projectDirectory isEqualToString:projectDirectory])
    {
        _projectDirectory = nil;
        [_projectTableViewController setupTableView];
        [_projectTableViewController.tableView reloadData];
        [self loadImageWithImageDirectory:nil];
    }
}

- (void)selectProjectAtProjectDirectory:(NSNotification *)notification
{
    [self loadFirstImageWithProjectDirectory:notification.object];
}

#pragma mark - Tutorial

- (void)setupTutorialView
{
    if (Settings.singleton.tutorialEnable)
    {
        [_tutorialView setHidden:YES];
        
        if (_projectTableViewController.projects.count > 0)
        {
            _noProjectView.hidden = YES;
        }
        else
        {
            _noProjectView.hidden = NO;
        }
    }
    else
    {
        [_tutorialView setHidden:NO];
        
        _pageImages = [NSArray arrayWithObjects:
                       [UIImage imageNamed:@"one.png"],
                       [UIImage imageNamed:@"two.png"],
                       [UIImage imageNamed:@"three.png"],
                       [UIImage imageNamed:@"four.png"],
                       [UIImage imageNamed:@"five.png"],
                       nil];
        
        NSInteger pageCount = _pageImages.count;
        
        // Set up the page control
        _pageControl.currentPage = 0;
        _pageControl.numberOfPages = pageCount;
        
        // Set up the array to hold the views for each page
        _pageViews = [[NSMutableArray alloc] init];
        for (NSInteger i = 0; i < pageCount; ++i)
        {
            [_pageViews addObject:[NSNull null]];
        }
        
        // Set up the content size of the scroll view
        CGSize pagesScrollViewSize = _scrollView.frame.size;
        _scrollView.contentSize = CGSizeMake(pagesScrollViewSize.width * _pageImages.count, pagesScrollViewSize.height);
        
        // Load the initial set of pages that are on screen
        [self loadVisiblePages];
    }
}

- (void)loadVisiblePages
{
    // First, determine which page is currently visible
    CGFloat pageWidth = _scrollView.frame.size.width;
    NSInteger page = (NSInteger)floor((_scrollView.contentOffset.x * 2.0f + pageWidth) / (pageWidth * 2.0f));
    
    // Update the page control
    _pageControl.currentPage = page;
    
    // Work out which pages we want to load
    NSInteger firstPage = page - 1;
    NSInteger lastPage = page + 1;
    
    // Purge anything before the first page
    for (NSInteger i=0; i<firstPage; i++)
    {
        [self purgePage:i];
    }
    for (NSInteger i=firstPage; i<=lastPage; i++)
    {
        [self loadPage:i];
    }
    for (NSInteger i=lastPage+1; i<_pageImages.count; i++)
    {
        [self purgePage:i];
    }
}

- (void)loadPage:(NSInteger)page
{
    if (page < 0 || page >= _pageImages.count)
    {
        // If it's outside the range of what we have to display, then do nothing
        return;
    }
    
    // Load an individual page, first seeing if we've already loaded it
    UIView *pageView = [_pageViews objectAtIndex:page];
    if ((NSNull*)pageView == [NSNull null])
    {
        CGRect frame = _scrollView.bounds;
        frame.origin.x = frame.size.width * page;
        frame.origin.y = 0.0f;
        
        UIImageView *newPageView = [[UIImageView alloc] initWithImage:[self.pageImages objectAtIndex:page]];
        newPageView.contentMode = UIViewContentModeScaleAspectFit;
        newPageView.frame = frame;
        [self.scrollView addSubview:newPageView];
        [self.pageViews replaceObjectAtIndex:page withObject:newPageView];
    }
}

- (void)purgePage:(NSInteger)page
{
    if (page < 0 || page >= self.pageImages.count)
    {
        // If it's outside the range of what we have to display, then do nothing
        return;
    }
    
    // Remove a page from the scroll view and reset the container array
    UIView *pageView = [self.pageViews objectAtIndex:page];
    if ((NSNull*)pageView != [NSNull null])
    {
        [pageView removeFromSuperview];
        [self.pageViews replaceObjectAtIndex:page withObject:[NSNull null]];
    }
}

#pragma mark - UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    // Load the pages which are now on screen
    [self loadVisiblePages];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    if (_pageControl.currentPage == 4)
    {
        [_skipBtn setTitle:@"Start" forState:UIControlStateNormal];
        [_skipBtn setBackgroundColor:BACKGROUND_RED_COLOR];
    }
    else
    {
        [_skipBtn setTitle:@"Skip" forState:UIControlStateNormal];
        [_skipBtn setBackgroundColor:BACKGROUND_GREEN_COLOR];
    }
}

@end
