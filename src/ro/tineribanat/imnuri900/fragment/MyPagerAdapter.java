package ro.tineribanat.imnuri900.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

	private final String[] TITLES = { "   Categorii   ", "   Imnuri   ", "   Favorite   ", "   Random   " };

	public MyPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}

	@Override
	public Fragment getItem(int position) {
		if(position == 0) {
			return new FragmentCategories();
		} else if(position == 1) {
			return new FragmentHymns();
		} else if(position == 2) {
			return new FragmentFavorites();
		} else if(position == 3) {
			return new FragmentRandom();
		}
		return null;
	}
}