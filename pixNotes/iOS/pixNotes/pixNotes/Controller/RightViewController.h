//
//  RightViewController.h
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "AbtractViewController.h"
#import "AGImagePickerController.h"


@interface RightViewController : AbtractViewController <UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIScrollViewAccessibilityDelegate, UIGestureRecognizerDelegate, AGImagePickerControllerDelegate>
{
    NSCache *_imageCache;
    NSMutableArray *_imageViews;
    NSMutableArray *_images;
    
    CGFloat _imageHeight;
    CGFloat _imageWidth;
    
    UIView *_deletedImageView;
    UIView *_moveImageView;
    
    CGPoint _originalCenter;
    CGRect _originalFrame;
    CGPoint _previousPoint;
    NSInteger _currentIndex;
    NSInteger _originIndex;
    CGPoint _currentCenter;
    
    NSInteger _firstVisibleImageIndex;
}

@property (nonatomic, strong) UIView *headerView;
@property (nonatomic, strong) UIButton *takePictureButton;
@property (nonatomic, strong) UIButton *importFromLibraryButton;

@property (nonatomic, strong) UIImageView *separateLine;

@property (nonatomic, strong) NSString *activeImageDirectory;
@property (nonatomic, strong) NSString *activeProjectDirectory;
@property (nonatomic, strong) UIScrollView *scrollView;

@property (nonatomic, strong) UILabel *indicatorLabel;
@property (nonatomic, strong) UIImageView *indicatorImage;

-(void)reloadScrollViewContentWithResetOldContent:(BOOL)resetAllContent;
- (void)adjustSubviewFrame;
- (void)updateCacheWithImageDirectory:(NSString *)imageDirectory;
- (UIImage *)imageWithKey:(NSString *)key;


@end
