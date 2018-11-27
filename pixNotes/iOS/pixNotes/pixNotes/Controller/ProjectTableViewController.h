//
//  ProjectTableViewController.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 1/4/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ProjectTableViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, strong) NSMutableDictionary *projects;
@property (nonatomic, strong) IBOutlet UITableView *tableView;

- (void)setupTableView;

@end
