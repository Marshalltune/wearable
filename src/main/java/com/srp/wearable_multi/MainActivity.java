package com.srp.wearable_multi;



import com.srp.wearable_multi.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button heartRate = (Button) findViewById(R.id.heart_measure);
		Button accelerate = (Button) findViewById(R.id.acc_measure);
		heartRate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent heartrateIntent = new Intent(MainActivity.this,HeartActivity.class);
				heartrateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(heartrateIntent);
			}
		});
		accelerate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.i("click","click");
				Intent accIntent = new Intent(MainActivity.this,SensorActivity.class);
				startActivity(accIntent);
				
				
			}}
		);
	}

	long waitTime = 2000; 
	long touchTime = 0; 
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if (event.getAction() == KeyEvent.ACTION_DOWN    && KeyEvent.KEYCODE_BACK == keyCode) {   
			long currentTime = System.currentTimeMillis();   
			if ((currentTime - touchTime) >= waitTime) {    
				Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();    
				touchTime = currentTime;   
				} else {
					finish();   
					}   
			return true;  
			}  
		return super.onKeyDown(keyCode, event); }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		android.os.Process.killProcess(android.os.Process.myPid());
		Log.i("Activity", "ondestory");
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
