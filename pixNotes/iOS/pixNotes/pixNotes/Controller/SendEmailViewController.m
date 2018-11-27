//
//  ExportOrSendPictureViewController.m
//  pixNotes
//
//  Created by Tuyen Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#import "SendEmailViewController.h"
#import "Settings.h"
#import "PDFPrint.h"
#import "ZipArchive.h"
#import "GDataXMLNode.h"
#import "Utilities.h"
#import "Constants.h"
#import "Colors.h"
#import "ImageView.h"
#import "UIImage+Resize.h"

@implementation SendEmailViewController

#pragma mark - View lifecycle

- (void)viewDidLoad
{
	[super viewDidLoad];
    
    _isShow = NO;
    _switchView.delegate = self;
    _switchView.isOn = YES;
    _includeSystemEnvironment = YES;
    _formatWhenExport = EXPORT_FORMAT_HTML;
    _sendOption = SEND_ALL;

    _imageCache = [[NSCache alloc] init];
    _choosenImagesList = [[NSMutableArray alloc] init];
    
    UITapGestureRecognizer *selectImageGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(selectImagePressed:)];
    selectImageGesture.numberOfTapsRequired = 1;
    [_scrollView addGestureRecognizer:selectImageGesture];
    
    [_sendAllLabel setUserInteractionEnabled:YES];
    UITapGestureRecognizer *changeOptionGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(sendOptionButtonPressed:)];
    [_sendAllLabel addGestureRecognizer:changeOptionGesture];
    [_sendOneLabel setUserInteractionEnabled:YES];
    changeOptionGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(sendOptionButtonPressed:)];
    [_sendOneLabel addGestureRecognizer:changeOptionGesture];
    [_sendMultiLabel setUserInteractionEnabled:YES];
    changeOptionGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(sendOptionButtonPressed:)];
    [_sendMultiLabel addGestureRecognizer:changeOptionGesture];
    
    UIPanGestureRecognizer *panGesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(handlePan:)];
    panGesture.maximumNumberOfTouches = 1;
    panGesture.minimumNumberOfTouches = 1;
    [_panToShowImagesList addGestureRecognizer:panGesture];
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTap)];
    tapGesture.numberOfTapsRequired = 1;
    [_panToShowImagesList addGestureRecognizer:tapGesture];
    tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapOutside)];
    tapGesture.numberOfTapsRequired = 1;
    [_tapView addGestureRecognizer:tapGesture];
    
    [self updateSendOptionStatus];
    [self updateFormatExportStatus];
    [self updateStateOfSendButton];
    
    if (IS_IPAD())
    {
        [_headerView setHidden:YES];
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    if (IS_IPAD())
    {
        [_contentView setFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
    }
    
    _imageWidth = _scrollView.frame.size.width;
    _imageHeight = _imageWidth * 3 / 4;
    [self setDataSource];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    _switchView.delegate = nil;
    _switchView = nil;
}

- (void)dealloc
{
    _switchView.delegate = nil;
    _switchView = nil;
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
    
    NSString *choosenImage = [_images objectAtIndex:index];
    
    if ([_choosenImagesList indexOfObject:choosenImage] == NSNotFound)
    {
        [_choosenImagesList addObject:choosenImage];
        
        if ((NSNull *)[_imageViews objectAtIndex:index] != [NSNull null])
        {
            ((ImageView *)[_imageViews objectAtIndex:index]).imageViewIndicator.hidden = NO;
        }
    }
    else
    {
        [_choosenImagesList removeObject:choosenImage];
        
        if ((NSNull *)[_imageViews objectAtIndex:index] != [NSNull null])
        {
            ((ImageView *)[_imageViews objectAtIndex:index]).imageViewIndicator.hidden = YES;
        }
    }
    
    [self updateStateOfSendButton];
}


- (IBAction)back:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
    [self dismissModalViewControllerAnimated:YES];
}


#pragma SwitchViewDelegate


- (void)toggleSwitch:(BOOL)enable sender:(id)sender
{
    if (_includeSystemEnvironment != enable)
    {
        _includeSystemEnvironment = enable;
    }
}

- (void)updateFormatExportStatus
{
    if (_formatWhenExport == EXPORT_FORMAT_PDF)
    {
        [_pdfImage setHidden:NO];
        [_wordImage setHidden:YES];
        [_htmlImage setHidden:YES];
    }
    else if (_formatWhenExport == EXPORT_FORMAT_WORD)
    {
        [_pdfImage setHidden:YES];
        [_wordImage setHidden:NO];
        [_htmlImage setHidden:YES];
    }
    else
    {
        [_pdfImage setHidden:YES];
        [_wordImage setHidden:YES];
        [_htmlImage setHidden:NO];
    }
}

