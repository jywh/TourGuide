package com.csc780.tourguide.maps;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * This class displays the tracking route on the map after the user selects Show
 * Tracking in the menu. The information for the route is stored in the
 * TrackingInfo array list whose elements, which contain coordinates, are read
 * one by one and a line is drawn between each two adjacent coordinates.
 */
public class RouteOverlay extends Overlay {

	private Projection projection;
	private ArrayList<TrackingInfo> trackingInfo;

	public RouteOverlay(MapView mapview, ArrayList<TrackingInfo> trackingInfo) {
		this.projection = mapview.getProjection();
		this.trackingInfo = trackingInfo;
	}

	/**
	 * This method draws a line between each two adjacent coordinates in the
	 * TrackingInfo array list.
	 * 
	 */
	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		super.draw(canvas, mapv, shadow);

		if (trackingInfo.size() < 2)
			return;

		Paint mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.rgb(138, 43, 226));
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(3);

		GeoPoint firstPoint = new GeoPoint((int) (trackingInfo.get(0)
				.getLatitude() * 1e6), (int) (trackingInfo.get(0)
				.getLongitude() * 1e6));
		GeoPoint lastPoint;
		Point p1, p2;
		Path path;
		for (int i = 1; i < trackingInfo.size(); i++) {
			lastPoint = new GeoPoint(
					(int) (trackingInfo.get(i).getLatitude() * 1e6),
					(int) (trackingInfo.get(i).getLongitude() * 1e6));
			p1 = new Point();
			p2 = new Point();
			path = new Path();
			projection.toPixels(firstPoint, p1);
			projection.toPixels(lastPoint, p2);

			path.moveTo(p2.x, p2.y);
			path.lineTo(p1.x, p1.y);

			canvas.drawPath(path, mPaint);
			firstPoint = lastPoint;
		}
	}

}
