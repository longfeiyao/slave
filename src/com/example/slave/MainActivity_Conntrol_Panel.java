package com.example.slave;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity_Conntrol_Panel extends Activity implements View.OnClickListener, View.OnTouchListener{
	

	private View view;
	private static boolean network_on = false;
	final String pic_Dir = "/sdcard/rcv/pic";// directory of this app
	final String checkingFile = "/sdcard/rcv/myvideo.mp4";

	

	NativeAgent nativeAgt;
	//CameraView myCamView;


	boolean inServer = false;// server flag: true,is server
	boolean inStreaming = false;
	int targetWid = 320;
	int targetHei = 240;

	private Button btn_capture;// start the slave terminal
	private Button reset_Button;// reset all the configurations
	private Button btn_play;// stop the slave terminal
	
	private Semaphore onTouchSemaphore;
	private static Boolean URGENTINTERRUPT = false;

	private Button Control_Right;// control panel->right
	//buttonListner control_Right_Listner;
	private Button control_Left;// control panel->left
	//buttonListner control_Left_Listner;
	private Button Control_SpeedUp;// control panel->speed up the motor
	//buttonListner Control_SpeedUp_Listner;
	private Button Control_SpeedDown;// control panel->speed down the motor
	//buttonListner Control_SpeedDown_Listner;

	private TextView state;// connection state flag
	private TextView info_GPS_display;// item display frame
	private TextView info_GPS;// real-time value field of GPS of the drone
	/*
	 * private EditText info_GPS_longitude_display;//item display frame private
	 * EditText info_GPS_longitude;//real-time value field of GPS longitude of
	 * the drone
	 */private TextView servo_angle_display;// item display frame
	private TextView servo_angle;// real-time value field of the servo angle
	private TextView motor_speed_display;// item display frame
	private TextView motor_speed;// real-time value field of motor speed r/s
	private VideoView myVideoView;

	/*
	 * private VideoView real_time_video;//real-time video display frame
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//super.setContentView(R.layout.activity_control_panel);
		setContentView(R.layout.activity_control_panel);

//		state = (TextView) findViewById(R.id.state);
//		info_GPS_display = (TextView) findViewById(R.id.info_GPS_display);
//		info_GPS = (TextView) findViewById(R.id.info_GPS);
		/*
		 * info_GPS_longitude_display =
		 * (EditText)findViewById(R.id.info_GPS_longitude_display);
		 * info_GPS_longitude = (EditText)findViewById(R.id.info_GPS_longitude);
		 */
//		servo_angle_display = (TextView) findViewById(R.id.servo_angle_display);
//		servo_angle = (TextView) findViewById(R.id.servo_angle);
//		motor_speed_display = (TextView) findViewById(R.id.motor_speed_display);
//		motor_speed = (TextView) findViewById(R.id.motor_speed);
		/* real_time_video = (VideoView)findViewById(R.id.real_time_video); */
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		btn_capture = (Button) findViewById(R.id.btn_capture);
		reset_Button = (Button) findViewById(R.id.reset);
		btn_play = (Button) findViewById(R.id.btn_play);
		control_Left = (Button) findViewById(R.id.Control_Left);
		Control_Right = (Button) findViewById(R.id.Control_Right);
		Control_SpeedUp = (Button) findViewById(R.id.Control_SpeedUp);
		Control_SpeedDown = (Button) findViewById(R.id.Control_SpeedDown);
		myVideoView = (VideoView)findViewById(R.id.myvideoView);
		
		btn_capture.setOnClickListener(this);
		reset_Button.setOnClickListener(this);
		btn_play.setOnClickListener(this);
		control_Left.setOnClickListener(this);
		Control_Right.setOnClickListener(this);
		Control_SpeedUp.setOnClickListener(this);
		Control_SpeedDown.setOnClickListener(this);
		
		onTouchSemaphore = new Semaphore(1);
		callServer();
	}
	/*
	 * set to connect remote server
	 */
	private void callServer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
//
//	@Override
////	public void onStart() {
////		super.onStart();
////		setup();
////	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//	}

	@Override
	public void onPause() {
		super.onPause();
		finish();
		System.exit(0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_play:
			startListning();
			startStreaming();
			break;
		case R.id.btn_capture:
			catchSnapShot();
			break;
		case R.id.reset:
			URGENTINTERRUPT = true;
			break;
		default:
			break;
		}
	}
	
	/*
	 * to assure the security of
	 * the drone, we never stop listening
	 * since we lance it
	 * 
	 */
	private void startListning() {
		// TODO Auto-generated method stub
//		control_Left_Listner = new buttonListner();
//		control_Right_Listner = new buttonListner();
//		Control_SpeedUp_Listner = new buttonListner();
//		Control_SpeedDown_Listner = new buttonListner();
	}


