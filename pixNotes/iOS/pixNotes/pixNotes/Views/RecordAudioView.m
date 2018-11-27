//
//  RecordAudioView.m
//  pixNotes
//
//  Created by Tuyen Le on 2/12/14.
//  Copyright (c) 2014 KMS Technology Vietnam. All rights reserved.
//

#import "RecordAudioView.h"
#import "Constants.h"
#import "Colors.h"

#define CONTENT_WIDTH       210
#define CONTENT_WIDTH_IPAD  210
#define CONTENT_HEIGHT      110
#define CONTENT_HEIGHT_IPAD 110

#define ICON_WIDTH          32


@implementation RecordAudioView

- (id)init
{
    self = [super init];
    if (self)
    {
        [self initSubviews];
    }
    return self;
}

- (void)initRecordIndicatorLabel
{
    _recordIndicator = [[UILabel alloc] init];
    [_recordIndicator setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:15]];
    _recordIndicator.textAlignment = UITextAlignmentCenter;
    [_recordIndicator setBackgroundColor:[UIColor clearColor]];
    [_recordIndicator setTextColor:RED_TEXT_COLOR];
    _recordIndicator.text = @"REC";
    _recordIndicator.hidden = YES;
    [self.contentView addSubview:_recordIndicator];
}

- (void)initTimeIndicatorLabel
{
    _timeIndicator = [[UILabel alloc] init];
    [_timeIndicator setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:24]];
    [_timeIndicator setBackgroundColor:[UIColor clearColor]];
    [_timeIndicator setTextColor:GRAY_TEXT_COLOR];
    _timeIndicator.textAlignment = UITextAlignmentCenter;
    _currMinute = 0;
    _currSeconds = 0;
    [self changeTimerLabel];
    [self.contentView addSubview:_timeIndicator];
}

- (void)initRecordButton
{
    _recordBtn = [[UIButton alloc] init];
    [_recordBtn setBackgroundColor:[UIColor clearColor]];
    [_recordBtn addTarget:self action:@selector(recordAudio) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:_recordBtn];
}

- (void)initStopRecordButton
{
    _stopBtn = [[UIButton alloc] init];
    [_stopBtn setBackgroundColor:[UIColor clearColor]];
    [_stopBtn setImage:[UIImage imageNamed:@"stop.png"] forState:UIControlStateNormal];
    [_stopBtn setImage:[UIImage imageNamed:@"stop_dis.png"] forState:UIControlStateDisabled];
    [_stopBtn addTarget:self action:@selector(stopAudio) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:_stopBtn];
}

- (void)initPlayButton
{
    _playBtn = [[UIButton alloc] init];
    [_playBtn setBackgroundColor:[UIColor clearColor]];
    [_playBtn setImage:[UIImage imageNamed:@"play.png"] forState:UIControlStateNormal];
    [_playBtn setImage:[UIImage imageNamed:@"play_dis.png"] forState:UIControlStateDisabled];
    [_playBtn addTarget:self action:@selector(playAudio) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:_playBtn];
}

- (void)initDeleteButton
{
    _deleteBtn = [[UIButton alloc] init];
    [_deleteBtn setBackgroundColor:[UIColor clearColor]];
    [_deleteBtn setImage:[UIImage imageNamed:@"deleted_sound.png"] forState:UIControlStateNormal];
    [_deleteBtn setImage:[UIImage imageNamed:@"deleted_sound_dis.png"] forState:UIControlStateDisabled];
    [_deleteBtn addTarget:self action:@selector(deleteAudio) forControlEvents:UIControlEventTouchUpInside];
    [_contentView addSubview:_deleteBtn];
}

- (void)initSubviews
{
    [self setBackgroundColor:[[UIColor blackColor] colorWithAlphaComponent:0.5]];
    
    _tapView = [[UIView alloc] init];
    [_tapView setBackgroundColor:[UIColor clearColor]];
    [self addSubview:_tapView];
    UITapGestureRecognizer *tapOutside = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapOutside:)];
    [_tapView addGestureRecognizer:tapOutside];
    
    _contentView = [[UIView alloc] init];
    [_contentView setBackgroundColor:BACKGROUND_POPUP_VIEW];
    _contentView.layer.cornerRadius = 10;
    _contentView.layer.masksToBounds = YES;
    [self addSubview:_contentView];
    
    [self initRecordIndicatorLabel];
    
    [self initTimeIndicatorLabel];
    
    [self initRecordButton];
    
    [self initStopRecordButton];
    
    [self initPlayButton];
    
    [self initDeleteButton];
}

- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)
    {
        _contentWidth = CONTENT_WIDTH;
        _contentHeight = CONTENT_HEIGHT;
    }
    else
    {
        _contentWidth = CONTENT_WIDTH_IPAD;
        _contentHeight = CONTENT_HEIGHT_IPAD;
    }
    
    [_tapView setFrame:CGRectMake(0, 0, frame.size.width, frame.size.height)];
    [_contentView setFrame:CGRectMake((self.frame.size.width - _contentWidth) / 2,
                                      (self.frame.size.height - _contentHeight) / 2,
                                      _contentWidth, _contentHeight)];
    
    [_recordIndicator setFrame:CGRectMake((_contentView.frame.size.width - 50) / 2, 8, 50, 15)];
    [_timeIndicator setFrame:CGRectMake((_contentView.frame.size.width - 70) / 2,
                                        _recordIndicator.frame.origin.y + _recordIndicator.frame.size.height,
                                        70, ICON_WIDTH)];
    [_recordBtn setFrame:CGRectMake(_contentView.frame.size.width / 13,
                                    _contentView.frame.size.height / 2,
                                    ICON_WIDTH, ICON_WIDTH)];
    [_playBtn setFrame:CGRectMake(4 * _contentView.frame.size.width / 13,
                                    _recordBtn.frame.origin.y,
                                    ICON_WIDTH, ICON_WIDTH)];
    [_stopBtn setFrame:CGRectMake(7 * _contentView.frame.size.width / 13,
                                    _recordBtn.frame.origin.y,
                                    ICON_WIDTH, ICON_WIDTH)];
    [_deleteBtn setFrame:CGRectMake(10 * _contentView.frame.size.width / 13,
                                    _recordBtn.frame.origin.y,
                                    ICON_WIDTH, ICON_WIDTH)];
}

- (void)handleTapOutside:(UITapGestureRecognizer *)recognizer
{
    CGPoint tapLocation = [recognizer locationInView:self];
    if( (tapLocation.x < _contentView.frame.origin.x
       || tapLocation.x > _contentView.frame.origin.x + _contentView.frame.size.width
       || tapLocation.y < _contentView.frame.origin.y
       || tapLocation.y > _contentView.frame.origin.y + _contentView.frame.size.height)
       && (!_audioRecorder || ![_audioRecorder isRecording]))
    {
        [_audioRecorder stop];
        [_audioPlayer stop];
        [self removeTimer];
        
        [_delegate closeRecordAudioView];
    }
}


- (void)updateTimerCountup
{
    _recordIndicator.hidden = !_recordIndicator.hidden;

    if (_currSeconds < 59)
    {
        _currSeconds += 1;
    }
    else
    {
        _currMinute += 1;
        _currSeconds = 0;
    }

    [self changeTimerLabel];
}

- (void)updateTimerCountdown
{
    if(_currSeconds > 0)
    {
        _currSeconds -= 1;
    }
    else
    {
        _currMinute -= 1;
        _currSeconds = 59;
    }
    
    if(_currMinute <= -1)
    {
        _currSeconds = 0;
        _currMinute = 0;
        
        [self removeTimer];
        [self updateTimerOfAudio];
        [self updateStateOfSubviews];
    }

    [self changeTimerLabel];
}

- (void)removeTimer
{
    [_recordTimer invalidate];
    _recordTimer = nil;
}

- (void)startTimer:(BOOL)isCountdown
{
    if (_recordTimer)
    {
        [_recordTimer invalidate];
        _recordTimer = nil;
    }
    
    if (isCountdown)
    {
        _recordTimer = [NSTimer scheduledTimerWithTimeInterval:1
                                                        target:self
                                                      selector:@selector(updateTimerCountdown)
                                                      userInfo:nil
                                                       repeats:YES];
    }
    else
    {
        _recordTimer = [NSTimer scheduledTimerWithTimeInterval:1
                                                        target:self
                                                      selector:@selector(updateTimerCountup)
                                                      userInfo:nil
                                                       repeats:YES];
    }
}

- (void)recordAudio
{
    if ([_audioPlayer isPlaying])
    {
        [_audioPlayer stop];
    }
    _audioPlayer = nil;
    
    if ([_audioRecorder isRecording])
    {
        [_audioRecorder pause];
        _isPause = YES;
        [self removeTimer];

        NSLog(@"pause recording");
    }
    else
    {
        if (!_isPause)
        {
            [self updateTimerOfAudio];
        }

        if ([_audioRecorder prepareToRecord])
        {
            [_audioRecorder record];
            [self startTimer:NO];
            _isPause = NO;
            NSLog(@"start recording");
        }
        else
        {
            NSLog(@"Cannot recording");
        }
    }
    
    [self updateStateOfSubviews];
}

