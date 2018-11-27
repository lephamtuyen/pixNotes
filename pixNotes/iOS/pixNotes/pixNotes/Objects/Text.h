//
//  Text.h
//  pixNotes
//
//  Created by Tuyen Le on 12/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "DrawingShape.h"

@interface Text : DrawingShape

// Only for text
@property (strong) NSString *text;
@property (assign) CGFloat fontSize;

@end
