package com.mromanro.evader;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class HighScores extends Activity{
	public final static String LIST_OF_NAMES = "com.mromanro.evader.list_of_names";
	public final static String LIST_OF_SCORES = "com.mromanro.evader.list_of_scores";
	public final static String CLEAR_ALL_BUTTON = "com.mromanro.evader.clear_all_button";
	public final static int HIGHSCORES = 2;
	
	private ListView listview;
	private ArrayList<String> item;
	private ArrayAdapter<String> adapter;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_activity);
		
		listview = (ListView)findViewById(R.id.listview);
		
		final String[] names = getIntent().getStringArrayExtra(LIST_OF_NAMES);
		final int[] scores = getIntent().getIntArrayExtra(LIST_OF_SCORES);
		
		item = new ArrayList<String>(names.length);
		for(int i = 0; i < names.length; i++){
			item.add("Name: " + names[i] + "  Score: " + scores[i]);
		}
		
		adapter = new ArrayAdapter<String>(this, R.layout.simplerow, item);
		listview.setAdapter(adapter);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId() == R.id.clear_all){
			clearButtonPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void clearButtonPressed(){
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setMessage("Are you sure you want to clear all data?");
		alert.setPositiveButton("Yes", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.putExtra(CLEAR_ALL_BUTTON, true);
				setResult(RESULT_OK, intent);
				finish();
			}
			
		});
		alert.setNegativeButton("No", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
			
		});
		alert.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.listview, menu);
		return true;
	}

}
