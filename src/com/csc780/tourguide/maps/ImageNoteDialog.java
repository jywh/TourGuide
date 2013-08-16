package com.csc780.tourguide.maps;

import android.app.Dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.csc780.tourguide.R;

/**
 * This class is used to display a dialog (small window) for a note that
 * consists of both text and image. This dialog contains the note author, note
 * creation date, note text and a photo that is stored for that note. When the
 * user taps on a note icon on the map to see the content, if the note has an
 * image, an instance of this class is instantiated and the relevant information
 * and photo retrieved from the database are displayed on this dialog.
 * 
 */
public class ImageNoteDialog extends Dialog {

	public static final String TAG = "ImageNoteDialog";
	private TextView note;
	private TextView textAuthor;
	private TextView textDate;
	private ImageView imageRemove, photo;
	private String author, body, date;
	private Context context;
	private long rowId = -1;
	private int index;
	private Handler handler;
	private Bitmap bitmap;

	/**
	 * This is the dialog constructor for notes containing both text and image
	 * 
	 * @param context
	 * @param theme
	 * @param x
	 *            sets the width of the dialog
	 * @param y
	 *            sets the height of the dialog
	 * @param rowId
	 *            indicates the note that is being displayed. It is necessary to
	 *            know the row Id if the user wants to delete his note.
	 * @param isOwn
	 *            determines if the user is viewing his own note or another
	 *            user's note. If it is his note, he can delete it.
	 */
	public ImageNoteDialog(Context context, int theme, int x, int y,
			long rowId, boolean isOwn) {
		super(context, theme);
		this.context = context;
		this.rowId = rowId;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		int width = FindLocationActivity.LAYOUT_WIDTH / 2;
		int height = FindLocationActivity.LAYOUT_HEIGHT / 2;
		if (x > width)
			lp.x = x - width;

		if (y > height)
			lp.y = y - height - FindLocationActivity.LAYOUT_HEIGHT / 4;
		else
			lp.y = y - FindLocationActivity.LAYOUT_HEIGHT / 4;

		getWindow().setAttributes(lp);
		setContentView(R.layout.view_image_note_dialog);

		note = (TextView) findViewById(R.id.textView_note);
		textAuthor = (TextView) findViewById(R.id.textView_author);
		textDate = (TextView) findViewById(R.id.textView_date1);
		photo = (ImageView) findViewById(R.id.imageView_photo);
		photo.setVisibility(View.GONE);

		// delete icon for user's own note
		if (isOwn) {
			textDate.setVisibility(View.GONE);
			imageRemove = (ImageView) findViewById(R.id.imageView_remove);
			imageRemove.setVisibility(View.VISIBLE);
			imageRemove.setClickable(true);
			imageRemove.setOnClickListener(removeListener);
		}
	}

	/**
	 * An anonymous class instance that provides a listener for note's delete
	 * icon. By clicking on the delete icon, this callback method is called and
	 * the note is removed from the database. After a successful database query,
	 * the icon of the deleted note is removed from the map.
	 */
	private View.OnClickListener removeListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// deletes the note by using rowId
			String result = ServerInterface.deleteMemo(Long.toString(rowId));
			if (result.equals("1")) {
				Message msg = handler.obtainMessage();
				msg.what = FindLocationActivity.MESSAGE_REMOVE_OVERLAY;
				msg.obj = index;
				msg.sendToTarget();
			}
			dismiss();
		}
	};

	/**
	 * set method to set the index
	 * 
	 * @param index
	 *            refers to the index of a note within green notes (user's own
	 *            notes) itemized overlay array
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * This method uses the information retrieved from the database to set the
	 * text fields of the dialog. It also calls a method to retrieve the photo
	 * from the database and displays it.
	 * 
	 * @param info
	 *            an array containing information retrieved from the database
	 *            for a particular note
	 */

	public void setInfo(String[] info) {
		this.author = info[0];
		this.date = info[1];
		this.body = info[2];
		textAuthor.setText(author);
		textDate.setText(date);
		note.setText(body);
		bitmap = ServerInterface.retreiveImage(info[3]);
		if (bitmap != null) {
			photo.setImageBitmap(bitmap);
			photo.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.dismiss();
	}
}
