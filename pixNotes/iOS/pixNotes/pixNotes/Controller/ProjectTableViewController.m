//
//  ProjectTableViewController.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 1/4/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "ProjectTableViewController.h"
#import "ProjectCell.h"
#import "Utilities.h"
#import "Constants.h"
#import "ProjectSettings.h"


static NSString * const CellIdentifier = @"Cell";

@implementation ProjectTableViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [_tableView registerNib:[ProjectCell nib] forCellReuseIdentifier:CellIdentifier];
    [self setupTableView];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    _tableView = nil;
    _projects = nil;
}

- (void)dealloc
{
    _tableView = nil;
    _projects = nil;
}

- (void)setupTableView
{
    _projects = [[NSMutableDictionary alloc] init];
    for (NSString *path in [Utilities getProjectsPathList])
    {
        NSString *projectSettingsPath = [path stringByAppendingPathComponent:FileProjectSettingsFile];
        ProjectSettings *projectSettings = [NSKeyedUnarchiver unarchiveObjectWithFile:projectSettingsPath];
        [_projects setObject:projectSettings.projectName forKey:path];
    }
}

- (id)itemAtIndexPath:(NSIndexPath *)indexPath
{
    return [_projects objectForKey:[[_projects allKeys] objectAtIndex:indexPath.row]];
}

#pragma mark UITableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _projects.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ProjectCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    id item = [self itemAtIndexPath:indexPath];
    [cell loadContentWithName:item];
    return cell;
}


#pragma mark UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    [[NSNotificationCenter defaultCenter] postNotificationName:NSNotificationSelectProject object:[[_projects allKeys] objectAtIndex:indexPath.row]];
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    [cell setBackgroundColor:[UIColor clearColor]];
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete)
    {
        NSString *projectDirectory = [[_projects allKeys] objectAtIndex:indexPath.row];
        [[NSFileManager defaultManager] removeItemAtPath:projectDirectory error:nil];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:NSNotificationDeleteProject
                                                            object:[[_projects allKeys] objectAtIndex:indexPath.row]];
        
        [self setupTableView];
        [_tableView reloadData];
    }
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return self.editing;
}

@end