- (void)showSelectMultiImagesView:(BOOL)isShow
{
    if (isShow)
    {
        _imagesListView.hidden = NO;
        
        //
        if (!_isShow)
        {
            [self show];
            _isShow = YES;
        }
    }
    else
    {
        //
        if (_isShow)
        {
            [self hide];
            _isShow = NO;
        }
        
        _imagesListView.hidden = YES;
    }
}

- (void)show
{
    [UIView animateWithDuration:0.2
                     animations:^{
                         [_imagesListView setCenter:CGPointMake([_imagesListView center].x - 100.0f, [_imagesListView center].y)];
                     }];
}

- (void)hide
{
    [UIView animateWithDuration:0.2
                     animations:^{
                         [_imagesListView setCenter:CGPointMake([_imagesListView center].x + 100.0f, [_imagesListView center].y)];
                     }];
}

- (void)updateStateOfSendButton
{
    if (_sendOption == SEND_MULTI)
    {
        if (_choosenImagesList.count == 0)
        {
            _sendBtn.enabled = NO;
        }
        else
        {
            _sendBtn.enabled = YES;
        }
    }
    else
    {
        _sendBtn.enabled = YES;
    }
}

- (void)updateSendOptionStatus
{
    if (_currentImageDirectory)
    {
        [_sendOneBtn setEnabled:YES];
        _sendOneLabel.textColor = GRAY_TEXT_COLOR;
    }
    else
    {
        [_sendOneBtn setEnabled:NO];
        _sendOneLabel.textColor = [UIColor darkGrayColor];
    }
    
    if (_sendOption == SEND_ALL)
    {
        [_sendAllBtn setImage:[UIImage imageNamed:@"sel.png"] forState:UIControlStateNormal];
        [_sendOneBtn setImage:[UIImage imageNamed:@"not_sel.png"] forState:UIControlStateNormal];
        [_sendMultiBtn setImage:[UIImage imageNamed:@"not_sel.png"] forState:UIControlStateNormal];
        
        [self showSelectMultiImagesView:NO];
    }
    else if (_sendOption == SEND_ONE)
    {
        [_sendAllBtn setImage:[UIImage imageNamed:@"not_sel.png"] forState:UIControlStateNormal];
        [_sendOneBtn setImage:[UIImage imageNamed:@"sel.png"] forState:UIControlStateNormal];
        [_sendMultiBtn setImage:[UIImage imageNamed:@"not_sel.png"] forState:UIControlStateNormal];
        
        [self showSelectMultiImagesView:NO];
    }
    else
    {
        [_sendAllBtn setImage:[UIImage imageNamed:@"not_sel.png"] forState:UIControlStateNormal];
        [_sendOneBtn setImage:[UIImage imageNamed:@"not_sel.png"] forState:UIControlStateNormal];
        [_sendMultiBtn setImage:[UIImage imageNamed:@"sel.png"] forState:UIControlStateNormal];
        
        [self showSelectMultiImagesView:YES];
    }
}

- (IBAction)sendOptionButtonPressed:(id)sender
{
    UIView *view;
    if ([sender isKindOfClass:UITapGestureRecognizer.class])
    {
        view = ((UITapGestureRecognizer *)sender).view;
    }
    else
    {
        view = (UIView *)sender;
    }
    
    if (_currentImageDirectory || view.tag != 1)
    {
        _sendOption = view.tag;
    }
    
    [self updateSendOptionStatus];
    [self updateStateOfSendButton];
}

- (IBAction)formatButtonPressed:(id)sender
{
    UIButton *button = (UIButton *)sender;
    _formatWhenExport = button.tag;
    [self updateFormatExportStatus];
}

- (NSArray *)listOfImages
{
    if (_sendOption == SEND_ALL)
    {
        return [Utilities getImagesFromProjectDirectory:_projectDirectory];
    }
    else if (_sendOption == SEND_ONE)
    {
        return [[NSArray alloc] initWithObjects:_currentImageDirectory, nil];
    }

    return _choosenImagesList;
}

- (NSArray *)systemEnvironmentLines
{
    return [NSArray arrayWithObjects:
            [NSString stringWithFormat:@"System version: %@", [[UIDevice currentDevice] systemVersion]],
            [NSString stringWithFormat:@"Model: %@", [[UIDevice currentDevice] model]],
            [NSString stringWithFormat:@"Resolution: %@",
             [NSString stringWithFormat:@"%.0f x %.0f",
              [UIScreen mainScreen].bounds.size.width,
              [UIScreen mainScreen].bounds.size.height]], nil];
}

