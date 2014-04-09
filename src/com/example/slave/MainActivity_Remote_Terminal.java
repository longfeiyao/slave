package com.example.slave;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.*;
import android.hardware.Camera.PictureCallback;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity_Remote_Terminal extends Activity implements View.OnClickListener{

	View v;
	private static final int TAKE_PHOTO = 1;//User mode of camera
	private static final int RECORD_VIDEO = 2;//user mode of streaming
	private Time currentTime = null; // current time of the system

	private static boolean camero_on = false;
	Camera iCamera;
	private CameraService iCameraPreview;
	private static FrameLayout previewFrameLayout;
	private static Boolean onStreaming = false;//flag true if the system is on streaming
	Intent camIntent;
	private final int isData = 0;
	Bitmap bmp;
	private FileOutputStream fileOutputStream = null;
	
	
	/*
	 * file manupilation
	 */
	//private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private final String pic_Dir = Environment.getExternalStorageDirectory().toString()+ "/slave/pic";// directory of this app
	private final String checkingFile = "/sdcard/slave/myvideo.mp4";
	private Uri fileUri;

	private TextView Remote_terminal_Network_state;
	private TextView RCV_servo_angle;// display received servo angle
	private TextView RCV_motor_speed;// display received motor rotation speed
	private Button startBlueTooth;//
	private Button Remote_Terminal_Start;// lance the server mode
	private Button Remote_Terminal_Stop;
	private Button capture_Test;
	private PictureCallback mPicture;
	//MediaStore iMediaStore;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	
	//network part
	private StreamingServer strmServer;
	private StreamingLoop cameraLoop;
	private StreamingLoop httpLoop;
	
	/*
	 * GPS module definition
	 */
	
	private final String TAG = null;
	private Logger log = null;
	private static boolean GPS_State = false;
	private TextView Remote_GPS_info;// display GPS info
	private TextView Remote_Terminal_GPS;
	private LocationManager myLocationManager = null;
	private Location myLocation = null;
	final GpsLocationListener myGpsLocationListener = new GpsLocationListener();
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */

	@Override
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote_terminal);
		//set
		init();
		
	}
	
	/*
	 * system initialization
	 */
	public void init(){
		Remote_Terminal_Start = (Button) findViewById(R.id.Remote_Terminal_Start);
		Remote_Terminal_Stop = (Button) findViewById(R.id.Remote_Terminal_Stop);
		capture_Test = (Button)findViewById(R.id.capture_Test);
		previewFrameLayout = (FrameLayout)findViewById(R.id.camera_preview);

		Remote_terminal_Network_state = (TextView) findViewById(R.id.Remote_terminal_Network_state);
		Remote_GPS_info = (TextView) findViewById(R.id.Remote_GPS_info);
		Remote_Terminal_GPS = (TextView) findViewById(R.id.Remote_Terminal_GPS);
		Remote_Terminal_GPS.setText(R.string.Remote_State_GPS_OFF);
		Remote_Terminal_GPS.setTextColor(android.graphics.Color.RED);
		startBlueTooth = (Button)findViewById(R.id.startBlueTooth);
		myLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		myLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		

		RCV_servo_angle = (TextView) findViewById(R.id.RCV_servo_angle);
		RCV_motor_speed = (TextView) findViewById(R.id.RCV_motor_speed);
		
		//initialize the directory if it desn't exist.
//		if(!(new File(pic_Dir + "/index.html").exists())){
//			clearResource();
//			buildResource();
//		}
//		//log.log(Level.INFO, pic_Dir);
		
		initCam();
		initWIFI();
		initFileInputOutput();

		Remote_Terminal_Start.setOnClickListener((OnClickListener) this);
		Remote_Terminal_Stop.setOnClickListener((OnClickListener) this);
		capture_Test.setOnClickListener((OnClickListener) this);
		
	}
	/*
	 * initialize the file operation 
	 */
	private void initFileInputOutput() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * initialize the WIFI connection
	 */
	private void initWIFI() {
		// TODO Auto-generated method stub
		startWIFIServer();
	}

	/*
	 * initialize camera
	 */
	private void initCam() {
		// TODO Auto-generated method stub
//		camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(iCamera != null){
	           try{
	              Camera.Parameters parameters = iCamera.getParameters();
	              parameters.setPictureFormat(PixelFormat.JPEG);
	              iCamera.setParameters(parameters);
	              /* open preview */
	              iCamera.startPreview();
	           }catch(Exception e){
	              e.printStackTrace();
	           }
	       }
		if (getCameraInstance()!= null) {
			iCamera = getCameraInstance();//open a camera
		}else {
			camero_on = false;
			log.log(Level.INFO, "Getting a camera instance fails");
			if (checkCameraHardware(this)) {
				log.log(Level.INFO, "please retry to open the camera");
			}else {
				System.out.println("open camera fails!");
			}
		}
		iCameraPreview = new CameraService(this, iCamera);
		previewFrameLayout.addView(iCameraPreview);//set the preview the content of the activity
		previewFrameLayout.canScrollVertically(getPreviewDegree(this));

		
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.capture_Test:
			takePicture();
			startVideoStreaming();
			break;
		case R.id.Remote_Terminal_Start:
			startGPS();
			startWIFIServer();
			
			break;
		case R.id.Remote_Terminal_Stop:
			stopGPS();
			stopCamera();
			break;
		case R.id.startBlueTooth:
			startBlueTooth();
			Remote_terminal_Network_state.setText("bluetooth");
			break;
		default:
			break;
		}
	}
	
	
	private void startBlueTooth() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(MainActivity_Remote_Terminal.this,BlueTerm.class);
		startActivity(intent);
	}

	/*
	 * start video streaming
	 */
	private void startVideoStreaming() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Take a picture
	 */

	 private void takePicture() {
	
		// TODO Auto-generated method stub
		if (onStreaming) {
			takeSnapShot();
		}else {
			takePicOnPicMode();
		}
	 }
	
	/*
	 * when the phone is on streaming
	 * we take a snapshot instead of an interrupt  to change camera mode
	 */
	private void takeSnapShot() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * phone is on picture mode 
	 * we take a picture and save it
	 * in a default directory
	 */
	private void takePicOnPicMode() {
		// TODO Auto-generated method stub
		iCamera.takePicture(null, null, new PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				try {
					Bundle tempData = new Bundle();
					tempData.putByteArray("byte", data);//save picture binary data
					savePhoto(data);//save pic data into SD card 
					Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
//		camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
//		camIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
////		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
//		// start the image capture Intent
//	    startActivityForResult(camIntent, TAKE_PHOTO);
//	    
	    
		
//		if(savePhoto()==null){
//
//			AlertDialog.Builder alertdialog = new AlertDialog.Builder(
//					MainActivity_Remote_Terminal.this);
//			alertdialog
//					.setTitle(
//							"Fails to take a picture!")
//					.setMessage("please check your device and re-try!")
//					.setPositiveButton("Yes",
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO Auto-generated method stub
//									MainActivity_Remote_Terminal.this.finish();
//								}
//							})
//					.setNegativeButton("Cancel",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO Auto-generated method stub
//									dialog.cancel();
//								}
//							}).create();
//			alertdialog.show();
//		}
		
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			
			switch (requestCode) {
			case TAKE_PHOTO:
				Bundle bundle = data.getExtras();
				bmp = (Bitmap)bundle.get(get_Pic_Name());
				//imageView1.setImageBitmap(bmp);
				bmp.recycle();
				savePhoto(data.getByteArrayExtra(get_Pic_Name()));
				break;
			case RECORD_VIDEO:
				
				break;

			default:
				break;
			}
			
		}
	}

	private File savePhoto(byte[] data){
		// TODO Auto-generated method stub
		File fileFolder = new File(pic_Dir);
		if (!fileFolder.exists()) {//if this dir doesn't exist  create one
			fileFolder.mkdir();
		}
		File picFile = new File(fileFolder, get_Pic_Name());
		
		try {
			fileOutputStream = new FileOutputStream(picFile);
			fileOutputStream.write(data);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return picFile;
		
	}
	

	private void startWIFIServer() {
		// TODO Auto-generated method stub
		
	}

	private void startCam() {
		// TODO Auto-generated method stub
		
	}
	/*
	 * stop camero preview
	 */
    private void stopCamera(){
       if(iCamera != null){
           try{
              iCamera.stopPreview();
           }catch(Exception e){
              e.printStackTrace();
           }
           
       }
    }


	private void startGPS() {
		// TODO Auto-generated method stub
		if (!GPS_State) {
			// TODO Auto-generated method stub
			/*
			 * launch the GPS service and display in the gps tag
			 */
			myGpsLocationListener.onProviderEnabled(LocationManager.GPS_PROVIDER);
			launceGPS(myGpsLocationListener);
			GPS_State = true;
			Remote_Terminal_GPS.setText(R.string.Remote_State_GPS_ON);
			Remote_Terminal_GPS.setTextColor(android.graphics.Color.GREEN);
		}
	}

	private void stopGPS() {
		// TODO Auto-generated method stub
		myGpsLocationListener.onProviderDisabled(LocationManager.GPS_PROVIDER);
		//myLocationManager.removeUpdates(myGpsLocationListener); 
		Remote_Terminal_GPS.setText(R.string.Remote_State_GPS_OFF);
		Remote_Terminal_GPS.setTextColor(android.graphics.Color.RED);
		Remote_GPS_info.setText(R.string.Remote_GPS_info_TURN_OFF);
		GPS_State = false;
	}

	/*
	 * launch the GPS sevice
	 */
	public void launceGPS(GpsLocationListener gpsLocationListener) {
		
		printGpsLocation(myLocation);
		if (myLocation == null) {
			Remote_GPS_info.setText("There is no GPS info!");
		}
		myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10 , gpsLocationListener);
	}
	
	/*
	 * print the GPS location on the GPS tag
	 */
	public void printGpsLocation(Location location) {
		if (location != null) {
			location.getAccuracy();
			location.getAltitude();
			location.getLatitude();
			location.getLongitude();
			location.getSpeed();
			location.getBearing();
			
			Remote_GPS_info.setText("Accuracy : " + location.getAccuracy() + 
                    "\nAltitude : " + location.getAltitude() + 
                    "\nBearing : " + location.getBearing() + 
                    "\nSpeed : " + location.getSpeed() + 
                    "\nLatitude ：" + location.getLatitude() + 
                    "\nLongitude : " + location.getLongitude());
		}
	}
	

	/*
	 * inner class GPS listener
	 *  set up a GPS location listener
	 */
	public class GpsLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			printGpsLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Log.d(TAG, "ProviderDisabled : " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			 Log.d(TAG, "ProviderEnabled : " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			Log.d(TAG, "StatusChanged : " + provider + status); 
		}
		
	}

	/*
	 * open a camera in a safe way
	 */
	public Camera getCameraInstance(){
		try {
		iCamera = Camera.open();
		iCamera.setDisplayOrientation(getPreviewDegree(this));
		} catch (Exception e) {
			// TODO: handle exception
			camero_on = false;
			return null;
		}
		camero_on = true;
		return iCamera;
	}
	
	/*
	 * get the camera orientation 
	 * in according to the camera  
	 */
    public int getPreviewDegree(Activity activity) {  
        //get camera orientation
        int rotation = activity.getWindowManager().getDefaultDisplay()  
                .getRotation();  
        int degree = 0;  
        // adjust preview orientation according to the camera
        switch (rotation) {  
        case Surface.ROTATION_0:  
            degree = 90;  
            break;  
        case Surface.ROTATION_90:  
            degree = 0;  
            break;  
        case Surface.ROTATION_180:  
            degree = 270;  
            break;  
        case Surface.ROTATION_270:  
            degree = 180;  
            break;  
        }  
        return degree;  
    }  
	
	//check if this device has a camera
	private boolean checkCameraHardware(Context context)
    {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA))
        {
            // this device has a camera
            return true;
        }
        else
        {
            // no camera on this device
            return false;
        }
    }
	
	/** Create a file Uri for saving an image or video */

	private static Uri getOutputMediaFileUri(int type){

	      return Uri.fromFile(getOutputMediaFile(type));

	}

	 

	/** Create a File for saving an image or video */

	private static File getOutputMediaFile(int type){

	    // To be safe, you should check that the SDCard is mounted

	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(

	    		Environment.DIRECTORY_PICTURES), "MyCameraApp");

	    // This location works best if you want the created images to be shared

	    // between applications and persist after your app has been uninstalled.

	 

	    // Create the storage directory if it does not exist

	    if (! mediaStorageDir.exists()){

	        if (! mediaStorageDir.mkdirs()){

	            Log.d("MyCameraApp", "failed to create directory");

	            return null;

	        }
	    }

	    // Create a media file name

	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

	    File mediaFile;

	    if (type == MEDIA_TYPE_IMAGE){

	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +

	        "IMG_"+ timeStamp + ".jpg");

	    } else if(type == MEDIA_TYPE_VIDEO) {

	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +

	        "VID_"+ timeStamp + ".mp4");

	    } else {

	        return null;

	    }

	 

	    return mediaFile;

	}
	
	
	/*
	 * file operation part
	 */
	
	
	/*
	 * clear files in the specified directory
	 */
	private void clearResource() {
		String[] str = { "rm", "-r", pic_Dir };

		try {
			Process ps = Runtime.getRuntime().exec(str);
			try {
				ps.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/*
	 * build a directory in device intern memory and put in some app files
	 */
	private void buildResource() {
		String[] str = { "mkdir", pic_Dir };

		try {
			Process ps = Runtime.getRuntime().exec(str);
			try {
				ps.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			copyResourceFile(R.raw.index, pic_Dir + "/index.html");
			copyResourceFile(R.raw.style, pic_Dir + "/style.css");
			copyResourceFile(R.raw.player, pic_Dir + "/player.js");
			copyResourceFile(R.raw.player_object, pic_Dir
					+ "/player_object.swf");
			copyResourceFile(R.raw.player_controler, pic_Dir
					+ "/player_controler.swf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * copy files from a specified directory
	 */
	private void copyResourceFile(int rid, String targetFile)
			throws IOException {
		InputStream fin = ((Context) this).getResources().openRawResource(rid);
		FileOutputStream fos = new FileOutputStream(targetFile);

		int length;
		byte[] buffer = new byte[1024 * 32];
		while ((length = fin.read(buffer)) != -1) {
			fos.write(buffer, 0, length);
		}
		fin.close();
		fos.close();
	}
	
	public String get_Pic_Name() {
		String fileName;
		int pic_count = 0;
		fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+pic_count + ".jpg";
		pic_count++;
		return fileName ;
	}

}
