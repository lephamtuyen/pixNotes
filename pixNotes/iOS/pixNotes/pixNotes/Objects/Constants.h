//
//  Constants.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/14/13.
//  Copyright (c) 2013 KMS Technology. All rights reserved.
//

#ifndef pixNotes_Constants_h
#define pixNotes_Constants_h

static NSString* const FileShapesFile           =  @"s";
static NSString* const FileImageNamesFile       =  @"imageName";
static NSString* const FileFlatDrawing          =  @"flat.png";
static NSString* const FileOriginDrawing        =  @"origin.png";
static NSString* const FileDrawingsDirectory    =  @"drawings";
static NSString* const FileDescriptionFile      =  @"description.txt";
static NSString* const FileAudioFile            =  @"audio.m4a";
static NSString* const FileProjectSettingsFile      =  @"settings";

static NSString* const NSNotificationSelectProject =  @"NSNotificationSelectProject";
static NSString* const NSNotificationDeleteProject =  @"NSNotificationDeleteProject";

#define SMALL_FONT              8.0f
#define MEDIUM_FONT             20.0f
#define LARGER_FONT             32.0f

#define SMALL_BORDER            1.0f
#define MEDIUM_BORDER           2.5f
#define LARGER_BORDER           4.0f

#define GRID_LINE_WIDTH         0.175f

#define GRID_STYLE_DIAGONAL     (0)
#define GRID_STYLE_COLUMN       (1)
#define GRID_STYLE_ROW          (2)
#define GRID_STYLE_SQUARE       (3)

#define EXPORT_FORMAT_PDF       (2)
#define EXPORT_FORMAT_WORD      (1)
#define EXPORT_FORMAT_HTML      (0)

#define SEND_ALL                (0)
#define SEND_ONE                (1)
#define SEND_MULTI              (2)

#define STANDARD_BLUR           0.3f

#define IS_IPAD()                                                               ([[UIDevice currentDevice] respondsToSelector:@selector(userInterfaceIdiom)] && \
[[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad)

#endif
