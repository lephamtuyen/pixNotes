//
//  GridView.h
//  pixNotes
//
//  Created by Tuyen Le on 11/26/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol GridViewDataSource;

@interface GridView : UIView
{
    IBOutlet id <GridViewDataSource> _dataSource;
}
@end

@protocol GridViewDataSource <NSObject>

@required
- (BOOL)isExistedDrawing;

@end