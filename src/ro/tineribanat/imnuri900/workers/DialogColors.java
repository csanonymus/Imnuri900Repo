package ro.tineribanat.imnuri900.workers;

import java.util.ArrayList;
import java.util.List;

import ro.tineribanat.imnuriazsmr.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DialogColors extends Dialog implements
		android.view.View.OnClickListener {

	public DialogColors(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
	ImageView a,b,c,d,e,f;
	LinearLayout colors;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_colors);
		
		colors = (LinearLayout) findViewById(R.id.colors);
		List<View> children = new ArrayList<View>();
		for(int i = 0;i<colors.getChildCount();i++) {
			View childAt = colors.getChildAt(i);
			childAt.setOnClickListener(this);
		}
		this.setTitle("Alege Culoare");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int color = Color.parseColor(v.getTag().toString());
		setColor(color);
		this.dismiss();
	}

	private void setColor(int color) {
		PrefManager prefs = new PrefManager(getContext());
		prefs.setCurrentColor(color);
	}
}
