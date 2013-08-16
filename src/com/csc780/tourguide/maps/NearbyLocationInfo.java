package com.csc780.tourguide.maps;

/**
 * This class is instantiated for every note that has been retrieved from the
 * database or has just been added to the database and should be displayed on
 * the map. It contains the latitude, longitude and rowId of each note. The
 * location information are used to display the note icon on the location it was
 * created and the rowId is used to retrieve the note information from the
 * database.
 * 
 */
public class NearbyLocationInfo {
	private String rowId;
	private int latE6;
	private int longE6;
	private double latitude;
	private double longitude;

	public NearbyLocationInfo(String rowId, double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		latE6 = degree2microDegree(latitude);
		longE6 = degree2microDegree(longitude);
		this.rowId = rowId;
	}

	public String getRowId() {
		return rowId;
	}

	private int degree2microDegree(double degree) {
		return (int) (degree * 1E6);
	}

	public int getLatE6() {
		return latE6;
	}

	public int getLongE6() {
		return longE6;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
