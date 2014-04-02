package com.mromanro.evader;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.mromanro.util.DatabaseHandler;

public class MainActivity extends Activity {

	private DatabaseHandler db = new DatabaseHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		Button startGameButton = (Button)findViewById(R.id.start_game);
		startGameButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, EvaderActivity.class);
				startActivityForResult(intent, EvaderActivity.EVADER_ACTIVITY);
			}
		});
		
		Button scoresButton = (Button)findViewById(R.id.scores);
		scoresButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, HighScores.class);
				List<User> users = db.getAllUsers();
				String[] names = new String[users.size()];
				int[] scores = new int[users.size()];
				for(int i = 0; i < names.length; i++){
					names[i] = users.get(i).getName();
					scores[i] = users.get(i).getScore();
				}
				intent.putExtra(HighScores.LIST_OF_NAMES, names);
				intent.putExtra(HighScores.LIST_OF_SCORES, scores);
				startActivityForResult(intent, HighScores.HIGHSCORES);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data){
		if(data == null)
			return;
		if(requestCode == EvaderActivity.EVADER_ACTIVITY){
			if(resultCode == RESULT_OK){
				final AlertDialog.Builder alert = new AlertDialog.Builder(this);
				final EditText input =  new EditText(this);
				final int score = data.getIntExtra(EvaderActivity.USER_SCORE, 0);
			
				alert.setMessage("Game Over. Your score is: " + score + " \n Enter name");
				alert.setView(input);
				alert.setPositiveButton("Save", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String name = input.getText().toString().trim();
						db.addUser(new User(name, score));
					}
				
				});
			
				alert.setNegativeButton("Cancel", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				
				});
				alert.setCancelable(false);
				alert.show();
			}
		}
		if(requestCode == HighScores.HIGHSCORES){
			if(resultCode == RESULT_OK){
				if(data.getBooleanExtra(HighScores.CLEAR_ALL_BUTTON, false)){
					List<User> users = db.getAllUsers();
					for(User user : users){
						db.deleteUser(user);
					}
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
