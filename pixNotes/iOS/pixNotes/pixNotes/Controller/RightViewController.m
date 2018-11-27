//
//  RightViewController.m
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "RightViewController.h"
#import "Colors.h"
#import "Utilities.h"
#import "ImageView.h"
#import "UIImage+Resize.h"
#import "UIImage+FixOrientation.h"
#import "Constants.h"
#import "NSString+UUID.h"
#import "ProgressController.h"

#define BUTTON_WIDTH 44

@implementation RightViewController


- (void)initScrollView
{
    _scrollView = [[UIScrollView alloc] init];
    _scrollView.showsVerticalScrollIndicator = NO;
    _scrollView.bounces = NO;
    _scrollView.delegate = self;
    _scrollView.decelerationRate = UIScrollViewDecelerationRateFast;
    
    UITapGestureRecognizer *selectImageGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(selectImagePressed:)];
    selectImageGesture.numberOfTapsRequired = 1;
    selectImageGesture.enabled = YES;
    selectImageGesture.cancelsTouchesInView = NO;
    [_scrollView addGestureRecognizer:selectImageGesture];
    
    UIPanGestureRecognizer *removeImageGesture = [[UIPanGestureRecognizer alloc]
                                                  initWithTarget:self
                                                  action:@selector(removeImageDectected:)];
    removeImageGesture.delegate = self;
    [_scrollView addGestureRecognizer:removeImageGesture];
    
    UILongPressGestureRecognizer *reorderImageGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(reoderImageDetected:)];
    [_scrollView addGestureRecognizer:reorderImageGesture];
    
    [self.view addSubview:_scrollView];
}

- (void)initHeaderView
{
    _headerView = [[UIView alloc] init];
    [_headerView setBackgroundColor:FOOTER_COLOR];
    [self.view addSubview:_headerView];
    
    _takePictureButton = [[UIButton alloc] init];
    if (IS_IPAD())
    {
        [_takePictureButton setImage:[UIImage imageNamed:@"camera.png"] forState:UIControlStateNormal];
    }
    else
    {
        [_takePictureButton setImage:[UIImage imageNamed:@"camera_small.png"] forState:UIControlStateNormal];
    }
    
    [_takePictureButton addTarget:self action:@selector(showCameraToTakeImage:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_takePictureButton];
    
    _importFromLibraryButton = [[UIButton alloc] init];
    if (IS_IPAD())
    {
        [_importFromLibraryButton setImage:[UIImage imageNamed:@"galery.png"] forState:UIControlStateNormal];
    }
    else
    {
        [_importFromLibraryButton setImage:[UIImage imageNamed:@"galery_small.png"] forState:UIControlStateNormal];
    }
    
    [_importFromLibraryButton addTarget:self action:@selector(showImportImage:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_importFromLibraryButton];
}

- (void)initNoImageIndicatorView
{
    _indicatorImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"big_arrow.png"]];
    [self.view addSubview:_indicatorImage];
    
    _indicatorLabel = [[UILabel alloc] init];
    [_indicatorLabel setBackgroundColor:[UIColor clearColor]];
    [_indicatorLabel setFont:[UIFont fontWithName:@"Bradley Hand" size:19]];
    [_indicatorLabel setTextColor:GRAY_TEXT_COLOR];
    [_indicatorLabel setTextAlignment:NSTextAlignmentCenter];
    _indicatorLabel.text = @"Insert Photos";
    [self.view addSubview:_indicatorLabel];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    _imageWidth = ((MainViewController *)self.delegate).rightVisibleWidth;
    _imageHeight = _imageWidth * 3 / 4;
    
    _imageCache = [[NSCache alloc] init];
    
    [self initScrollView];
    
    [self initHeaderView];
    
    [self initNoImageIndicatorView];
}

- (void)viewWillAppear:(BOOL)animated
{
    [self adjustSubviewFrame];
}

