//
//  SettingViewTestCases.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/9/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PixNotesTestCases.h"
#import "EditImagesViewController.h"
#import "AppDelegate.h"
#import "OtherShapeSettingView.h"
#import "DrawingShape.h"
#import "Settings.h"
#import "Colors.h"
#import "Constants.h"
#import "FreeHand.h"

@interface SettingViewTestCases : PixNotesTestCases
{
    AppDelegate *_appDelegate;
    EditImagesViewController *_editImageViewController;
    SettingView *_settingView;
}
@end

@implementation SettingViewTestCases

- (void)setUp
{
    _appDelegate = [[UIApplication sharedApplication] delegate];
    _editImageViewController = (EditImagesViewController *)_appDelegate.viewController.centerPanel;

    _editImageViewController.shapes = [[NSMutableArray alloc] init];
    FreeHand *newShape = [[FreeHand alloc] init];
    newShape.path = [UIBezierPath bezierPath];
    newShape.path.lineWidth = MEDIUM_BORDER;
    [_editImageViewController.shapes addObject:newShape];
}

- (void)tearDown
{
    // Close setting view before testing
    if (_settingView)
    {
        [_settingView.delegate closeSettingView];
    }
    [super tearDown];
}

// Show Setting View When pressed setting button and exist a selected shape
- (void)testShowSettingViewWhenPressedSettingButtonAndExistSelectedShape
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;
    STAssertTrue([_editImageViewController.view.subviews containsObject:_settingView], @"Not show setting view");
}

// Dont show setting view when pressed setting button but not exist a selected shape
- (void)testDontShowSettingViewWhenPressedSettingButtonButNotExistSelectedShape
{
    _editImageViewController.selectedShapeIndex = NSNotFound;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;
    STAssertFalse([_editImageViewController.view.subviews containsObject:_settingView], @"Show setting view");
}

//// Close setting view if pressed outside its contentView
//- (void)testCloseSettingViewWhenPressedOutsideItsContentView
//{
//    _editImageViewController.selectedShapeIndex = 0;
////    [_editImageViewController settingActionTapped:nil];
//    _settingView = _editImageViewController.settingView;
//    CGRect frame = _settingView.contentView.frame;
//    
//    // Point outside setting view
//    CGPoint point = CGPointMake(frame.origin.x - 1, frame.origin.y - 1);
//    
//    [_settingView pointInside:point withEvent:nil];
//    STAssertFalse([_editImageViewController.view.subviews containsObject:_settingView], @"Don't close setting view when pressed outside its contentView");
//}

//// Save change of line width when pressed outside its contentView
//- (void)testSaveLineWidthWhenPressedOutsideItsContentView
//{
//    _editImageViewController.selectedShapeIndex = 0;
//    [_editImageViewController showSettingView];
//    _settingView = _editImageViewController.settingView;
//
//    _settingView.lineWidth = LARGER_BORDER;
//    
//    // Point outside content view of setting view
//    CGRect frame = _settingView.contentView.frame;
//    CGPoint point = CGPointMake(frame.origin.x - 1, frame.origin.y - 1);
//    [_settingView pointInside:point withEvent:nil];
//    
//    STAssertEquals(_editImageViewController.selectedShape.path.lineWidth, LARGER_BORDER, @"Not save line width");
//}


// Save change of line color when pressed outside its contentView
//- (void)testSaveLineColorWhenPressedOutsideItsContentView
//{
//    _editImageViewController.selectedShapeIndex = 0;
//    [_editImageViewController showSettingView];
//    _settingView = _editImageViewController.settingView;
//    
//    _settingView.lineColor = WHITE_COLOR;
//    
//    // Point outside content view of setting view
//    CGRect frame = _settingView.contentView.frame;
//    CGPoint point = CGPointMake(frame.origin.x - 1, frame.origin.y - 1);
//    [_settingView pointInside:point withEvent:nil];
//    
//    STAssertEqualObjects(_editImageViewController.selectedShape.lineColor, WHITE_COLOR, @"Not save line color");
//}

