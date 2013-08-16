package com.csc780.tourguide.maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.csc780.tourguide.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * This class reads the file that contains the tracking information and displays
 * the tracking route on the map. If the user created notes while tracking,
 * these notes are also displayed on their creation location on the route.
 * 
 */
public class DisplayTrackingInfoActivity extends MapActivity {

	public static final String TAG = "DisplayTrackingActivity";
	public static String FILENAME = "filename";
	private ArrayList<TrackingInfo> trackingInfo = new ArrayList<TrackingInfo>();
	private MapView mapview;
	private List<Overlay> mapOverlays;
	private MapController mapController;
	private int currentIndex = 0;
	private int trackingInfoSize = 0;
	private MyLocationItemizedOverlay noteOverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String filename = (savedInstanceState == null) ? null
				: (String) savedInstanceState.getSerializable(FILENAME);
		if (filename == null && getIntent().getExtras() != null) {
			Bundle extras = getIntent().getExtras();
			filename = extras != null ? extras.getString(FILENAME) : null;
		}

		if (filename == null) {
			finish();
			return;
		}

		setContentView(R.layout.tracking);

		mapview = (MapView) findViewById(R.id.mapview1);
		mapview.setBuiltInZoomControls(true);
		mapController = mapview.getController();
		mapController.setZoom(20);
		mapview.setClickable(true);
		mapview.setEnabled(true);


		mapOverlays = mapview.getOverlays();

		new LoadTrackingFileTask().execute(filename);

		noteOverlay = new MyLocationItemizedOverlay(getResources().getDrawable(
				R.drawable.note), this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * This private class is used by DisplayTrackingInfoActivity to read and
	 * parse the file (ReadFile class), and add appropriate overlays on the map
	 * based on the information retrieved from the file. After the file is read,
	 * it uses RouteOverlay class to draw the route; then, it uses SlideshowTask
	 * to display the notes one by one in a slide show manner (having a delay of
	 * 3.5 seconds between each two notes).
	 * 
	 */
	private class LoadTrackingFileTask extends
			AsyncTask<String, String, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(
				DisplayTrackingInfoActivity.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Loading......");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			if (!ApplicationSettings.isExternalStorageAvailable())
				return false;
			try {

				ReadFile readfile = new ReadFile(params[0]);
				trackingInfo = readfile.readFile();
				trackingInfoSize = trackingInfo.size();
				if (trackingInfo.size() == 0)
					return false;

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (!result)
				Toast.makeText(DisplayTrackingInfoActivity.this,
						"Fail to load tracking info from sdcard",
						Toast.LENGTH_LONG).show();
			else {
				addOverlayItems();
				RouteOverlay overlay = new RouteOverlay(mapview, trackingInfo);
				mapOverlays.add(overlay);
				new SlideShowTask().execute();

			}

		}
	}

	/**
	 * This method displays an icon at the current index, which is increased by
	 * clicking the forward arrow on the map and decreased by clicking the back
	 * arrow on the map. This feature has been removed.
	 */
	private void addOverlayItems() {
		GeoPoint geopoint = new GeoPoint((int) (trackingInfo.get(currentIndex)
				.getLatitude() * 1e6), (int) (trackingInfo.get(currentIndex)
				.getLongitude() * 1e6));
		MyLocationItemizedOverlay itemizedoverlay = new MyLocationItemizedOverlay(
				getResources().getDrawable(R.drawable.location_off), this);
		OverlayItem overlayitem = new OverlayItem(geopoint, "location:", "-1");
		itemizedoverlay.addOverlay(overlayitem);
		if (!mapOverlays.isEmpty())
			mapOverlays.remove(0);
		mapOverlays.add(0, itemizedoverlay);

	}

	/**
	 * This private class shows the notes for a given route. If the user creates
	 * a note after he started tracking, the rowId of the note is saved along
	 * with its coordinate. This class checks the value of rowId for every
	 * element in the TrackingInfo. If it is a non-negative value, it indicates
	 * that a note has been created; hence, it displays a note icon at the
	 * corresponding coordinate.It also places a time delay of 3500 milliseconds
	 * between displaying every two notes.
	 * 
	 */
	private class SlideShowTask extends
			AsyncTask<String, TrackingInfo, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			Log.i(TAG,
					"size of trackingInfo:"
							+ Integer.toString(trackingInfoSize));
			for (TrackingInfo info : trackingInfo) {
				Log.i(TAG, "rowId:" + info.getRowId());
				if (!info.getRowId().equals("-1")) {
					publishProgress(info);
					long elapse = System.currentTimeMillis() + 3500;
					while (System.currentTimeMillis() < elapse) {
					}
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(TrackingInfo... values) {
			super.onProgressUpdate(values);

			GeoPoint geopoint = new GeoPoint(
					(int) (values[0].getLatitude() * 1e6),
					(int) (values[0].getLongitude() * 1e6));

			OverlayItem overlayitem = new OverlayItem(geopoint, "",
					values[0].getRowId());
			noteOverlay.addOverlay(overlayitem);
			Log.i(TAG,
					"size of noteOverlay:"
							+ Integer.toString(noteOverlay.size()));
			if (noteOverlay.size() == 1) {
				mapOverlays.add(noteOverlay);
			}
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
