package ro.tineribanat.imnuri900.workers;

import ro.tineribanat.imnuriazsmr.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarDialog extends Dialog implements
		android.view.View.OnClickListener, OnSeekBarChangeListener {

	SeekBar seekBar;
	Button button;
	TextView tvProgress;

	Context context;
	PrefManager prefs;

	public SeekBarDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		prefs = new PrefManager(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.custom_seekbardialog);

		tvProgress = (TextView) findViewById(R.id.tvProgressSeekBarDialog);

		seekBar = (SeekBar) findViewById(R.id.sbSeekBarDialog);

		int progressToSet = prefs.getHymnTextSize();
		seekBar.setProgress(progressToSet);
		tvProgress.setText(progressToSet + "px");
		seekBar.setOnSeekBarChangeListener(this);

		button = (Button) findViewById(R.id.bSeekBarDialog);
		button.setOnClickListener(this);

		LayoutParams p = getWindow().getAttributes();
		p.width = 4 * p.width / 5;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) p);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int progress = seekBar.getProgress();
		prefs.setHymnTextSize(progress);
		this.dismiss();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		tvProgress.setText(progress + "px");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

}
