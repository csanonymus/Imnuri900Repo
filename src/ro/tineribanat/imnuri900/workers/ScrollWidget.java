package ro.tineribanat.imnuri900.workers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollWidget extends ScrollView {

	Context context;
	
	public ScrollWidget(Context context) {
	    super(context);
	}

	public ScrollWidget(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}

	public ScrollWidget(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getPointerCount() == 2) {
			return false;
		} else if(ev.getPointerCount() == 3) {
			return false;
		}
		return super.onTouchEvent(ev);
	}

}
