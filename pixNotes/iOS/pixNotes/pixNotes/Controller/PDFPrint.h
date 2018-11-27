//
//  PDFPrint.h
//  pixNotes
//
//  Created by Tuyen Le on 11/27/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PDFPrint : UIViewController
{
    IBOutlet UILabel *_systemVersion;
    IBOutlet UILabel *_model;
    IBOutlet UILabel *_resolution;
    IBOutlet UIImageView *_imageView;
    IBOutlet UILabel *_description;
}

@property (nonatomic, assign) BOOL isInclude;

- (id)initWithIncludeSystemEnvironment:(BOOL)isInclude;
- (NSData *)generatePdfDataWithImagesList:(NSArray *)imagesList;

@end
