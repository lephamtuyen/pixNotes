//
//  NewProjectView.h
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@class NewProjectView;

@protocol NewProjectViewDelegate <NSObject>

- (void)closeNewProjectViewPopup;
- (void)showCentralPanel;
- (void)closeCameraController;
- (void)showImportImageController:(UIView *)view;
- (void)showCameraToCaptureImage:(UIView *)view;
- (void)loadProjectWithProjectDirectory:(NSString *)projectID;

@end

@interface NewProjectView : UIView <UINavigationControllerDelegate, UIImagePickerControllerDelegate, UITextFieldDelegate>
{
    CGFloat _contentWidth;
    CGFloat _contentHeight;
    
    BOOL _keyboardVisible;
}

@property (nonatomic, strong) id<NewProjectViewDelegate>delegate;

@property (nonatomic, strong) UIView *tapView;
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UITextField *projectNameTextField;
@property (nonatomic, strong) UIButton *takePictureButton;
@property (nonatomic, strong) UIButton *importFromLibraryButton;
@property (nonatomic, strong) UILabel *cameraLabel;
@property (nonatomic, strong) UILabel *insertLabel;
@property (nonatomic, strong) UIButton *startBtn;

@end
