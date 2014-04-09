package com.example.slave;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends Activity {

	private Button Control_Terminal;
	private Button Remote_Terminal;
	private Button Exit;
	private TextView textView_intro;
	private static Boolean isSlave = false;
	private static Boolean isMaster = true;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		/*
		 * test videoView module
		 */
//		Uri uri = Uri.parse("https://www.youtube.com/watch?v=02EulRKN1m0");  
  //      myVideoView = (VideoView)this.findViewById(R.id.myvideoView);  
//        videoView.setMediaController(new MediaController(this));  
//        videoView.setVideoURI(uri);  
//        //videoView.start();  
//        videoView.requestFocus();  
		/*
		 * test finishes
		 */

		textView_intro = (TextView) findViewById(R.id.TextView1);
		Control_Terminal = (Button) findViewById(R.id.Control_Terminal);
		Remote_Terminal = (Button) findViewById(R.id.Remote_Terminal);
		Exit = (Button)findViewById(R.id.Exit);

		/*
		 * set onClickListener, start the app
		 */
		
		/*
		 * test camero capture
		 */
		
		
		Control_Terminal.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

//				if (!isSlave && isMaster) {

					intent.setClass(MainActivity.this,
							MainActivity_Conntrol_Panel.class);
					startActivity(intent);
//				} else {
//					textView_intro
//							.setText("Your device is in remote terminal state now!");
//					textView_intro.setTextColor(android.graphics.Color.RED);
//				}

			}

		});

		/*
		 * set onClickListener, reset the app
		 */
		//
		Remote_Terminal.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				//if (isSlave && !isMaster) {
					intent.setClass(MainActivity.this,
							MainActivity_Remote_Terminal.class);
					startActivity(intent);
				//} else {
					textView_intro
							.setText("Your device is in control terminal state now!");
					textView_intro.setTextColor(android.graphics.Color.GREEN);
				//}

			}

		});

		/*
		 * set onClickListener, stop the app
		 */
		//
		Exit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				AlertDialog.Builder alertdialog = new AlertDialog.Builder(
						MainActivity.this);
				alertdialog
						.setTitle(
								"Are you sure to exit?")
						.setMessage("You are about to exit, please make sure you have terminated the control process!")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										MainActivity.this.finish();
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								}).create();
				alertdialog.show();

			}

		});

	}

	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK) {
//			videoUri = data.getData();
//		}
//	}
//	
//	public void onClick(View v) {
//		int id = v.getId();
//		
//		if (id == R.id.btn_capture){
//			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//			startActivityForResult(intent, 1);
//		}else if (id == R.id.btn_play) {
//			myVideoView.setVideoURI(videoUri);
//			myVideoView.start();
//		}
//	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder alertdialog = new AlertDialog.Builder(
					MainActivity.this);
			alertdialog
					.setTitle(
							"Are you sure to exit?")
					.setMessage("You are about to exit, please make sure you have terminated the control process!")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									MainActivity.this.finish();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).create();
			alertdialog.show();
		}
		return true;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
		
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
