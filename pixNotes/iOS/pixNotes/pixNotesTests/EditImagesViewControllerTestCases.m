//
//  EditPictureViewControllerTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "AppDelegate.h"
#import "EditImagesViewController.h"
#import "AGImagePickerController.h"
#import "AGIPCAlbumsController.h"
#import "Utilities.h"
#import "Constants.h"
#import "FreeHand.h"

@interface EditImagesViewControllerTestCases : PixNotesTestCases
{
    AppDelegate *_appDelegate;
    EditImagesViewController *_editImageViewController;
    UIView *_editImageView;
}
@end

@implementation EditImagesViewControllerTestCases

- (void)setUp
{
    [super setUp];
    _appDelegate = [[UIApplication sharedApplication] delegate];
    _editImageViewController = (EditImagesViewController *)_appDelegate.viewController.centerPanel;
    _editImageView = _editImageViewController.view;
    
    // Prepare shape list
    if (!_editImageViewController.shapes)
    {
        _editImageViewController.shapes = [[NSMutableArray alloc] init];
    }
    
    [_editImageViewController.shapes removeAllObjects];
    
    for (NSInteger i = 0; i < 2; i++)
    {
        DrawingShape * shape = [[DrawingShape alloc] init];
        shape.path = [UIBezierPath bezierPath];
        shape.path.lineWidth = MEDIUM_BORDER;
        
        [_editImageViewController.shapes addObject:shape];
    }
}

- (void)tearDown
{
    // Put teardown code here. This method is called after the invocation of each test method in the class. 
    [super tearDown];
}


- (void)testInitEditImagesViewController
{
    STAssertNotNil(_editImageViewController, @"EditImagesViewController nil");
    
    STAssertNotNil(_editImageView, @"");
    STAssertNotNil(_editImageViewController.undoBtn, @"");
    STAssertNotNil(_editImageViewController.redoBtn, @"");
    STAssertNotNil(_editImageViewController.shareBtn, @"");
    STAssertNotNil(_editImageViewController.loadImagesBtn, @"");
    STAssertNotNil(_editImageViewController.allowDrawingBtn, @"");
    STAssertNotNil(_editImageViewController.takeDescriptionBtn, @"");
    STAssertNotNil(_editImageViewController.workSpaceView, @"");
    STAssertNotNil(_editImageViewController.workingImageView, @"");
    STAssertNotNil(_editImageViewController.imageView, @"");
    STAssertNotNil(_editImageViewController.gridView, @"");
    STAssertNotNil(_editImageViewController.drawingView, @"");
    STAssertNotNil(_editImageViewController.frontBlurView, @"");
    STAssertNotNil(_editImageViewController.backBlurView, @"");
    STAssertNotNil(_editImageViewController.shapeRecognizer, @"");
}

- (void)testStateOfUndoButton
{
     // Only anable undo button when undo stack count > 0
    _editImageViewController.undoStackCount = 1;
    [_editImageViewController updateStateOfButtons];
    
    STAssertTrue(_editImageViewController.undoBtn.isEnabled, @"Not enable");
    
    _editImageViewController.undoStackCount = 0;
    [_editImageViewController updateStateOfButtons];
    
    STAssertFalse(_editImageViewController.undoBtn.isEnabled, @"Not disable");
}

- (void)testStateOfRedoButton
{
    // Only anable redo button when undo stack count < last index of shape list
    _editImageViewController.activeShapeIndex = 1;
    [_editImageViewController updateStateOfButtons];
    
    STAssertFalse(_editImageViewController.redoBtn.isEnabled, @"Not disable");
    _editImageViewController.activeShapeIndex = 0;
    [_editImageViewController updateStateOfButtons];
    
    STAssertTrue(_editImageViewController.redoBtn.isEnabled, @"Not enable");
}

- (void)testStateOfDeleteButtonAndSettingButton
{
    // Only show delete shape button and setting button when we have a selected shape
    _editImageViewController.selectedShapeIndex = 1;
    [_editImageViewController updateStateOfButtons];
    
    _editImageViewController.selectedShapeIndex = NSNotFound;
    [_editImageViewController updateStateOfButtons];
}

- (void)testStateOfTakeDescriptionButton
{
    // Only show take description button when we have a existed drawing
    _editImageViewController.imageDirectory = @"test ID";
    [_editImageViewController updateStateOfButtons];
    STAssertFalse(_editImageViewController.takeDescriptionBtn.isHidden, @"Not showed");
    
    _editImageViewController.imageDirectory = nil;
    [_editImageViewController updateStateOfButtons];
    STAssertTrue(_editImageViewController.takeDescriptionBtn.isHidden, @"Not hidden");
}