- (NSData *)attachedData
{
    if (_formatWhenExport == EXPORT_FORMAT_PDF)
    {
        return [[[PDFPrint alloc] initWithIncludeSystemEnvironment:_includeSystemEnvironment] generatePdfDataWithImagesList:[self listOfImages]];
    }
    else if (_formatWhenExport == EXPORT_FORMAT_WORD)
    {
        NSString *documentsDirectory = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        NSString *zipFilePath = [documentsDirectory stringByAppendingPathComponent:@"MSWord.docx"];
        
        NSFileManager *fileManager = [NSFileManager defaultManager];
        
        // Create new one
        ZipArchive *za = [[ZipArchive alloc] init];
        [za CreateZipFile2:zipFilePath];
        
        // Add default files to MSWord folder
        NSArray *resourcePaths = [NSArray arrayWithObjects:
                                  @"[Content_Types].xml",
                                  @".rels", @"app.xml",
                                  @"core.xml",
                                  @"thumbnail.jpeg",
                                  @"fontTable.xml",
                                  @"settings.xml",
                                  @"styles.xml",
                                  @"stylesWithEffects.xml",
                                  @"theme1.xml",
                                  @"webSettings.xml",
                                  nil];
        NSArray *newPaths = [NSArray arrayWithObjects:
                             @"[Content_Types].xml",
                             @"_rels/.rels",
                             @"docProps/app.xml",
                             @"docProps/core.xml",
                             @"docProps/thumbnail.jpeg",
                             @"word/fontTable.xml",
                             @"word/settings.xml",
                             @"word/styles.xml",
                             @"word/stylesWithEffects.xml",
                             @"word/theme/theme1.xml",
                             @"word/webSettings.xml",
                             nil];
        
        NSString * resourcePath;
        for (NSInteger i = 0; i < resourcePaths.count; i++)
        {
            resourcePath = [[NSBundle mainBundle] pathForResource:[resourcePaths objectAtIndex:i] ofType:nil];
            [za addFileToZip:resourcePath newname:[newPaths objectAtIndex:i]];
        }
        
        NSError *error;
        // document.xml.rels
        resourcePath = [[NSBundle mainBundle] pathForResource:@"document.xml.rels" ofType:nil];
        NSData *xmlData = [[NSMutableData alloc] initWithContentsOfFile:resourcePath];
        GDataXMLDocument *documentRels = [[GDataXMLDocument alloc] initWithData:xmlData options:0 error:&error];
        
        // document.xml
        resourcePath = [[NSBundle mainBundle] pathForResource:@"document.xml" ofType:nil];
        xmlData = [[NSMutableData alloc] initWithContentsOfFile:resourcePath];
        GDataXMLDocument *document = [[GDataXMLDocument alloc] initWithData:xmlData options:0 error:&error];
        
        GDataXMLElement *body = [[document nodesForXPath:@"//w:document/w:body" error:nil] objectAtIndex:0];
        // Insert System Environment
        if (_includeSystemEnvironment)
        {
            NSArray *sysEnvLines = [self systemEnvironmentLines];
            for (int i = 0; i < 3; i++)
            {
                GDataXMLElement *p = [GDataXMLNode elementWithName:@"w:p"];
                GDataXMLElement *r = [GDataXMLNode elementWithName:@"w:r"];
                GDataXMLElement *t = [GDataXMLNode elementWithName:@"w:t" stringValue:[sysEnvLines objectAtIndex:i]];
                [r addChild:t];
                [p addChild:r];
                [body addChild:p];
            }
        }
        
        // New line
        GDataXMLElement *p = [GDataXMLNode elementWithName:@"w:p"];
        [body addChild:p];
        
        CGFloat emuWidth;
        CGFloat emuHeight;
        
        NSArray *imageList = [self listOfImages];
        for (NSInteger i = 0; i < imageList.count; i++)
        {
            NSString *photoDirectory = [imageList objectAtIndex:i];
            
            // Add image to MSWord folder
            // This is computed from fomula (http://startbigthinksmall.wordpress.com/2010/01/04/points-inches-and-emus-measuring-units-in-office-open-xml/)
            NSString *imageFileName = [photoDirectory stringByAppendingPathComponent:FileFlatDrawing];
            UIImage *image = [UIImage imageWithContentsOfFile:imageFileName];
            if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad
                || (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone && image.size.width > image.size.height))
            {
                emuWidth = 4681070;
            }
            else
            {
                emuWidth = 4065270;
            }
            emuHeight = emuWidth * image.size.height / image.size.width;
            [za addFileToZip:imageFileName newname:[NSString stringWithFormat:@"word/media/image%ld.png", (long)i]];
            
            // document.xml
            GDataXMLElement *p = [GDataXMLNode elementWithName:@"w:p"];
            GDataXMLElement *pPr = [GDataXMLNode elementWithName:@"w:pPr"];
            GDataXMLElement *jc = [GDataXMLNode elementWithName:@"w:jc"];
            [jc addAttribute:[GDataXMLNode attributeWithName:@"w:val" stringValue:@"center"]];
            [pPr addChild:jc];
            [p addChild:pPr];
            
            GDataXMLElement *r = [GDataXMLNode elementWithName:@"w:r"];
            GDataXMLElement *rPr = [GDataXMLNode elementWithName:@"w:rPr"];
            GDataXMLElement *noProof = [GDataXMLNode elementWithName:@"w:noProof"];
            [rPr addChild:noProof];
            [r addChild:rPr];
            
            GDataXMLElement *drawing = [GDataXMLNode elementWithName:@"w:drawing"];
            GDataXMLElement *il = [GDataXMLNode elementWithName:@"wp:inline"];
            [il addAttribute:[GDataXMLNode attributeWithName:@"distT" stringValue:@"0"]];
            [il addAttribute:[GDataXMLNode attributeWithName:@"distB" stringValue:@"0"]];
            [il addAttribute:[GDataXMLNode attributeWithName:@"distL" stringValue:@"0"]];
            [il addAttribute:[GDataXMLNode attributeWithName:@"distR" stringValue:@"0"]];
            GDataXMLElement *extent = [GDataXMLNode elementWithName:@"wp:extent"];
            [extent addAttribute:[GDataXMLNode attributeWithName:@"cx" stringValue:[NSString stringWithFormat:@"%.0f", emuWidth]]];
            [extent addAttribute:[GDataXMLNode attributeWithName:@"cy" stringValue:[NSString stringWithFormat:@"%.0f", emuHeight]]];
            GDataXMLElement *effectExtent = [GDataXMLNode elementWithName:@"wp:effectExtent"];
            [effectExtent addAttribute:[GDataXMLNode attributeWithName:@"l" stringValue:@"0"]];
            [effectExtent addAttribute:[GDataXMLNode attributeWithName:@"t" stringValue:@"0"]];
            [effectExtent addAttribute:[GDataXMLNode attributeWithName:@"r" stringValue:@"0"]];
            [effectExtent addAttribute:[GDataXMLNode attributeWithName:@"b" stringValue:@"0"]];
            GDataXMLElement *docPr = [GDataXMLNode elementWithName:@"wp:docPr"];
            [docPr addAttribute:[GDataXMLNode attributeWithName:@"id" stringValue:[NSString stringWithFormat:@"%ld", (long)i]]];
            [docPr addAttribute:[GDataXMLNode attributeWithName:@"name" stringValue:@""]];
            GDataXMLElement *cNvGraphicFramePr = [GDataXMLNode elementWithName:@"wp:cNvGraphicFramePr"];
            GDataXMLElement *graphicFrameLocks = [GDataXMLNode elementWithName:@"a:graphicFrameLocks"];
            [graphicFrameLocks addAttribute:[GDataXMLNode attributeWithName:@"xmlns:a" stringValue:@"http://schemas.openxmlformats.org/drawingml/2006/main"]];
            [graphicFrameLocks addAttribute:[GDataXMLNode attributeWithName:@"noChangeAspect" stringValue:@"1"]];
            GDataXMLElement *graphic = [GDataXMLNode elementWithName:@"a:graphic"];
            [graphic addAttribute:[GDataXMLNode attributeWithName:@"xmlns:a" stringValue:@"http://schemas.openxmlformats.org/drawingml/2006/main"]];
            GDataXMLElement *graphicData = [GDataXMLNode elementWithName:@"a:graphicData"];
            [graphicData addAttribute:[GDataXMLNode attributeWithName:@"uri" stringValue:@"http://schemas.openxmlformats.org/drawingml/2006/picture"]];
            GDataXMLElement *pic = [GDataXMLNode elementWithName:@"pic:pic"];
            [pic addAttribute:[GDataXMLNode attributeWithName:@"xmlns:pic" stringValue:@"http://schemas.openxmlformats.org/drawingml/2006/picture"]];
            GDataXMLElement *nvPicPr = [GDataXMLNode elementWithName:@"pic:nvPicPr"];
            GDataXMLElement *cNvPr = [GDataXMLNode elementWithName:@"pic:cNvPr"];
            [cNvPr addAttribute:[GDataXMLNode attributeWithName:@"id" stringValue:@"0"]];
            [cNvPr addAttribute:[GDataXMLNode attributeWithName:@"name" stringValue:@""]];
            GDataXMLElement *cNvPicPr = [GDataXMLNode elementWithName:@"pic:cNvPicPr"];
            GDataXMLElement *picLocks = [GDataXMLNode elementWithName:@"a:picLocks"];
            [picLocks addAttribute:[GDataXMLNode attributeWithName:@"noChangeAspect" stringValue:@"1"]];
            [picLocks addAttribute:[GDataXMLNode attributeWithName:@"noChangeArrowheads" stringValue:@"1"]];
            GDataXMLElement *blipFill = [GDataXMLNode elementWithName:@"pic:blipFill"];
            GDataXMLElement *blip = [GDataXMLNode elementWithName:@"a:blip"];
            [blip addAttribute:[GDataXMLNode attributeWithName:@"r:embed" stringValue:[NSString stringWithFormat:@"rId%ld", (long)i + 8]]];
            GDataXMLElement *srcRect = [GDataXMLNode elementWithName:@"a:srcRect"];
            GDataXMLElement *stretch = [GDataXMLNode elementWithName:@"a:stretch"];
            GDataXMLElement *fillRect = [GDataXMLNode elementWithName:@"a:fitRect"];
            GDataXMLElement *spPr = [GDataXMLNode elementWithName:@"pic:spPr"];
            [spPr addAttribute:[GDataXMLNode attributeWithName:@"bwMode" stringValue:@"auto"]];
            GDataXMLElement *xfrm = [GDataXMLNode elementWithName:@"a:xfrm"];
            GDataXMLElement *off = [GDataXMLNode elementWithName:@"a:off"];
            [off addAttribute:[GDataXMLNode attributeWithName:@"x" stringValue:@"0"]];
            [off addAttribute:[GDataXMLNode attributeWithName:@"y" stringValue:@"0"]];
            GDataXMLElement *ext = [GDataXMLNode elementWithName:@"a:ext"];
            [ext addAttribute:[GDataXMLNode attributeWithName:@"cx" stringValue:[NSString stringWithFormat:@"%.0f", emuWidth]]];
            [ext addAttribute:[GDataXMLNode attributeWithName:@"cy" stringValue:[NSString stringWithFormat:@"%.0f", emuHeight]]];
            GDataXMLElement *prstGeom = [GDataXMLNode elementWithName:@"a:prstGeom"];
            [prstGeom addAttribute:[GDataXMLNode attributeWithName:@"prst" stringValue:@"rect"]];
            GDataXMLElement *avLst = [GDataXMLNode elementWithName:@"a:avLst"];
            GDataXMLElement *noFill1 = [GDataXMLNode elementWithName:@"a:noFill"];
            GDataXMLElement *ln = [GDataXMLNode elementWithName:@"a:ln"];
            GDataXMLElement *noFill2 = [GDataXMLNode elementWithName:@"a:noFill"];
            [ln addChild:noFill2];
            [prstGeom addChild:avLst];
            [xfrm addChild:off];
            [xfrm addChild:ext];
            [spPr addChild:xfrm];
            [spPr addChild:prstGeom];
            [spPr addChild:noFill1];
            [spPr addChild:ln];
            [stretch addChild:fillRect];
            [blipFill addChild:blip];
            [blipFill addChild:srcRect];
            [blipFill addChild:stretch];
            [cNvPicPr addChild:picLocks];
            [nvPicPr addChild:cNvPr];
            [nvPicPr addChild:cNvPicPr];
            [pic addChild:nvPicPr];
            [pic addChild:blipFill];
            [pic addChild:spPr];
            [graphicData addChild:pic];
            [graphic addChild:graphicData];
            [il addChild:extent];
            [il addChild:effectExtent];
            [il addChild:docPr];
            [cNvGraphicFramePr addChild:graphicFrameLocks];
            [il addChild:cNvGraphicFramePr];
            [il addChild:graphic];
            [drawing addChild:il];
            [r addChild:drawing];
            [p addChild:r];
            [body addChild:p];
            
            // New line
            p = [GDataXMLNode elementWithName:@"w:p"];
            [body addChild:p];
            
            // Get description
            NSString *textFilePath = [photoDirectory stringByAppendingPathComponent:FileDescriptionFile];
            NSString *description = [NSString stringWithContentsOfURL:[NSURL fileURLWithPath:textFilePath] encoding:NSUTF8StringEncoding error:nil];
            
            NSUInteger length = [description length];
            NSUInteger paraStart = 0, paraEnd = 0, contentsEnd = 0;
            NSRange rangeInWholeDesc;
            while (paraEnd < length)
            {
                [description getParagraphStart:&paraStart end:&paraEnd
                                   contentsEnd:&contentsEnd forRange:NSMakeRange(paraEnd, 0)];
                rangeInWholeDesc = NSMakeRange(paraStart, contentsEnd - paraStart);
                NSString *sentense = [description substringWithRange:rangeInWholeDesc];
                // Add description
                p = [GDataXMLNode elementWithName:@"w:p"];
                r = [GDataXMLNode elementWithName:@"w:r"];
                GDataXMLElement *t = [GDataXMLNode elementWithName:@"w:t" stringValue:sentense];
                [r addChild:t];
                [p addChild:r];
                [body addChild:p];
            }
            
            // New line
            p = [GDataXMLNode elementWithName:@"w:p"];
            [body addChild:p];
            
            // Document.xml.rels
            GDataXMLElement *relationship = [GDataXMLNode elementWithName:@"Relationship"];
            [relationship addAttribute:[GDataXMLNode attributeWithName:@"Id" stringValue:[NSString stringWithFormat:@"rId%ld", (long)i + 8]]];
            [relationship addAttribute:[GDataXMLNode attributeWithName:@"Type" stringValue:@"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"]];
            [relationship addAttribute:[GDataXMLNode attributeWithName:@"Target" stringValue:[NSString stringWithFormat:@"media/image%ld.png", (long)i]]];
            [documentRels.rootElement addChild:relationship];
        }
        
        // document.xml.rels
        NSString *modifiedPath = [documentsDirectory stringByAppendingPathComponent:@"document.xml.rels"];
        xmlData = documentRels.XMLData;
        [xmlData writeToFile:modifiedPath atomically:YES];
        [za addFileToZip:modifiedPath newname:@"word/_rels/document.xml.rels"];
        [fileManager removeItemAtPath:modifiedPath error:nil];
        
        // document.xml
        modifiedPath = [documentsDirectory stringByAppendingPathComponent:@"document.xml"];
        xmlData = document.XMLData;
        [xmlData writeToFile:modifiedPath atomically:YES];
        [za addFileToZip:modifiedPath newname:@"word/document.xml"];
        [fileManager removeItemAtPath:modifiedPath error:nil];
        
        [za CloseZipFile2];
        
        NSData *wordData = [NSData dataWithContentsOfFile:zipFilePath];
        // Remove file immediate
        [fileManager removeItemAtPath:zipFilePath error:nil];
        
        return wordData;
    }
    // Export html
    else
    {
        NSFileManager *fileManager = [NSFileManager defaultManager];
        
        GDataXMLElement *html = [GDataXMLNode elementWithName:@"html"];
        GDataXMLElement *head = [GDataXMLNode elementWithName:@"head"];
        GDataXMLElement *meta = [GDataXMLNode elementWithName:@"meta"];
        [meta addAttribute:[GDataXMLNode attributeWithName:@"http-equiv" stringValue:@"Content-Type"]];
        [meta addAttribute:[GDataXMLNode attributeWithName:@"content" stringValue:@"text/html; charset=utf-8"]];
        GDataXMLElement *title = [GDataXMLNode elementWithName:@"title" stringValue:@"Report issues"];
        GDataXMLElement *body = [GDataXMLNode elementWithName:@"body"];
        [head addChild:meta];
        [head addChild:title];
        [html addChild:head];
        
        // Insert System Environment
        if (_includeSystemEnvironment)
        {
            NSArray *sysEnvLines = [self systemEnvironmentLines];
            for (int i = 0; i < 3; i++)
            {
                GDataXMLElement *p = [GDataXMLNode elementWithName:@"p" stringValue:[sysEnvLines objectAtIndex:i]];
                [body addChild:p];
            }
        }
        
        NSArray *imageList = [self listOfImages];
        for (NSInteger i = 0; i < imageList.count; i++)
        {
            NSString *photoDirectory = [imageList objectAtIndex:i];
            
            NSString *imageFileName = [photoDirectory stringByAppendingPathComponent:FileFlatDrawing];
            UIImage *image = [UIImage imageWithContentsOfFile:imageFileName];
            
            // Line break
            GDataXMLElement *p;
            for (int i = 0; i < 3; i++)
            {
                p = [GDataXMLNode elementWithName:@"br"];
                [body addChild:p];
            }
            
            // Image
            GDataXMLElement *img = [GDataXMLNode elementWithName:@"img"];
            [img addAttribute:[GDataXMLNode attributeWithName:@"src" stringValue:[NSString stringWithFormat:@"data:image/png;base64,%@",
                                                                                  [Utilities btoa:UIImagePNGRepresentation(image)]]]];
            p = [GDataXMLNode elementWithName:@"p"];
            [p addChild:img];
            [body addChild:p];
            
            // Description
            NSString *textFilePath = [photoDirectory stringByAppendingPathComponent:FileDescriptionFile];
            NSString *description = [NSString stringWithContentsOfURL:[NSURL fileURLWithPath:textFilePath] encoding:NSUTF8StringEncoding error:nil];
            
            NSUInteger length = [description length];
            NSUInteger paraStart = 0, paraEnd = 0, contentsEnd = 0;
            NSRange rangeInWholeDesc;
            while (paraEnd < length)
            {
                [description getParagraphStart:&paraStart end:&paraEnd
                                   contentsEnd:&contentsEnd forRange:NSMakeRange(paraEnd, 0)];
                rangeInWholeDesc = NSMakeRange(paraStart, contentsEnd - paraStart);
                NSString *sentense = [description substringWithRange:rangeInWholeDesc];
                // Add description
                p = [GDataXMLNode elementWithName:@"p" stringValue:sentense];
                [body addChild:p];
            }
            
            // Audio
            NSString *audioFilePath = [photoDirectory stringByAppendingPathComponent:FileAudioFile];
            if([fileManager fileExistsAtPath:audioFilePath])
            {
                NSData *data = [[NSFileManager defaultManager] contentsAtPath:audioFilePath];
                GDataXMLElement *audio = [GDataXMLNode elementWithName:@"audio"];
                [audio addAttribute:[GDataXMLNode attributeWithName:@"src" stringValue:[NSString stringWithFormat:@"data:audio/x-m4a;base64,%@", [Utilities btoa:data]]]];
                [audio addAttribute:[GDataXMLNode attributeWithName:@"controls" stringValue:@"controls"]];
                [audio addAttribute:[GDataXMLNode attributeWithName:@"preload" stringValue:@"auto"]];
                p = [GDataXMLNode elementWithName:@"p"];
                [p addChild:audio];
                [body addChild:p];
            }
            else
            {
                NSLog(@"Audio file not exits");
            }
        }
        
        [html addChild:body];
        GDataXMLDocument *document = [[GDataXMLDocument alloc] initWithRootElement:html];
        
        return document.XMLData;
    }
    
    return nil;
}

