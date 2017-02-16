package com.example.audiorecordmp3demo.utils;

import android.media.MediaPlayer;

import com.example.lamemp3.MP3Recorder;

import java.io.IOException;

/**
 * 封装音频录音工具类
 * @author zhouyou
 */
public class RecorderAndPlayUtil {

	private MP3Recorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private String mPlayingPath = null;

	/** 播放完成监听 */
	public interface onPlayerCompletionListener {
		public void onPlayerCompletion();
	}

	private onPlayerCompletionListener mPlayerCompletion = new onPlayerCompletionListener() {
		@Override
		public void onPlayerCompletion() {

		}
	};

	public void setOnPlayerCompletionListener(onPlayerCompletionListener l) {
		mPlayerCompletion = l;
	}

	public RecorderAndPlayUtil(String dir) {
		mPlayer = new MediaPlayer();
		mRecorder = new MP3Recorder(dir);
	}

	public void startRecording() {
		mRecorder.start();
	}

	public void stopRecording() {
		mRecorder.stop();
	}

	public void startPlaying(String filePath) {
		if (filePath == null) {
			return;
		}
		if (mPlayingPath != null && mPlayingPath.equals(filePath) && mPlayer != null && mPlayer.isPlaying()) {
			stopPlaying();
			mPlayingPath = null;
			return;
		}
		mPlayingPath = filePath;
		stopPlaying();
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mPlayerCompletion.onPlayerCompletion();
			}
		});

		try {
			mPlayer.setDataSource(filePath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopPlaying() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
		}
	}

	public void release() {
		stopRecording();
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.release();
		}
	}

	/**
	 * 获取录音路径
	 * @return
	 */
	public String getRecorderPath() {
		return mRecorder.getFilePath();
	}

	/**
	 * 获取录音类实例
	 * @return
	 */
	public MP3Recorder getRecorder() {
		return mRecorder;
	}

	/**
	 * 获取分贝值
	 * @return
	 */
	public int getVolume(){
		return mRecorder.getVolume();
	}
}
