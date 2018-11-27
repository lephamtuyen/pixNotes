//
//  Arrow.h
//  pixNotes
//
//  Created by Tuyen Le on 12/23/13.
//  Copyright (c) 2013 KMS Technology Vietnam. All rights reserved.
//

#import "DrawingShape.h"

@interface Arrow : DrawingShape

// Only for arrow
@property (assign) CGPoint tail;
@property (assign) CGPoint head;

- (void)computeHeadAndTailOfArrowWithPointsArray:(NSArray *)pointsArray;
@end
