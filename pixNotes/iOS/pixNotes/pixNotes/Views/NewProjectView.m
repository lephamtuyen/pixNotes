//
//  NewProjectView.m
//  pixNotes
//
//  Created by Tuyen Le on 1/3/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "NewProjectView.h"
#import "Colors.h"
#import "Utilities.h"
#import "AGImagePickerController.h"
#import "NSString+UUID.h"
#import "UIImage+FixOrientation.h"
#import "UIImage+Resize.h"
#import "Constants.h"
#import "ProjectSettings.h"

#define BUTTON_WIDTH 32

#define CONTENT_WIDTH       250
#define CONTENT_WIDTH_IPAD  250
#define CONTENT_HEIGHT      160
#define CONTENT_HEIGHT_IPAD 160

@implementation NewProjectView

- (id)init
{
    self = [super init];
    
    if (self)
    {
        [self initSubviews];

        _keyboardVisible = NO;
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleTextFieldChanged)
                                                     name:UITextFieldTextDidChangeNotification
                                                   object:_projectNameTextField];
        
        [self updateStateOfStartBtn];
    }
    
    return self;
}

- (void)initTapView
{
    // Tap view
    _tapView = [[UIView alloc] init];
    [_tapView setBackgroundColor:[UIColor clearColor]];
    [self addSubview:_tapView];
    UITapGestureRecognizer *tapOutside = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapOutside:)];
    [_tapView addGestureRecognizer:tapOutside];
}

- (void)initContentView
{
    // Content view
    _contentView = [[UIView alloc] init];
    _contentView.layer.cornerRadius = 5;
    _contentView.layer.masksToBounds = YES;
    [_contentView setBackgroundColor:BACKGROUND_POPUP_VIEW];
    [self addSubview:_contentView];
}

- (void)initProjectNameTextField
{
    // Project text field
    _projectNameTextField = [[UITextField alloc] init];
    _projectNameTextField.placeholder = @"Session Name";
    _projectNameTextField.font = [UIFont fontWithName:@"HelveticaNeue-Light" size:16];
    _projectNameTextField.layer.cornerRadius = 5;
    _projectNameTextField.layer.masksToBounds = YES;
    _projectNameTextField.backgroundColor = [UIColor whiteColor];
    _projectNameTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _projectNameTextField.autocapitalizationType = UITextAutocapitalizationTypeWords;
    UIView *leftPaddingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 5, 28)];
    _projectNameTextField.leftView = leftPaddingView;
    _projectNameTextField.leftViewMode = UITextFieldViewModeAlways;
    [_contentView addSubview:_projectNameTextField];
    [_projectNameTextField becomeFirstResponder];
}

- (void)initCameraButton
{
    // Camera button
    _takePictureButton = [[UIButton alloc] init];
    [_takePictureButton setImage:[UIImage imageNamed:@"camera.png"] forState:UIControlStateNormal];
    [_takePictureButton addTarget:self action:@selector(showCameraToTakeImage) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:_takePictureButton];
}

- (void)initInsertImageButton
{
    // Insert image button
    _importFromLibraryButton = [[UIButton alloc] init];
    [_importFromLibraryButton setImage:[UIImage imageNamed:@"galery.png"] forState:UIControlStateNormal];
    [_importFromLibraryButton addTarget:self action:@selector(showImportImage) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:_importFromLibraryButton];
}

- (void)initCameraLabel
{
    // Camera label
    _cameraLabel = [[UILabel alloc] init];
    [_cameraLabel setBackgroundColor:[UIColor clearColor]];
    [_cameraLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:12]];
    [_cameraLabel setTextColor:GRAY_TEXT_COLOR];
    [_cameraLabel setTextAlignment:NSTextAlignmentCenter];
    _cameraLabel.text = @"Capture";
    [_contentView addSubview:_cameraLabel];
    
    UITapGestureRecognizer *showCameraRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(showCameraToTakeImage)];
    [_cameraLabel addGestureRecognizer:showCameraRecognizer];
    [_cameraLabel setUserInteractionEnabled:YES];
}

- (void)initInsertImageLabel
{
    // Insert image label
    _insertLabel = [[UILabel alloc] init];
    [_insertLabel setBackgroundColor:[UIColor clearColor]];
    [_insertLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:12]];
    [_insertLabel setTextColor:GRAY_TEXT_COLOR];
    [_insertLabel setTextAlignment:NSTextAlignmentCenter];
    _insertLabel.text = @"Insert Photos";
    [_contentView addSubview:_insertLabel];
    
    [_insertLabel setUserInteractionEnabled:YES];
    UITapGestureRecognizer *tapInsertImageLabel = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(showImportImage)];
    [_insertLabel addGestureRecognizer:tapInsertImageLabel];
}

