//
//  ExportOrSendPictureViewController.h
//  pixNotes
//
//  Created by Tuyen Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SwitchView.h"
#import <MessageUI/MFMailComposeViewController.h>

@interface SendEmailViewController : UIViewController <SwitchViewDelegate, MFMailComposeViewControllerDelegate, UIScrollViewDelegate>
{
    NSCache *_imageCache;
    NSMutableArray *_imageViews;
    NSArray *_images;
    
    CGFloat _imageHeight;
    CGFloat _imageWidth;
    
    CGPoint _locationBeforePan;
    
    BOOL _isShow;
    
    NSMutableArray *_choosenImagesList;
}

@property (nonatomic, strong) IBOutlet UIView *headerView;
@property (nonatomic, strong) IBOutlet UIView *contentView;

@property (nonatomic, strong) IBOutlet UIView *tapView;
@property (nonatomic, strong) IBOutlet UIView *imagesListView;
@property (nonatomic, strong) IBOutlet UIScrollView *scrollView;
@property (nonatomic, strong) IBOutlet UIImageView *panToShowImagesList;

@property (nonatomic,assign) BOOL includeSystemEnvironment;
@property (nonatomic,assign) NSInteger formatWhenExport;
@property (nonatomic,assign) NSInteger sendOption;

@property (nonatomic,strong) IBOutlet UIButton *sendBtn;

@property (nonatomic,strong) IBOutlet UIImageView *pdfImage;
@property (nonatomic,strong) IBOutlet UIImageView *wordImage;
@property (nonatomic,strong) IBOutlet UIImageView *htmlImage;

@property (nonatomic,strong) IBOutlet UIButton *sendAllBtn;
@property (nonatomic,strong) IBOutlet UIButton *sendOneBtn;
@property (nonatomic,strong) IBOutlet UIButton *sendMultiBtn;

@property (nonatomic,strong) IBOutlet UILabel *sendAllLabel;
@property (nonatomic,strong) IBOutlet UILabel *sendOneLabel;
@property (nonatomic,strong) IBOutlet UILabel *sendMultiLabel;

@property (nonatomic,strong) IBOutlet SwitchView *switchView;

@property (nonatomic,strong) NSString *projectDirectory;
@property (nonatomic,strong) NSString *currentImageDirectory;

- (IBAction)formatButtonPressed:(id)sender;

@end