- (void)adjustSubviewFrame
{
    [_headerView setFrame:CGRectMake(0, 0, self.view.frame.size.width, BUTTON_WIDTH)];
    [_takePictureButton setFrame:CGRectMake(self.view.frame.size.width - 2.1 * _imageWidth / 3 - BUTTON_WIDTH / 2,
                                            0,
                                            BUTTON_WIDTH, BUTTON_WIDTH)];
    [_importFromLibraryButton setFrame:CGRectMake(self.view.frame.size.width - 0.8 * _imageWidth / 3 - BUTTON_WIDTH / 2,
                                                  _takePictureButton.frame.origin.y,
                                                  BUTTON_WIDTH, BUTTON_WIDTH)];
    
    if (IS_IPAD())
    {
        [_indicatorImage setFrame:CGRectMake(self.view.frame.size.width - _imageWidth / 2, BUTTON_WIDTH + 10, 55, 47)];
    }
    else
    {
        [_indicatorImage setFrame:CGRectMake(self.view.frame.size.width - _imageWidth / 2, BUTTON_WIDTH + 10, 36, 30)];
    }
    
    [_indicatorLabel setFrame:CGRectMake(self.view.frame.size.width - _imageWidth + (_imageWidth - 120) / 2,
                                         _indicatorImage.frame.origin.y + _indicatorImage.frame.size.height + 10, 120, 20)];
    
    
    [_scrollView setFrame:CGRectMake(self.view.frame.size.width - _imageWidth, BUTTON_WIDTH, _imageWidth,
                                     self.view.frame.size.height - BUTTON_WIDTH)];
}


- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    [self adjustSubviewFrame];
}


#pragma mark - select image gesture handle


- (void)selectImagePressed:(UITapGestureRecognizer *)gesture
{
    CGPoint touchPoint = [gesture locationInView: _scrollView];
    NSInteger index = floor(touchPoint.y / _imageHeight);
    
    if (index < 0 || index >= _images.count)
    {
        // If it's outside the range of what we have to display, then do nothing
        return;
    }
    
    NSString *nextActiveImageDir = [_images objectAtIndex:index];
    if (![_activeImageDirectory isEqualToString:nextActiveImageDir])
    {
        [self.delegate loadImageWithImageDirectory:nextActiveImageDir andShowCentralPanel:NO]; 

        [self highlightSelectedImage:nextActiveImageDir dehighlightImage:_activeImageDirectory];
        
        _activeImageDirectory = nextActiveImageDir;
    }
}


- (void)highlightSelectedImage:(NSString *)nextActiveImageDir dehighlightImage:(NSString *)activeImageDirectory
{
    // Highlight selected image
    NSInteger activeIndex = [_images indexOfObject:activeImageDirectory];
    if (activeIndex != NSNotFound && activeIndex < _imageViews.count)
    {
        ImageView *current = [_imageViews objectAtIndex:activeIndex];
        if (current && (NSNull*)current != [NSNull null])
        {
            current.highlightBorder = NO;
        }
    }
    
    activeIndex = [_images indexOfObject:nextActiveImageDir];
    if (activeIndex != NSNotFound && activeIndex < _imageViews.count)
    {
        ImageView *next = [_imageViews objectAtIndex:activeIndex];
        if (next && (NSNull*)next != [NSNull null])
        {
            next.highlightBorder = YES;
        }
    }
}

#pragma mark - Remove image gesture handle


