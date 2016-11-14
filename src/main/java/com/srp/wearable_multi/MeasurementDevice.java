package com.srp.wearable_multi;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

public class MeasurementDevice {
	public static MeasurementDevice accDevice = null;
	public static final int CONNECTING = 0x10;
	public static final int CONNECTED = 0x20;
	private BluetoothAdapter btAdapter;
	private int deviceState;
	private Context context;
	private int currentMode;
	BluetoothGattCharacteristic characteristicReadWrite;
	BluetoothGatt gatt;
	private int	x;
	private int	y;
	private int	z;
	private float ax;
	private float ay;
	private float az;
	private int a;
	private float a2;
	
	public MeasurementDevice(Activity activity) {
		accDevice = this;
		final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
		context = activity.getApplicationContext();
		btAdapter = bluetoothManager.getAdapter();
		currentMode = 1;
	}
	
	
	//需改！！！！！！！！！！！
//	private void onTextReceived(String text) {
//		if(text.startsWith("U1:")) {
//			onAmplitudeReceived(Integer.parseInt(text.substring(3, text.length()-2)));
//		} else if(text.startsWith("U2:")) {
//			onAccReceived(Integer.parseInt(text.substring(3, text.length()-2)));
//		}
//	}
	
	
	
//	public void switchMode() {
//		if(currentMode==0) {
//			characteristicReadWrite.setValue("AT+MD:1\r\n");
//			gatt.writeCharacteristic(characteristicReadWrite);
//			currentMode = 1;
//		} else {
//			characteristicReadWrite.setValue("AT+MD:0\r\n");
//			gatt.writeCharacteristic(characteristicReadWrite);
//			currentMode = 0;
//		}
//	}
	
	
	
	//需更改！！！！！！！！！！！！！！！！！
	private void deviceDiscovered(BluetoothDevice leDevice) {
 
		leDevice.connectGatt(context, false, new AccDeviceBtGattCallback(this));
		System.out.println("------>devicediscovered");
	}
	public void onAccReceived(final int heartRate) {
		
	}
	
//	public void onAmplitudeReceived(final float ax,final float ay,final float az,final int a) {
	public void onAmplitudeReceived(final int ax,final int ay,final int az) {
		
	}
	public void onReceive(byte type, byte[] message) {
		
	}
	public void dataProcessing(byte[] message){
		System.out.println("------>message:"+message);
		ax = x = 0;
		ay = y = 0;
		az = z = 0;
		a = 0;
		x |= message[0];
		x <<= 8;
		x |= (message[1]&0x00FF);
		System.out.println("x:"+x);
		y |= message[2];
		y <<= 8;
		y |= (message[3]&0x00FF);
		System.out.println("y:"+y);
		z |= message[4];
		z <<= 8;
		z |= (message[5]&0x00FF);
		System.out.println("z:"+z);
//		ax = (float) (x/32768*2*9.8);
//		ay = (float) (y/32768*2*9.8);
//		az = (float) (z/32768*2*9.8);
//		a2 = (float) (Math.pow(ay, 2)+Math.pow(ay, 2)+Math.pow(az, 2));
//		if((a2-9.8*9.8)>=0)
//		{
//			a = (int) Math.sqrt(a2-9.8*9.8);
//			a -=9.8;
//		}else{
//			a = (int) Math.sqrt(9.8*9.8-a2);
//			a -=9.8;
//			a = -a;
//		}
		System.out.println("------>xyz set");
		onAmplitudeReceived(x, y, z);
//		onAmplitudeReceived(x, y, z,a);
		System.out.println("------>onamplitudeReceived");
	}
	public void switchState(){
		
	}
	public void setInformation(final String deviceInfo){
		
	}
	@SuppressWarnings("deprecation")
	public void dicsoverDevice(BluetoothAdapter.LeScanCallback callback) {
		if(callback == null)
			btAdapter.startLeScan(new AccDeviceLeScanCallback(this));
		else
			btAdapter.startLeScan(callback);
	}
	public void disconnectDevice(){
//		setInformation("");
	}
	@SuppressLint("ShowToast")
	public void connectDevice(BluetoothDevice device) {
		System.out.println("------>connectDevice");
		this.deviceDiscovered(device);
		System.out.println("------>connected");
		Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
		setInformation(""+device.getName()+"\n"+device.getAddress()+"\n"+device.getUuids());
	}
	
	
	private class AccDeviceLeScanCallback implements BluetoothAdapter.LeScanCallback {
		private final static String ACCDEVICE_ADDRESS = "D0:A9:EF:AD:15:CE";		
		MeasurementDevice accDevice;
		
