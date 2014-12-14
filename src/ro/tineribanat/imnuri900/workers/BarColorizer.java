package ro.tineribanat.imnuri900.workers;

import java.io.Serializable;

import ro.tineribanat.imnuriazsmr.R;
import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

public class BarColorizer implements Serializable {
	private static final long serialVersionUID = 5697072707493245833L;
	Drawable oldBackground = null;
	int currentColor;

	PrefManager prefs;
	PagerSlidingTabStrip pagerSlidingTabStrip;
	Context context;

	ActionBar actionBar;

	public BarColorizer(Context context,
			PagerSlidingTabStrip pagerSlidingTabStrip, ActionBar actionBar) {
		// TODO Auto-generated constructor stub
		this.context = context;
		prefs = new PrefManager(context);
		this.pagerSlidingTabStrip = pagerSlidingTabStrip;
		this.actionBar = actionBar;

		setCurrentColor();
	}

	public BarColorizer(Context context, ActionBar actionBar) {
		// TODO Auto-generated constructor stub
		this.context = context;
		prefs = new PrefManager(context);
		this.actionBar = actionBar;

		setCurrentColor();
	}

	private void setCurrentColor() {
		currentColor = prefs.getActionBarColor();
		if (currentColor == 0) {
			currentColor = 0xFF666666;
			prefs.setCurrentColor(currentColor);
		} else {
			changeColor(currentColor);
		}
	}

	private void changeColor(int newColor) {
		if (pagerSlidingTabStrip != null) {
			pagerSlidingTabStrip.setIndicatorColor(newColor);
		}

		// change ActionBar color just if an ActionBar is available
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			Drawable colorDrawable = new ColorDrawable(newColor);
			Drawable bottomDrawable = context.getResources().getDrawable(
					R.drawable.actionbar_bottom);
			LayerDrawable ld = new LayerDrawable(new Drawable[] {
					colorDrawable, bottomDrawable });

			if (oldBackground == null) {

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					showColorError();
				} else {
					actionBar.setBackgroundDrawable(ld);
				}

			} else {

				TransitionDrawable td = new TransitionDrawable(new Drawable[] {
						oldBackground, ld });

				// workaround for broken ActionBarContainer drawable handling on
				// pre-API 17 builds
				// https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					showColorError();
				} else {
					actionBar.setBackgroundDrawable(td);
				}

				td.startTransition(200);

			}

			oldBackground = ld;

			// http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowTitleEnabled(true);

		}

		currentColor = newColor;

	}

	private void showColorError() {
		Toast.makeText(
				context,
				"Acesta functie este indisponibila pentru dispozitivul dumneavoastra!",
				Toast.LENGTH_LONG).show();
	}
}