//	class buttonListner implements OnClickListener, OnTouchListener{
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			sendHigh();
//		}
//		@Override
//		public  boolean onTouch(View v, MotionEvent event) {
//			// TODO Auto-generated method stub
//			TimerTask timerTask = null;
//			while (event.getAction() == MotionEvent.ACTION_DOWN) {
//					timerTask = new TimerTask() {						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							sendHigh();
//						}
//					};
//			}
//			timerTask.cancel();
//			return false;
//		}
//		
//	}
	/*
	 * command to catch a snapshot from the streaming
	 * from the remote terminal
	 */
	private void catchSnapShot() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * send out high level command indicating that 
	 */
	private void sendHigh(int buttonId) {
		// TODO Auto-generated method stub
				
	}


	/*
	 * start streaming on the videoView
	 */
	private void startStreaming() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 * 
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Control_Left:
			if (!URGENTINTERRUPT) {
				try {
					onTouchSemaphore.acquire();
					while (event.getAction() == MotionEvent.ACTION_DOWN) {
						sendHigh(R.id.Control_Left);
					}
					onTouchSemaphore.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				resetALL();
				break;
			}		
			break;
		case R.id.Control_Right:
			if (!URGENTINTERRUPT) {
				try {
					onTouchSemaphore.acquire();
					while (event.getAction() == MotionEvent.ACTION_DOWN) {
						sendHigh(R.id.Control_Right);
					}
					onTouchSemaphore.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				resetALL();
				break;
			}		
			break;
		case R.id.Control_SpeedUp:
			if (!URGENTINTERRUPT) {
				try {
					onTouchSemaphore.acquire();
					while (event.getAction() == MotionEvent.ACTION_DOWN) {
						sendHigh(R.id.Control_SpeedUp);
					}
					onTouchSemaphore.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				resetALL();
				break;
			}		
			break;
		case R.id.Control_SpeedDown:
			if (!URGENTINTERRUPT){
				try {
					onTouchSemaphore.acquire();
					while(event.getAction()==MotionEvent.ACTION_DOWN){
						sendHigh(R.id.Control_SpeedDown);
					}
					onTouchSemaphore.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}else {
				resetALL();
				break;
			}		
			break;
		default:
			break;
		}
		return false;
	}

	/*
	 * clear all the ongoing operations
	 * reset all 
	 */
	private void resetALL() {
		// TODO Auto-generated method stub
		
	}

//	private OnClickListener low_res_listener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			targetWid = 320;
//			targetHei = 240;
//		}
//	};
//
//	private OnClickListener medium_res_listener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			targetWid = 640;
//			targetHei = 480;
//		}
//	};
//	private OnClickListener high_res_listener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			targetWid = 1280;
//			targetHei = 720;
//		}
//	};

//	private void setup() {
//		clearResource();
//		buildResource();
//
//		NativeAgent.LoadLibraries();
//		nativeAgt = new NativeAgent();
//		cameraLoop = new StreamingLoop("com.example");
//		httpLoop = new StreamingLoop("slave.http");
//
//		myCamView = (CameraView) findViewById(R.id.surface_overlay);
//		SurfaceView sv = (SurfaceView) findViewById(R.id.surface_camera);
//		myCamView.SetupCamera(sv);
//
//		myMessage = (TextView) findViewById(R.id.label_msg);
//
//		btnStart = (Button) findViewById(R.id.btn_start);
//		// btnStart.setOnClickListener(startAction);
//		btnStart.setOnClickListener(startAction);
//		btnStart.setEnabled(true);
//
//		/*
//		 * Button btnTest = (Button)findViewById(R.id.btn_test);
//		 * btnTest.setOnClickListener(testAction);
//		 * btnTest.setVisibility(View.INVISIBLE);
//		 */
//
//		RadioButton rb = (RadioButton) findViewById(R.id.res_low);
//		rb.setOnClickListener(low_res_listener);
//		rb = (RadioButton) findViewById(R.id.res_medium);
//		rb.setOnClickListener(medium_res_listener);
//		rb = (RadioButton) findViewById(R.id.res_high);
//		rb.setOnClickListener(high_res_listener);
//
//		resRadios = (RadioGroup) findViewById(R.id.resolution);
//
//		View v = findViewById(R.id.layout_setup);
//		v.setVisibility(View.VISIBLE);
//	}
//
//	private void startServer() {
//		inServer = true;
//		btnStart.setText(getString(R.string.action_stop));
//		btnStart.setEnabled(true);
//		NetInfoAdapter.Update(this);
//		myMessage.setText(getString(R.string.msg_prepare_ok) + " http://"
//				+ NetInfoAdapter.getInfo("IP") + ":8080");
//
//		try {
//			strServer = new StreamingServer(8080, pic_Dir);
//			strServer.setOnRequestListen(streamingRequest);
//		} catch (IOException e) {
//			e.printStackTrace();
//			showToast(this, "Can't start http server..");
//		}
//	}
//
//	private void stopServer() {
//		inServer = false;
//		btnStart.setText(getString(R.string.action_start));
//		btnStart.setEnabled(true);
//		myMessage.setText(getString(R.string.msg_idle));
//		if (strServer != null) {
//			strServer.stop();
//			strServer = null;
//		}
//	}
//
//	private boolean startStreaming() {
//		if (inStreaming == true)
//			return false;
//
//		cameraLoop.InitLoop();
//		httpLoop.InitLoop();
//		NativeAgent.NativeStartStreamingMedia(
//				cameraLoop.getReceiverFileDescriptor(),
//				httpLoop.getSenderFileDescriptor());
//
//		myCamView.PrepareMedia(targetWid, targetHei);
//		boolean ret = myCamView.StartStreaming(cameraLoop
//				.getSenderFileDescriptor());
//		if (ret == false) {
//			return false;
//		}
//
//		new Handler(Looper.getMainLooper()).post(new Runnable() {
//			@Override
//			public void run() {
//				showToast(MainActivity_Conntrol_Panel.this,
//						getString(R.string.msg_streaming));
//				btnStart.setEnabled(false);
//			}
//		});
//
//		inStreaming = true;
//		return true;
//	}
//
//	private void stopStreaming() {
//		if (inStreaming == false)
//			return;
//		inStreaming = false;
//
//		myCamView.StopMedia();
//		httpLoop.ReleaseLoop();
//		cameraLoop.ReleaseLoop();
//
//		NativeAgent.NativeStopStreamingMedia();
//		new Handler(Looper.getMainLooper()).post(new Runnable() {
//			@Override
//			public void run() {
//				btnStart.setEnabled(true);
//			}
//		});
//	}
//
//	StreamingServer.OnRequestListen streamingRequest = new StreamingServer.OnRequestListen() {
//		@Override
//		public InputStream onRequest() {
//			Log.d("Slave", "Request live streaming...");
//			if (startStreaming() == false)
//				return null;
//			try {
//				InputStream ins = httpLoop.getInputStream();
//				return ins;
//			} catch (IOException e) {
//				e.printStackTrace();
//				Log.d("Slave", "call httpLoop.getInputStream() error");
//				stopStreaming();
//			}
//
//			Log.d("Slave", "Return a null response to request");
//			return null;
//		}
//
//		@Override
//		public void requestDone() {
//
//			Log.d("TEAONLY", "Request live streaming is Done!");
//
//			stopStreaming();
//
//		}
//
//	};
//
//	private OnClickListener startAction = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//
//			doAction();
//
//		}
//	};
//
//	private void doAction() {
//		if (inServer == false) {
//			myCamView.PrepareMedia(targetWid, targetHei);
//			boolean ret = myCamView.StartRecording(checkingFile);
//			if (ret) {
//				btnStart.setEnabled(false);
//				resRadios.setEnabled(false);
//				myMessage.setText(getString(R.string.msg_prepare_waiting));
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						myCamView.StopMedia();
//						if (NativeAgent.NativeCheckMedia(targetWid, targetHei,
//								checkingFile)) {
//							startServer();
//						} else {
//							btnStart.setEnabled(true);
//							resRadios.setEnabled(false);
//							showToast(MainActivity_Conntrol_Panel.this,
//									getString(R.string.msg_prepare_error2));
//						}
//					}
//				}, 2000); // 2 seconds to release
//			} else {
//				showToast(this, getString(R.string.msg_prepare_error1));
//			}
//		} else {
//			stopServer();
//		}
//	}
//
//	private void showToast(Context context, String message) {
//		// create the view
//		LayoutInflater vi = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View view = vi.inflate(R.layout.message_toast, null);
//
//		// set the text in the view
//		TextView tv = (TextView) view.findViewById(R.id.message);
//		tv.setText(message);
//
//		// show the toast
//		Toast toast = new Toast(context);
//		toast.setView(view);
//		toast.setDuration(Toast.LENGTH_SHORT);
//		toast.show();
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	public void onClick() {
//	}
//
//	/*
//	 * write a jpg file into the specified directory
//	 */
//	private void pic_write() {
//
//	}
}
