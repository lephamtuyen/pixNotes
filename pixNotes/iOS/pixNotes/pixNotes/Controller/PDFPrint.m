//
//  PDFPrint.m
//  pixNotes
//
//  Created by Tuyen Le on 11/27/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "PDFPrint.h"
#import "Constants.h"
#import "Utilities.h"

@implementation PDFPrint

- (id)initWithIncludeSystemEnvironment:(BOOL)isInclude
{
    self = [super init];
    
    if (self)
    {
        _isInclude = isInclude;
    }
    
    return self;
}

- (NSData *)generatePdfDataWithImagesList:(NSArray *)imagesList
{
    [self view];

    NSMutableData *data = [NSMutableData data];
    UIGraphicsBeginPDFContextToData(data, self.view.bounds, nil);
    
    for (NSInteger i = 0; i < imagesList.count; i++)
    {
        NSString *photoDirectory = [imagesList objectAtIndex:i];
        
        // Get image
        NSString *imageFileName = [photoDirectory stringByAppendingPathComponent:FileFlatDrawing];
        UIImage *image = [UIImage imageWithContentsOfFile:imageFileName];
        
        // Get description
        NSString *textFilePath = [photoDirectory stringByAppendingPathComponent:FileDescriptionFile];
        NSString *description = [NSString stringWithContentsOfURL:[NSURL fileURLWithPath:textFilePath] encoding:NSUTF8StringEncoding error:nil];
        if (i == 0 && _isInclude)
        {
            _systemVersion.text = [NSString stringWithFormat:@"System version: %@", [[UIDevice currentDevice] systemVersion]];
            _model.text = [NSString stringWithFormat:@"Model: %@", [[UIDevice currentDevice] model]];
            _resolution.text = [NSString stringWithFormat:@"Resolution: %.0f x %.0f",
                                [UIScreen mainScreen].bounds.size.width,
                                [UIScreen mainScreen].bounds.size.height];
        }
        else
        {
            _systemVersion.text = @"";
            _model.text = @"";
            _resolution.text = @"";
        }
        _imageView.image = image;
        
        if (description == nil)
        {
            _description.text = @"";
        }
        else
        {
            _description.text = description;
        }

        UIGraphicsBeginPDFPage();
        [self.view.layer renderInContext:UIGraphicsGetCurrentContext()];
    }

    UIGraphicsEndPDFContext();
    return data;
}

@end
