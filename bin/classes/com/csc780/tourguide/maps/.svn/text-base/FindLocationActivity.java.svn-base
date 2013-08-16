package com.csc780.tourguide.maps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.csc780.tourguide.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * This is the main activity of the application. When the user starts the
 * application, his current location and all the existing notes in the database,
 * which are within a specified radius, are displayed on the map. This activity
 * uses other classes to provide the user with different functionalities and
 * feature of this application.
 */

public class FindLocationActivity extends MapActivity {

	public static final String TAG = "FindLocationActivity";
	public static int LAYOUT_HEIGHT = 800;
	public static int LAYOUT_WIDTH = 480;
	private final int DIALOG_SHOW_TRACKING = 1;

	private MapView mapview;
	private MapController mapController;

	// map overlays
	private List<Overlay> mapOverlays;
	private LocationItemizedOverlay itemizedoverlay;
	private LocationItemizedOverlay myItemizedOverLay;
	private MyLocation myLocationOverlay;

	private Handler mHandler;
	private Handler threadHandler;

	private long lastBound = 0;

	private static final int MESSAGE_COMPUTE_DISTANCE = 103;
	private static final int MESSAGE_DISTANCE_ALERT = 104;
	public static final int MESSAGE_ADD_MEMO = 105;
	public static final int MESSAGE_REMOVE_OVERLAY = 106;
	public static final int MESSAGE_RADIUS_CHANGE = 107;
	public static final int MESSAGE_TAKE_PHOTO = 108;
	private static final int ACTIVITY_PREF = 1;
	protected static final int ACTIVITY_TAKE_PHOTO = 2;

	public static final String IMAGE_NAME = "imageName";
	// default values
	private double currentLatitude = 1000., currentLongitude = 1000.,
			prevLatitude = 1000., prevLongitude = 1000., latBound1 = 0.,
			latBound2 = 0., longBound2 = 0., longBound1 = 0.;

	private ApplicationSettings app;

	// compass on the map
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private MapCompassView mView;

	// retrieved notes
	public static ArrayList<NearbyLocationInfo> nearbyLocation = new ArrayList<NearbyLocationInfo>();
	// added notes
	public static ArrayList<NearbyLocationInfo> ownMemo = new ArrayList<NearbyLocationInfo>();
	private AddImageNoteDialog imageNoteDialog;

	// tracking variables
	private static ArrayList<TrackingInfo> trackingInfo = new ArrayList<TrackingInfo>();
	public static boolean startTracking = false;
	private String[] trackingFiles = null;
	private File[] files = null;

	private float old_radius;

	// a sensor listener for accelerometer
	private final SensorEventListener mListener = new SensorEventListener() {
		float[] magnitude_values;
		boolean sensorReady = false;
		float[] accelerometer_values;
		float degree = 0;
		float[] mValues = new float[3];

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_MAGNETIC_FIELD:
				magnitude_values = event.values.clone();
				sensorReady = true;
				break;
			case Sensor.TYPE_ACCELEROMETER:
				accelerometer_values = event.values.clone();
			}

			if (magnitude_values != null && accelerometer_values != null
					&& sensorReady) {
				sensorReady = false;

				float[] R = new float[16];
				float[] I = new float[16];

				SensorManager.getRotationMatrix(R, I,
						this.accelerometer_values, this.magnitude_values);

				SensorManager.getOrientation(R, mValues);
				// radius to degree
				degree = (float) (mValues[0] * 180 / Math.PI);
			} else {
				// even though this method is deprecated, but it does still
				// work, and it is better than nothing
				mValues = event.values;
				degree = mValues[0];
			}