- (void)removeImageDectected:(UIPanGestureRecognizer *)recognizer
{
    if (recognizer.state == UIGestureRecognizerStateBegan)
    {
		CGPoint touchPoint = [recognizer locationInView: _scrollView];
        NSInteger removeImageIndex = floor(touchPoint.y / _imageHeight);
        if (removeImageIndex < 0 || removeImageIndex >= _images.count)
        {
            _deletedImageView = nil;
        }
        else
        {
            _deletedImageView = [_imageViews objectAtIndex:removeImageIndex];
            if (_deletedImageView && (NSNull*)_deletedImageView != [NSNull null])
            {
                _originalCenter = _deletedImageView.center;
            }
        }
    }
    else if (recognizer.state == UIGestureRecognizerStateChanged)
    {
        if (_deletedImageView && (NSNull*)_deletedImageView != [NSNull null])
        {
            CGPoint translation = [recognizer translationInView:[recognizer view]];
            
            if (fabsf(translation.x) > fabsf(translation.y))
            {
                [_deletedImageView setCenter:CGPointMake([_deletedImageView center].x + translation.x, [_deletedImageView center].y)];
                [recognizer setTranslation:CGPointZero inView:[recognizer view]];
                
                if (_deletedImageView.center.x < _originalCenter.x)
                {
                    _deletedImageView.center = _originalCenter;
                }
                
                if (_deletedImageView.center.x < 0 || _deletedImageView.center.x > _scrollView.frame.size.width)
                {
                    _deletedImageView.alpha = 0.0f;
                }
                else if (_deletedImageView.center.x < _scrollView.frame.size.width / 2)
                {
                    _deletedImageView.alpha = fabsf(_deletedImageView.center.x) / (_scrollView.frame.size.width / 2);
                }
                else
                {
                    _deletedImageView.alpha = fabsf(_scrollView.frame.size.width - _deletedImageView.center.x) / (_scrollView.frame.size.width / 2);
                }
            }
        }
    }
    else if (recognizer.state == UIGestureRecognizerStateEnded)
    {
        if (_deletedImageView && (NSNull*)_deletedImageView != [NSNull null])
        {
            if (fabsf(_deletedImageView.center.x - _originalCenter.x) > _deletedImageView.frame.size.width / 3)
            {
                NSInteger deletedImageIndex = [_imageViews indexOfObject:_deletedImageView];
                
                NSString *imageDirectory = [_images objectAtIndex:deletedImageIndex];
                
                //Remove file
                [Utilities removeImageAtDirectory:imageDirectory];

                if ([_activeImageDirectory isEqualToString:imageDirectory])
                {
                    [self.delegate removeCurrentDrawing:deletedImageIndex];
                    
                    // Update current image directory
                    NSString *nextActiveImageDirectory = [Utilities getImageDirectoryFromProjectDirectory:_activeProjectDirectory nextToIndex:deletedImageIndex];
                    
                    if (![_activeImageDirectory isEqualToString:nextActiveImageDirectory])
                    {
                        [self highlightSelectedImage:nextActiveImageDirectory dehighlightImage:_activeImageDirectory];
                        _activeImageDirectory = nextActiveImageDirectory;
                    }
                }
                
                // Remove image from local variables
                [_imageCache removeObjectForKey:imageDirectory];
                [_images removeObject:imageDirectory];
                [_deletedImageView removeFromSuperview];
                [_imageViews removeObjectAtIndex:deletedImageIndex];

                [self.delegate updateButtonsState];
                [self updateIndicatorOfNumberOfImage];
                [UIView animateWithDuration:0.2
                                 animations:^{
                                     [self reloadScrollViewContentWithResetOldContent:NO];
                                 }];
            }
            else
            {
                [UIView animateWithDuration:0.2
                                 animations:^{
                                     _deletedImageView.center = CGPointMake(_originalCenter.x, _deletedImageView.center.y);
                                     _deletedImageView.alpha = 1.0f;
                                     
                                     _deletedImageView = nil;
                                 }
                 ];
            }
        }
    }
}


#pragma mark - reorder image gesture handle