		public AccDeviceLeScanCallback(MeasurementDevice accDevice) {
			super();
			this.accDevice = accDevice;
		}
		
		
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			System.out.println("------>"+device.getName());
			
//			if(device.getAddress().equals(ACCDEVICE_ADDRESS)) {
//				accDevice.btAdapter.stopLeScan(this);
//				accDevice.deviceDiscovered(device);
//			}
		}
		
	}
	
	private class AccDeviceBtGattCallback extends BluetoothGattCallback {	
		private static final String NORDIC_UART_SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
		private static final String ACC_CHARACTERISTIC_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
		private static final String ACC_DESCRIPTOR_UUID = "0x0002";
		private static final String NORTIFICATION_SERVICE_UUID = "0000aaf2-0000-1000-8000-00805f9b34fb";
		private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
			//需改！！！

		MeasurementDevice accDevice;
		BluetoothGatt btGatt;
		Context context;
		
		public AccDeviceBtGattCallback(MeasurementDevice accDevice) {
			this.accDevice = accDevice;
		}
		
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			System.out.println("----->onconnectStateChange"+newState);
			if(newState == BluetoothGatt.STATE_CONNECTED) {
				
				accDevice.deviceState = CONNECTED;
				System.out.println("------>connected");
				btGatt = gatt;
				gatt.discoverServices();
				switchState();
			}
		}
		
		@Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			accDevice.gatt = gatt;
			System.out.println("------>"+status);
			List<BluetoothGattService> services = gatt.getServices();
			Log.i("service", "serivces.size:"+services.size());
			if(services != null && services.size()>0){
			for(int i=0;i<services.size();i++){ 
				Log.i("service", services.get(i).getUuid().toString());
			}
			for(BluetoothGattService service : services) {
				if(service.getUuid().toString().equals(NORDIC_UART_SERVICE_UUID)) {
					Log.i("ACC", service.getUuid().toString());
					System.out.println("------>service:"+service.getUuid().toString());
					List<BluetoothGattCharacteristic> charList = service.getCharacteristics();
					//chaList = charList;
					if(charList != null && charList.size()>0){
						for(BluetoothGattCharacteristic cha : charList) {
							String chara = cha.getUuid().toString();
							System.out.println("------>characteristci"+chara+""+cha.getValue());
							accDevice.characteristicReadWrite = cha;
							gatt.setCharacteristicNotification(cha, true);
							if(chara.equals(ACC_CHARACTERISTIC_UUID)) {
								BluetoothGattDescriptor descriptor = cha.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
								descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
								gatt.writeDescriptor(descriptor);
								accDevice.characteristicReadWrite = cha;
								System.out.println("characteristicReadWrite:"+characteristicReadWrite);
							}
						}
					}
				}
			}
			}
        }
		
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic){
			System.out.println("------>onCharacteristicChanged");
			int chaLong = characteristic.getValue().length;
			System.out.println("------>messageLength:"+chaLong);
			byte[] message = characteristic.getValue();
			for(int i=0;i<chaLong;i++){
			System.out.println("message:"+message[i]);
			}
			if(message != null && message.length > 0){
				accDevice.dataProcessing(message);
			}
		}
		
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			super.onCharacteristicRead(gatt, characteristic, status);
			System.out.println("------>characteristicread"+characteristic.getValue());
		}
		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// TODO Auto-generated method stub
			System.out.println("------>rssi:"+rssi);
			super.onReadRemoteRssi(gatt, rssi, status);
		}
		
	}
}
