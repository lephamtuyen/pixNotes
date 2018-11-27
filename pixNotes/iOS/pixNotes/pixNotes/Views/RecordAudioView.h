//
//  RecordAudioView.h
//  pixNotes
//
//  Created by Tuyen Le on 2/12/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreAudio/CoreAudioTypes.h>
#import <AVFoundation/AVFoundation.h>

@class RecordAudioView;


@protocol RecordAudioViewDelegate <NSObject>

- (void)closeRecordAudioView;

@end

@interface RecordAudioView : UIView
{
    CGFloat _contentWidth;
    CGFloat _contentHeight;
    
    int _currMinute;
    int _currSeconds;
    
    NSString *_imageDirectory;
    
    BOOL _isPause;
}

@property (nonatomic, strong) id<RecordAudioViewDelegate>delegate;
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIView *tapView;

@property (nonatomic, strong) UILabel *recordIndicator;
@property (nonatomic, strong) UILabel *timeIndicator;
@property (nonatomic, strong) UIButton *recordBtn;
@property (nonatomic, strong) UIButton *stopBtn;
@property (nonatomic, strong) UIButton *playBtn;
@property (nonatomic, strong) UIButton *deleteBtn;

@property (strong, nonatomic) AVAudioRecorder *audioRecorder;
@property (strong, nonatomic) AVAudioPlayer *audioPlayer;

@property (strong, nonatomic) NSTimer *recordTimer;

- (void)loadImageDirectory:(NSString *)imageDirectory;

@end
