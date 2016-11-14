package com.srp.wearable_multi;


import java.util.HashSet;

import com.srp.wearable_multi.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
public class SensorDeviceDiscoverActivity extends Activity {
	private TextView deviceText;
	private final SensorMeasurementDevice accDevice = SensorMeasurementDevice.accDevice;
	private final HashSet<String> deviceSet = new HashSet<String>();
	private LinearLayout list ;
	private LayoutInflater inflater;
	private View view;
	private TextView deviceName;
	private TextView deviceMac;
	public class DeviceScanCallback implements BluetoothAdapter.LeScanCallback {

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
	
		deviceText = (TextView)findViewById(R.id.deviceText);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		deviceText = (TextView) findViewById(R.id.deviceText);
		deviceText.setText("加速度传感器设备");
		deviceSet.clear();
		setDeviceList();
		
	}
	public void setDeviceList(){
	
		accDevice.dicsoverDevice(new BluetoothAdapter.LeScanCallback(){
			@Override
			public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {				
				final BluetoothDevice deviceFound = device;
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						if(!deviceSet.contains(deviceFound.getAddress())) {
							deviceSet.add(deviceFound.getAddress());
							LinearLayout list = (LinearLayout) findViewById(R.id.deviceList);
							LayoutInflater inflater = getLayoutInflater();
							View view = inflater.inflate(R.layout.device_name, null);
							TextView deviceName = (TextView) view.findViewById(R.id.device_name);
							TextView deviceMac = (TextView) view.findViewById(R.id.device_mac);
							deviceName.setText(deviceFound.getName());
							deviceMac.setText(deviceFound.getAddress());
							view.setOnClickListener(new OnClickListener(){

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									accDevice.connectDevice(deviceFound);
									finish();
								}});
							list.addView(view);
						}
					}});
				
			}});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_list, menu);
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

