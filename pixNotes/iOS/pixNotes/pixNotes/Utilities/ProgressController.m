//
//  ProgressController.m
//  pixNotes
//
//  Created by Tuyen Le on 2/24/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "ProgressController.h"


@implementation ProgressController

+ (ProgressController *)sharedInstance
{
    static ProgressController *sharedInstance = nil;
    if (sharedInstance == nil)
    {
        sharedInstance = [[ProgressController alloc] init];
    }
    return sharedInstance;
}

- (void)present
{
    self.view.frame = [[UIScreen mainScreen] bounds];
    UIWindow *appWindow = [UIApplication.sharedApplication.windows lastObject];
    [appWindow addSubview:self.view];
    
    self.view.alpha = 0;
    [UIView animateWithDuration:0.5
                     animations:^{
                         self.view.alpha = 0.5;
                     }
                     completion:^(BOOL finished){
                     }];
}


- (void)dismiss
{
    [UIView animateWithDuration:0.5
                     animations:^{
                         self.view.alpha = 0;
                     }
                     completion:^(BOOL finished){
                         [self.view removeFromSuperview];
                     }];
}

@end
