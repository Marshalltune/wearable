package com.srp.wearable_multi;

import android.app.Activity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.srp.wearable_multi.R;




public class HeartActivity extends Activity {
	private BluetoothAdapter btAdapter;
	private LinearLayout chartDisp;
	private GraphicalView chartView;
	
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

	private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	
	private XYSeries series;
	private XYSeriesRenderer xyRenderer;
	HeartRateMeasurementDevice hrmDevice;
	
	private int curX=0;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure_heart);
		System.out.println("run---------creat");
		chartDisp = (LinearLayout)findViewById(R.id.chartDisp);
		final BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
		btAdapter = bluetoothManager.getAdapter();
		chartView = ChartFactory.getLineChartView(this, dataset, renderer);
	    renderer.setClickEnabled(false);
	    renderer.setSelectableBuffer(100);
		
	    chartDisp.addView(chartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    
	    series = new XYSeries("amp");
        dataset.addSeries(series);
        xyRenderer = new XYSeriesRenderer();
        renderer.addSeriesRenderer(xyRenderer);
        xyRenderer.setPointStyle(PointStyle.POINT);
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(100);
//        renderer.setYAxisMin(1500);
//        renderer.setYAxisMax(2500);
        xyRenderer.setFillPoints(true);
		
        System.out.println("run---------try");
		

        File file1 = new File(this.getFilesDir(),"u1.txt");
        File file2 = new File(this.getFilesDir(),"u2.txt");
		try {
			System.out.println("run---------file");
			final FileOutputStream ofs2 = new FileOutputStream(file2);
			final FileOutputStream ofs1 = new FileOutputStream(file1);
	
			System.out.println("run---------stream");
			final HeartRateMeasurementDevice hrmDevice = new HeartRateMeasurementDevice(this) {

				@Override
				public void onHeartRateReceived(final int heartRate) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							System.out.println("run---------receive");
							TextView modeButton = (TextView) findViewById(R.id.switchButton);
							modeButton.setText("U2:心率");
							TextView textView = (TextView) findViewById(R.id.heartRateU2);
							textView.setText(""+heartRate);
//							
//							try{
//								
//								ofs2.write(("U2:"+heartRate+"\n").getBytes());
//							}catch(Exception e) {
//								e.printStackTrace();
//							}
					}});
				}
				
				@Override
				public void onAmplitudeReceived(final int amplitude) {

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							TextView modeButton = (TextView) findViewById(R.id.switchButton);
							modeButton.setText("U1:幅值");
							series.add(curX++, amplitude);
							if(curX > 100) {
								renderer.setXAxisMin(curX - 100);
						        renderer.setXAxisMax(curX);
							}
							chartView.repaint();
							
							try{
								
								ofs1.write(("U1:"+amplitude+"\n").getBytes());
							}catch(Exception e) {
								e.printStackTrace();
							}
					}});
				}
			};
			

			System.out.println("run---------button");
			TextView findViewButton = (TextView)findViewById(R.id.findDeviceButton);
			TextView modeButton = (TextView) findViewById(R.id.switchButton);
			findViewButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Log.i("click","click");
					if(btAdapter.getState()==BluetoothAdapter.STATE_OFF){//若蓝牙未打开,则打开
						btAdapter.enable();	
						Toast.makeText(HeartActivity.this, "opening Bluetooth...", 1).show();
						try {
							Thread.sleep(1000);						//设置延迟，等待蓝牙打开
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
					Intent findDeviceIntent = new Intent(HeartActivity.this, HeartDeviceDiscoverActivity.class);
					startActivity(findDeviceIntent);
					
				}}
			);
			
			modeButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					hrmDevice.switchMode();
					
				}}
			);
			
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		System.exit(0);
		finish();
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
