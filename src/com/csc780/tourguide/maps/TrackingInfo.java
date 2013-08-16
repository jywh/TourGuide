package com.csc780.tourguide.maps;

/**
 * 
 * This class is used to store the information needed to display a tracking
 * route. Each instance of this class contains longitude, latitude, and time of
 * one of the locations that user visited while tracking. It also has the rowId
 * of a note that the user has added for that location while tracking his route
 * (if any).
 * 
 */
public class TrackingInfo {

	private double latitude, longitude;
	private long elapse; // in milliseconds
	private String rowId = "-1";

	public TrackingInfo(double latitude, double longitude, long elapse) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.elapse = elapse;
	}

	public TrackingInfo(double latitude, double longitude, long elapse,
			String rowId) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.elapse = elapse;
		this.rowId = rowId;
	}

	@Override
	public String toString() {
		String text = "";

		text += Double.toString(latitude) + " " + Double.toString(longitude)
				+ " " + Long.toString(elapse) + " " + rowId;

		return text;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public long getElapse() {
		return elapse;
	}

	public void saveRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getRowId() {
		return rowId;
	}

}
