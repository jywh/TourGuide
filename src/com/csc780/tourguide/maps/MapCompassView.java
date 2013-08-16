package com.csc780.tourguide.maps;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.csc780.tourguide.R;

/**
 * This class displays a small compass on the top left corner of the map. 
 *
 */
public class MapCompassView extends ImageView{

	public MapCompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setImageResource(R.drawable.ic_compass_needle);
	}

	private float direction=0;

	@Override
	protected void onDraw(Canvas canvas) {
		
		int height = this.getHeight();
		int width = this.getWidth();
	    canvas.rotate(direction, width / 2, height / 2);
	    super.onDraw(canvas);
		
	}
	
	public void setDirection(float direction){
		this.direction = direction;
		this.invalidate();
	}

}
