package ro.tineribanat.imnuri900.unused;

import ro.tineribanat.imnuri900.SoundProcessor;
import ro.tineribanat.imnuriazsmr.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayHymn extends Activity implements OnClickListener {

	static SoundProcessor sound;

	static SeekBar sbProgress;
	static TextView tvTotalTime;
	
	static TextView tvCurrentTime;
	ImageButton ibPlayPause, ibStop;
	boolean ibPlayPauseIsOnPlay = true; //ImageButton has play on it

	String hymnNumber;
	String rootURL = "http://www.salvipergrazia.it/Imnuri/mp3/";
	String finalURL;
	String extension = ".mp3";

	Bitmap bmpPlay, bmpPause, bmpStop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_hymn);

		init();
	}

	private void init() {
		Intent i = getIntent();
		Bundle bundle = i.getExtras();
		
		hymnNumber = bundle.getString("hymnNumber");
		finalURL = rootURL + hymnNumber + extension;
		
		sound = new SoundProcessor(this);
		sound.setStreamUrl(finalURL);
		
		sbProgress = (SeekBar) findViewById(R.id.sbProgress);
		
		tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);
		tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
		tvCurrentTime.setText("UNDF");
		tvTotalTime.setText("UNDF");
		
		ibPlayPause = (ImageButton) findViewById(R.id.ibPlayPause);
		ibPlayPause.setOnClickListener(this);
		
		ibStop = (ImageButton) findViewById(R.id.ibStop);
		ibStop.setOnClickListener(this);
		
		setImageButtons();
	}
	
	private void setImageButtons() {
		bmpPlay = BitmapFactory.decodeResource(getResources(), R.drawable.play);
		Bitmap scaledPlay = Bitmap.createScaledBitmap(bmpPlay, 40, 40, false);
		ibPlayPause.setImageBitmap(scaledPlay);
		
		bmpPause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
		
		bmpStop = BitmapFactory.decodeResource(getResources(), R.drawable.stop);
		Bitmap scaledStop = Bitmap.createScaledBitmap(bmpStop, 40, 40, false);
		ibStop.setImageBitmap(scaledStop);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		
		if(id == R.id.ibPlayPause) {
			if(ibPlayPauseIsOnPlay) {
				Bitmap scaledPause = Bitmap.createScaledBitmap(bmpPause, 40, 40, false);
				ibPlayPause.setImageBitmap(scaledPause);
				sound.play();
			} else {
				Bitmap scaledPlay = Bitmap.createScaledBitmap(bmpPlay, 40, 40, false);
				ibPlayPause.setImageBitmap(scaledPlay);
				sound.pause();
			}
			ibPlayPauseIsOnPlay = !ibPlayPauseIsOnPlay;
		} else if(id == R.id.ibStop) {
			Bitmap scaledPlay = Bitmap.createScaledBitmap(bmpPlay, 40, 40, false);
			ibPlayPause.setImageBitmap(scaledPlay);
			sound.stop();
			ibPlayPauseIsOnPlay = true;
		}
	}
	
	
	/*public static Runnable updateProgressSeekbar = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(sound.mediaPlayerStatus == 2) //STATUS_PLAY
			tvCurrentTime.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String currentTime = null;
					String totalTime = null;
					
					int progressNow = sound.getProgress()/1000;
					int progressTotal = sound.getTotalTime()/1000;
					
					if(progressNow > 60) {
						currentTime = progressNow/60+":"+(progressNow-((int)(progressNow/60)*60));
					} else {
						currentTime = "0:"+progressNow;
					}
					
					if(progressTotal > 60) {
						totalTime = progressTotal/60+":"+(progressTotal-((int)(progressTotal/60)*60));
					} else {
						totalTime = "0:"+progressTotal;
					}
					tvCurrentTime.setText(currentTime);
					tvTotalTime.setText(totalTime);
				}
			});
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	public static Thread updateProgress = new Thread(updateProgressSeekbar);*/
}
