//
//  ProgressController.h
//  pixNotes
//
//  Created by Tuyen Le on 2/24/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ProgressController : UIViewController

+ (ProgressController *)sharedInstance;
- (void)present;
- (void)dismiss;

@end
