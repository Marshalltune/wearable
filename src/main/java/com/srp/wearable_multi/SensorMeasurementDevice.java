package com.srp.wearable_multi;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
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
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.sqrt;

public class SensorMeasurementDevice {
	public static SensorMeasurementDevice accDevice = null;
	public static final int CONNECTING = 0x10;
	public static final int CONNECTED = 0x20;
	private BluetoothAdapter btAdapter;
	private int deviceState;
	private Context context;
	private int currentMode;
	BluetoothGattCharacteristic btCharacteristicAngel;
	BluetoothGattCharacteristic btCharacteristicAcc;
	BluetoothGattDescriptor btDescriptorAngel,btDescriptorAcc;
	BluetoothGatt gatt;
	private int t;
	private float at;
	private int	x;
	private int	y;
	private int	z;
	private static float gx;
	private static float gy;
	private static float gz;
	private static float ax;
	private static float ay;
	private static float az;
	private static float a;
	private static double a2;
	private static final float ACC_G = 0.92f;
	private static MyFile myFile;
	private String s;
	public static boolean mRecordState;
	private static final String NORDIC_UART_SERVICE_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
	private static final String ANGEL_CHARACTERISTIC_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
	private static final String ACC_CHARACTERISTIC_UUID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
	private static final String NORTIFICATION_SERVICE_UUID = "0000aaf2-0000-1000-8000-00805f9b34fb";
	private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	private static final String DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
	private AccDeviceBtGattCallback accGattCallBack;
	public SensorMeasurementDevice(Activity activity) {
		accDevice = this;
		mRecordState = false;
		final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
		context = activity.getApplicationContext();
		btAdapter = bluetoothManager.getAdapter();
		currentMode = 1;
		ax = ay = az =gx= gy =gz =0.0f;
	}


	public void onAmplitudeReceivedA1(final float a) {

	}
	public void onAmplitudeReceivedA0(final float a ) {

	}
	public void onAmplitudeReceivedA2(float a2) {
		// TODO Auto-generated method stub

	}

	public void dataAngelProcessing(byte[] message, int number){
		Log.i("angel","");
		gx = x = 0;
		gy = y = 0;
		gz = z = 0;
		a = 0;
		x |= message[0];
		x <<= 8;
		x |= (message[1]&0x00FF);
		y |= message[2];
		y <<= 8;
		y |= (message[3]&0x00FF);
		z |= message[4];
		z <<= 8;
		z |= (message[5]&0x00FF);
		gx = (float) (x/16.384);
		gy = (float) (y/16.384);
		gz = (float) (z/16.384);
		if(mRecordState){
			try {
//			recordAngel(gx,gy,gz);!!!
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("record错误");
			}
		}
		gx = (float) ((gx*Math.PI)/180.0);
		gy = (float) ((gy*Math.PI)/180.0);
		gz = (float) ((gz*Math.PI)/180.0);
//		onAmplitudeReceived(x, y, z,a);
//		System.out.println("------>onamplitudeReceived");
	}
	public void dataAccProcessingA0(final byte[] message){
		accProgress(message);
		Log.i("acc",""+a);
		onAmplitudeReceivedA0(a);
		if(mRecordState){
			try {
				recordAcc(ax, ay, az,a,1);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("record错误");
			}
		}
	}
	public void dataAccProcessingA1(final byte[] message){
		accProgress(message);
		onAmplitudeReceivedA1(a);
		if(mRecordState){
			try {
				recordAcc(ax, ay, az,a,2);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("record错误");
			}
		}
	}
	public void dataAccProcessingA2(final byte[] message){
		accProgress(message);
		onAmplitudeReceivedA2(a);
		if(mRecordState){
			try {
				recordAcc(ax, ay, az,a,3);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("record错误");
			}
		}
	}
	public void accProgress(byte[] message){
		ax = x = 0;
		ay = y = 0;
		az = z = 0;
		a = 0;
		x |= message[0];
		x <<= 8;
		x |= (message[1]&0x00FF);
		y |= message[2];
		y <<= 8;
		y |= (message[3]&0x00FF);
		z |= message[4];
		z <<= 8;
		z |= (message[5]&0x00FF);
		ax = (float) (x/16384.00);
		ay = (float) (y/16384.00);
		az = (float) (z/16384.00);
		a2  = sqrt(ax*ax+ay*ay+az*az);
		Log.e("Acc","a2"+a2);
		float agx = 2*ACC_G*(q[1]*q[3]-q[0]*q[3]);
		float agy = 2*ACC_G*(q[0]*q[1]+q[2]*q[3]);
		float agz = (float) (2*ACC_G*(0.5-q[1]*q[1]-q[2]*q[2]));
		System.out.println("agx"+agx+"agy"+agy+"agz"+agz);
		ax -= agx;
		ay -= agy;
		az -= agz;
		a2  = sqrt(ax*ax+ay*ay+az*az);
		a = (float)a2;
//		a -= ACC_G;


//		ax2 = (float) ((ax*Math.cos(pitch)+az*Math.sin(pitch))*ACC_G);
//		ay2 = (float) ((ay*Math.cos(roll)+ax*Math.sin(roll)*Math.sin(pitch)-az*Math.sin(roll)*Math.cos(pitch))*ACC_G);
//		az2 = (float) (((-ax*Math.cos(roll)*Math.sin(pitch)+ay*Math.sin(roll)+az*Math.cos(roll)*Math.cos(pitch))-ACC_OFFSET_Z)*ACC_G);
//		a2  = sqrt(ax2*ax2+ay2*ay2+az2*az2);
//		a = (float)a2;

	}

