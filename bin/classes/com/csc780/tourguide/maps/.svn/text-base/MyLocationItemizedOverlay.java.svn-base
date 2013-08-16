package com.csc780.tourguide.maps;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.csc780.tourguide.R;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * This class is instantiated when the user wants to see the information of the
 * notes that have been displayed with the tracking route. When the user taps on
 * the note, ServerInterface class is used to retrieve and process the
 * information of that note. If the note has been removed by the user, it will
 * inform the user that it has been deleted.
 * 
 */

public class MyLocationItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;

	public MyLocationItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		shadow = false;
		super.draw(canvas, mapView, shadow);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	/**
	 * This method overrides onTap() method for the notes displayed with the
	 * route the user has visited. When the user taps on the note,
	 * ServerInterface class is used to retrieve and process the information of
	 * that note. Then, it displays the information in a dialog. If the note has
	 * been removed by the user, it will inform the user that it has been
	 * deleted.
	 */
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		String rowId = item.getSnippet();
		// Toast.makeText(mContext, item.getSnippet(),
		// Toast.LENGTH_LONG).show();
		if (!rowId.equals("-1")) {
			String result = ServerInterface.getMemo(rowId);
			if (result.equals("")) {
				Toast.makeText(mContext, "note has been deleted",
						Toast.LENGTH_LONG).show();
				return true;
			} else {
				String[] memoInfo = ServerInterface.processMemoInfo(result);
				NoteDialog dialog = new NoteDialog(mContext,
						R.style.CustomDialogTheme, 100, 100, -1, false);
				dialog.setText(memoInfo[0], memoInfo[1], memoInfo[2]);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		}
		return false;
	}

	public OverlayItem getOverlayItem(int index) {
		return mOverlays.get(index);
	}
}
