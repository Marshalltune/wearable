package com.srp.wearable_multi;

import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class HeartRateMeasurementDevice {
	public static HeartRateMeasurementDevice hrmDevice = null;
	
	public static final int CONNECTING = 0x10;
	public static final int CONNECTED = 0x20;
	private BluetoothAdapter btAdapter;
	private int deviceState;
	private BluetoothDevice leDevice;
	private Context context;
	private Activity activity;
	private int currentMode;
	BluetoothGattCharacteristic characteristicReadWrite;
	BluetoothGatt gatt;
	public HeartRateMeasurementDevice(Activity activity) {
		hrmDevice = this;
		final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
		context = activity.getApplicationContext();
		btAdapter = bluetoothManager.getAdapter();
		currentMode = 1;
	}
	private void onTextReceived(String text) {
		if(text.startsWith("U1:")) {
			onAmplitudeReceived(Integer.parseInt(text.substring(3, text.length()-2)));
		} else if(text.startsWith("U2:")) {
			onHeartRateReceived(Integer.parseInt(text.substring(3, text.length()-2)));
		}
	}
	
	private void deviceDiscovered(BluetoothDevice leDevice) {
		leDevice.connectGatt(context, false, new HRMDeviceBtGattCallback(this));
	}
	public void onHeartRateReceived(final int heartRate) {
		
	}
	
	public void onAmplitudeReceived(final int amplitude) {
		
	}
	
	public void dicsoverDevice(BluetoothAdapter.LeScanCallback callback) {
		if(callback == null)
			btAdapter.startLeScan(new HRMDeviceLeScanCallback(this));
		else
			btAdapter.startLeScan(callback);
	}
	
	public void connectDevice(BluetoothDevice device) {
		this.deviceDiscovered(device);
		Toast.makeText(context, "connected", Toast.LENGTH_LONG).show();
	}
	public void switchMode() {
		if(currentMode==0) {
			characteristicReadWrite.setValue("AT+MD:1\r\n");
			gatt.writeCharacteristic(characteristicReadWrite);
			currentMode = 1;
		} else {
			characteristicReadWrite.setValue("AT+MD:0\r\n");
			gatt.writeCharacteristic(characteristicReadWrite);
			currentMode = 0;
		}
	}
	
	
	private class HRMDeviceLeScanCallback implements BluetoothAdapter.LeScanCallback {
		private final static String HRMDEVICE_ADDRESS = "78:A5:04:57:E0:45";
		HeartRateMeasurementDevice hrmDevice;
		
		public HRMDeviceLeScanCallback(HeartRateMeasurementDevice hrmDevice) {
			super();
			this.hrmDevice = hrmDevice;
		}
		
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			Log.i("device",device.getAddress());
			if(device.getAddress().equals(HRMDEVICE_ADDRESS)) {
				hrmDevice.btAdapter.stopLeScan(this);
				hrmDevice.deviceDiscovered(device);
			}
		}
		
	}
	
	private class HRMDeviceBtGattCallback extends BluetoothGattCallback {		
		private static final String HEART_RATE_MEASUREMENT_UUID = "0000aaf0-0000-1000-8000-00805f9b34fb";
		private static final String READWRITE_SERVICE_UUID = "0000aaf1-0000-1000-8000-00805f9b34fb";
		private static final String NORTIFICATION_SERVICE_UUID = "0000aaf2-0000-1000-8000-00805f9b34fb";
		private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
		HeartRateMeasurementDevice hrmDevice;
		BluetoothGatt btGatt;
		Context context;
		
		public HRMDeviceBtGattCallback(HeartRateMeasurementDevice hrmDevice) {
			this.hrmDevice = hrmDevice;
		}
		
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			
			if(newState == BluetoothGatt.STATE_CONNECTED) {
				hrmDevice.deviceState = CONNECTED;
				btGatt = gatt;
				gatt.discoverServices();
			}
		}
		
		@Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			hrmDevice.gatt = gatt;
			Log.w("asdf", "onServicesDiscovered received: " + status);
			List<BluetoothGattService> services = gatt.getServices();
			Log.i("service", ""+services.size());
			for(int i=0;i<services.size();i++) 
				Log.i("service", services.get(i).getUuid().toString());
			
			for(BluetoothGattService service : services) {
				if(service.getUuid().toString().equals(HEART_RATE_MEASUREMENT_UUID)) {
					Log.i("HRATE", service.getUuid().toString());
					List<BluetoothGattCharacteristic> charList = service.getCharacteristics();
					//chaList = charList;
					for(BluetoothGattCharacteristic cha : charList) {
						Log.i("HRATE", cha.getUuid().toString());
						gatt.setCharacteristicNotification(cha, true);
						if(cha.getUuid().toString().equals(NORTIFICATION_SERVICE_UUID)){
							BluetoothGattDescriptor descriptor = cha.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
							descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
							gatt.writeDescriptor(descriptor);
						}
						if(cha.getUuid().toString().equals(READWRITE_SERVICE_UUID)) {
							hrmDevice.characteristicReadWrite = cha;
						}
					}
				}
			}
        }
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic){
			final String text = new String(characteristic.getValue());
			Log.i("characteristic2", text);
			hrmDevice.onTextReceived(text);
		}
		
	}
}

