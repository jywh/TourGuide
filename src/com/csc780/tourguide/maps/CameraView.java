package com.csc780.tourguide.maps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.csc780.tourguide.R;

public class CameraView extends Activity implements SurfaceHolder.Callback,
		OnClickListener {
	private static final String TAG = "CameraView";
	Camera mCamera;
	boolean mPreviewRunning = false;
	private Context mContext = this;
	private String imageName;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_preview);
		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		final ImageButton capture = (ImageButton)findViewById(R.id.imageButton_capture);
		capture.setOnClickListener(this);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		imageName = generateImageName();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {

			if (imageData != null) {

				new StoreImageTask(imageData).execute();

			}
		}
	};

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);

	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

	
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.e(TAG, "surfaceChanged");

		// XXX stopPreview() will crash if preview is not running
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		Camera.Parameters parameters = mCamera.getParameters();

        List<Size> sizes = parameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(sizes, w, h);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);

        mCamera.setParameters(parameters);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;

	public void onClick(View arg0) {

		mCamera.takePicture(null, mPictureCallback, mPictureCallback);

	}

	/**
	 * Generate a unqiue image name for each image
	 * @return
	 */
	private String generateImageName(){
		long now = System.currentTimeMillis();
		return Long.toString(now)+".jpg";
	}
	
	private class StoreImageTask extends AsyncTask<Byte, Boolean, Boolean>{

		private ProgressDialog dialog;
		private byte[] imageData;
		public StoreImageTask(byte[] imageData){
			dialog = new ProgressDialog(mContext);
			this.imageData = imageData;
		}
		
		@Override
		protected Boolean doInBackground(Byte... image) {
			
			File imageCache = getCacheDir();
			FileOutputStream fileOutputStream = null;
			try {

				Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
						imageData.length, null);

				fileOutputStream = new FileOutputStream(
						imageCache.toString() + "/" + imageName);

				BufferedOutputStream bos = new BufferedOutputStream(
						fileOutputStream);

				myImage.compress(CompressFormat.JPEG, 90, bos);

				bos.flush();
				bos.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if(result){
				Intent mIntent = new Intent();
				mIntent.putExtra(FindLocationActivity.IMAGE_NAME, imageName);
				setResult(RESULT_OK, mIntent);
				finish();
			}else{
				Toast.makeText(mContext, "Fail to save image, please try again", 
						Toast.LENGTH_SHORT).show();
				mCamera.startPreview();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Save image......");
			dialog.show();
		}
		
	}

}