- (NSString *)attachedMimeType
{
    if (_formatWhenExport == EXPORT_FORMAT_PDF)
    {
        return @"application/pdf";
    }
    else if (_formatWhenExport == EXPORT_FORMAT_PDF)
    {
        return @"application/msword";
    }
    
    return @"txt/html";
}

- (NSString *)attachedFileName
{
    if (_formatWhenExport == EXPORT_FORMAT_PDF)
    {
        return @"Issues.pdf";
    }
    else if (_formatWhenExport == EXPORT_FORMAT_PDF)
    {
        return @"Issues.docx";
    }
    
    return @"Issues.html";
}

- (IBAction)sendMailButtonPressed:(id)sender
{
    if ([MFMailComposeViewController canSendMail])
    {
        MFMailComposeViewController *mailer = [[MFMailComposeViewController alloc] init];
        [mailer addAttachmentData:[self attachedData] mimeType:[self attachedMimeType] fileName:[self attachedFileName]];
        mailer.mailComposeDelegate = self;
        [mailer setSubject:@"Report Issues"];
        NSString *emailBody = @"";
        [mailer setMessageBody:emailBody isHTML:NO];
        
        [self presentModalViewController:mailer animated:YES];
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Emailing"
                                                        message:@"Your device doesn't support the email composer"
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
    }
}

- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error
{
    switch (result)
    {
        case MFMailComposeResultCancelled:
            NSLog(@"Mail cancelled: you cancelled the operation and no email message was queued.");
            break;
        case MFMailComposeResultSaved:
            NSLog(@"Mail saved: you saved the email message in the drafts folder.");
            break;
        case MFMailComposeResultSent:
            NSLog(@"Mail send: the email message is queued in the outbox. It is ready to send.");
            break;
        case MFMailComposeResultFailed:
            NSLog(@"Mail failed: the email message was not saved or queued, possibly due to an error.");
            break;
        default:
            NSLog(@"Mail not sent.");
            break;
    }

    // Remove the mail view
    [controller dismissModalViewControllerAnimated:YES];
}

#pragma mark - Rotation methods
// This method was deprecated in iOS 6
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if (IS_IPAD())
    {
        return YES;
    }
    
    return (interfaceOrientation == UIInterfaceOrientationPortrait || interfaceOrientation == UIInterfaceOrientationPortraitUpsideDown);
}

#pragma mark Orientation in iOS 6.0
// Apple replaced shouldAutorotateToInterfaceOrientation method with 02 other methods: supportedInterfaceOrientations, shouldAutorotate
- (NSUInteger)supportedInterfaceOrientations
{
    if (IS_IPAD())
    {
        return UIInterfaceOrientationMaskAll;
    }
    
    return UIInterfaceOrientationPortrait | UIInterfaceOrientationPortraitUpsideDown;
}

