package com.csc780.tourguide.maps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * This class is responsible for all the interactions with the server and the
 * database.It connects to the server using an HTTP connection and exchanges
 * data with the server.All the necessary methods to communicate with the
 * database to insert a new note, to update an edited note, to delete a note, to
 * view a note and to upload/download a photo are implemented in this class.
 * 
 */
public class ServerInterface {
	// Declared Constants
	public static final String TAG = "ServerInterface";
	private final static String PHONE_IP="130.212.156.89";
	private final static String EMULATOR_IP="10.0.2.2";
	public static final String SERVER_URL = "http://"+PHONE_IP+"/test/index.php";
	public static final String IMAGE_URL = "http://"+PHONE_IP+"/test/image1.php";
	public static final String IMAGE_DIR = "http://"+PHONE_IP+"/test/uploads/thumb/";
	public static int MAX_IMAGE_SIZE = 85;
	public static HttpURLConnection connection = null;

	/**
	 * This method provides the server with location bounds. These bounds
	 * indicate the radius for which the notes should be retrieved from the
	 * database.
	 * 
	 * @param latBound1
	 *            specifies the lower bound of latitude
	 * @param latBound2
	 *            specifies the higher bound of latitude
	 * @param lonBound1
	 *            specifies the lower bound of longitude
	 * @param lonBound2
	 *            specifies the higher bound of longitude
	 * @return a string containing the latitudes, longitudes and ids of all the
	 *         notes that exist within the specified bounds.
	 */
	public static String getNearby(double latBound1, double latBound2,
			double lonBound1, double lonBound2) {
		String data;
		Log.i(TAG, "bounds:" + Double.toString(lonBound1));
		data = "command=" + URLEncoder.encode("getNearby");
		data += "&latitudebound1="
				+ URLEncoder.encode(Double.toString(latBound1))
				+ "&latitudebound2="
				+ URLEncoder.encode(Double.toString(latBound2))
				+ "&longitudebound1="
				+ URLEncoder.encode(Double.toString(lonBound1))
				+ "&longitudebound2="
				+ URLEncoder.encode(Double.toString(lonBound2));
		return executeHttpRequest(data);
	}

	/**
	 * This method asks the server to retrieve a particular note by providing
	 * its rowId.
	 * 
	 * @param rowId
	 *            the id of a particular note in the database
	 * @return the response from the server, which is a string containing the
	 *         information of that note.
	 */
	public static String getMemo(String rowId) {
		String data;
		data = "command=" + URLEncoder.encode("getMemo") + "&rowId="
				+ URLEncoder.encode(rowId);
		return executeHttpRequest(data);
	}

	/**
	 * This method asks the server to insert a new note to the database. It
	 * provides the server with the note information and ask the server to
	 * insert in into the database.
	 * 
	 * @param author
	 *            the name of the author of the note
	 * @param body
	 *            the note text that the user has written
	 * @param date
	 *            the note creation date, available from the system
	 * @param latitude
	 *            the latitude at which the note is created (user's current
	 *            latitude)
	 * @param longitude
	 *            the longitude at which the note is created (user's current
	 *            longitude)
	 * @return a string containing the rowId of the note just inserted. rowId
	 *         uniquely identifies each note in the database.
	 */
	public static String insertMemo(String author, String body, String date,
			String latitude, String longitude) {
		String data = "command=" + URLEncoder.encode("insertMemo");
		data += "&author=" + URLEncoder.encode(author) + "&body="
				+ URLEncoder.encode(body) + "&date=" + URLEncoder.encode(date)
				+ "&latitude=" + URLEncoder.encode(latitude) + "&longitude="
				+ URLEncoder.encode(longitude);
		return executeHttpRequest(data);
	}

	/**
	 * This method asks the server to edit a note. It provides the server with
	 * the rowId of the note that has been edited and needs to be updated in the
	 * database along with the information (author and body) that has been
	 * edited.
	 * 
	 * @param rowId
	 *            rowId of the note that has been edited and needs to be updated
	 * @param author
	 *            the author of the note that might have been edited
	 * @param body
	 *            the text of the note that might have been edited
	 * @return a string from the server containing the rowId of the note that
	 *         has been updated.
	 */

	public static String editMemo(String rowId, String author, String body) {
		String data = "command=" + URLEncoder.encode("editMemo");
		data += "&rowId=" + URLEncoder.encode(rowId) + "&author="
				+ URLEncoder.encode(author) + "&body="
				+ URLEncoder.encode(body);
		return executeHttpRequest(data);
	}

	/**
	 * This method asks the server to delete the note that has just been added
	 * to the database.
	 * 
	 * @param rowId
	 *            the rowId of the note that should be deleted
	 * @return the response from the server which is 1 if the note is deleted successfully.
	 */

	public static String deleteMemo(String rowId) {
		String data = "command=" + URLEncoder.encode("deleteMemo");
		data += "&rowId=" + URLEncoder.encode(rowId);
		return executeHttpRequest(data);
	}

