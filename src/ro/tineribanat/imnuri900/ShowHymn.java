package ro.tineribanat.imnuri900;

import java.io.File;

import ro.tineribanat.imnuri900.workers.BarColorizer;
import ro.tineribanat.imnuri900.workers.PrefManager;
import ro.tineribanat.imnuriazsmr.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class ShowHymn extends Activity implements OnTouchListener {

	TextView tvTitle, tvHymn, tvCategory;
	ImageView ivSheet, ivMP3, ivFavorited;
	SeekBar hymnTextSize;
	LinearLayout llImageButtons;

	ImageButton ibPlayPause, ibStop;
	Bitmap bmpPlay, bmpPause, bmpStop, heart;
	boolean ibPlayPauseIsOnPlay = true;
	SoundProcessor sound;
	public static ProgressDialog progress;
	Menu menu;
	boolean isFavorited = false;

	String hymnNumber, hymnTitle;

	DatabaseHelper database;

	int textSize = 24;

	boolean textReady = false;

	ImageProcessor ip;
	File musicSheet;

	int favoriteHeartButton, mediaButtons, sheetAndMediaButtons;

	Intent sharingIntent = null;
	public static boolean isMusicPlaying = false;

	PrefManager prefs;

	// pinchZoom
	final static float STEP = 200;
	float mRatio = 1.0f;
	int mBaseDist;
	float mBaseRatio;
	float maxFontSize = 50;

	GestureDetector gestureDetector;

	BarColorizer barColorizer;

	ScrollView swScroll;
	boolean sdkPermits = false;
	
	WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		prefs = new PrefManager(getApplicationContext());
		boolean night = prefs.getNightMode();
		if (night) {
			setTheme(R.style.ThemeHoloDark);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_hymn);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		favoriteHeartButton = width / 6;
		mediaButtons = width / 6;
		sheetAndMediaButtons = width / 6;

		init();
		showText();
		initShare();
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {

		super.dispatchTouchEvent(ev);

		return gestureDetector.onTouchEvent(ev);

	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (isMusicPlaying) {
			sound.stop();
		}
		
		if(isFinishing()) {
			sound.destroy();
			database.close();
			
			bmpPause.recycle();
			bmpPlay.recycle();
			bmpStop.recycle();
			heart.recycle();
		}
		wakeLock.release();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		finish();
		startActivity(getIntent());
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int progress = prefs.getHymnTextSize();
		tvHymn.setTextSize(progress);
		hymnTextSize.setProgress(progress);
		wakeLock.acquire();
	}

	private void showText() {
		int textSize = prefs.getHymnTextSize();
		tvHymn.setTextSize(textSize);

		Intent i = getIntent();
		Bundle b = i.getExtras();

		hymnNumber = b.getString("number");
		hymnTitle = b.getString("title");

		Cursor c = database.queryFor(hymnNumber);
		if (c != null && c.moveToFirst()) {
			String number = c.getString(c.getColumnIndex("tNumber"));
			String title = c.getString(c.getColumnIndex("tName"));
			String hymn = c.getString(c.getColumnIndex("tContent"));
			String category = c.getString(c.getColumnIndex("tCategory"));
			tvTitle.setText(number + ". " + title);
			tvHymn.setText(hymn);
			tvCategory.setText(category);
			initSheetFile();
			if (Integer.parseInt(hymnNumber) <= 777) {
				Bitmap bitmap = null;
				if (musicSheet.exists()) {
					bitmap = BitmapFactory.decodeResource(getResources(),
							R.drawable.musicsheet_available);
				} else {
					bitmap = BitmapFactory.decodeResource(getResources(),
							R.drawable.musicsheet);
				}
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
						sheetAndMediaButtons, sheetAndMediaButtons, false);
				ivSheet.setImageBitmap(scaledBitmap);
			}
			if (Integer.parseInt(hymnNumber) <= 777) {
				new BitmapFactory();
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.mp3);
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
						sheetAndMediaButtons, sheetAndMediaButtons, false);
				ivMP3.setImageBitmap(scaledBitmap);
			}
			textReady = true;
			hymnTextSize.setProgress(textSize);
		}
	}

	private void initSheetFile() {
		String imagesFolder = Environment.getExternalStorageDirectory()
				.toString() + "/.imnuriazsmr/Imnuri/";
		String filePath = imagesFolder + hymnNumber + ".jpeg";
		musicSheet = new File(filePath);

		isFavorited = database.isFavorite(hymnNumber + "");
		updateHeartBitmap();
	}

	private void initShare() {
		sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareTitle = tvTitle.getText().toString();
		String shareBody = tvHymn.getText().toString();
		sharingIntent
				.putExtra(android.content.Intent.EXTRA_SUBJECT, shareTitle);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	}

	

	@SuppressWarnings("deprecation")
	private void init() {
		sound = new SoundProcessor(this);
		ip = new ImageProcessor(this);

		database = new DatabaseHelper(this);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(null, Typeface.BOLD);

		tvHymn = (TextView) findViewById(R.id.tvHymn);

		tvCategory = (TextView) findViewById(R.id.tvCategory);
		tvCategory.setTypeface(null, Typeface.BOLD_ITALIC);

		ivSheet = (ImageView) findViewById(R.id.ivSheet);
		ivSheet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (musicSheet != null) {
					if (musicSheet.exists()) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.fromFile(musicSheet),
								"image/*");
						startActivity(intent);
					} else {
						final Toast toastError = Toast.makeText(ShowHymn.this,
								"Eroare!!!Nu sunteti conectat la internet",
								Toast.LENGTH_LONG);
						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								ip.setUrl(hymnNumber + "");
								boolean isSuccessful = ip.getImageFromWeb();
								if (isSuccessful) {
									runOnUiThread(run);
								} else {
									toastError.show();
								}
							}
						});
						t.start();
					}
				}
			}
		});

		ivMP3 = (ImageView) findViewById(R.id.ivMP3);
		ivMP3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progress = new ProgressDialog(ShowHymn.this);
				progress.setCancelable(false);
				progress.setMessage("Se incarca...");
				progress.show();

				String x = hymnNumber;
				if (x.length() == 1) {
					x = "00" + x;
				} else if (x.length() == 2) {
					x = "0" + x;
				}

				String rootURL = "http://www.salvipergrazia.it/Imnuri/mp3/";
				String extension = ".mp3";
				String finalURL = rootURL + x + extension;
				boolean success = sound.setStreamUrl(finalURL);
				if (success) {
					llImageButtons.setVisibility(View.VISIBLE);
				}
				/*
				 * Intent i = new Intent(ShowHymn.this, PlayHymn.class);
				 * i.putExtra("hymnNumber", x); startActivity(i);
				 */
			}
		});

		hymnTextSize = (SeekBar) findViewById(R.id.sbTextSize);
		hymnTextSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (textReady) {
					prefs.setHymnTextSize(progress);
					tvHymn.setTextSize(progress);
				}
			}
		});

		ibPlayPause = (ImageButton) findViewById(R.id.ibPlayPause);

		ibPlayPause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ibPlayPauseIsOnPlay) {
					Bitmap scaledPause = Bitmap.createScaledBitmap(bmpPause,
							mediaButtons, mediaButtons, false);
					ibPlayPause.setImageBitmap(scaledPause);
					sound.play();
				} else {
					Bitmap scaledPlay = Bitmap.createScaledBitmap(bmpPlay,
							mediaButtons, mediaButtons, false);
					ibPlayPause.setImageBitmap(scaledPlay);
					sound.pause();
				}
				ibPlayPauseIsOnPlay = !ibPlayPauseIsOnPlay;
			}
		});

		ibStop = (ImageButton) findViewById(R.id.ibStop);
		ibStop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bitmap scaledPlay = Bitmap.createScaledBitmap(bmpPlay,
						mediaButtons, mediaButtons, false);
				ibPlayPause.setImageBitmap(scaledPlay);
				sound.stop();
				ibPlayPauseIsOnPlay = true;
			}
		});

		llImageButtons = (LinearLayout) findViewById(R.id.llImageButtons);
		setImageButtons();

		ivFavorited = (ImageView) findViewById(R.id.ivFavorited);

		ivFavorited.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast t = null;
				if (isFavorited) {
					database.removeFavorite(hymnNumber + "");
					isFavorited = false;
					t = Toast.makeText(ShowHymn.this,
							"Imnul \"" + hymnTitle.trim()
									+ "\" a fost eliminat de la favorite.",
							Toast.LENGTH_LONG);
				} else {
					database.addFavorite(hymnNumber + "");
					isFavorited = true;
					t = Toast.makeText(ShowHymn.this,
							"Imnul \"" + hymnTitle.trim()
									+ "\" a fost adaugat la favorite.",
							Toast.LENGTH_LONG);
				}
				t.show();
				updateHeartBitmap();
			}
		});
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 11) {
			sdkPermits = true;
		}
		if (sdkPermits) {
			barColorizer = new BarColorizer(this, getActionBar());
		}

		swScroll = (ScrollView) findViewById(R.id.swScroll);
		swScroll.setOnTouchListener(this);

		gestureDetector = new GestureDetector(new MyGestureListener());
		if (sdkPermits) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

	}

	private void updateHeartBitmap() {
		if (isFavorited) {
			heart = BitmapFactory.decodeResource(getResources(),
					R.drawable.full_heart);

		} else {
			heart = BitmapFactory.decodeResource(getResources(),
					R.drawable.empty_heart);

		}
		int height;
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		if (sdkVersion >= 11) {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			height = size.y;
		} else {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			height = dm.heightPixels; // 533 dip
		}
		int heartHeight = height / 15;

		Bitmap scaledBmp = Bitmap.createScaledBitmap(heart,
				favoriteHeartButton, favoriteHeartButton, false);
		ivFavorited.setImageBitmap(scaledBmp);
	}

	private void setImageButtons() {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inDither = false; // Disable Dithering mode
		opts.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		opts.inInputShareable = true; // Which kind of reference will be used to
										// recover the Bitmap data after being
										// clear, when it will be used in the
										// future
		opts.inTempStorage = new byte[32 * 1024];

		bmpPlay = BitmapFactory.decodeResource(getResources(), R.drawable.play);
		Bitmap scaledPlay = Bitmap.createScaledBitmap(bmpPlay, mediaButtons,
				mediaButtons, false);
		ibPlayPause.setImageBitmap(scaledPlay);

		bmpPause = BitmapFactory.decodeResource(getResources(),
				R.drawable.pause);

		bmpStop = BitmapFactory.decodeResource(getResources(), R.drawable.stop);
		Bitmap scaledStop = Bitmap.createScaledBitmap(bmpStop, mediaButtons,
				mediaButtons, false);
		ibStop.setImageBitmap(scaledStop);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		initSheetFile();
		this.menu = menu;
		getMenuInflater().inflate(R.menu.hymn_menu, menu);

		if (android.os.Build.VERSION.SDK_INT >= 14) {
			MenuItem item = menu.findItem(R.id.action_share);
			ShareActionProvider mShareActionProvider = (ShareActionProvider) item
					.getActionProvider();
			if (mShareActionProvider != null) {
				mShareActionProvider.setShareIntent(sharingIntent);
			}
		} else {
			MenuItem item = menu.findItem(R.id.action_share);
			item.setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		makeAllVisible();
		// TODO Auto-generated method stub
		if (musicSheet.exists()) {
			MenuItem downloadSheet = menu.findItem(R.id.action_download_sheet);
			downloadSheet.setVisible(false);
		} else {
			MenuItem openSheet = menu.findItem(R.id.action_open_sheet);
			openSheet.setVisible(false);
		}
		int soundStatus = sound.getStatus();
		if ((soundStatus == 1) || (soundStatus == 4) || (soundStatus == 3)) {
			// daca nu e pe play
			MenuItem stopMp3 = menu.findItem(R.id.action_stopmp3);
			stopMp3.setVisible(false);
			MenuItem loadMp3 = menu.findItem(R.id.action_loadmp3);
			loadMp3.setVisible(false);
		} else if (soundStatus == 2) {
			MenuItem startMp3 = menu.findItem(R.id.action_playmp3);
			startMp3.setVisible(false);
			MenuItem loadMp3 = menu.findItem(R.id.action_loadmp3);
			loadMp3.setVisible(false);
		} else if (soundStatus == 0) {
			MenuItem stopMp3 = menu.findItem(R.id.action_stopmp3);
			stopMp3.setVisible(false);
			MenuItem startMp3 = menu.findItem(R.id.action_playmp3);
			startMp3.setVisible(false);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	private void makeAllVisible() {
		MenuItem downloadSheet = menu.findItem(R.id.action_download_sheet);
		downloadSheet.setVisible(true);
		MenuItem openSheet = menu.findItem(R.id.action_open_sheet);
		openSheet.setVisible(true);

		MenuItem loadMp3 = menu.findItem(R.id.action_loadmp3);
		loadMp3.setVisible(true);
		MenuItem startMp3 = menu.findItem(R.id.action_playmp3);
		startMp3.setVisible(true);
		MenuItem stopMp3 = menu.findItem(R.id.action_stopmp3);
		stopMp3.setVisible(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		if (id == R.id.action_download_sheet) {
			ivSheet.performClick();
		} else if (id == R.id.action_open_sheet) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(musicSheet), "image/*");
			startActivity(intent);
		} else if (id == R.id.action_loadmp3) {
			ivMP3.performClick();
		} else if (id == R.id.action_playmp3) {
			ibPlayPause.performClick();
		} else if (id == R.id.action_stopmp3) {
			ibStop.performClick();
		} else if (id == R.id.action_settings) {
			Intent i = new Intent(this, Preferences.class);
			startActivity(i);
		} else if (id == android.R.id.home) {
			this.finish();
		}

		return super.onOptionsItemSelected(item);
	}

	Runnable run = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ip.saveImageToSD(ip.getBitmap(), hymnNumber + ".jpeg");
			Toast t = Toast.makeText(ShowHymn.this, "Partitura salvata",
					Toast.LENGTH_LONG);
			t.show();

			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.musicsheet_available);

			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
					sheetAndMediaButtons, sheetAndMediaButtons, false);
			ivSheet.setImageBitmap(scaledBitmap);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int pointers = event.getPointerCount();
		if (pointers == 3) {

		} else if (pointers == 2) {
			int action = event.getAction();
			int pureaction = action & MotionEvent.ACTION_MASK;
			if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
				mBaseDist = getDistance(event);
				mBaseRatio = mRatio;
			} else {
				float delta = (getDistance(event) - mBaseDist) / STEP;
				float multi = (float) Math.pow(2, delta);
				mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
				if (mRatio > 1.0f) {
					mRatio = 1.0f;
				}
				tvHymn.setTextSize(mRatio * maxFontSize);
				hymnTextSize.setProgress((int) (mRatio * maxFontSize));
			}
		}
		return true;
	}

	private int getDistance(MotionEvent event) {
		int dx = (int) (event.getX(0) - event.getX(1));
		int dy = (int) (event.getY(0) - event.getY(1));
		return (int) (Math.sqrt(dx * dx + dy * dy));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	private void onLeftSwipe() {
		if (Integer.parseInt(hymnNumber) != 900) {
			Intent i = new Intent(getIntent());
			i.putExtra("title", hymnTitle);
			i.putExtra("number", (Integer.parseInt(hymnNumber) + 1) + "");
			startActivity(i);
			finish();
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
		}
	}

	private void onRightSwipe() {
		if (Integer.parseInt(hymnNumber) != 1) {
			Intent i = new Intent(getIntent());
			i.putExtra("title", hymnTitle);
			i.putExtra("number", (Integer.parseInt(hymnNumber) - 1) + "");
			startActivity(i);
			finish();
			overridePendingTransition(R.anim.slide_out_right,
					R.anim.slide_in_left);
		}
	}

	SimpleOnGestureListener simpleGesture = new SimpleOnGestureListener() {
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
					return false;
				}
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					ShowHymn.this.onLeftSwipe();
				}
				// left to right swipe
				else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					ShowHymn.this.onRightSwipe();
				}
			} catch (Exception e) {

			}
			return false;
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			@SuppressWarnings("deprecation")
			GestureDetector gdt = new GestureDetector(simpleGesture);
			gdt.onTouchEvent(event);
			Log.i("APP", "Touched");
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion > 11) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 150;

		private static final int SWIPE_MAX_OFF_PATH = 100;

		private static final int SWIPE_THRESHOLD_VELOCITY = 100;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,

		float velocityY) {

			float dX = e2.getX() - e1.getX();

			float dY = e1.getY() - e2.getY();

			if (Math.abs(dY) < SWIPE_MAX_OFF_PATH &&

			Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY &&

			Math.abs(dX) >= SWIPE_MIN_DISTANCE) {

				if (dX > 0) {
					// rightSwipe
					ShowHymn.this.onRightSwipe();

				} else {
					// leftSwipe
					ShowHymn.this.onLeftSwipe();
				}

				return true;

			} else if (Math.abs(dX) < SWIPE_MAX_OFF_PATH &&

			Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY &&

			Math.abs(dY) >= SWIPE_MIN_DISTANCE) {

				if (dY > 0) {

					// Toast.makeText(getApplicationContext(), "Up Swipe",
					// Toast.LENGTH_SHORT).show();

				} else {

					// Toast.makeText(getApplicationContext(), "Down Swipe",
					// Toast.LENGTH_SHORT).show();

				}

				return true;

			}

			return false;

		}

		boolean isFullscreened = false;

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (!isFullscreened) {
				// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion >= 11) {
					getActionBar().hide();
				}
				View decorView = getWindow().getDecorView();
				int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE
						| View.SYSTEM_UI_FLAG_FULLSCREEN;
				decorView.setSystemUiVisibility(uiOptions);

			} else {
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion >= 11) {
					getActionBar().show();
				}
				View decorView = getWindow().getDecorView();
				decorView.setSystemUiVisibility(0);
			}
			isFullscreened = !isFullscreened;
			return super.onDoubleTap(e);

		}
	}

}