- (BOOL)shouldAutorotate
{
    if (IS_IPAD())
    {
        return YES;
    }

    return NO;
}


-(void)setDataSource
{
    [self resetContentView];
    
    _images = [Utilities getImagesFromProjectDirectory:_projectDirectory];
    
    [self setScrollViewContentSize];
    
    // Set up the array to hold the views for each image
    _imageViews = [[NSMutableArray alloc] init];
    
    
    for (NSInteger i = 0; i < _images.count; ++i)
    {
        [_imageViews addObject:[NSNull null]];
    }
    
    [self loadVisibleImagesFromImageIndex:(NSInteger)floor(_scrollView.contentOffset.y / _imageHeight) toImageIndex:(NSInteger)floor((_scrollView.contentOffset.y + _scrollView.frame.size.height) / _imageHeight)];
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
    
    if ((NSNull*)imageView == [NSNull null])
    {
        CGRect frame = _scrollView.bounds;
        frame.origin.x = 0.0f;
        frame.origin.y = _imageHeight * index;
        frame.size.height = _imageHeight;
        frame = CGRectInset(frame, 0.0f, 5.0f);
        
        imageView = [[ImageView alloc] init];
        imageView.imageViewIndicator.image = [UIImage imageNamed:@"OK.png"];
        imageView.indicatorWidth = 24;
        imageView.indicatorHeight = 24;
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        imageView.frame = frame;
        
        [_scrollView addSubview:imageView];
        [_imageViews replaceObjectAtIndex:index withObject:imageView];
        
        [self updateImageView:imageView withImageFromDirectory:imageDirectory];
    }
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
    if ((NSNull*)imageView != [NSNull null])
    {
        [imageView removeFromSuperview];
        [_imageViews replaceObjectAtIndex:index withObject:[NSNull null]];
    }
}