	/**
	 * This method is used to communicate with the server by sending/receiving
	 * POST commands.It connects to the server, sends the requests and receive
	 * responses to and from the server.
	 * 
	 * @param data
	 *            a string representing the command and (possibly) arguments.
	 * @return a string which is the response from the server.
	 */
	private static String executeHttpRequest(String data) {
		String result = "";
		try {
			URL url = new URL(SERVER_URL);
			connection = (HttpURLConnection) url.openConnection();

			// specifies that this connection has input and output
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// disables caching to get the most up-to-date result
			connection.setUseCaches(false);
			connection.setRequestProperty("Connection", "Keep-Alive");
			// sets correct content type for the data
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			// sends the POST data
			DataOutputStream dataOut = new DataOutputStream(
					connection.getOutputStream());
			dataOut.writeBytes(data);
			dataOut.flush();
			dataOut.close();

			// receives the response from the server and stores it in result
			DataInputStream dataIn = new DataInputStream(
					connection.getInputStream());
			String inputLine;
			while ((inputLine = dataIn.readLine()) != null) {
				result += inputLine;
			}
			dataIn.close();
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	/**
	 * This method processes the stream that has been received from the server
	 * when the notes for nearby places are retrieved. The response from the
	 * server is a long string containing the latitude, longitude and rowId of
	 * all the notes that are within a specified range. First, notes should be
	 * separated from each other using the delimiter that the server used to
	 * concatenate them and then the information within each note is split and
	 * stored as an object in an array list which will be used to display note
	 * icons on the map.
	 * 
	 * @param stream
	 *            response from the server
	 */
	public static void processStream(String stream) {
		Log.i(TAG, stream);
		String[] temp = stream.split("@@@");
		FindLocationActivity.nearbyLocation.clear();
		for (String s : temp) {
			try {
				String[] temp1 = s.split("%%%"); // temp1[0] is rowId
				String[] temp2 = temp1[1].split("&&&"); // temp2[0] is latitude
														// temp2[1] is longitude
				FindLocationActivity.nearbyLocation.add(new NearbyLocationInfo(
						temp1[0], Double.parseDouble(temp2[0]), Double
								.parseDouble(temp2[1])));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method disconnects the connection
	 */
	public static void disconnect() {
		if (connection != null)
			connection.disconnect();
	}

	/**
	 * This method processes the stream that has been received from the server
	 * when a request to retrieve the note's information has been sent. The
	 * response from the server is a long string containing the author, date,
	 * body and an image (if any). They should be split based on different
	 * delimiters defined at the server and stored in an array to be displayed
	 * in a dialog.
	 * 
	 * @param stream
	 * @return
	 */
	public static String[] processMemoInfo(String stream) {
		Log.i(TAG, "String from server:" + stream);
		String[] temp = stream.split("@@@");
		try {
			String[] temp1 = temp[0].split("%%%");// temp1[0] is author
			String[] temp2 = temp1[1].split("&&&"); // temp2[0] is date,
			String[] temp3 = temp2[1].split("###"); // temp3[0] is body,
													// temp3[1] is image

			String date = (String) DateFormat.format("MM/dd/yy",
					Long.parseLong(temp2[0]));
			return new String[] { temp1[0], date, temp3[0], temp3[1] };
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method uploads a photo to the server and asks the server to store it
	 * in the database.
	 * 
	 * @param imagePath
	 *            the path of the image that should be uploaded
	 */
	public static void uploadImage(File imagePath) {
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		DataInputStream inStream = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		try {
			// ------------------ CLIENT REQUEST
			FileInputStream fileInputStream = new FileInputStream(imagePath);
			// open a URL connection
			URL url = new URL(IMAGE_URL);
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			// allows Inputs
			conn.setDoInput(true);
			// allows Outputs
			conn.setDoOutput(true);
			// disables caching
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("POST");
			// conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ imagePath.toString() + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			// send multi-part form data necessary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			// close streams
			Log.e("Debug", "File is written");
			fileInputStream.close();
			dos.flush();
			dos.close();
		} catch (MalformedURLException ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		} catch (IOException ioe) {
			Log.e("Debug", "error: " + ioe.getMessage(), ioe);
		}
		// ------------------ read the SERVER RESPONSE
		try {
			inStream = new DataInputStream(conn.getInputStream());
			String str;

			while ((str = inStream.readLine()) != null) {
				Log.e("Debug", "Server Response " + str);
			}
			inStream.close();

		} catch (IOException ioex) {
			Log.e("Debug", "error: " + ioex.getMessage(), ioex);
		}
	}

	/**
	 * This method calls other methods to retrieve a photo from the server and
	 * reduce the size of the photo
	 * 
	 * @return a photo
	 */
	public static Bitmap retreiveImage(String imageName) {

		try {
			InputStream fis = downloadImage(imageName);
			Bitmap bitmap = BitmapFactory.decodeStream(fis, null, null);
			fis.close();
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * This method downloads (retrieves) a photo from the server. It specifies
	 * the path of the photo on the file system in the server, opens a
	 * connection to the server and downloads the photo.
	 * 
	 * @param imageName
	 *            the name of the photo that should be retrieved
	 * @return response from the server
	 */
	public static InputStream downloadImage(String imageName) {

		URL myFileUrl = null;
		try {
			myFileUrl = new URL(IMAGE_DIR + imageName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			return conn.getInputStream();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