			mView.setDirection(degree);
		}

	};

	/**
	 * This method gets the device's screen resolution.The size of the smaller
	 * windows such as note dialogs is based on these values.
	 */
	public void getScreenResolution() {
		Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		LAYOUT_WIDTH = d.getWidth();
		LAYOUT_HEIGHT = d.getHeight();
	}

	/**
	 * This is a callback method which is called when the activity is created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.find_location);
		getScreenResolution();

		// preferences
		app = (ApplicationSettings) getApplication();
		app.onSettingChange();

		// displays a map
		mapview = (MapView) findViewById(R.id.mapview);

		// displays a small compass on top of the map
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mView = (MapCompassView) findViewById(R.id.imageView_compass_needle);

		mapview.setBuiltInZoomControls(true);
		mapController = mapview.getController();
		mapController.setZoom(20);

		mapview.setClickable(true);
		mapview.setEnabled(true);

		mapOverlays = mapview.getOverlays();

		// a handler that performs different functionalities based on the
		// messages that it receives.
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				// if the user moves more than 10 meters, notes on the map
				// will be updated.
				case MESSAGE_DISTANCE_ALERT:
					Float distance = (Float) msg.obj;
					if (distance > 10.) {
						computeBound();
						new GetNearbyPlacesTask().execute();
					}
					break;
				/*
				 * if a new note has been added to the database, a new note icon
				 * will be displayed on the map. If the tracking is on, the
				 * rowId of the new note will be saved.
				 */
				case MESSAGE_ADD_MEMO:
					addNewMemoOverlayItem();
					if (startTracking) {
						String rowId = ownMemo.get(ownMemo.size() - 1)
								.getRowId();
						trackingInfo.get(trackingInfo.size() - 1).saveRowId(
								rowId);
					}
					break;
				// if a note has been removed from the database, its icon will
				// be removed from the map.
				case MESSAGE_REMOVE_OVERLAY:
					Integer index = (Integer) msg.obj;
					Log.i(TAG, "remove note:" + Integer.toString(index));
					addAllOwnMemoOverlayItem(index);
					break;
				// if the user wants to take a photo, a message will be sent to
				// the CameraView class.
				case MESSAGE_TAKE_PHOTO:
					Intent takePhotoIntent = new Intent(
							FindLocationActivity.this, CameraView.class);
					startActivityForResult(takePhotoIntent, ACTIVITY_TAKE_PHOTO);
				}
			}

		};

		new ComputeDistanceThread().start();
		myLocationOverlay = new MyLocation(this, mapview);
		mapOverlays.add(myLocationOverlay);
		myLocationOverlay.disableCompass();
		itemizedoverlay = new LocationItemizedOverlay(getResources()
				.getDrawable(R.drawable.note), this, mapview);
		myItemizedOverLay = new LocationItemizedOverlay(getResources()
				.getDrawable(R.drawable.note_green), this, mapview);
		myItemizedOverLay.setHandler(mHandler);
		// new UploadImageTestTask().execute();
	}

	/**
	 * This method computes the bounds within which the notes should be
	 * retrieved. RADIUS_OFFSET is set according to the user preferences but it
	 * has a default value that is used if the user does not change the
	 * preferences.
	 */
	private void computeBound() {
		latBound1 = currentLatitude - ApplicationSettings.RADIUS_OFFSET;
		latBound2 = currentLatitude + ApplicationSettings.RADIUS_OFFSET;
		longBound1 = currentLongitude - ApplicationSettings.RADIUS_OFFSET;
		longBound2 = currentLongitude + ApplicationSettings.RADIUS_OFFSET;
	}

	/**
	 * This is a callback method which is called when the application resumes.
	 */

	@Override
	protected void onResume() {
		myLocationOverlay.enableMyLocation();
		mSensorManager.registerListener(mListener, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
		super.onResume();
	}

	/**
	 * This method retrieves the location information (latitude and longitude)
	 * for every note that has been retrieved from the database, instantiates a
	 * new OverlayItem object and adds it to the array list containing all of
	 * these note. In addition, it sets the snippet to -1 which indicates an
	 * note that has not been added by the current user .
	 */
	private void addLocationOverlayItems() {
		int size = nearbyLocation.size();
		itemizedoverlay.clearOverlay();
		for (int i = 0; i < size; i++) {
			GeoPoint geopoint = new GeoPoint(nearbyLocation.get(i).getLatE6(),
					nearbyLocation.get(i).getLongE6());
			OverlayItem overlayitem = new OverlayItem(geopoint, "", "-1");
			itemizedoverlay.addOverlay(overlayitem);
		}

	}

	/**
	 * When a new note is added to the database, an object containing its
	 * location information and rowId is added to ownMemo. The last item in the
	 * ownMemo is the most recent note that has been added and should be
	 * displayed on the map. This method retrieves the location information for
	 * the new note instantiates a new overlay item and adds it to the array
	 * list that contains all the new notes.
	 */
	private void addNewMemoOverlayItem() {
		int index = ownMemo.size() - 1;
		NearbyLocationInfo info = ownMemo.get(index);
		GeoPoint geopoint = new GeoPoint(info.getLatE6(), info.getLongE6());
		OverlayItem overlayitem = new OverlayItem(geopoint,
				Integer.toString(index), info.getRowId());
		myItemizedOverLay.addOverlay(overlayitem);
		if (myItemizedOverLay.size() == 1)
			mapOverlays.add(myItemizedOverLay);
	}

	/**
	 * This method is called to remove the note icon from the map after the user
	 * removes one of the notes he has just created. It removes all the users
	 * notes from the map, removes the deleted note from the array list
	 * containing all user's notes and adds the rest of user notes to the array
	 * list of user's overlay items.
	 * 
	 * @param index
	 *            index of the note that has been removed.
	 */
	private void addAllOwnMemoOverlayItem(int index) {
		mapOverlays.remove(myItemizedOverLay);
		ownMemo.remove(index);
		myItemizedOverLay = new LocationItemizedOverlay(getResources()
				.getDrawable(R.drawable.note_green), FindLocationActivity.this,
				mapview);
		myItemizedOverLay.setHandler(mHandler);
		if (ownMemo.isEmpty())
			return;
		for (int i = 0; i < ownMemo.size(); i++) {
			NearbyLocationInfo info = ownMemo.get(i);
			GeoPoint geopoint = new GeoPoint(info.getLatE6(), info.getLongE6());
			OverlayItem overlayitem = new OverlayItem(geopoint,
					Integer.toString(i), info.getRowId());
			myItemizedOverLay.addOverlay(overlayitem);
		}
		mapOverlays.add(myItemizedOverLay);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mListener);
		myLocationOverlay.disableMyLocation();
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		try {
			threadHandler.getLooper().quit();
			ServerInterface.disconnect();
		} catch (NullPointerException npe) {
		}
		super.onDestroy();

	}

	/**
	 * This method overrides onCreatedialog for displaying tracking files after
	 * the user selects Show Tracking in the menu. It displays all the tracking
	 * files in order of their creation date and lets the user choose the file
	 * he wants to see the tracking information for. When the user selects a
	 * file, an intent is sent to show the tracking on the map using the file
	 * information.
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {

		switch (id) {
		case DIALOG_SHOW_TRACKING:
			if (files == null)
				listFilesByName();

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			return builder
					.setTitle("Choose One")
					.setItems(trackingFiles,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											FindLocationActivity.this,
											DisplayTrackingInfoActivity.class);
									intent.putExtra(
											DisplayTrackingInfoActivity.FILENAME,
											files[which].getPath());
									startActivity(intent);
									dialog.dismiss();
								}
							}).create();
		}
		return super.onCreateDialog(id, args);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.maps_menu, menu);
		return true;
	}

	/**
	 * This method provides functionalities for each menu item that is being
	 * selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		/*
		 * when Add Note is selected, an AddNoteDialog object is instantiated
		 * and a dialog is displayed to let the user enter note's information
		 * (text only).
		 */
		case R.id.menu_add_note:
			if (currentLatitude == 1000.) {
				Toast.makeText(this, "Wait for GPS to fix your location",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			AddNoteDialog addNoteDialog = new AddNoteDialog(this,
					R.style.CustomDialogTheme, currentLatitude,
					currentLongitude, mHandler);
			addNoteDialog.show();
			break;
		/*
		 * when Add Image Note is selected, an AddImageNoteDialog object is
		 * instantiated and a dialog is displayed to the the user create a note
		 * containing both text and image.
		 */
		case R.id.menu_add_imge_note:
			if (currentLatitude == 1000.) {
				Toast.makeText(this, "Wait for GPS to fix your location",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			imageNoteDialog = new AddImageNoteDialog(this,
					R.style.CustomDialogTheme, currentLatitude,
					currentLongitude, mHandler);
			imageNoteDialog.show();
			break;
		// selecting Satellite View changes the view.
		case R.id.menu_view:
			if (mapview.isSatellite())
				mapview.setSatellite(false);
			else
				mapview.setSatellite(true);
			break;
		/*
		 * when Start Tracking is selected, a flag is set and the current
		 * location and time is added to the relevant array list as the starting
		 * point.From then on, every location that the user visits will be added
		 * to the array list. The menu item will be changed to Stop Tracking,
		 * when selected, tracking will be stopped and the tracking info will be
		 * saved to a file.
		 */
		case R.id.menu_start_tracking:
			if (!startTracking) {
				startTracking = true;
				Toast.makeText(this, "Start Tracking", Toast.LENGTH_SHORT)
						.show();
				trackingInfo.add(new TrackingInfo(currentLatitude,
						currentLongitude, 0));
				lastBound = System.currentTimeMillis();
			} else {
				startTracking = false;
				Toast.makeText(this, "Stop Tracking", Toast.LENGTH_SHORT)
						.show();
				new ExportTrackingInfoToSDTask().execute();
			}
			break;
		/*
		 * when Show Tracking is selected, all tracking files will be displayed
		 * and the user can select one of them to view the tracking information
		 * on the map.
		 */
		case R.id.menu_show_tracking:
			showDialog(DIALOG_SHOW_TRACKING);
			break;
		/*
		 * when Settings is selected, the user is directed to the preferences
		 * menu which enables him to choose the radius within which he wants to
		 * retrieve the notes.
		 */
		case R.id.menu_setttings:
			old_radius = ApplicationSettings.RADIUS_OFFSET;
			Intent prefIntent = new Intent(this, PrefsActivity.class);
			startActivityForResult(prefIntent, ACTIVITY_PREF);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * This methods changes the menu items when the user selects that menu item.
	 * Satellite View will be changed to Map View and vice versa when the user
	 * selects the third item. Start Tracking changes to Stop Tracking and vice
	 * versa when the user selects the fourth item of the menu.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem item = menu.getItem(2);
		MenuItem item1 = menu.getItem(3);
		if (mapview.isSatellite()) {
			item.setTitle("Map View");
			item.setIcon(R.drawable.ic_menu_mapview);
		} else {
			item.setTitle("Satellite View");
			item.setIcon(R.drawable.ic_menu_satellite);
		}

		if (startTracking) {
			item1.setTitle(R.string.stop_tracking);
		} else
			item1.setTitle(R.string.start_tracking);

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * This method is used for processing the results of two intents sent from
	 * the main activity. First, it will check the intent result for the
	 * Settings item of the menu. In this case, it will check the radius that
	 * the user has selected and if it is different from the previous radius, it
	 * computes the new bounds and retrieves the notes within the new
	 * bounds.Second, it checks the result of the intent for the camera and if
	 * the result is OK, it will add the photo to the user's note.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ACTIVITY_PREF
				&& old_radius != ApplicationSettings.RADIUS_OFFSET) {
			computeBound();
			new GetNearbyPlacesTask().execute();
		} else if (requestCode == ACTIVITY_TAKE_PHOTO) {
			if (resultCode == RESULT_OK && imageNoteDialog != null) {
				String imageName = data.getStringExtra(IMAGE_NAME);
				Log.i(TAG, "photo name: " + imageName);
				imageNoteDialog.setImage(imageName);
			}
		}

	}

	/**
	 * This is an auxiliary class that gets the user's current location and
	 * displays it on the map. The map center is set to the current location. On
	 * every location change, a message is sent to a thread to compute the
	 * distance. If tracking is on, current location and time are stored in an
	 * array list to be saved on a file.
	 */
	class MyLocation extends MyLocationOverlay {

		MapView mapview;

		public MyLocation(Context context, MapView mapView) {
			super(context, mapView);
			this.mapview = mapView;
		}

		@Override
		public synchronized void onLocationChanged(Location location) {
			super.onLocationChanged(location);

			prevLatitude = currentLatitude;
			prevLongitude = currentLongitude;
			currentLatitude = location.getLatitude();
			currentLongitude = location.getLongitude();

			mapview.getController().setCenter(
					new GeoPoint((int) (currentLatitude * 1e6),
							(int) (currentLongitude * 1e6)));
			Message msg1 = threadHandler.obtainMessage();
			msg1.what = MESSAGE_COMPUTE_DISTANCE;
			msg1.sendToTarget();
			if (startTracking) {
				trackingInfo.add(new TrackingInfo(currentLatitude,
						currentLongitude, System.currentTimeMillis()
								- lastBound));

			}
		}

	}

	/**
	 * This private class is a thread that computes the distance between the
	 * previous and current location and sends the result to the main activity
	 * as a message.
	 * 
	 */
	private class ComputeDistanceThread extends Thread {

		@Override
		public void run() {
			Looper.prepare();
			threadHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {

					if (msg.what == MESSAGE_COMPUTE_DISTANCE) {
						float distance = computeDistanceOfTwoPoints(
								currentLatitude, currentLongitude,
								prevLatitude, prevLongitude);
						Message message = mHandler.obtainMessage();
						// Log.i(TAG, "distance: " + Float.toString(distance));
						message.what = MESSAGE_DISTANCE_ALERT;
						message.obj = distance;
						message.sendToTarget();
					}
				}

			};
			Looper.loop();
		}

		/**
		 * This method computes the distance between two point using their
		 * latitude and longitude.
		 * 
		 * @param startLat
		 *            latitude of the starting
		 * @param startLong
		 *            longitude of starting point
		 * @param endLat
		 *            latitude of ending point
		 * @param endLong
		 *            longitude of ending point
		 * @return the distance between two points
		 */
		private float computeDistanceOfTwoPoints(double startLat,
				double startLong, double endLat, double endLong) {
			float[] results = new float[4];
			Location.distanceBetween(startLat, startLong, endLat, endLong,
					results);
			if (results.length > 0)
				return results[0];
			return -1f;

		}

	}

	/**
	 * This is a private class, which uses ServerInterface class to retrieve the
	 * notes near current location. After receiving the response from the
	 * server, it calls another method in the ServerInterface class to process
	 * the response. It also calls the method to add the retrieved notes to the
	 * map overlay array list.
	 * 
	 */
	private class GetNearbyPlacesTask extends
			AsyncTask<String, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			// get current location and send HTTP request
			Log.i(TAG, "Start send query to server");
			String result = ServerInterface.getNearby(latBound1, latBound2,
					longBound1, longBound2);
			Log.i(TAG, "query result:" + result);
			if (result.length() == 0)
				return false;
			ServerInterface.processStream(result);
			addLocationOverlayItems();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) {
				mapOverlays.add(itemizedoverlay);
			} else {
				itemizedoverlay.clearOverlay();
			}

		}
	}

	/**
	 * This private class is responsible for saving the tracking information on
	 * a file. It creates a file and uses the current date and the name of the
	 * city the user is in to name the file. Then, it writes tracking
	 * information (latitude, longitude, time and rowId) which is stored in an
	 * array list to the file.
	 */
	private class ExportTrackingInfoToSDTask extends
			AsyncTask<String, String, Boolean> {
		private Activity activity = FindLocationActivity.this;
		private ProgressDialog dialog = new ProgressDialog(activity);

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result)
				Toast.makeText(activity, "tracking info stored successfully",
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(activity, "fail to store tracking info",
						Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Export tracking info to sdcard");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			// open external directory for exporting
			if (ApplicationSettings.isExternalStorageAvailable()) {
				try {
					File storeDir = new File(ApplicationSettings.APP_DIR);
					if (!storeDir.exists())
						storeDir.mkdirs();
					String fileName = Long.toString(System.currentTimeMillis())
							+ "_" + getCurrentCity();
					File infoFile = new File(storeDir, fileName);
					// Log.i(TAG, "file name:"+infoFile.toString());
					StringBuffer buffer = new StringBuffer();
					for (TrackingInfo info : trackingInfo)
						buffer.append(info.toString() + "\n");
					FileOutputStream fout = new FileOutputStream(infoFile);
					String content = buffer.toString();
					fout.write(content.getBytes());
					fout.close();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return false;
		}

		/**
		 * This methods uses the current latitude and longitude to get the city
		 * name.
		 * 
		 * @return name of the cite
		 */
		private String getCurrentCity() {
			Geocoder geocoder = new Geocoder(activity);
			try {
				List<Address> address = geocoder.getFromLocation(
						currentLatitude, currentLongitude, 1);
				if (address != null & address.size() > 0)
					return address.get(0).getLocality();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "Unknown";
		}

	}

	/**
	 * This method sorts the tracking files according to their creation date.
	 */
	private void listFilesByName() {
		File dir = new File(ApplicationSettings.APP_DIR);
		files = dir.listFiles();

		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File object1, File object2) {
				return object1.getName().toLowerCase()
						.compareTo(object2.getName().toLowerCase());
			}
		});
		long2timeList();
	}

	/**
	 * This method renames the tracking files. It changes the format of the date
	 * from milliseconds to a user-friendly date and time format in the file
	 * name.
	 */
	private void long2timeList() {
		ArrayList<String> info = new ArrayList<String>();
		String name;
		for (File f : files) {
			name = f.getName();
			String[] temp = name.split("_");
			String date = (String) DateFormat.format("MMM dd, h:mmaa",
					Long.parseLong(temp[0]));
			info.add(temp[1] + ", " + date);
		}
		trackingFiles = info.toArray(new String[info.size()]);
	}

}
