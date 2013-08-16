package com.csc780.tourguide.maps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class is used to read the file that contains tracking information. When
 * the user starts tracking, his locations are saved to a file until he stops
 * tracking. When he wants to see his tracking route, the file containing the
 * tracking information is read and parsed using this class.
 * 
 */

public class ReadFile {

	private BufferedReader reader;

	public ReadFile(String path) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(path));
	}

	/**
	 * This method reads the file line by line, parses each line and stores the
	 * location (longitude and latitude), the time and the rowId (if the user
	 * added a note for that location) on each line as a TrackigInfo object and
	 * adds it to an array list.
	 * 
	 * @return an array list of TrackingInfo objects which is used to draw the route.
	 */
	public ArrayList<TrackingInfo> readFile() {
		String line, rowId;
		StringTokenizer token;
		ArrayList<TrackingInfo> trackingInfo = new ArrayList<TrackingInfo>();
		double latitude, longitude;
		long elapse;
		try {
			while ((line = reader.readLine()) != null) {
				token = new StringTokenizer(line);
				latitude = Double.parseDouble(token.nextToken());
				longitude = Double.parseDouble(token.nextToken());
				elapse = Long.parseLong(token.nextToken());
				rowId = token.nextToken();
				trackingInfo.add(new TrackingInfo(latitude, longitude, elapse,
						rowId));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return trackingInfo;
	}
}