- (void)deleteAudio
{
    [_audioPlayer stop];
    [[NSFileManager defaultManager] removeItemAtPath:[_imageDirectory stringByAppendingPathComponent:FileAudioFile] error:nil];
    _audioPlayer = nil;
    
    [self updateTimerOfAudio];
    [self updateStateOfSubviews];
}

- (void)stopAudio
{
    [_audioRecorder stop];
    [self removeTimer];
    
    _isPause = NO;
    
    [_audioPlayer stop];
     NSString *audioFileURL = [_imageDirectory stringByAppendingPathComponent:FileAudioFile];
    _audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:[NSURL fileURLWithPath:audioFileURL] error:nil];

    [self updateTimerOfAudio];
    [self updateStateOfSubviews];
}

- (void)playAudio
{
    if ([_audioPlayer isPlaying])
    {
        [_audioPlayer pause];
        [self removeTimer];
        _isPause = YES;
        NSLog(@"pause play");
    }
    else
    {
        if ([_audioPlayer prepareToPlay])
        {
            [_audioPlayer play];
            [self startTimer:YES];
            _isPause = NO;
            NSLog(@"start play");
        }
        else
        {
            NSLog(@"cannot start play");
        }
    }
    
    [self updateStateOfSubviews];
}

- (void)loadImageDirectory:(NSString *)imageDirectory
{
    _imageDirectory = imageDirectory;
    
    NSString *audioFileURL = [_imageDirectory stringByAppendingPathComponent:FileAudioFile];
    NSDictionary *audioSettings = [NSDictionary dictionaryWithObjectsAndKeys:
                                   [NSNumber numberWithFloat:44100],AVSampleRateKey,
                                   [NSNumber numberWithInt: kAudioFormatMPEG4AAC],AVFormatIDKey,
                                   [NSNumber numberWithInt: 1],AVNumberOfChannelsKey,
                                   [NSNumber numberWithInt:AVAudioQualityMedium],AVEncoderAudioQualityKey,nil];
    
    AVAudioSession *audioSession = [AVAudioSession sharedInstance];
    [audioSession setCategory:AVAudioSessionCategoryPlayAndRecord error:nil];
    [audioSession setActive:YES error:nil];
    
    _audioRecorder = [[AVAudioRecorder alloc]
                      initWithURL:[NSURL fileURLWithPath:audioFileURL]
                      settings:audioSettings
                      error:nil];
    _audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:[NSURL fileURLWithPath:audioFileURL] error:nil];

    UInt32 audioRouteOverride = kAudioSessionOverrideAudioRoute_Speaker;                        // 1
    
    AudioSessionSetProperty (
                             kAudioSessionProperty_OverrideAudioRoute,                          // 2
                             sizeof (audioRouteOverride),                                       // 3
                             &audioRouteOverride                                                // 4
                             );
    
    [self updateTimerOfAudio];
    [self updateStateOfSubviews];
}

- (void)updateTimerOfAudio
{
    if (_audioPlayer)
    {
        int time = (int)_audioPlayer.duration;
        _currMinute = time / 60;
        _currSeconds = time % 60;
    }
    else
    {
        _currMinute = 0;
        _currSeconds = 0;
    }
    
    [self changeTimerLabel];
}

- (void)changeTimerLabel
{
    [_timeIndicator setText:[NSString stringWithFormat:@"%02d%@%02d", _currMinute, @":", _currSeconds]];
}

- (void)updateStateOfSubviews
{
    if ([_audioRecorder isRecording])
    {
        _stopBtn.enabled = YES;
        
        [_recordBtn setImage:[UIImage imageNamed:@"pause_record.png"] forState:UIControlStateNormal];
    }
    else
    {
        if (!_isPause)
        {
            _stopBtn.enabled = NO;
            _recordIndicator.hidden = YES;
        }
        
        [_recordBtn setImage:[UIImage imageNamed:@"record.png"] forState:UIControlStateNormal];
    }
    
    if (_audioPlayer)
    {
        _playBtn.enabled = YES;
        if ([_audioPlayer isPlaying])
        {
            _stopBtn.enabled = YES;
            _recordBtn.enabled = NO;
            _deleteBtn.enabled = NO;
            
            [_playBtn setImage:[UIImage imageNamed:@"pause_play.png"] forState:UIControlStateNormal];
        }
        else
        {
            if (!_isPause)
            {
                _stopBtn.enabled = NO;
                _recordBtn.enabled = YES;
                _deleteBtn.enabled = YES;
            }
            else
            {
                _deleteBtn.enabled = NO;
            }

            [_playBtn setImage:[UIImage imageNamed:@"play.png"] forState:UIControlStateNormal];
        }
    }
    else
    {
        _playBtn.enabled = NO;
        _deleteBtn.enabled = NO;
    }
}

@end