- (void)setScrollViewContentSize
{
    // Set up the content size of the scroll view
    CGSize imagesScrollViewSize = _scrollView.frame.size;
    _scrollView.contentSize = CGSizeMake(imagesScrollViewSize.width, _imageHeight * _images.count);
}

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

- (void)updateImageView:(ImageView *)imageView withImageFromDirectory:(NSString *)imageDirectory
{
    UIImage *cachedImage = [_imageCache objectForKey:imageDirectory];
    if (cachedImage)
    {
        [imageView setImageViewWithImage:cachedImage];
        if ([_choosenImagesList indexOfObject:imageDirectory] != NSNotFound)
        {
            imageView.imageViewIndicator.hidden = NO;
        }
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
                        if ([_choosenImagesList indexOfObject:imageDirectory] != NSNotFound)
                        {
                            imageView.imageViewIndicator.hidden = NO;
                        }
                        
                        [_imageCache setObject:image forKey:imageDirectory];
                        
                    });
                }
            }
        });
    }
}

- (void)handleTapOutside
{
    if (_isShow)
    {
        [self hide];
        _isShow = NO;
    }
}

- (void)handleTap
{
    if (_isShow)
    {
        [self hide];
    }
    else
    {
        [self show];
    }
    
    _isShow = !_isShow;
}