- (void)reoderImageDetected:(UILongPressGestureRecognizer *)recognizer
{
    CGPoint point = [recognizer locationInView:recognizer.view];
    if (recognizer.state == UIGestureRecognizerStateBegan)
    {
        NSInteger moveImageIndex = floor(point.y / _imageHeight);
        if (moveImageIndex < 0 || moveImageIndex >= _images.count)
        {
            _moveImageView = nil;
        }
        else
        {
            _moveImageView = [_imageViews objectAtIndex:moveImageIndex];
            if (_moveImageView && (NSNull*)_moveImageView != [NSNull null])
            {
                _originalFrame = _moveImageView.frame;
                _originIndex = moveImageIndex;
                _currentIndex = moveImageIndex;
                _currentCenter = _moveImageView.center;
                
                [UIView animateWithDuration:0.2
                                 animations:^{
                                     CGRect newFrame = _moveImageView.frame;
                                     newFrame.origin.x -= 0.1 * newFrame.size.width;
                                     newFrame.origin.y -= 0.1 * newFrame.size.height;
                                     newFrame.size.width += 0.2 * newFrame.size.width;
                                     newFrame.size.height += 0.2 * newFrame.size.height;
                                     [_moveImageView setFrame:newFrame];
                                 }
                 ];
            }
        }
    }
    else if (recognizer.state == UIGestureRecognizerStateChanged)
    {
        if (_moveImageView && (NSNull*)_moveImageView != [NSNull null])
        {
            [recognizer.view bringSubviewToFront:_moveImageView];
            CGPoint center = _moveImageView.center;
            center.y += point.y - _previousPoint.y;
            _moveImageView.center = center;
            
            if (_moveImageView.frame.origin.y + _moveImageView.frame.size.height / 3 < _scrollView.contentOffset.y)
            {
                NSInteger firstVisibleImage = (NSInteger)floor(_scrollView.contentOffset.y / _imageHeight);
                if (firstVisibleImage >= 0)
                {
                    CGPoint newPointOffset = _scrollView.contentOffset;
                    newPointOffset.y -= 10.0f;
                    if (newPointOffset.y < 0)
                    {
                        newPointOffset.y = 0;
                    }
                    _scrollView.contentOffset = newPointOffset;
                }
            }
            else if (_moveImageView.frame.origin.y + 2 * _moveImageView.frame.size.height / 3 > _scrollView.contentOffset.y + _scrollView.frame.size.height)
            {
                NSInteger lastVisibleImage = (NSInteger)floor((_scrollView.contentOffset.y + _scrollView.frame.size.height) / _imageHeight);
                if (lastVisibleImage < _imageViews.count)
                {
                    CGPoint newPointOffset = _scrollView.contentOffset;
                    newPointOffset.y += 10.0f;
                    _scrollView.contentOffset = newPointOffset;
                }
            }
            
            NSInteger nextImageIndex = floor(center.y / _imageHeight);
            if (nextImageIndex != _currentIndex && nextImageIndex >= 0 && nextImageIndex < _images.count)
            {
                CGPoint nextCenter = CGPointMake(_imageWidth / 2, _imageHeight * nextImageIndex + _imageHeight / 2);
                UIView *view = [self viewContaintPoint:nextCenter andNotEqualView:_moveImageView];
                [UIView animateWithDuration:0.2
                                 animations:^{
                                     view.center = _currentCenter;
                                     _currentCenter = nextCenter;
                                     _currentIndex = nextImageIndex;
                                 }
                 ];
            }
        }
    }
    else if (recognizer.state == UIGestureRecognizerStateEnded)
    {
        if (_moveImageView && (NSNull*)_moveImageView != [NSNull null])
        {
            [_moveImageView setFrame:_originalFrame];
            _moveImageView.center = _currentCenter;
            _moveImageView = nil;
            
            if (_currentIndex != _originIndex)
            {
                NSString *originImageDirectory = [_images objectAtIndex:_originIndex];
                [Utilities reorderImage:originImageDirectory toIndex:_currentIndex];
                
                [self reloadScrollViewContentWithResetOldContent:YES];
            }
        }
    }
    _previousPoint = point;
}

- (UIView *)viewContaintPoint:(CGPoint)point andNotEqualView:(UIView *)movedView
{
    for (UIView *view in _imageViews)
    {
        if ((NSNull*)view != [NSNull null] && view != movedView && CGRectContainsPoint(view.frame, point))
        {
            return view;
        }
    }
    return nil;
}


#pragma mark - Action when touch button/label


