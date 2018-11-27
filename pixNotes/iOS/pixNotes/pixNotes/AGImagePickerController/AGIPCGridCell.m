//
//  AGIPCGridCell.m
//  AGImagePickerController
//
//  Created by Artur Grigor on 17.02.2012.
//  Copyright (c) 2012 - 2013 Artur Grigor. All rights reserved.
//  
//  For the full copyright and license information, please view the LICENSE
//  file that was distributed with this source code.
//  

#import "AGIPCGridCell.h"
#import "AGIPCGridItem.h"

#import "AGImagePickerController.h"
#import "AGImagePickerController+Helper.h"

@interface AGIPCGridCell ()
{
	NSArray *_items;
    AGImagePickerController *_imagePickerController;
}

@end

@implementation AGIPCGridCell

#pragma mark - Properties

@synthesize items = _items, imagePickerController = _imagePickerController;

- (void)setItems:(NSArray *)items
{
    @synchronized (self)
    {
        if (_items != items)
        {
            for (UIView *view in self.items)
            {		
                [view removeFromSuperview];
            }
            
            _items = items;
            
            [self addNewSubView];
        }
    }
}

- (NSArray *)items
{
    NSArray *array = nil;
    
    @synchronized (self)
    {
        array = _items;
    }
    
    return array;
}

- (void)addNewSubView
{
    for (AGIPCGridItem *gridItem in self.items)
    {
        gridItem.thumbnailImageView.image = [UIImage imageWithCGImage:gridItem.asset.thumbnail];
        gridItem.checkmarkImageView.image = [UIImage imageNamed:@"OK.png"];
        UITapGestureRecognizer *tapToSelectGesture = [[UITapGestureRecognizer alloc] initWithTarget:gridItem action:@selector(tap)];
        tapToSelectGesture.numberOfTapsRequired = 1;
		[gridItem addGestureRecognizer:tapToSelectGesture];
        
        [self.contentView addSubview:gridItem];
	}
}


#pragma mark - Object Lifecycle

- (id)initWithImagePickerController:(AGImagePickerController *)imagePickerController andReuseIdentifier:(NSString *)identifier
{
    self = [super initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    if (self)
    {
        self.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        self.autoresizesSubviews = YES;
        self.contentView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        self.contentView.autoresizesSubviews = YES;
        self.imagePickerController = imagePickerController;
	}
	
	return self;
}


- (void)layoutSubviews
{
    CGRect frame = self.imagePickerController.itemRect;
    CGFloat leftMargin = frame.origin.x;
    for (AGIPCGridItem *gridItem in self.items)
    {
        [gridItem setFrame:frame];
		frame.origin.x = frame.origin.x + frame.size.width + leftMargin;
	}
}

@end