	public void setRecord(boolean recordState){
		mRecordState = recordState;
		System.out.println("this.recordstate ="+mRecordState);
	}

	public static String inputFileName;
	@SuppressLint("SimpleDateFormat")
	public void iniRecord(){
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String time ="开始时间："+formatter.format(curDate)+"\r\n";
		try{
//			myFile = new MyFile("/storage/emulated/0/wearable/Record"+formatter.format(curDate)+".txt");
			myFile = new MyFile("/storage/emulated/0/wearable/"+inputFileName+".txt");
		} catch (Exception e) {
//			Toast.makeText(SensorActivity.class,"文件写入错误",Toast.LENGTH_SHORT).show();
			System.out.println("文件myFile写入错误");
		}

		try {
			myFile.Write(time+"\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("myFile write 错误");
		}

	}
	public void recordAcc(float ax,float ay,float az,float a,int flag) {
		s= "加速度X:"+ax+"加速度Y:"+ay+"加速度Z:"+az+"总加速度"+flag+":"+a ;
		try {
			myFile.Write(s+"\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("myFile write 错误");
		}
	}
	public void recordAngel(float gx,float gy,float gz) {
		s= "   角速度X："+gx+" 角速度Y："+gy+" 角速度Z："+gz ;
		try {
			myFile.Write(s+"\r\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("myFile write 错误");
		}
	}
	public void switchState() {

	}
	public void setInformation(final String deviceInfo){

	}
	public void getTemperature(byte[] message){
		at=t =0;
		t |= message[0];
		t <<= 8;
		t |= (message[1]&0x00FF);
		at = (float) (t/10.00);
		at = (float)(Math.round(at*100))/100;
		setTemperature(at);
		System.out.println("----->temperature:"+at);
	}
	public void setTemperature(final float at){

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
	public void connectDevice(final BluetoothDevice leDevice) {
//		System.out.println("------>connectDevice");
		Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show();
		setInformation(""+leDevice.getName()+"\n"+leDevice.getAddress()+"\n"+leDevice.getUuids());
		Runnable mRunnable =new  Runnable() {
			public void run() {
				try {
					leDevice.connectGatt(context, false, new AccDeviceBtGattCallback(SensorMeasurementDevice.this,SensorActivity.acN)
							{
								@Override
								public void onServicesDiscovered(BluetoothGatt gatt, int status) {
									System.out.println("------>acNo"+SensorActivity.acN);

//							System.out.println("------>onServicesDiscovered status"+status);
									List<BluetoothGattService> services = gatt.getServices();
									Log.i("service", "serivces.size:"+services.size());
									if(services != null && services.size()>0){
										for(BluetoothGattService service : services) {
											Log.i("ACC", service.getUuid().toString());
//									System.out.println("------>service:"+service.getUuid().toString());
											List<BluetoothGattCharacteristic> charList = service.getCharacteristics();
											//chaList = charList;
											if(charList != null && charList.size()>0){
												for(BluetoothGattCharacteristic cha : charList) {
													if(cha.getUuid().toString().equals(ACC_CHARACTERISTIC_UUID)) {
//												System.out.println("------>btAngelChararistic"+cha.getUuid().toString());
														btCharacteristicAcc = cha;
														btDescriptorAcc = cha.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
														btDescriptorAcc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
														gatt.writeDescriptor(btDescriptorAcc);
														gatt.setCharacteristicNotification(btCharacteristicAcc, true);
													}
												}
											}
										}
									}
								};
							}
					);
				} catch (Exception e) {
					// TODO Auto-generated catch block
//					System.out.println("------>connect error");
				}
				SensorActivity.acN+=1;
			}};
		new Thread(mRunnable).start();
		new Thread(new  Runnable() {
			public void run() {
				try {
					leDevice.connectGatt(context, false, new AccDeviceBtGattCallback(SensorMeasurementDevice.this,SensorActivity.acN){
						@Override
						public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//								System.out.println("------>acNo"+SensorActivity.acN);

//								System.out.println("------>onServicesDiscovered status"+status);
							List<BluetoothGattService> services = gatt.getServices();
							Log.i("service", "serivces.size:"+services.size());
							if(services != null && services.size()>0){
								for(BluetoothGattService service : services) {
									Log.i("ACC", service.getUuid().toString());
//										System.out.println("------>service:"+service.getUuid().toString());
									List<BluetoothGattCharacteristic> charList = service.getCharacteristics();
									//chaList = charList;
									if(charList != null && charList.size()>0){
										for(BluetoothGattCharacteristic cha : charList) {
											if(cha.getUuid().toString().equals(ANGEL_CHARACTERISTIC_UUID)) {
//													System.out.println("------>btAngelChararistic"+cha.getUuid().toString());
												btCharacteristicAngel = cha;
												gatt.readCharacteristic(btCharacteristicAngel);
												btDescriptorAngel = cha.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
												btDescriptorAngel.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
												gatt.writeDescriptor(btDescriptorAngel);
												gatt.setCharacteristicNotification(btCharacteristicAngel, true);
											}
										}
									}
								}
							}
						};
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
//						System.out.println("------>connect error");
				}
				SensorActivity.acN +=1;
			}}).start();
	}


	private class AccDeviceLeScanCallback implements BluetoothAdapter.LeScanCallback {
		SensorMeasurementDevice accDevice;

		public AccDeviceLeScanCallback(SensorMeasurementDevice accDevice) {
			super();
			this.accDevice = accDevice;
		}


		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		}

	}

	private class AccDeviceBtGattCallback extends BluetoothGattCallback {

		SensorMeasurementDevice accDevice;
		BluetoothGatt btGatt;
		Context context;
		int deviceNumber=0;

		public AccDeviceBtGattCallback(SensorMeasurementDevice accDevice,int n) {
			this.accDevice = accDevice;
			deviceNumber = n;
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			if(newState == BluetoothGatt.STATE_CONNECTED) {

				accDevice.deviceState = CONNECTED;
				btGatt = gatt;
				gatt.discoverServices();
				switchState();
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic){
//			System.out.println("characteristicUUID:" + characteristic.getUuid());
			if(deviceNumber==0||deviceNumber==1){
				if(characteristic.getUuid().toString().equals(ANGEL_CHARACTERISTIC_UUID)){
					int chaLong = characteristic.getValue().length;
//				System.out.println("------>messageLength:"+chaLong);
					byte[] message = characteristic.getValue();
					for(int i=0;i<chaLong;i++){
					}
					if(message != null && message.length > 0){
						accDevice.dataAngelProcessing(message,deviceNumber);
					}
				}else{
					if(characteristic.getUuid().toString().equals(ACC_CHARACTERISTIC_UUID)){
						byte[] message = characteristic.getValue();
						if(message != null && message.length > 0){
							accDevice.dataAccProcessingA0(message);
						}
					}
				}
			}else{
				if(deviceNumber==2||deviceNumber==3){
					if(characteristic.getUuid().toString().equals(ANGEL_CHARACTERISTIC_UUID)){
						int chaLong = characteristic.getValue().length;
//					System.out.println("------>messageLength:"+chaLong);
						byte[] message = characteristic.getValue();
						for(int i=0;i<chaLong;i++){
						}
						if(message != null && message.length > 0){
							accDevice.dataAngelProcessing(message,deviceNumber);
						}
					}else{
						if(characteristic.getUuid().toString().equals(ACC_CHARACTERISTIC_UUID)){
							byte[] message = characteristic.getValue();
							if(message != null && message.length > 0){
								accDevice.dataAccProcessingA1(message);
							}
						}
					}
				}else {
					if(deviceNumber==3||deviceNumber==4){
						if(characteristic.getUuid().toString().equals(ANGEL_CHARACTERISTIC_UUID)){
							int chaLong = characteristic.getValue().length;
//							System.out.println("------>messageLength:"+chaLong);
							byte[] message = characteristic.getValue();
							for(int i=0;i<chaLong;i++){
							}
							if(message != null && message.length > 0){
								accDevice.dataAngelProcessing(message,deviceNumber);
							}
						}else{
							if(characteristic.getUuid().toString().equals(ACC_CHARACTERISTIC_UUID)){
								byte[] message = characteristic.getValue();
								if(message != null && message.length > 0){
									accDevice.dataAccProcessingA2(message);
								}
							}
						}
					}else
						Toast.makeText(context, "传感器过多", Toast.LENGTH_SHORT).show();
				}
			}
		}


		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			// TODO Auto-generated method stub
			super.onReadRemoteRssi(gatt, rssi, status);
		}

	}
	private class MyFile{
		FileOutputStream fout;
		public MyFile(String fileName){
			try{
				fout = new FileOutputStream(fileName,false);
			}catch(FileNotFoundException e){
				System.out.println("fout 错误");
			}
		}
		public void Write( String str) throws IOException {
			byte[] bytes = str.getBytes();
			fout.write(bytes);
		}
		public void Close() throws IOException {
			fout.close();
			fout.flush();
		}
	}

	private static float FACTOR = 0.001f;//取接近0的数
	private static float interval =0.14f;
	private static float[] q ={1,0,0,0};
	void mix_gyrAcc_crossMethod(final float gyr[],final float acc[],float interval)
	{
		//
		float w_q = q[0];
		float x_q = q[1];
		float y_q = q[2];
		float z_q = q[3];
		float x_q_2 = x_q * 2;
		float y_q_2 = y_q * 2;
		float z_q_2 = z_q * 2;
		//
		// 加速度计的读数，单位化。
		double a2= Math.sqrt(acc[0]*acc[0]+acc[1]*acc[1]+acc[2]*acc[2]);
		float a_rsqrt = (float) a2;
		float x_aa = acc[0] * a_rsqrt;
		float y_aa = acc[1] * a_rsqrt;
		float z_aa = acc[2] * a_rsqrt;   //加速度计测量出的加速度向量(载体坐标系下)
		//
		// 载体坐标下的重力加速度向量，单位化。
		float x_ac = x_q*z_q_2 - w_q*y_q_2;
		float y_ac = y_q*z_q_2 + w_q*x_q_2; //通过四元数旋转矩阵与地理坐标系下的重力加速度向量[0 0 0 1]叉乘得到载体坐标系下的重力加速度向量
		float z_ac = 1 - x_q*x_q_2 - y_q*y_q_2;//（主要）角速度计测出的四元数表示的载体坐标系下的重力加速度向量（这里已转换成载体坐标系下）
		//
		// 测量值与常量的叉积。
		float x_ca = y_aa * z_ac - z_aa * y_ac;
		float y_ca = z_aa * x_ac - x_aa * z_ac;
		float z_ca = x_aa * y_ac - y_aa * x_ac;//角速度计测出的角度误差，叠加的FACTOR大小可以实验试凑

		// 构造增量旋转。
		float delta_x = gyr[0] * interval / 2 + x_ca * FACTOR;
		float delta_y = gyr[1] * interval / 2 + y_ca * FACTOR;
		float delta_z = gyr[2] * interval / 2 + z_ca * FACTOR;
		//
		// 融合，四元数乘法。
		q[0] = w_q         - x_q*delta_x - y_q*delta_y - z_q*delta_z;
		q[1] = w_q*delta_x + x_q         + y_q*delta_z - z_q*delta_y;
		q[2] = w_q*delta_y - x_q*delta_z + y_q         + z_q*delta_x;
		q[3] = w_q*delta_z + x_q*delta_y - y_q*delta_x + z_q;
		float recipNorm = (float) Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
		q[0] *= recipNorm;
		q[1] *= recipNorm;
		q[2] *= recipNorm;
		q[3] *= recipNorm;//归一化
	}

}
