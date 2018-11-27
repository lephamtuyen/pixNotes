//
//  LeftViewController.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "AbtractViewController.h"

@interface LeftViewController : AbtractViewController <UITableViewDelegate, UITableViewDataSource>
{
    NSArray *_items;
}

@property (nonatomic, strong) IBOutlet UITableView *tableView;
- (void)setupTableView;

@end