- (void)showCameraToTakeImage:(id)sender
{
    [self.delegate viewController:self showCameraToCaptureImageControllerPressed:sender];
}


- (void)showImportImage:(id)sender
{
    [self.delegate viewController:self showImportImageControllerPressed:sender];
}


#pragma mark - UIImagePickerController delegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    [self.delegate viewController:self closeCameraController:nil];
    [Utilities importImage:info[UIImagePickerControllerOriginalImage] toProjectDirectory:_activeProjectDirectory];
    
    if (!_activeImageDirectory)
    {
        _activeImageDirectory = [Utilities getFirstImageFromProjectDirectory:_activeProjectDirectory];
        [self.delegate loadImageWithImageDirectory:_activeImageDirectory andShowCentralPanel:NO];
    }
    
    [self.delegate updateButtonsState];
    [self reloadScrollViewContentWithResetOldContent:YES];
}


- (void)agImagePickerController:(AGImagePickerController *)picker didFinishPickingMediaWithInfo:(NSArray *)infos
{
    for (ALAsset *info in infos)
    {
        ALAssetRepresentation *representation = [info defaultRepresentation];
        UIImage *image = [UIImage imageWithCGImage:[representation fullScreenImage]];
        [Utilities importImage:image toProjectDirectory:_activeProjectDirectory];
    }
    
    if (!_activeImageDirectory)
    {
        _activeImageDirectory = [Utilities getFirstImageFromProjectDirectory:_activeProjectDirectory];
        [self.delegate loadImageWithImageDirectory:_activeImageDirectory andShowCentralPanel:NO];
    }

    [self.delegate updateButtonsState];
    [self reloadScrollViewContentWithResetOldContent:YES];
}

#pragma mark - Process images


- (void)updateCacheWithImageDirectory:(NSString *)imageDirectory
{
    //the get the image in the background
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        //resize the image in this thread to avoid UIImageView resizing it in the main thread
        UIImage *image = [UIImage imageWithContentsOfFile:[imageDirectory stringByAppendingPathComponent:FileFlatDrawing]];
        CGSize realTargetSize = [Utilities getRealTargetSizeFromImage:image fitWithSize:CGSizeMake(_imageWidth, _imageHeight)];
        UIImage *resizeImage = [image resizedImage:realTargetSize interpolationQuality:kCGInterpolationDefault];
        
        //if we found it, then update UI
        if (resizeImage)
        {
            dispatch_async(dispatch_get_main_queue(), ^{
                
                [_imageCache setObject:resizeImage forKey:imageDirectory];
                
                // Update imageView
                NSInteger updateImageViewIndex = [_images indexOfObject:imageDirectory];
                if (updateImageViewIndex != NSNotFound && updateImageViewIndex < _imageViews.count)
                {
                    ImageView *imageView = [_imageViews objectAtIndex:updateImageViewIndex];
                    if (imageView && (NSNull*)imageView != [NSNull null])
                    {
                        [imageView setImageViewWithImage:resizeImage];
                    }
                }
            });
        }
    });
}

- (UIImage *)imageWithKey:(NSString *)key
{
    return [_imageCache objectForKey:key];
}


#pragma mark - Handling scroll load visible images

- (void)loadVisibleImagesFromImageIndex:(NSInteger)firstVisibleImageIndex toImageIndex:(NSInteger)lastVisibleImageIndex
{
    // Also load image which is before the first one and after the last one
    NSInteger beforeFirstVisibleImage = firstVisibleImageIndex - 1;
    NSInteger afterLastVisibleImage = lastVisibleImageIndex + 1;
    
    // Purge anything before the first page
    for (NSInteger i=0; i<beforeFirstVisibleImage; i++)
    {
        [self unloadImage:i];
    }
    for (NSInteger i=beforeFirstVisibleImage; i<=afterLastVisibleImage; i++)
    {
        [self loadImage:i];
    }
    for (NSInteger i=afterLastVisibleImage+1; i<_images.count; i++)
    {
        [self unloadImage:i];
    }
}