- (void)initStartButton
{
    // Start button
    _startBtn = [[UIButton alloc] init];
    [_startBtn setBackgroundImage:[UIImage imageNamed:@"save.png"] forState:UIControlStateNormal];
    [_startBtn setTitle:@"Start" forState:UIControlStateNormal];
    [_startBtn.titleLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:16]];
    [_startBtn addTarget:self action:@selector(startNewProject) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:_startBtn];
}

- (void)initSubviews
{
    [self setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.5]];
    
    [self initTapView];
    
    [self initContentView];
    
    [self initProjectNameTextField];
    
    [self initCameraButton];
    
    [self initInsertImageButton];
    
    [self initCameraLabel];
    
    [self initInsertImageLabel];
    
    [self initStartButton];
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)
    {
        _contentWidth = CONTENT_WIDTH;
        _contentHeight = CONTENT_HEIGHT;
    }
    else
    {
        _contentWidth = CONTENT_WIDTH_IPAD;
        _contentHeight = CONTENT_HEIGHT_IPAD;
    }
    
    [_tapView setFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
    [_contentView setFrame:CGRectMake((self.frame.size.width - _contentWidth) / 2,
                                      (self.frame.size.height - _contentHeight) / 2,
                                      _contentWidth, _contentHeight)];
    [_projectNameTextField setFrame:CGRectMake((_contentWidth - 210) / 2, 15, 210,  30)];
    [_takePictureButton setFrame:CGRectMake(_contentView.frame.size.width / 3 - BUTTON_WIDTH / 2,
                                            _projectNameTextField.frame.origin.y + _projectNameTextField.frame.size.height + 10,
                                            BUTTON_WIDTH, BUTTON_WIDTH)];
    [_importFromLibraryButton setFrame:CGRectMake(2 * _contentView.frame.size.width / 3 - BUTTON_WIDTH / 2,
                                                  _takePictureButton.frame.origin.y,
                                                  BUTTON_WIDTH, BUTTON_WIDTH)];
    [_cameraLabel setFrame:CGRectMake(_contentView.frame.size.width / 3 - BUTTON_WIDTH,
                                      _takePictureButton.frame.origin.y + 24,
                                      BUTTON_WIDTH * 2, BUTTON_WIDTH)];
    [_insertLabel setFrame:CGRectMake(2 * _contentView.frame.size.width / 3 - BUTTON_WIDTH * 1.5,
                                      _cameraLabel.frame.origin.y,
                                      BUTTON_WIDTH * 3, BUTTON_WIDTH)];
    [_startBtn setFrame:CGRectMake(_projectNameTextField.frame.origin.x,
                                   _cameraLabel.frame.origin.y + _cameraLabel.frame.size.height + 5,
                                   _projectNameTextField.frame.size.width, 35)];
}


- (void)handleTapOutside:(UITapGestureRecognizer *)recognizer
{
    CGPoint tapLocation = [recognizer locationInView:self];
    if( tapLocation.x < _contentView.frame.origin.x
       || tapLocation.x > _contentView.frame.origin.x + _contentView.frame.size.width
       || tapLocation.y < _contentView.frame.origin.y
       || tapLocation.y > _contentView.frame.origin.y + _contentView.frame.size.height )
    {
        [_delegate closeNewProjectViewPopup];
    }
}


- (void)handleTextFieldChanged
{
    [self updateStateOfStartBtn];
}

- (void)updateStateOfStartBtn
{
    if (_projectNameTextField.text && ![_projectNameTextField.text isEqualToString:@""])
    {
        [_startBtn setEnabled:YES];
    }
    else
    {
        [_startBtn setEnabled:NO];
    }
}

#pragma mark - Handle keyboard

- (CGFloat)getHeightOfKeyboard:(CGRect)rect
{
    if (UIInterfaceOrientationIsLandscape([[UIApplication sharedApplication] statusBarOrientation]))
    {
        return rect.size.width;
    }

    // Portrait
    return rect.size.height;
}