//// Don't close setting view if pressed inside its contentView
//- (void)testDontCloseSettingViewWhenPressedInsideItsContentView
//{
//    _editImageViewController.selectedShapeIndex = 0;
//    [_editImageViewController showSettingView];
//    _settingView = _editImageViewController.settingView;
//    CGRect frame = _settingView.contentView.frame;
//    
//    // Point outside take description view
//    CGPoint point = CGPointMake(frame.origin.x, frame.origin.y);
//    
//    [_settingView pointInside:point withEvent:nil];
//    STAssertTrue([_editImageViewController.view.subviews containsObject:_settingView], @"Close setting view when pressed inside its contentView");
//}

// Load lineWidth correctlly
- (void)testLoadLineWidthCorrectly
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;
    
    STAssertEquals(((OtherShapeSettingView *)_settingView).lineWidth, _editImageViewController.selectedShape.path.lineWidth, @"Not correct lineWidth");
}

// Load line color correctlly
- (void)testLoadColorCorrectly
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;
    
    STAssertEqualObjects(((OtherShapeSettingView *)_settingView).lineColor, _editImageViewController.selectedShape.lineColor, @"Not correct line color");
}

// Press another color button => change line color
- (void)testLoadLineColorCorrectly
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;

    STAssertEqualObjects(((OtherShapeSettingView *)_settingView).lineColor, _editImageViewController.selectedShape.lineColor, @"Not correct line color");
}


// Pressed color button => change line color
- (void)testChangeLineColorWhenPressedColorButton
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;

    // Set white color
    [(OtherShapeSettingView *)_settingView borderColor:[((OtherShapeSettingView *)_settingView).colorButtons objectAtIndex:5]];
    
    STAssertEqualObjects(((OtherShapeSettingView *)_settingView).lineColor, WHITE_COLOR, @"Not change color");
}

// Pressed color button => change image of thumb
- (void)testChangeThumbImageWhenPressedColorButton
{
    // Cannot find API to get thumb image of UISlider
}

// Pressed color button => change color of size border view
- (void)testChangeSizeBorderColorWhenPressedColorButton
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;
    
    // Set white color
    [(OtherShapeSettingView *)_settingView borderColor:[((OtherShapeSettingView *)_settingView).colorButtons objectAtIndex:5]];
    
    STAssertEqualObjects(((UIView *)[((OtherShapeSettingView *)_settingView).sizeBorderViews objectAtIndex:0]).backgroundColor, WHITE_COLOR, @"Not change color");
}

// Pressed size button (large size) => change line width variable
- (void)testChangeLineWidthWhenPressedBorderSizeBtn
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;
    
    // Set larger siz
    [(OtherShapeSettingView *)_settingView borderSize:[((OtherShapeSettingView *)_settingView).borderSizeButtons objectAtIndex:2]];
    
    STAssertEquals(((OtherShapeSettingView *)_settingView).lineWidth, LARGER_BORDER, @"Not change line width");
}

// Pressed size border button (large size) => change value of slider
- (void)testChangeSliderValueWhenPressedBorderSizeBtn
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;
    
    // Set larger size
    [(OtherShapeSettingView *)_settingView borderSize:[((OtherShapeSettingView *)_settingView).borderSizeButtons objectAtIndex:2]];
    
    STAssertEquals(((OtherShapeSettingView *)_settingView).sliderView.value, 2.0f, @"Not change line width");
}

// Drag slide to the right => change value of line width
- (void)testChangeLineWidthWhenDragSlider
{
    _editImageViewController.selectedShapeIndex = 0;
    [_editImageViewController showSettingView];
    _settingView = _editImageViewController.settingView;
    
    ((OtherShapeSettingView *)_settingView).sliderView.value = 1.7f;
    // Set white color
    [(OtherShapeSettingView *)_settingView valueChanged:((OtherShapeSettingView *)_settingView).sliderView];
    
    STAssertEquals(((OtherShapeSettingView *)_settingView).lineWidth, LARGER_BORDER, @"Not change line width");
}

@end