- (void)handlePan:(UIPanGestureRecognizer *)recognizer
{
    if ([recognizer state] == UIGestureRecognizerStateBegan)
    {
        _locationBeforePan = _imagesListView.center;
    }
    
    if ([recognizer state] == UIGestureRecognizerStateBegan || [recognizer state] == UIGestureRecognizerStateChanged)
    {
        CGPoint translation = [recognizer translationInView:[recognizer.view superview]];
        
        [_imagesListView setCenter:CGPointMake([_imagesListView center].x + translation.x, [_imagesListView center].y)];
        
        if (_isShow)
        {
            if (_locationBeforePan.x - _imagesListView.center.x > 0.0f)
            {
                [_imagesListView setCenter:CGPointMake(_locationBeforePan.x, [_imagesListView center].y)];
            }
            else if (_locationBeforePan.x - _imagesListView.center.x < -100.0f)
            {
                [_imagesListView setCenter:CGPointMake(_locationBeforePan.x + 100.0f, [_imagesListView center].y)];
            }
        }
        else
        {
            if (_locationBeforePan.x - _imagesListView.center.x < 0.0f)
            {
                [_imagesListView setCenter:CGPointMake(_locationBeforePan.x, [_imagesListView center].y)];
            }
            else if (_locationBeforePan.x - _imagesListView.center.x > 100.0f)
            {
                [_imagesListView setCenter:CGPointMake(_locationBeforePan.x - 100.0f, [_imagesListView center].y)];
            }
        }
        
        [recognizer setTranslation:CGPointZero inView:[_imagesListView superview]];
    }
    else if ([recognizer state] == UIGestureRecognizerStateEnded)
    {
        if (_isShow)
        {
            if (_imagesListView.center.x - _locationBeforePan.x > 50.0f)
            {
                [_imagesListView setCenter:CGPointMake(_locationBeforePan.x + 100.0f, [_imagesListView center].y)];
                _isShow = NO;
            }
            else
            {
                [_imagesListView setCenter:CGPointMake(_locationBeforePan.x, [_imagesListView center].y)];
            }
        }
        else
        {
            if (_locationBeforePan.x - _imagesListView.center.x > 50.0f)
            {
                [_imagesListView setCenter:CGPointMake(_locationBeforePan.x - 100.0f, [_imagesListView center].y)];
                _isShow = YES;
            }
            else
            {
                [_imagesListView setCenter:CGPointMake(_locationBeforePan.x, [_imagesListView center].y)];
            }
        }
    }
}

#pragma -mark UIScrollViewDelegate

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    [self loadVisibleImagesFromImageIndex:(NSInteger)floor(_scrollView.contentOffset.y / _imageHeight) toImageIndex:(NSInteger)floor((_scrollView.contentOffset.y + _scrollView.frame.size.height) / _imageHeight)];
}


@end
