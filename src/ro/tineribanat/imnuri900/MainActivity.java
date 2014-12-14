package ro.tineribanat.imnuri900;

import ro.tineribanat.imnuri900.fragment.Communicator;
import ro.tineribanat.imnuri900.fragment.FragmentHymns;
import ro.tineribanat.imnuri900.fragment.MyPagerAdapter;
import ro.tineribanat.imnuri900.workers.BarColorizer;
import ro.tineribanat.imnuri900.workers.PrefManager;
import ro.tineribanat.imnuriazsmr.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

public class MainActivity extends FragmentActivity implements Communicator {

	public static PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private static MyPagerAdapter adapter;

	int pageMargin;

	private static int TAB_CATEGORIES = 0;
	private static int TAB_HYMNS = 1;
	private static int TAB_FAVORITES = 2;
	private static int TAB_RANDOM = 3;

	boolean initialActivityStart = true;

	BarColorizer colorizer;
	PrefManager prefs;
	BarColorizer bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = new PrefManager(getApplicationContext());
		boolean night = prefs.getNightMode();
		if (night) {
			setTheme(R.style.ThemeHoloDark);
			setContentView(R.layout.activity_swipe_night);
		} else {
			setContentView(R.layout.activity_swipe);
		}

		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());

		pager.setAdapter(adapter);
		pager.setPageMargin(pageMargin);
		tabs.setViewPager(pager);

		pager.setCurrentItem(TAB_CATEGORIES, true);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 11) {
			bar = new BarColorizer(this, tabs, getActionBar());
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (initialActivityStart) {
			Log.i("APP", initialActivityStart + "");
			pager.setCurrentItem(0, true);
			initialActivityStart = false;
			Handler h = new Handler();
			h.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					pager.setCurrentItem(TAB_HYMNS, true);
				}
			}, 100);
		}

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		finish();
		startActivity(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, Preferences.class);
			startActivity(i);
		}
		/*
		 * if (id == R.id.action_random) { Intent i = new Intent(this,
		 * Random.class); startActivity(i); } else if (id ==
		 * R.id.action_favorites) { Intent i = new Intent(this,
		 * ShowFavorites.class); startActivity(i); }
		 */

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void respond(String Data) {
		// TODO Auto-generated method stub
		FragmentHymns.categorySelected(Data);
		pager.setCurrentItem(TAB_HYMNS, true);
	}

}
