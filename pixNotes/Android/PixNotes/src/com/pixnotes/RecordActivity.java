package com.pixnotes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.wifi.WifiConfiguration.Status;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pixnotes.common.AudioRecorder;
import com.pixnotes.common.Utilities;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.ExternalStorage;
import com.pixnotes.datastorage.PreferenceConnector;
import com.pixnotes.utils.LogUtils;

public class RecordActivity extends BaseActivity implements OnClickListener{

	private int requestCode = 0;
	private ImageView img_record_audio;
	private ImageView img_play_audio;
	private ImageView img_stop_audio;
	private ImageView img_delete_audio;
	private MediaPlayer   mPlayer = null;
	private TextView tv_minutes;
	private TextView tv_seconds;
	private TextView tv_rec;
//	private int isStatusRecord = 0;
//	private final int isWaittingRecord = 0;
//	private final int isRecording = 1;
//	private final int isStopRecord = 2;
//	private final int isPauseRecord = 3;
//	
//	private int isStatusPlay = 0;
//	private final int isPausePlay = 0;
//	private final int isPlaying = 1;
	
    private AudioRecorder mAudioRecorder;
    
    private Timer mTimer;
    private TimerTask mCountUpTask, mCountDownTask;    
    private int mTotalTime = 0; //milisecond
    private final int TIME_SCHEDULE = 100;
    
