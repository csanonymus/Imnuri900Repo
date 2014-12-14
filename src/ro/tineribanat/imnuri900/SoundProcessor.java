package ro.tineribanat.imnuri900;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

public class SoundProcessor {

	Thread playThread = null;
	// Media player status
	private static int STATUS_NULL = 0;
	private static int STATUS_DEFINED = 1;
	private static int STATUS_PLAY = 2;
	private static int STATUS_PAUSED = 3;
	private static int STATUS_STOPPED = 4;

	public int mediaPlayerStatus = 0;

	Context context;
	String streamUrl;

	MediaPlayer mediaPlayer = null;
	boolean urlSet = false;
	int pausedProgress;

	public SoundProcessor(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mediaPlayer = new MediaPlayer();
		
	}

	public boolean setStreamUrl(String url) {
		this.streamUrl = url;
		this.urlSet = true;
		try {
			if (haveNetworkConnection()) {
				this.mediaPlayer.setDataSource(context, Uri.parse(streamUrl));
				this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				this.mediaPlayer.prepare();
				mediaPlayerStatus = STATUS_DEFINED;
				ShowHymn.progress.dismiss();
				return true;
			} else {
				Toast t = Toast.makeText(context, "Eroare!!...Nu s-a putut stabili o conexiune la internet", Toast.LENGTH_LONG);
				t.show();
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ShowHymn.progress.dismiss();
		return false;

	}

	public int getProgress() {
		return mediaPlayer.getCurrentPosition();
	}

	public int getTotalTime() {
		return mediaPlayer.getDuration();
	}
	
	public int getStatus() {
		return mediaPlayerStatus;
	}

	public void pause() {
		if (mediaPlayerStatus == STATUS_PLAY) {
			pausedProgress = this.getProgress();
			mediaPlayer.pause();
			mediaPlayerStatus = STATUS_PAUSED;
			playThread = null;
		}
		ShowHymn.isMusicPlaying = false;
	}

	public void play() {
		if ((mediaPlayerStatus == STATUS_STOPPED)
				|| (mediaPlayerStatus == STATUS_DEFINED)) {
			mediaPlayer.start();

		} else if (mediaPlayerStatus == STATUS_PAUSED) {
			mediaPlayer.seekTo(pausedProgress);
			mediaPlayer.start();

		}
		mediaPlayerStatus = STATUS_PLAY;
		ShowHymn.isMusicPlaying = true;
		// PlayHymn.updateProgress.start();
		// playThread.start();
	}

	public void stop() {
		if ((mediaPlayerStatus == STATUS_PAUSED)
				|| (mediaPlayerStatus == STATUS_PLAY)) {
			mediaPlayer.pause();
			mediaPlayer.seekTo(1);
			mediaPlayerStatus = STATUS_DEFINED;
		}
		ShowHymn.isMusicPlaying = false;
	}
	
	public void destroy() {
		mediaPlayer.release();
	}

	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) this.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
}
