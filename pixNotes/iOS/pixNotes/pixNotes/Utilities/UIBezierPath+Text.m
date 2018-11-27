//
//  UIBezierPath+Text.m
//  pixNotes
//
//  Created by Tuyen Le on 12/20/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "UIBezierPath+Text.h"

@implementation UIBezierPath (Text)

+ (UIBezierPath *)drawText:(NSString*)textToDraw atPoint:(CGPoint)textPoint fontSize:(CGFloat)fontSize
{
    // Create path from text
    // See: http://www.codeproject.com/KB/iPhone/Glyph.aspx
    // License: The Code Project Open License (CPOL) 1.02 http://www.codeproject.com/info/cpol10.aspx
    CGMutablePathRef letters = CGPathCreateMutable();
    CGAffineTransform trans;
    CTFontRef font = CTFontCreateWithName(CFSTR("HelveticaNeue-Bold"), fontSize, NULL);
    NSDictionary *attrs = [NSDictionary dictionaryWithObjectsAndKeys:(__bridge id)font, kCTFontAttributeName, nil];
    NSAttributedString *attrString = [[NSAttributedString alloc] initWithString:textToDraw
                                                                     attributes:attrs];
    CTLineRef line = CTLineCreateWithAttributedString((CFAttributedStringRef)attrString);
	CFArrayRef runArray = CTLineGetGlyphRuns(line);
    // for each RUN
    for (CFIndex runIndex = 0; runIndex < CFArrayGetCount(runArray); runIndex++)
    {
        // Get FONT for this run
        CTRunRef run = (CTRunRef)CFArrayGetValueAtIndex(runArray, runIndex);
        CTFontRef runFont = CFDictionaryGetValue(CTRunGetAttributes(run), kCTFontAttributeName);
        
        // for each GLYPH in run
        for (CFIndex runGlyphIndex = 0; runGlyphIndex < CTRunGetGlyphCount(run); runGlyphIndex++)
        {
            // get Glyph & Glyph-data
            CFRange thisGlyphRange = CFRangeMake(runGlyphIndex, 1);
            CGGlyph glyph;
            CGPoint position;
            CTRunGetGlyphs(run, thisGlyphRange, &glyph);
            CTRunGetPositions(run, thisGlyphRange, &position);
            
            // Get PATH of outline
            {
                CGPathRef reverseLetter = CTFontCreatePathForGlyph(runFont, glyph, NULL);
                trans = CGAffineTransformMakeTranslation(position.x, position.y);
                CGPathRef tempLetterPath = CGPathCreateCopyByTransformingPath(reverseLetter,
                                                                                      &trans);
                CGPathRelease(reverseLetter);
                
                trans = CGAffineTransformMakeScale(1, -1);
                CGPathAddPath(letters, &trans, tempLetterPath);
                
                CGPathRelease(tempLetterPath);
            }
        }
    }
    CFRelease(line);
    
    trans = CGAffineTransformMakeTranslation(textPoint.x, textPoint.y);
    CGPathRef tempLetterPath = CGPathCreateCopyByTransformingPath(letters,
                                                                          &trans);
    UIBezierPath *path = [UIBezierPath bezierPathWithCGPath:tempLetterPath];
    
    CGPathRelease(letters);
    CGPathRelease(tempLetterPath);
    CFRelease(font);
    
    return path;
}


@end
