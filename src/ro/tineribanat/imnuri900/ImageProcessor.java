package ro.tineribanat.imnuri900;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class ImageProcessor {

	Bitmap hymnBitmap;

	Context callerContext;

	String downloadRoot = "http://imnuri.tineribanat.ro/Imnuri/";
	String sdCardRoot;
	String url;

	boolean urlIsSet = false;

	public ImageProcessor(Context c) {
		// TODO Auto-generated constructor stub
		this.callerContext = c;
		sdCardRoot = Environment.getExternalStorageDirectory().toString();
	}

	public boolean getImageFromWeb() {
		//check if connected to the internet
		boolean networkIsActive = false;
		ConnectivityManager conMgr = (ConnectivityManager) callerContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			networkIsActive = true;
		}
		//if url is set and device is connected to the internet get the sheet
		if (urlIsSet && networkIsActive) {
			try {
				String link = downloadRoot + url;
				URL imageUrl = new URL(link);
				InputStream in = imageUrl.openStream();
				hymnBitmap = BitmapFactory
						.decodeStream(new PatchInputStream(in));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} finally {
				return true;
			}
		}
		return false;
	}

	public void getImageFromSD(String name) {
		File file = new File(sdCardRoot + "/.imnuriazsmr/Imnuri/" + name
				+ ".png");
		hymnBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
	}

	public void saveImageToSD(Bitmap image, String name) {
		File myDir = new File(sdCardRoot + "/.imnuriazsmr/Imnuri/");
		if (!myDir.isDirectory())
			myDir.mkdirs();
		File file = new File(myDir, name);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			boolean x = image.compress(Bitmap.CompressFormat.PNG, 80, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUrl(String url) {
		int number = Integer.parseInt(url);
		if (number < 10) {
			url = "00" + url + ".png";
		} else if (number >= 10 && number < 100) {
			url = "0" + url + ".png";
		} else {
			url = url + ".png";
		}
		this.url = url;
		this.urlIsSet = true;
	}

	public String getUrl() {
		return this.url;
	}

	public Bitmap getBitmap() {
		return this.hymnBitmap;
	}

	public class PatchInputStream extends FilterInputStream {
		public PatchInputStream(InputStream in) {
			super(in);
		}

		public long skip(long n) throws IOException {
			long m = 0L;
			while (m < n) {
				long _m = in.skip(n - m);
				if (_m == 0L)
					break;
				m += _m;
			}
			return m;
		}
	}
}