    private MediaState mState = MediaState.Invalid;
    private RelativeLayout rl_mainscreen;
    private enum MediaState {
    	Invalid,
    	Recording,
    	PauseRecording,
    	StopRecording,
    	Playing,
    	PausePlaying,
    	StopPlaying,
    }

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_audio_screen);
		initComponent();
		setComponentListener();
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_RECORD_AUDIO))
			{
				requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_RECORD_AUDIO);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_RECORD_AUDIO);
			}
		}
	
		mAudioRecorder = AudioRecorder.build(this, getAudioFileName());
		
		// check audio exist 
		if(isCheckAudioExist())
		{
			stopPlaying();
		}
		else
		{
			resetValueDeleteSound();
		}
		
	}
	
	private void resetValueDeleteSound()
	{
		mState = MediaState.StopRecording;
		img_record_audio.setBackgroundResource(R.drawable.record);
		img_record_audio.setEnabled(true);
		img_play_audio.setBackgroundResource(R.drawable.play_invisible);
		img_play_audio.setEnabled(false);
		img_stop_audio.setBackgroundResource(R.drawable.stop_invisible);
		img_stop_audio.setEnabled(false);
		img_delete_audio.setBackgroundResource(R.drawable.deleted_sound_invisible);
		img_delete_audio.setEnabled(false);
		tv_minutes.setText("00"); 
		tv_seconds.setText("00"); 
		tv_rec.setVisibility(View.INVISIBLE);
	}
	
	private String getTimeString(long millis) {
	    StringBuffer buf = new StringBuffer();

	    int hours = (int) (millis / (1000 * 60 * 60));
	    int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
	    int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

	    buf
	        .append(String.format("%02d", hours))
	        .append(":")
	        .append(String.format("%02d", minutes))
	        .append(":")
	        .append(String.format("%02d", seconds));

	    return buf.toString();
	}
	
	private String getSeconds(long millis)
	{
		StringBuffer buf = new StringBuffer();
		int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
		buf.append(String.format("%02d", seconds));
		return buf.toString();
	}
	
	private String getMinutes(long millis)
	{
		StringBuffer buf = new StringBuffer();
		int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
		buf.append(String.format("%02d", minutes));
		return buf.toString();
	}
	
	public void initComponent()
	{
		img_record_audio = (ImageView)findViewById(R.id.img_record_audio);
		img_play_audio = (ImageView)findViewById(R.id.img_play_audio);
		img_stop_audio = (ImageView)findViewById(R.id.img_stop_audio);
		img_delete_audio = (ImageView)findViewById(R.id.img_delete_audio);
		tv_minutes = (TextView)findViewById(R.id.tv_minutes);
		tv_seconds = (TextView)findViewById(R.id.tv_seconds);
		tv_rec =  (TextView)findViewById(R.id.tv_rec);
		rl_mainscreen = (RelativeLayout)findViewById(R.id.rl_mainscreen);
	}
	
	public void setComponentListener()
	{
		img_record_audio.setOnClickListener(this);
		img_play_audio.setOnClickListener(this);
		img_stop_audio.setOnClickListener(this);
		img_delete_audio.setOnClickListener(this);
		rl_mainscreen.setOnClickListener(this);
	}
    
    private boolean isCheckAudioExist()
    {
    	boolean flag = false;
		File fileAudio = new File(getAudioFileName());
		if(fileAudio.exists())
		{
			flag = true;
		}
		return flag;
    }
    
    private String getAudioFileName()
    {
    	int index = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
		String folderPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(index).getOriginalImageGalleryPath();
		String fileAudioPath = Utilities.getImageDrawFileNamePath(MainActivity.getInstance().mCurrentProject, folderPath + Configs.FLAG_AUDIO_FORMAT);
		Log.e("fileAudioPath", "fileAudioPath ========== "+fileAudioPath);
		return fileAudioPath;
    }
    
    protected void startRecording(){
    	
    	mState = MediaState.Recording;
    	img_record_audio.setBackgroundResource(R.drawable.pause_stop);
		img_record_audio.setEnabled(true);
		img_play_audio.setBackgroundResource(R.drawable.play_invisible);
		img_play_audio.setEnabled(false);
		img_stop_audio.setBackgroundResource(R.drawable.stop);
		img_stop_audio.setEnabled(true);
		img_delete_audio.setBackgroundResource(R.drawable.deleted_sound_invisible);
		img_delete_audio.setEnabled(false);
    	
    	mAudioRecorder.start(new AudioRecorder.OnStartListener() {
            @Override
            public void onStarted() {
               
            }

            @Override
            public void onException(Exception e) {
               
            }
        });
		
		if(mTimer == null){
			mTimer = new Timer();
			mTotalTime = 0;
			mCountUpTask = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mState == MediaState.Recording){
						mTotalTime += TIME_SCHEDULE;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tv_minutes.setText(getMinutes(mTotalTime));
								tv_seconds.setText(getSeconds(mTotalTime));
								if(mTotalTime%300 == 0){
									if(tv_rec.getVisibility() == View.VISIBLE){
										tv_rec.setVisibility(View.INVISIBLE);
									} else {
										tv_rec.setVisibility(View.VISIBLE);
									}
								}
							}
						});
						
					}
				}
			};
			mTimer.scheduleAtFixedRate(mCountUpTask, 0, TIME_SCHEDULE);
		}
    }
    
    protected void pauseRecording(){
    	mState = MediaState.PauseRecording;
    	img_record_audio.setBackgroundResource(R.drawable.record);
		img_record_audio.setEnabled(true);
		img_play_audio.setBackgroundResource(R.drawable.play_invisible);
		img_play_audio.setEnabled(false);
		img_stop_audio.setBackgroundResource(R.drawable.stop);
		img_stop_audio.setEnabled(true);
		img_delete_audio.setBackgroundResource(R.drawable.deleted_sound);
		img_delete_audio.setEnabled(true);
		
		if(mAudioRecorder.isRecording()){
	    	mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
	            @Override
	            public void onPaused(String activeRecordFileName) {
	            }
	
	            @Override
	            public void onException(Exception e) {
	            }
	        });
		}
    }
    
    protected void stopRecording(){
    	mState = MediaState.StopRecording;
    	
    	img_record_audio.setBackgroundResource(R.drawable.record);
		img_record_audio.setEnabled(true);
		img_play_audio.setBackgroundResource(R.drawable.play);
		img_play_audio.setEnabled(true);
		img_stop_audio.setBackgroundResource(R.drawable.stop_invisible);
		img_stop_audio.setEnabled(false);
		img_delete_audio.setBackgroundResource(R.drawable.deleted_sound);
		img_delete_audio.setEnabled(true);
		tv_rec.setVisibility(View.INVISIBLE);
		
		if(mAudioRecorder.isRecording()){
	    	mAudioRecorder.pause(new AudioRecorder.OnPauseListener() {
	            @Override
	            public void onPaused(String activeRecordFileName) {
	            }
	
	            @Override
	            public void onException(Exception e) {
	            }
	        });
		}
    }
    
    protected void startPlaying(){
    	mState = MediaState.Playing;
    	img_record_audio.setBackgroundResource(R.drawable.record_invisible);
		img_record_audio.setEnabled(false);
		img_play_audio.setBackgroundResource(R.drawable.pause_play);
		img_play_audio.setEnabled(true);
		img_stop_audio.setBackgroundResource(R.drawable.stop);
		img_stop_audio.setEnabled(true);
		img_delete_audio.setBackgroundResource(R.drawable.deleted_sound_invisible);
		img_delete_audio.setEnabled(false);
		
		
		if(mPlayer == null){
			initialMediaPlayer();
		}
		mPlayer.start();
		if(mTimer == null){
			mTimer = new Timer();
			mCountDownTask = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mState == MediaState.Playing){
						mTotalTime -= TIME_SCHEDULE;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tv_minutes.setText(getMinutes(mTotalTime));
								tv_seconds.setText(getSeconds(mTotalTime));
							}
						});
						
					}
				}
			};
			mTimer.scheduleAtFixedRate(mCountDownTask, 0, TIME_SCHEDULE);
		}
        
    }
    
    protected void pausePlaying(){
    	mState = MediaState.PausePlaying;
    	img_record_audio.setBackgroundResource(R.drawable.record_invisible);
		img_record_audio.setEnabled(false);
		img_play_audio.setBackgroundResource(R.drawable.play);
		img_play_audio.setEnabled(true);
		img_stop_audio.setBackgroundResource(R.drawable.stop);
		img_stop_audio.setEnabled(true);
		img_delete_audio.setBackgroundResource(R.drawable.deleted_sound_invisible);
		img_delete_audio.setEnabled(false);
		
    	if(mPlayer.isPlaying()){
		    mPlayer.pause();
		} else {
		    mPlayer.start();
		}
    }
    
    protected void resumePlaying(){
    	mState = MediaState.Playing;
    	img_record_audio.setBackgroundResource(R.drawable.record_invisible);
		img_record_audio.setEnabled(false);
		img_play_audio.setBackgroundResource(R.drawable.pause_play);
		img_play_audio.setEnabled(true);
		img_stop_audio.setBackgroundResource(R.drawable.stop);
		img_stop_audio.setEnabled(true);
		img_delete_audio.setBackgroundResource(R.drawable.deleted_sound_invisible);
		img_delete_audio.setEnabled(false);
		
		if(mPlayer.isPlaying()){
		    mPlayer.pause();
		} else {
		    mPlayer.start();
		}
    }
    
    protected void stopPlaying(){
    	mState = MediaState.StopPlaying;
    	img_record_audio.setBackgroundResource(R.drawable.record);
		img_record_audio.setEnabled(true);
		img_play_audio.setBackgroundResource(R.drawable.play);
		img_play_audio.setEnabled(true);
		img_stop_audio.setBackgroundResource(R.drawable.stop_invisible);
		img_stop_audio.setEnabled(false);
		img_delete_audio.setBackgroundResource(R.drawable.deleted_sound);
		img_delete_audio.setEnabled(true);
		
		if(mPlayer != null){
			mPlayer.release();
        	mPlayer = null;
		}
		if(mTimer != null){
			mTimer.cancel();
			mTimer =null;
		}
        //Reset value for media player
        
        initialMediaPlayer();
    }
    
    private void initialMediaPlayer(){
    	mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getAudioFileName());
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer arg0) {
					stopPlaying();
				}
			});
            mPlayer.prepare();
            mTotalTime = mPlayer.getDuration();
            tv_minutes.setText(getMinutes(mTotalTime));
    		tv_seconds.setText(getSeconds(mTotalTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
	
    void deleteRecordFile(){
    	resetValueDeleteSound();
		File fileAudio = new File(getAudioFileName());
		if(fileAudio.exists())
		{
			fileAudio.delete();
			mPlayer = null;
		}
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
		}
		mTotalTime = 0;
    }
    
	@Override
	public void onClick(View v) {
		
		LogUtils.LogError("============mState " + mState);
		
		switch (v.getId()) {
		case R.id.img_record_audio:{
			switch (mState) {
			case StopRecording:
			case StopPlaying:
				//delete old file and record new file
				deleteRecordFile();
				startRecording();
				break;
			case Playing:
			case PausePlaying:
				//stop playing and record new file
				stopPlaying();
				startRecording();
				break;
			case Recording:
				//pause recording
				pauseRecording();
				break;
				
			case PauseRecording:
				startRecording();
				break;
				
			default:
				break;
			}
			break;
		}
		case R.id.img_play_audio:{
			switch (mState) {
			case StopRecording:
			case StopPlaying:
				//start playing
				startPlaying();
				break;
			case Playing:
				//pause
				pausePlaying();
				break;
			case PausePlaying:
				//resume playing
				resumePlaying();
				break;
			default:
				break;
			}
			

			break;
		}
		case R.id.img_stop_audio:{
			switch (mState) {
			case Playing:
			case PausePlaying:
				stopPlaying();
				break;
			case Recording:
			case PauseRecording:
				stopRecording();
				//Append file
				break;

			default:
				break;
			}
			break;
		}
		case R.id.img_delete_audio:{
			deleteRecordFile();
			break;
		}
		case R.id.rl_mainscreen:
			 finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		    
	    setResult(requestCode);
	    finish();
		
	}
	
}