- (void)loadImage:(NSInteger)index
{
    if (index < 0 || index >= _images.count)
    {
        // If it's outside the range of what we have to display, then do nothing
        return;
    }
    
    ImageView *imageView = [_imageViews objectAtIndex:index];
    NSString *imageDirectory = [_images objectAtIndex:index];
    
    BOOL flag = NO;
    if ((NSNull*)imageView == [NSNull null])
    {
        flag = YES;
        imageView = [[ImageView alloc] init];

        [_scrollView addSubview:imageView];
        [_imageViews replaceObjectAtIndex:index withObject:imageView];
        [self updateImageView:imageView withImageFromDirectory:imageDirectory];
    }
    
    CGRect frame = _scrollView.bounds;
    frame.origin.x = 0.0f;
    frame.origin.y = _imageHeight * index;
    frame.size.height = _imageHeight;
    frame = CGRectInset(frame, 0.0f, 5.0f);
    imageView.frame = frame;
}


- (void)unloadImage:(NSInteger)index
{
    if (index < 0 || index >= _images.count)
    {
        // If it's outside the range of what we have to display, then do nothing
        return;
    }
    
    // Remove a image from the scroll view and reset the container array
    UIView *imageView = [_imageViews objectAtIndex:index];
    if ((NSNull*)imageView != [NSNull null] && imageView != _moveImageView)
    {
        [imageView removeFromSuperview];
        [_imageViews replaceObjectAtIndex:index withObject:[NSNull null]];
    }
}


- (void)updateImageView:(ImageView *)imageView withImageFromDirectory:(NSString *)imageDirectory
{
    UIImage *cachedImage = [_imageCache objectForKey:imageDirectory];
    if (cachedImage)
    {
        [imageView setImageViewWithImage:cachedImage];
        imageView.highlightBorder = [_activeImageDirectory isEqualToString:imageDirectory];
    }
    else
    {
        //the get the image in the background
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            
            //get the UIImage
            NSString *imageFileName = [imageDirectory stringByAppendingPathComponent:FileFlatDrawing];
            
            if ([[NSFileManager defaultManager] fileExistsAtPath:imageFileName])
            {
                UIImage *image = [UIImage imageWithContentsOfFile:imageFileName];
                //resize the image in this thread to avoid UIImageView resizing it in the main thread
                CGSize realTargetSize = [Utilities getRealTargetSizeFromImage:image fitWithSize:CGSizeMake(_imageWidth, _imageHeight)];
                image = [image resizedImage:realTargetSize interpolationQuality:kCGInterpolationDefault];
                
                //if we found it, then update UI
                if (image)
                {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        
                        [imageView setImageViewWithImage:image];
                        imageView.highlightBorder = [_activeImageDirectory isEqualToString:imageDirectory];
                        [_imageCache setObject:image forKey:imageDirectory];
                        
                    });
                }
            }
        });
    }
}


#pragma mark - UIGestureRecornizer delegate


- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
    if ([gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]] && [otherGestureRecognizer isKindOfClass:[UITapGestureRecognizer class]])
    {
        return NO;
    }
    
    if ([gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]] && [otherGestureRecognizer isKindOfClass:[UILongPressGestureRecognizer class]])
    {
        return NO;
    }
    
    if ([gestureRecognizer isKindOfClass:[UILongPressGestureRecognizer class]])
    {
        return NO;
    }
    
    return YES;
}


#pragma mark - UINanivationController delegate


- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated
{
    [[UIApplication sharedApplication] setStatusBarHidden:YES];
}


#pragma mark - UIScrollViewDelegate


- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (_moveImageView && (NSNull*)_moveImageView != [NSNull null])
    {
        NSInteger firstVisibleImage = (NSInteger)floor(_scrollView.contentOffset.y / _imageHeight);
        NSUInteger lastVisibleImage = (NSInteger)floor((_scrollView.contentOffset.y + _scrollView.frame.size.height) / _imageHeight);
        if (_firstVisibleImageIndex != firstVisibleImage)
        {
            // Load the images which are now on screen
            [self loadVisibleImagesFromImageIndex:firstVisibleImage toImageIndex:lastVisibleImage];
            _firstVisibleImageIndex = firstVisibleImage;
        }
    }
}

