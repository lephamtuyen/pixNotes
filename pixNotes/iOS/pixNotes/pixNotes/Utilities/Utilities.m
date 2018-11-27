//
//  Utilities.m
//  pixNotes
//
//  Created by Tuyen Pham Le on 12/1/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "Utilities.h"
#import "Constants.h"
#import "UIImage+FixOrientation.h"
#import "UIImage+Resize.h"
#import "NSString+UUID.h"

@implementation Utilities


+ (NSArray *)getProjectsPathList
{
    NSMutableArray *projectsList = [[NSMutableArray alloc] init];
    NSString *projectsDirectory = [self documentsDirectory];
    
    NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:projectsDirectory error:nil];
    
    for (NSString *file in files)
    {
        NSString *fullPath = [projectsDirectory stringByAppendingPathComponent:file];
        [projectsList addObject:fullPath];
    }
    
    // reverse sort the drawings by modified date
    return [projectsList sortedArrayUsingComparator:^NSComparisonResult(id nameA, id nameB) {
        
        NSString *filePathA = [(NSString*)nameA stringByAppendingPathComponent:FileProjectSettingsFile];
        NSString *filePathB = [(NSString*)nameB stringByAppendingPathComponent:FileProjectSettingsFile];
        
        NSDate *modDateA = [self modDateForFileAtPath:filePathA];
        NSDate *modDateB = [self modDateForFileAtPath:filePathB];
        
        return [modDateB compare:modDateA];
    }];
}


+ (NSDate*)modDateForFileAtPath:(NSString*)filePath {
    
    NSDictionary *properties = [[NSFileManager defaultManager]
                                attributesOfItemAtPath:filePath
                                error:nil];
    return properties[NSFileModificationDate];
}


+ (NSString *)getFirstImageFromProjectDirectory:(NSString *)projectDirectory
{
    NSArray *imageList = [NSKeyedUnarchiver unarchiveObjectWithFile:[projectDirectory stringByAppendingPathComponent:FileImageNamesFile]];
    if (imageList.count != 0)
    {
        return [imageList firstObject];
    }
    
    return nil;
}


+ (NSMutableArray *)getImagesFromProjectDirectory:(NSString *)projectDirectory
{
    return [NSKeyedUnarchiver unarchiveObjectWithFile:[projectDirectory stringByAppendingPathComponent:FileImageNamesFile]];
//    NSMutableArray *imageArray = [[NSMutableArray alloc] init];
//    
//    NSArray *files = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:projectDirectory error:nil];
//    
//    for (NSString *file in files)
//    {
//        BOOL isDirectory;
//        NSString *fullPath = [projectDirectory stringByAppendingPathComponent:file];
//        [[NSFileManager defaultManager] fileExistsAtPath:fullPath isDirectory:&isDirectory];
//        
//        if (isDirectory)
//        {
//            [imageArray addObject:fullPath];
//        }
//    }
//    
//    return imageArray;
}


+ (NSString *)getImageDirectoryFromProjectDirectory:(NSString *)projectDirectory nextToIndex:(NSInteger)removedIndex
{
    NSArray *imagesList = [self getImagesFromProjectDirectory:projectDirectory];
    if (imagesList.count != 0)
    {
        if (removedIndex <= imagesList.count - 1)
        {
            return [imagesList objectAtIndex:removedIndex];
        }

        // Else case.
        return [imagesList objectAtIndex:removedIndex - 1];
    }
    
    return nil;
}

+ (void)reorderImage:(NSString *)imageDirectory toIndex:(NSInteger)toIndex
{
    NSString *imageFileName = [[imageDirectory stringByDeletingLastPathComponent] stringByAppendingPathComponent:FileImageNamesFile];
    NSMutableArray *imageNames = [[NSKeyedUnarchiver unarchiveObjectWithFile:imageFileName] mutableCopy];
    [imageNames removeObject:imageDirectory];
    [imageNames insertObject:imageDirectory atIndex:toIndex];
    [NSKeyedArchiver archiveRootObject:imageNames toFile:imageFileName];
}