//- (void)testRemoveCurrentDrawing
//{
//    // Pre-config
//    // Remove all images
//    
//    
//    // Action
//    [_editImageViewController removeCurrentDrawing:0];
//    
//    STAssertEquals(_editImageViewController.undoStackCount, 0, @"");
//    STAssertNil(_editImageViewController.imageDirectory, @"");
//    STAssertFalse(_editImageViewController.allowHandleTouch, @"");
//    STAssertEquals((int)_editImageViewController.shapes.count, 0, @"");
//    STAssertEquals((int)_editImageViewController.backBlurView.subviews.count, 0, @"");
//    STAssertEquals((int)_editImageViewController.frontBlurView.subviews.count, 0, @"");
//    STAssertNil(_editImageViewController.imageView.image, @"");
//    STAssertTrue(_editImageViewController.takeDescriptionBtn.hidden, @"");
//    STAssertEqualObjects(_editImageViewController.descriptionLabel.text, @"", @"");
//    STAssertEquals((long)_editImageViewController.selectedShapeIndex, LONG_MAX, @"");
//    STAssertEquals(_editImageViewController.activeShapeIndex, -1, @"");
//}

- (void)testChangeBackgroundView
{
    // If existed a drawing image => clear background
    _editImageViewController.imageDirectory = @"test ID";
    [_editImageViewController setupViewBackground];
    STAssertFalse(_editImageViewController.workingImageView.hidden, @"");
    STAssertTrue(_editImageViewController.dashboardView.hidden, @"");
    
    _editImageViewController.imageDirectory = nil;
    _editImageViewController.projectDirectory = @"test ID";
    [_editImageViewController setupViewBackground];
    STAssertTrue(_editImageViewController.workingImageView.hidden, @"");
    STAssertTrue(_editImageViewController.dashboardView.hidden, @"");
    
    _editImageViewController.imageDirectory = nil;
    _editImageViewController.projectDirectory = nil;
    [_editImageViewController setupViewBackground];
    STAssertTrue(_editImageViewController.workingImageView.hidden, @"");
    STAssertFalse(_editImageViewController.dashboardView.hidden, @"");
}

//- (void)testGetTheFirstDrawingIDFunction
//{
//    // Prepare. list not null
//    NSMutableArray *imageList = (NSMutableArray *)[Utilities getImages];
//    if (imageList.count == 0)
//    {
//        [Utilities addImageAtFirst:@"Test ID"];
//    }
//    
//    STAssertEqualObjects([_editImageViewController getFirstDrawingID], [[[Utilities getImages] firstObject] lastPathComponent], @"");
//    
//    // Prepare. image list is null
//    imageList = (NSMutableArray *)[Utilities getImages];
//    if (imageList.count != 0)
//    {
//        [imageList removeAllObjects];
//    }
//    
//    STAssertNil([_editImageViewController getFirstDrawingID], @"");
//}

//- (void)testInitializeDrawingWithDrawingIDFunction
//{
//    // If existed a drawing image => allow handle touch and load image with drawing id
//    [_editImageViewController initializeDrawingImageWithImageDirectory:@"test id"];
//    STAssertEqualObjects(_editImageViewController.drawingID, @"test id", @"");
//    STAssertTrue(_editImageViewController.allowHandleTouch, @"");
//    
//    // If not existed a drawing image => not allow handle touch
//    [_editImageViewController initializeDrawingImageWithImageDirectory:nil];
//    STAssertNil(_editImageViewController.drawingID, @"");
//    STAssertFalse(_editImageViewController.allowHandleTouch, @"");
//}

- (void)testAddGestureFunction
{
    _editImageViewController.oneTapDescriptionLabelRecognizer = nil;
    _editImageViewController.oneTapHitShapeRecognizer = nil;
    _editImageViewController.doubleTapDrawTextRecognizer = nil;
    _editImageViewController.doubleTapTwoTouchScaleToOriginRecognizer = nil;
    _editImageViewController.drawShapeRecognizer = nil;
    _editImageViewController.moveDrawingRecognizer = nil;
    _editImageViewController.zoomDrawingRecognizer = nil;
    
    [_editImageViewController addGesture];
    
    STAssertNotNil(_editImageViewController.oneTapHitShapeRecognizer, @"");
    STAssertNotNil(_editImageViewController.oneTapDescriptionLabelRecognizer, @"");
    STAssertNotNil(_editImageViewController.doubleTapTwoTouchScaleToOriginRecognizer, @"");
    STAssertNotNil(_editImageViewController.doubleTapDrawTextRecognizer, @"");
    STAssertNotNil(_editImageViewController.moveDrawingRecognizer, @"");
    STAssertNotNil(_editImageViewController.zoomDrawingRecognizer, @"");
    STAssertNotNil(_editImageViewController.drawShapeRecognizer, @"");
}

- (void)testBlurImageChangeNew
{
    _editImageViewController.imageDirectory = nil;
    STAssertNil([_editImageViewController blurImageChangeNew:YES], @"");
    
    _editImageViewController.imageDirectory = @"Test ID";
    _editImageViewController.blurImage = nil;
    STAssertNotNil([_editImageViewController blurImageChangeNew:NO], @"");
    
    _editImageViewController.imageDirectory = @"Test ID";
    _editImageViewController.blurImage = [[UIImage alloc] init];
    UIImage *image = _editImageViewController.blurImage;
    [_editImageViewController blurImageChangeNew:NO];
    STAssertEqualObjects(_editImageViewController.blurImage, image, @"");
    
    _editImageViewController.imageDirectory = @"Test ID";
    _editImageViewController.blurImage = nil;
    STAssertNotNil([_editImageViewController blurImageChangeNew:YES], @"");
}

