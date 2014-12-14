package ro.tineribanat.imnuri900.unused;

import ro.tineribanat.imnuri900.fragment.ShowHymnFragment;
import ro.tineribanat.imnuriazsmr.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class ShowHymnFragmentBinder extends FragmentActivity {
	String hymnNumber;
	
    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

	
	@Override
	protected void onCreate(Bundle onSavedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(onSavedInstanceState);
		
		Intent i = getIntent();
		Bundle b = i.getExtras();

		hymnNumber = b.getString("number");
		
		mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
		
		
	}
	
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ShowHymnFragment(ShowHymnFragmentBinder.this, hymnNumber);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