- (void) scrollViewWillEndDragging:(UIScrollView *)scroll withVelocity:(CGPoint)velocity targetContentOffset:(inout CGPoint *)targetContentOffset
{
    //This is the index of the "images" that we will be landing at
    NSUInteger nearestIndex = (NSUInteger)(targetContentOffset->y / _imageHeight);
    NSUInteger farestIndex = (NSInteger)floor((targetContentOffset->y + _scrollView.frame.size.height) / _imageHeight);
    
//    //This is the actual x position in the scroll view
//    CGFloat xOffset = nearestIndex * _imageHeight;
//    
//    CGFloat soDu = targetContentOffset->y - xOffset;
//    
//    //I've found that scroll views will "stick" unless this is done
//    xOffset = xOffset==0?1:xOffset;
//    
//    //Tell the scroll view to land on our images
//    *targetContentOffset = CGPointMake(targetContentOffset->x, xOffset);
    
    if (_firstVisibleImageIndex != nearestIndex)
    {
        // Load the images which are now on screen
        [self loadVisibleImagesFromImageIndex:nearestIndex toImageIndex:farestIndex];
        _firstVisibleImageIndex = nearestIndex;
    }
}


//- (void) scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
//{
//    if(!decelerate)
//    {
//        NSUInteger currentIndex = (NSUInteger)(scrollView.contentOffset.y / _imageHeight);
//        
//        [scrollView setContentOffset:CGPointMake(0, _imageHeight * currentIndex) animated:YES];
//    }
//}


- (void)resetContentView
{
    if (_imageViews)
    {
        for (NSInteger i = 0; i < _imageViews.count; ++i)
        {
            UIView *imageView = [_imageViews objectAtIndex:i];
            if ((NSNull*)imageView != [NSNull null])
            {
                [imageView removeFromSuperview];
            }
        }
        
        [_imageViews removeAllObjects];
        _imageViews = nil;
    }
    
    _images = nil;
}


- (void)setScrollViewContentSize
{
    // Set up the content size of the scroll view
    CGSize imagesScrollViewSize = _scrollView.frame.size;
    _scrollView.contentSize = CGSizeMake(imagesScrollViewSize.width, _imageHeight * _images.count);
}


-(void)reloadScrollViewContentWithResetOldContent:(BOOL)resetAllContent
{
    if (resetAllContent)
    {
        [self resetContentView];
        
        _images = [Utilities getImagesFromProjectDirectory:_activeProjectDirectory];
        
        // Set up the array to hold the views for each image
        _imageViews = [[NSMutableArray alloc] init];
        
        for (NSInteger i = 0; i < _images.count; ++i)
        {
            [_imageViews addObject:[NSNull null]];
        }
    }

    [self updateIndicatorOfNumberOfImage];
    [self setScrollViewContentSize];
    [self loadVisibleImagesFromImageIndex:(NSInteger)floor(_scrollView.contentOffset.y / _imageHeight) toImageIndex:(NSInteger)floor((_scrollView.contentOffset.y + _scrollView.frame.size.height) / _imageHeight)];
}


- (void)setActiveProjectDirectory:(NSString *)activeProjectDir
{
    if (_activeProjectDirectory == nil || ![_activeProjectDirectory isEqualToString:activeProjectDir])
    {
        _activeProjectDirectory = activeProjectDir;
        [_imageCache removeAllObjects];
        
        [self reloadScrollViewContentWithResetOldContent:YES];
    }
}

- (void)updateIndicatorOfNumberOfImage
{
    if (_images.count > 0)
    {
        _indicatorImage.hidden = YES;
        _indicatorLabel.hidden = YES;
    }
    else
    {
        _indicatorImage.hidden = NO;
        _indicatorLabel.hidden = NO;
    }
}

@end