- (void)testLoadTemplateToRecognizeShape
{
    _editImageViewController.shapeRecognizer = nil;
    [_editImageViewController loadTemplateToRecognizeShape];
    STAssertNotNil(_editImageViewController.shapeRecognizer, @"");
}

- (void)testInitializeShapes
{
    _editImageViewController.shapes = nil;
    [_editImageViewController initializeShapes];
    STAssertEquals((long)_editImageViewController.selectedShapeIndex, LONG_MAX, @"");
    STAssertEquals((int)_editImageViewController.shapes.count, 0, @"");
    STAssertNotNil(_editImageViewController.shapes, @"");
}

- (void)testInitShapeWithShapeType
{
    [_editImageViewController createShapeWithShapeType:FreeHand.class];
    DrawingShape *shape = [_editImageViewController.shapes lastObject];
    STAssertEqualObjects(shape.class, FreeHand.class, @"");
}

- (void)testInitShapeFromFolloweeShape
{
    DrawingShape *followeeShape = [[FreeHand alloc] init];
    [_editImageViewController createShapeFromFolloweeShape:followeeShape];
    DrawingShape *shape = [_editImageViewController.shapes lastObject];
    STAssertEqualObjects(shape.class, FreeHand.class, @"");
    STAssertEqualObjects(shape.followeeShapeID, followeeShape.drawingID, @"");
}

- (void)testUpdateCurrentShapeIndex
{
    [_editImageViewController updateCurrentShapeIndex];
    STAssertEquals((int)_editImageViewController.shapes.count - 1, _editImageViewController.activeShapeIndex, @"");
}

- (void)testRemoveShapeBehindActiveShape
{
    _editImageViewController.activeShapeIndex = _editImageViewController.shapes.count - 2;
    int oldNumberOfShape = _editImageViewController.shapes.count;
    
    // Remove shape at index 1 => remain 1 shape in list
    [_editImageViewController removeShapeBehindActiveShape];
    
    STAssertEquals((int)_editImageViewController.shapes.count, oldNumberOfShape - 1, @"");
}

- (void)testdDrawingShapeAtIndex
{
    STAssertNotNil([_editImageViewController drawingShapeAtIndex:_editImageViewController.shapes.count - 1], @"");
    STAssertNil([_editImageViewController drawingShapeAtIndex:_editImageViewController.shapes.count], @"");
}

- (void)testDrawingShapeWithUUID
{
    DrawingShape *lastShape = [_editImageViewController.shapes lastObject];
    STAssertEqualObjects(lastShape, [_editImageViewController drawingShapeWithUUID:lastShape.drawingID], @"");
}

- (void)testSelectedShape
{
    DrawingShape *lastShape = [_editImageViewController.shapes lastObject];
    _editImageViewController.selectedShapeIndex = _editImageViewController.shapes.count - 1;
    STAssertEqualObjects(_editImageViewController.selectedShape, lastShape, @"");
    
    _editImageViewController.selectedShapeIndex = -1;
    STAssertNil(_editImageViewController.selectedShape, @"");
    
    _editImageViewController.selectedShapeIndex = NSNotFound;
    STAssertNil(_editImageViewController.selectedShape, @"");
    
    _editImageViewController.selectedShapeIndex = _editImageViewController.shapes.count;
    STAssertNil(_editImageViewController.selectedShape, @"");
    
    _editImageViewController.shapes = nil;
    STAssertNil(_editImageViewController.selectedShape, @"");
}

- (void)testActiveShape
{
    DrawingShape *lastShape = [_editImageViewController.shapes lastObject];
    _editImageViewController.activeShapeIndex = _editImageViewController.shapes.count - 1;
    STAssertEqualObjects(_editImageViewController.activeShape, lastShape, @"");
    
    _editImageViewController.activeShapeIndex = -1;
    STAssertNil(_editImageViewController.activeShape, @"");
    
    _editImageViewController.activeShapeIndex = _editImageViewController.shapes.count;
    STAssertNil(_editImageViewController.activeShape, @"");
    
    _editImageViewController.shapes = nil;
    STAssertNil(_editImageViewController.activeShape, @"");
}

- (void)testInitializePointsArray
{
    _editImageViewController.pointsArray = nil;
    [_editImageViewController initializePointsArray];
    STAssertNotNil(_editImageViewController.pointsArray, @"");
    
    CGPoint point = CGPointMake(0, 0);
    [_editImageViewController addPointToPointsArray:point];
    
    STAssertEquals((int)_editImageViewController.pointsArray.count,1, @"");
    STAssertEqualObjects([_editImageViewController.pointsArray objectAtIndex:0], [NSValue valueWithCGPoint:point], @"");
}


@end
