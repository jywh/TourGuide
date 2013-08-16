package com.csc780.tourguide.maps;

import java.io.File;
import java.io.FileInputStream;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.csc780.tourguide.R;

/**
 * This class is used to display a dialog (small window) when a user wants to
 * add a note containing a photo. The user can enter his name and the text for
 * the note in this dialog. The date is automatically set.He can then take a
 * photo using his phone's camera.By clicking on a save button, all the above
 * information along with the user's current location (longitude, latitude) will
 * be stored in the database.
 * 
 */

public class AddImageNoteDialog extends Dialog {

	public static final String TAG = "AddNoteDialog";
	private EditText editBody;
	private EditText editAuthor;
	private TextView textDate;
	private ImageView photo;
	private long now;
	private Context context;
	private double latitude;
	private double longitude;
	private Button saveButton;
	private Button cancelButton;
	private long rowId = -1;
	private Handler mHandler;
	private File imagePath = null;

	/**
	 * This constructor is used to add a note.
	 * 
	 * @param context
	 * @param theme
	 * @param latitude
	 *            of the user's current location
	 * @param longitude
	 *            of the user's current location
	 */
	public AddImageNoteDialog(final Context context, int theme,
			double latitude, double longitude, Handler handler) {
		super(context, theme);
		this.latitude = latitude;
		this.context = context;
		this.longitude = longitude;
		this.mHandler = handler;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.y = -150;
		getWindow().setAttributes(lp);
		setContentView(R.layout.add_image_note_dialog);
		editBody = (EditText) findViewById(R.id.editText_body);
		editAuthor = (EditText) findViewById(R.id.editText_author);
		textDate = (TextView) findViewById(R.id.textView_date1);
		saveButton = (Button) findViewById(R.id.button_save);
		cancelButton = (Button) findViewById(R.id.button_cancel);
		photo = (ImageView) findViewById(R.id.imageView_photo);
		photo.setClickable(true);
		now = System.currentTimeMillis();
		// sets the date
		String nowText = (String) DateFormat.format("MM/dd/yy", now);
		textDate.setText(nowText);
		editAuthor.setFocusable(true);

		// a listener for saving the note in the database
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveMemo();
				dismiss();
			}
		});

		// a listener for leaving the add new note dialog and going back to the
		// application main screen
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		// a listener for taking a photo using the camera
		photo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = mHandler.obtainMessage();
				msg.what = FindLocationActivity.MESSAGE_TAKE_PHOTO;
				msg.sendToTarget();
			}
		});

	}

	/**
	 * On clicking on save, a task is instantiated to save the new note to the
	 * database
	 */

	private void saveMemo() {
		if (editBody.getText().toString().length() == 0)
			return;
		new InsertMemoTask().execute();
	}

	/**
	 * On pressing back button, the current activity will be exited.
	 */

	@Override
	public void onBackPressed() {
		this.dismiss();
		super.onBackPressed();
	}

	/**
	 * After the photo is taken using the camera, it is stored in the phone's
	 * cache. This method retrieves the photo from the cache and instantiates a
	 * task to make the photo smaller and display it on the note dialog.
	 * 
	 * @param imageName
	 *            the name of the image save in cache directory
	 */
	public void setImage(String imageName) {
		File cache = context.getCacheDir();
		imagePath = new File(cache, imageName);
		new SetImageTask().execute(imagePath);
		// Bitmap thumbnail = reduceImageSize(imagePath, 85);
		// if (thumbnail != null)
		// photo.setImageBitmap(thumbnail);
	}

	/**
	 * This class is reduces the size of the photo by calling another method and
	 * after the photo is resized, it displays it on the dialog.
	 * 
	 */
	private class SetImageTask extends AsyncTask<File, Boolean, Bitmap> {

		@Override
		protected Bitmap doInBackground(File... params) {

			return reduceImageSize(params[0], 85);

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null)
				photo.setImageBitmap(result);
		}

		/**
		 * Reduce image size to save memory
		 * 
		 * @param imagePath the path to the image file
		 * @param maxSize the size (width and height are the same) the image
		 * 				going to be displayed
		 * @return an smaller size of bitmap of the image
		 */
		private Bitmap reduceImageSize(File imagePath, int maxSize) {

			try {
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;

				FileInputStream fis = new FileInputStream(imagePath);
				BitmapFactory.decodeStream(fis, null, o);
				fis.close();

				int scale = 1;
				if (o.outHeight > maxSize || o.outWidth > maxSize) {
					scale = (int) Math.pow(
							2,
							(int) Math.round(Math.log(maxSize
									/ (double) Math
											.max(o.outHeight, o.outWidth))
									/ Math.log(0.5)));
				}

				// Decode with inSampleSize
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				fis = new FileInputStream(imagePath);
				Bitmap thumbnail = BitmapFactory.decodeStream(fis, null, o2);
				fis.close();
				return thumbnail;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	/**
	 * This class inserts the new note to the database using a method in
	 * ServerInterface class. If the note text is successfully inserted, then
	 * another method in the ServerInterface class is called to upload the
	 * photo. It also stores the rowId and current location information of the
	 * new note in an array that contains all the notes the user has just added.
	 * This array is used to display note icons on the map for the new notes.
	 * 
	 */
	private class InsertMemoTask extends AsyncTask<String, Boolean, Boolean> {

		ProgressDialog dialog = new ProgressDialog(context);

		@Override
		protected Boolean doInBackground(String... params) {
			String author = editAuthor.getText().toString();
			String body = editBody.getText().toString();

			// Log.i(TAG, "latitude:"+Double.toString(latitude));
			String result = "";
			Log.i("AddNoteDialog", "rowId:" + Long.toString(rowId));
			if (author.length() == 0)
				author = "Anonymous";
			result = ServerInterface.insertMemo(author, body,
					Long.toString(now), Double.toString(latitude),
					Double.toString(longitude));
			if (!result.equals("-1")) {
				if (imagePath != null)
					ServerInterface.uploadImage(imagePath);
				// adds the rowId and current location information to an array
				// containing all the notes that the user has just created.
				FindLocationActivity.ownMemo.add(new NearbyLocationInfo(result,
						latitude, longitude));
			}

			Log.i(TAG, "insert memo result: " + result);
			return true;
		}

		/**
		 * Sends the proper message to main activity
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result && rowId < 0) {
				Message msg = mHandler
						.obtainMessage(FindLocationActivity.MESSAGE_ADD_MEMO);
				msg.sendToTarget();
			}

		}

		/**
		 * Informs the user that a note is being uploaded to the server
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Uploading note to server......");
			dialog.show();
		}
	}
}
