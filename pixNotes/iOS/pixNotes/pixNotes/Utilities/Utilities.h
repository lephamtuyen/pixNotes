//
//  Utilities.h
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/1/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Utilities : NSObject

+ (NSMutableArray *)getImagesFromProjectDirectory:(NSString *)projectDirectory;
+ (NSString *)documentsDirectory;
+ (CGSize)getRealTargetSizeFromImage:(UIImage *)image fitWithSize:(CGSize)size;
+ (NSArray *)getProjectsPathList;
+ (void)importImage:(UIImage *)flatImage toProjectDirectory:(NSString *)projectDirectory;
+ (void)removeImageAtDirectory:(NSString *)imageDirectory;
+ (void)reorderImage:(NSString *)imageDirectory toIndex:(NSInteger)toIndex;
+ (NSString *)getImageDirectoryFromProjectDirectory:(NSString *)projectDirectory nextToIndex:(NSInteger)removedIndex;
+ (NSString *)getFirstImageFromProjectDirectory:(NSString *)projectDirectory;
+ (NSString *) btoa: (NSData *)data;

@end
