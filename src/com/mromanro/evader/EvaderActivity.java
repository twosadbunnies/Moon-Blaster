package com.mromanro.evader;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class EvaderActivity extends Activity implements SensorEventListener {
	private final EvaderRenderer evaderRenderer = new EvaderRenderer(this, this);
	
	public final static int EVADER_ACTIVITY = 1;
	
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	public final static String USER_NAME = "com.mromanro.evader.user_name";
	public final static String USER_SCORE = "com.mromanro.evader.user_score";
	public final static String ASTEROIDS = "com.mromanro.evader.asteroids";

	public TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_layout);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this,  accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		glSurfaceView = (GLSurfaceView)findViewById(R.id.glsurfaceview);
		
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsES2 = configInfo.reqGlEsVersion >= 0x20000;
		
		if(supportsES2){
			glSurfaceView.setEGLContextClientVersion(2);
			
			glSurfaceView.setRenderer(evaderRenderer);
			rendererSet = true;
		}
		else{
			Toast.makeText(this, "This Device Does Not Support OpenGLES 2", Toast.LENGTH_LONG).show();
		}
		
		glSurfaceView.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event != null){
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						glSurfaceView.queueEvent(new Runnable(){

							@Override
							public void run() {
								evaderRenderer.handleTouch();
							}
							
						});
					}
					return true;
				}
				else{
					return false;
				}
			}
			
		});
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(rendererSet)
			glSurfaceView.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		if(rendererSet)
			glSurfaceView.onResume();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	public void setAnswerResult(int score){
		Intent data = new Intent();
		data.putExtra(USER_SCORE, score);
		setResult(RESULT_OK, data);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		float x = (event.values[0] / (float) width) * 4f;
		final float normalizedX;
		if(x > 1)
			normalizedX = 1;
		else if(x < -1)
			normalizedX = -1;
		else
			normalizedX = x;
		
		//System.out.println(normalizedX);
		glSurfaceView.queueEvent(new Runnable(){

			@Override
			public void run() {
				evaderRenderer.handleMove(normalizedX);
			}
			
		});
	}
}
