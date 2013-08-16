package com.csc780.tourguide.maps;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.csc780.tourguide.R;

/**
 * This class is responsible for displaying preferences in the menu. In this
 * application, preferences enable user to set the distance within which he
 * wants the notes to be retrieved.
 * 
 */
public class PrefsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("Preferences");
		addPreferencesFromResource(R.xml.settings);

	}

}
