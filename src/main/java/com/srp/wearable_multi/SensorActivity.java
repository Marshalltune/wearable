package com.srp.wearable_multi;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class SensorActivity extends Activity {
	private BluetoothAdapter btAdapter;
	private LinearLayout chartDisp0,chartDisp1,chartDisp2,chartDisp3;
	private GraphicalView chartView0,chartView1,chartView2,chartView3;
	private XYSeries seriesA1,seriesA2,seriesA3,seriesA0;
	private XYMultipleSeriesDataset dataset0,dataset1,dataset2,dataset3;
	private XYMultipleSeriesRenderer renderer0,renderer1,renderer2,renderer3;	
	SensorMeasurementDevice mAccDevice;
	private int XMax = 50;
	private int[] curX = {0,0,0,0,0};
	private boolean mRecordState;
	private final String title[]= {"传感器1","传感器2","传感器3","传感器4"};
	public static int acN,deviceNo;
	private EditText inputFileNameET;
//	private static final SensorMeasurementDevice accDevice;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure_sensor);
		System.out.println("run---------creat");
		mRecordState = true;
		acN = deviceNo =0;
		final BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
		btAdapter = bluetoothManager.getAdapter();
//	    renderer.setClickEnabled(false);
//	    renderer.setSelectableBuffer(100);	
		chartDisp0 = (LinearLayout)findViewById(R.id.chartDispView0);
		dataset0= new XYMultipleSeriesDataset();
		seriesA0 = new XYSeries("A0");   
		dataset0.addSeries(seriesA0); 
		renderer0 = new XYMultipleSeriesRenderer();
		renderer0.addSeriesRenderer(setR(Color.BLUE,PointStyle.CIRCLE,true,3,true));
		setChartSettings(renderer0, "X", "Y",0, XMax, -1, 5, Color.BLUE, Color.BLUE,title[0]);
		chartView0 = ChartFactory.getLineChartView(this, dataset0, renderer0);
		chartDisp0.addView(chartView0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		chartDisp1 = (LinearLayout)findViewById(R.id.chartDispView1);
		dataset1= new XYMultipleSeriesDataset();
        seriesA1 = new XYSeries("A1");   
        dataset1.addSeries(seriesA1);   
        renderer1 = new XYMultipleSeriesRenderer();
        renderer1.addSeriesRenderer(setR(Color.RED,PointStyle.CIRCLE,true,3,true));
        setChartSettings(renderer1, "X", "Y",0, XMax, -1, 5, Color.BLUE, Color.BLUE,title[1]);
        chartView1 = ChartFactory.getLineChartView(this, dataset1, renderer1);
        chartDisp1.addView(chartView1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        chartDisp2 = (LinearLayout)findViewById(R.id.chartDispView2);
        dataset2= new XYMultipleSeriesDataset();
        seriesA2 = new XYSeries("A2");   
        dataset2.addSeries(seriesA2);   
        renderer2 = new XYMultipleSeriesRenderer();
        renderer2.addSeriesRenderer(setR(Color.GREEN,PointStyle.CIRCLE,true,3,true));
        setChartSettings(renderer2, "X", "Y",0, XMax, -1, 5, Color.BLUE, Color.BLUE,title[2]);
        chartView2 = ChartFactory.getLineChartView(this, dataset2, renderer2);
        chartDisp2.addView(chartView2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        seriesA2 = new XYSeries("A2");   
//        dataset2.addSeries(seriesA2);   
//        seriesA3 = new XYSeries("A3");   
//        dataset3.addSeries(seriesA3);   
//	    renderer2 = new XYMultipleSeriesRenderer();
//	    renderer2.addSeriesRenderer(setR(Color.BLUE,PointStyle.CIRCLE,true,3,true));
//        setChartSettings(renderer1, "X", "Y",0, XMax, -5, 5, Color.BLUE, Color.BLUE,title[2]);
//        chartView2 = ChartFactory.getLineChartView(this, dataset2, renderer2);
//        chartDisp2.addView(chartView2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	
		}
	@Override
	protected void onStart() {
		Log.i("Activity", "onstart");
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("run---------try");
//		try {
			System.out.println("run---------file");
			System.out.println("run---------stream");
			final SensorMeasurementDevice accDevice = new SensorMeasurementDevice(this) {

				@Override
				public void onAmplitudeReceivedA0(final float a) {
					runOnUiThread(new Runnable() {					
						@Override
						public void run() {
							System.out.println("a0");
//							System.out.println("---->run");
							// TODO Auto-generated method stub
//							System.out.println("------>addSeries");
							curX[0]++;
							seriesA0.add(curX[0], a);
							if(curX[0] > XMax) {
								renderer0.setXAxisMin(curX[0] - XMax);
								renderer0.setXAxisMax(curX[0]);
							}
							chartView0.repaint();
						}
					});
				}
				@Override
				public void onAmplitudeReceivedA1(final float a1) {
					runOnUiThread(new Runnable() {					
						@Override
						public void run() {
							System.out.println("a1");
//							System.out.println("---->run");
							// TODO Auto-generated method stub
//							System.out.println("------>addSeries");
							curX[1]++;
							seriesA1.add(curX[1], a1);
							if(curX[1] > XMax) {
								renderer1.setXAxisMin(curX[1] - XMax);
								renderer1.setXAxisMax(curX[1]);
							}
							chartView1.repaint();
						}
					});
				}
				@Override
				public void onAmplitudeReceivedA2(final float a2) {
					runOnUiThread(new Runnable() {					
						@Override
						public void run() {
							System.out.println("a2");
//							System.out.println("---->run");
							// TODO Auto-generated method stub
//							System.out.println("------>addSeries");
							curX[2]++;
							seriesA2.add(curX[2], a2);
							if(curX[2] > XMax) {
								renderer2.setXAxisMin(curX[2] - XMax);
								renderer2.setXAxisMax(curX[2]);
							}
							chartView2.repaint();
						}
					});
				}

				@Override
				public void switchState(){
//				    iniRecord();!!!
					Toast.makeText(SensorActivity.this, "已连接", Toast.LENGTH_LONG).show();
					}
			};

			Button startRecord = (Button) findViewById(R.id.startRecord);
			Button stopRecord = (Button) findViewById(R.id.stopRecord);
			Button watchChart = (Button) findViewById(R.id.watchChart);
			inputFileNameET = (EditText) findViewById(R.id.fileNameEdit);
			mAccDevice = accDevice;
			startRecord.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					mRecordState = true;
					accDevice.mRecordState = mRecordState;
//					accDevice.setRecord(true);
					System.out.println("accdevice.record =true");
					accDevice.inputFileName=  inputFileNameET.getText().toString();
					accDevice.iniRecord();
					Toast.makeText(SensorActivity.this, "开始记录", Toast.LENGTH_SHORT).show();
				}}
			);
			stopRecord.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					accDevice.mRecordState = false;
					Toast.makeText(SensorActivity.this, "停止记录", Toast.LENGTH_SHORT).show();
				}
			});
			watchChart.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent drawChartIntent = new Intent(SensorActivity.this, DrawChartActivity.class);
					startActivity(drawChartIntent);
				}
			});
			System.out.println("run---------button");
			Button findViewButton = (Button)findViewById(R.id.findDeviceButton);
			findViewButton.setClickable(true);
