//
//  LeftViewController.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 11/16/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "LeftViewController.h"
#import "MainViewController.h"
#import "LeftControllerItemCell.h"


@implementation LeftViewController

static NSString * const CellIdentifier = @"Cell";

#pragma mark - UIViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [_tableView registerNib:[LeftControllerItemCell nib] forCellReuseIdentifier:CellIdentifier];
    [self setupTableView];
}

- (void)setupTableView
{
    _items = [[NSArray alloc] initWithObjects:
              [[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithBool:YES], CELL_NAME_NEW_PROJECT, nil],
              [[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithBool:YES], CELL_NAME_OPEN_PROJECTS, nil],
              [[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithBool:YES], CELL_NAME_MANAGE_PROJECTS, nil],
              [[NSDictionary alloc] initWithObjectsAndKeys:[NSNumber numberWithBool:[self.delegate getCurrentProjectDirectory] ? YES : NO], CELL_NAME_SETTINGS, nil],
              nil];
}

- (id)itemAtIndexPath:(NSIndexPath *)indexPath
{
    return _items[(NSUInteger) indexPath.row];
}

#pragma mark UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _items.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    LeftControllerItemCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    id item = [self itemAtIndexPath:indexPath];
    [cell loadContentWithName:item];
    return cell;
}


#pragma mark UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    if (self.delegate && [self.delegate respondsToSelector:@selector(viewController:choosenControllerIndex:)])
    {
        [self.delegate viewController:self choosenControllerIndex:indexPath.row];
    }
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    [cell setBackgroundColor:[UIColor clearColor]];
}

@end
