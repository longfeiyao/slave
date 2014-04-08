package com.example.slave;

import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraService extends SurfaceView implements SurfaceHolder.Callback{
	
	private SurfaceHolder mHolder;
	private Camera mCamera;
	String Tag = null;//display sysInfo

	public CameraService(Context context, Camera camera) {
		super(context);
		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (mHolder.getSurface()==null) {
			return;
		}
		/*
		 * stop preview when preview changed
		 */
		try {
			mCamera.startPreview();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		/*
		 * restart the preview in new setting
		 */
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			// TODO: handle exception
			Log.d(Tag, "Erro in starting camera preview!" + e.getStackTrace());
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//inform camera the location of preview 
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			
			// TODO: handle exception
			Log.d(Tag, "Erro setting camera preview" + e.getMessage());
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	       mCamera.release();
	       mCamera = null;
	}
	

	
	
}