-(void)keyboardWillShow:(NSNotification *)aNotification
{
    if (_keyboardVisible)
    {
        return;
    }
    
    CGRect keyboardFrame = [[[aNotification userInfo]
                             objectForKey:UIKeyboardFrameBeginUserInfoKey]
                            CGRectValue];
    CGFloat keyboardHeight = [self getHeightOfKeyboard:keyboardFrame];
    
    CGRect newFrame = [_contentView frame];
    
    if (self.frame.size.height - keyboardHeight < newFrame.origin.y + newFrame.size.height)
    {
        // Get animation info from userInfo
        NSTimeInterval animationDuration;
        UIViewAnimationCurve animationCurve;
        
        [[[aNotification userInfo] objectForKey:UIKeyboardAnimationCurveUserInfoKey] getValue:&animationCurve];
        [[[aNotification userInfo] objectForKey:UIKeyboardAnimationDurationUserInfoKey] getValue:&animationDuration];
        
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:animationDuration];
        [UIView setAnimationCurve:animationCurve];
        
        newFrame.origin.y -= keyboardHeight - (self.frame.size.height - newFrame.size.height - newFrame.origin.y) + 5;
        [_contentView setFrame:newFrame];
        
        [UIView commitAnimations];
    }
    
    _keyboardVisible = YES;
}

-(void)keyboardWillHide:(NSNotification *)aNotification
{
    CGRect newFrame = [_contentView frame];
    newFrame.origin.y = (self.frame.size.height - _contentHeight) / 2;
    [_contentView setFrame:newFrame];
    
    _keyboardVisible = NO;
}


#pragma mark - Action when touch button/label

- (void)showInsertProjectNameFirst
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                    message:@"Insert project name first"
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}

- (void)showCameraToTakeImage
{
    if (!_projectNameTextField.text || [_projectNameTextField.text isEqualToString:@""])
    {
        [self showInsertProjectNameFirst];
        return;
    }
    
    [_delegate showCameraToCaptureImage:self];
}


- (void)showImportImage
{
    if (!_projectNameTextField.text || [_projectNameTextField.text isEqualToString:@""])
    {
        [self showInsertProjectNameFirst];
        return;
    }
    
    [_delegate showImportImageController:self];
}

- (void)startNewProject
{
    NSString *projectDirectory = [self createNewProject];
    [_delegate loadProjectWithProjectDirectory:projectDirectory];
    [_delegate closeNewProjectViewPopup];
}

#pragma mark - UITextFieldDelegate

-(void)textFieldDidEndEditing:(UITextField *)textField
{
    [textField resignFirstResponder];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - project filename

- (NSString *)createNewProject
{
    NSString *projectID = [NSString UUIDString];
    
    NSString *projectDirectory = [[Utilities documentsDirectory] stringByAppendingPathComponent:projectID];
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    [fileManager createDirectoryAtPath:projectDirectory withIntermediateDirectories:YES attributes:nil error:NULL];
    
    // Save project settings
    NSString *projectName = [_projectNameTextField.text isEqualToString:@""] ? _projectNameTextField.placeholder : _projectNameTextField.text;

    ProjectSettings *projectSettings = [[ProjectSettings alloc] initWithProjectName:projectName];
    NSString *projectSettingsPath = [projectDirectory stringByAppendingPathComponent:FileProjectSettingsFile];
    [NSKeyedArchiver archiveRootObject:projectSettings toFile:projectSettingsPath];
    
    // Save Image Name list
    NSMutableArray *imageArray = [[NSMutableArray alloc] init];
    NSString *imageNames = [projectDirectory stringByAppendingPathComponent:FileImageNamesFile];
    [NSKeyedArchiver archiveRootObject:imageArray toFile:imageNames];
    
    return projectDirectory;
}

#pragma mark - UIImagePickerController delegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    NSString *projectDirectory = [self createNewProject];
    [Utilities importImage:info[UIImagePickerControllerOriginalImage] toProjectDirectory:projectDirectory];
    [_delegate closeCameraController];
    [_delegate loadProjectWithProjectDirectory:projectDirectory];
    [_delegate closeNewProjectViewPopup];
}


- (void)agImagePickerController:(AGImagePickerController *)picker didFinishPickingMediaWithInfo:(NSArray *)infos
{
    NSString *projectDirectory = [self createNewProject];
    for (ALAsset *info in infos)
    {
        ALAssetRepresentation *representation = [info defaultRepresentation];
        UIImage *image = [UIImage imageWithCGImage:[representation fullScreenImage]];
        [Utilities importImage:image toProjectDirectory:projectDirectory];
    }
    
    [_delegate loadProjectWithProjectDirectory:projectDirectory];
    [_delegate closeNewProjectViewPopup];
}

@end