//			TextView stateButton = (TextView) findViewById(R.id.connectState);
			findViewButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Log.i("click","click");
					if(btAdapter.getState()==BluetoothAdapter.STATE_OFF){//若蓝牙未打开,则打开
						btAdapter.enable();
						try {
							Thread.sleep(1000);						//设置延迟，等待蓝牙打开
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						}
					Intent findDeviceIntent = new Intent(SensorActivity.this, SensorDeviceDiscoverActivity.class);
					startActivity(findDeviceIntent);
					
				}}
			);
			
//		}
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("Activity", "onrestart");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("Activity", "onresume");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("Activity", "onstop");
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		mAccDevice =null;
//		mAccDevice.disconnectDevice();
//		System.out.println("disconnect");
//		finish();
//		System.exit(0);
//		android.os.Process.killProcess(android.os.Process.myPid());
		Log.i("Activity", "ondestory");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("Activity", "onpause");
	}
	protected XYSeriesRenderer setR(int color,PointStyle style, boolean FillPoints,int LineWidth,boolean ChartValues){
   	 XYSeriesRenderer r = new XYSeriesRenderer();
	     
	     r.setColor(color);
	     r.setPointStyle(style);
	     r.setFillPoints(FillPoints);
	     r.setLineWidth(3);
	     r.setDisplayChartValues(true);
	     return r;
   }
	
	  protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
			    double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor,String title) {
			     renderer.setChartTitle(title);
			     renderer.setXTitle(xTitle);
			     renderer.setYTitle(yTitle);
			     renderer.setAxisTitleTextSize(20);
			     renderer.setChartTitleTextSize(30);
			     renderer.setXAxisMin(xMin);
			     renderer.setXAxisMax(xMax);
			     renderer.setYAxisMin(yMin);
			     renderer.setYAxisMax(yMax);
			     renderer.setAxesColor(axesColor);
			     renderer.setLabelsColor(labelsColor);
			     renderer.setShowGrid(true);
			     renderer.setGridColor(Color.WHITE);
			     renderer.setXLabels(10);
			     renderer.setYLabels(10);
			     renderer.setYLabelsAlign(Align.RIGHT);
			     renderer.setPointSize((float) 2);
			     renderer.setShowLegend(false);
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

