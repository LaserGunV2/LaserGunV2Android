package com.toaster.sound;

import java.io.IOException;

import com.toaster.laser2.R;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.Log;

public class SoundController 
{
	public final static int SOUND_HIT=0;
	
	protected MediaPlayer soundPlayer;
	protected AudioManager audioManager;
	protected AssetFileDescriptor hitAsset;
	//protected boolean isReady;
	
	public SoundController(Context context)
	{
		this.audioManager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		//this.isReady=false;
		//Uri testUri=Uri.parse("file:///android_asset/Annoying_Alarm_Clock-UncleKornicob-420925725.mp3");
		this.soundPlayer=new MediaPlayer();
		//this.hitPlayer=MediaPlayer.create(context, testUri);
		//Log.v(this.getClass().getCanonicalName(), "player:"+this.hitPlayer);
		
		try
		{
			hitAsset = context.getAssets().openFd("Annoying_Alarm.wav");
			this.soundPlayer.setDataSource(hitAsset.getFileDescriptor(),hitAsset.getStartOffset(),hitAsset.getLength());
			this.soundPlayer.prepare();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void playSound(int sound)
	{
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		if (this.soundPlayer.isPlaying())
		{
			this.soundPlayer.stop();
			try
			{
				this.soundPlayer.prepare();
			}
			catch (IllegalStateException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try
		{
			this.soundPlayer.start(); 
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