+ (void)removeImageAtDirectory:(NSString *)imageDirectory
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    [fileManager removeItemAtPath:imageDirectory error:nil];
    
    NSString *imageFileName = [[imageDirectory stringByDeletingLastPathComponent] stringByAppendingPathComponent:FileImageNamesFile];
    NSMutableArray *imageNames = [[NSKeyedUnarchiver unarchiveObjectWithFile:imageFileName] mutableCopy];
    [imageNames removeObject:imageDirectory];
    [NSKeyedArchiver archiveRootObject:imageNames toFile:imageFileName];
}

+ (CGSize)getRealTargetSizeFromImage:(UIImage *)image fitWithSize:(CGSize)size
{
    CGFloat imageFactor = image.size.width / image.size.height;
    CGFloat viewFactor = size.width / size.height;
    if (size.width > image.size.width && size.height > image.size.height)
    {
        return image.size;
    }

    if (imageFactor > viewFactor)
    {
        return CGSizeMake(size.width, size.width / imageFactor);
    }
    
    // Else case
    return CGSizeMake(size.height * imageFactor, size.height);
}


+ (void)importImage:(UIImage *)flatImage toProjectDirectory:(NSString *)projectDirectory
{
    CGSize realTargetSize = [Utilities getRealTargetSizeFromImage:flatImage fitWithSize:[UIScreen mainScreen].bounds.size];
    
    NSString *imageDirectory = [projectDirectory stringByAppendingPathComponent:[NSString UUIDString]];
    NSString *imageFileName = [projectDirectory stringByAppendingPathComponent:FileImageNamesFile];
    NSMutableArray *imageNames = [[NSKeyedUnarchiver unarchiveObjectWithFile:imageFileName] mutableCopy];
    [imageNames addObject:imageDirectory];
    
    [NSKeyedArchiver archiveRootObject:imageNames toFile:imageFileName];
    UIImage *saveImage = [[flatImage fixOrientation] resizedImage:realTargetSize interpolationQuality:kCGInterpolationDefault];
    NSData *photoData = UIImagePNGRepresentation(saveImage);
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    [fileManager createDirectoryAtPath:imageDirectory withIntermediateDirectories:YES attributes:nil error:NULL];
    
    // Save origin image
    NSString *originImageName = [imageDirectory stringByAppendingPathComponent:FileOriginDrawing];
    [photoData writeToFile:originImageName atomically:YES];
    
    // Save Drawing Description
    NSString *description = @"";
    NSString *textFilePath = [imageDirectory stringByAppendingPathComponent:FileDescriptionFile];
    [description writeToFile:textFilePath atomically:YES encoding:NSUTF8StringEncoding error:nil];
    
    // Save flat image
    NSString *photoFileName = [imageDirectory stringByAppendingPathComponent:FileFlatDrawing];
    [photoData writeToFile:photoFileName atomically:YES];
}

+ (NSString*)documentsDirectory
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask ,YES);
    return paths[0];
}

+ (NSString *) btoa: (NSData *)data {
    int i, j;
    int length;
    char *ascii;
    const char *binary;
    char *b;
    int c;
    int part1, part2, part3, part4;
    NSString *text;
    
    length = [data length];
    ascii = (char *)malloc (length * 2 + 1);
    binary = (const char *)[data bytes];
    b = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    
    for (i = 0, j = 0; i < length; i += 3) {
        c = (unsigned char)binary [i];
        part1 = c >> 2;
        part2 = (c & 0x3) << 4;
        if (i + 1 == length) {
            part3 = 64;
            part4 = 64;
        }
        else {
            c = binary [i + 1];
            part2 |= (c & 0xf0) >> 4;
            part3 = (c & 0x0f) << 2;
            if (i + 2 == length) {
                part4 = 64;
            }
            else {
                c = binary [i + 2];
                part3 |= (c & 0xc0) >> 6;
                part4 = c & 0x3f;
            }
        }
        ascii [j] = b [part1];
        j ++;
        ascii [j] = b [part2];
        j ++;
        ascii [j] = b [part3];
        j ++;
        ascii [j] = b [part4];
        j ++;
    }
    
    text
    = [[NSString alloc]
       initWithBytes: ascii
       length: j
       encoding: NSASCIIStringEncoding];
    free (ascii);
    
    return text;
}

@end
