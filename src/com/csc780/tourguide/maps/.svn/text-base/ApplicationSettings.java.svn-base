package com.csc780.tourguide.maps;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * This class provides functionality for options defined in the PrefActivity
 * class. It sets the offset within which the user wants to retrieve
 * the notes according to the radius (distance) that the user chooses in the preferences.
 * 
 */
public class ApplicationSettings extends Application implements
		OnSharedPreferenceChangeListener {

	public static String APP_DIR = Environment.getExternalStorageDirectory()
			.toString() + "/tour_guide";
	public static int MAX_IMAGE_SIZE = 85;

	public static boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	private SharedPreferences settings;

	@Override
	public void onCreate() {
		super.onCreate();
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		settings.registerOnSharedPreferenceChangeListener(this);
	}

	public static float RADIUS_OFFSET = 0.0005f;

	/**
	 * This method sets the offset to be added to the current location's longitude
	 * and latitude based on the radius (distance) that user chooses.
	 */
	public void onSettingChange() {
		String distance = settings.getString("pref_distance", "0");

		if (distance.equals("0")) {
			RADIUS_OFFSET = 0.00025f;
		}
		if (distance.equals("1")) {
			RADIUS_OFFSET = 0.0005f;
		}
		if (distance.equals("2")) {
			RADIUS_OFFSET = 0.00075f;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {

		onSettingChange();

	}

}
