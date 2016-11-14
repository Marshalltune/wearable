package com.srp.wearable_multi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class DrawChartActivity extends Activity {

		private LinearLayout chartDisp0,chartDisp1,chartDisp2;
		private GraphicalView chartView0,chartView1,chartView2;
		private String accTitle = "ACC Sensor";
		private XYSeries seriesA0,seriesA1,seriesA2,serierA3;
		private XYMultipleSeriesDataset dataset0,dataset1,dataset2;
		private XYMultipleSeriesRenderer renderer0,renderer1,renderer2;	
		private EditText fileNameEt;
		private String fileLastName;
		private int XMax = 50;
		private int[] curX={0,0,0,0};
		
		@SuppressLint("NewApi")
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.draw_chart);
//			TextView nameTv = (TextView) findViewById(R.id.acc);
//			nameTv.setText(fileName);
			dataset0= new XYMultipleSeriesDataset();
			chartDisp0 = (LinearLayout)findViewById(R.id.chartDispDraw0);
	        seriesA0 = new XYSeries("A");   
	        dataset0.addSeries(seriesA0); 
	        renderer0 = new XYMultipleSeriesRenderer();
		    renderer0.addSeriesRenderer(setR(Color.BLUE,PointStyle.CIRCLE,true,3,true));
		    setChartSettings(renderer0, "X", "Y",0, XMax, -1, 5, Color.BLUE, Color.BLUE,accTitle);
	        chartView0 = ChartFactory.getLineChartView(this, dataset0, renderer0);
	        chartDisp0.addView(chartView0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        dataset1= new XYMultipleSeriesDataset();
	        chartDisp1 = (LinearLayout)findViewById(R.id.chartDispDraw1);
	        seriesA1 = new XYSeries("A1");   
	        dataset1.addSeries(seriesA1); 
	        renderer1 = new XYMultipleSeriesRenderer();
	        renderer1.addSeriesRenderer(setR(Color.RED,PointStyle.CIRCLE,true,3,true));
	        setChartSettings(renderer1, "X", "Y",0, XMax, -1, 5, Color.BLUE, Color.BLUE,accTitle);
	        chartView1 = ChartFactory.getLineChartView(this, dataset1, renderer1);
	        chartDisp1.addView(chartView1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	        dataset2= new XYMultipleSeriesDataset();
	        chartDisp2 = (LinearLayout)findViewById(R.id.chartDispDraw2);
	        seriesA2 = new XYSeries("A2");   
	        dataset2.addSeries(seriesA2); 
	        renderer2 = new XYMultipleSeriesRenderer();
	        renderer2.addSeriesRenderer(setR(Color.GREEN,PointStyle.CIRCLE,true,3,true));
	        setChartSettings(renderer2, "X", "Y",0, XMax, -1, 5, Color.BLUE, Color.BLUE,accTitle);
	        chartView2 = ChartFactory.getLineChartView(this, dataset2, renderer2);
	        chartDisp2.addView(chartView2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			fileNameEt = (EditText) findViewById(R.id.file_name_et);
			Button fileNameBt = (Button) findViewById(R.id.file_name_button);
			fileNameBt.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					fileLastName = fileNameEt.getText().toString();
					drawChart();
				}
			});
		}

		protected void drawChart(){
			final String fileName ="/storage/emulated/0/wearable/"+fileLastName+".txt";
			new Thread(new  Runnable() {
				public void run() {
					FileInputStream fis;
					try {
						fis = new FileInputStream(fileName);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader bffr = new BufferedReader(isr);
						StringBuffer sBuffer = new StringBuffer();
						DataInputStream dataIO = new DataInputStream(fis);//读取文件数据流
						String strLine = null;
						byte[] byteLine = null;
						DataInputStream dis;
						try {
							while((strLine =  bffr.readLine()) != null) {//通过readline按行读取
							    System.out.println(strLine);
							    byteLine = strLine.getBytes();
							    int len = byteLine.length;
							    int start=0;
							    for(int i = 0;i<len;i++){
//							    	System.out.println(byteLine[i]);
							    	if(byteLine[i]==-70&&byteLine[i+1]==-90){
							    		if(byteLine[i+2]==49){
							    			byte[] byteAcc = new byte[8];
							    			System.out.println(""+byteLine[i+4]);
									    	for(int j=0;j<8;j++){
									    		byteAcc[j] = byteLine[i+4+j];
									    		System.out.println(""+byteAcc[j]);
									    		}
									    	String srt0=new String(byteAcc,"UTF-8");
									    	System.out.println("str2:"+srt0);
											float f0 =Float.parseFloat(srt0);
											onAmplitudeReceivedA0(f0);
									    	System.out.println("e:"+f0);
							    		}
							    		if(byteLine[i+2]==50){
						    				System.out.println(""+byteLine[i+4]);
						    				byte[] byteAcc1 = new byte[8];
									    	for(int j=0;j<8;j++){
									    		byteAcc1[j] = byteLine[i+4+j];
									    		System.out.println(""+byteAcc1[j]);
									    		}
									    	String srt1=new String(byteAcc1,"UTF-8");
									    	System.out.println("str2:"+srt1);
											float f1 =Float.parseFloat(srt1);
											onAmplitudeReceivedA1(f1);
									    	System.out.println("e:"+f1);
						    			}
							    		if(byteLine[i+2]==51){
							    			byte[] byteAcc2 = new byte[8];
					    					System.out.println(""+byteLine[i+4]);
									    	for(int j=0;j<8;j++){
									    		byteAcc2[j] = byteLine[i+4+j];
									    		System.out.println(""+byteAcc2[j]);
									    		}
									    	String srt2=new String(byteAcc2,"UTF-8");
									    	System.out.println("str2:"+srt2);
											float f2 =Float.parseFloat(srt2);
											onAmplitudeReceivedA2(f2);
									    	System.out.println("e:"+f2);
					    				}	
							    	}
							    }
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}      
						try {
							bffr.close();
							isr.close();
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}}).start();
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
					     renderer.setXTitle("Point");
					     renderer.setYTitle("a");
					     renderer.setYLabelsAlign(Align.RIGHT);
					     renderer.setPointSize((float) 2);
					     renderer.setShowLegend(false);
					    }
		public void onAmplitudeReceivedA0(final float a) {
			runOnUiThread(new Runnable() {					
				@Override
				public void run() {
					// TODO Auto-generated method stub
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
		public void onAmplitudeReceivedA1(final float a) {
			runOnUiThread(new Runnable() {					
				@Override
				public void run() {
					// TODO Auto-generated method stub
					curX[1]++;
					seriesA1.add(curX[1], a);
					if(curX[1] > XMax) {
						renderer1.setXAxisMin(curX[1] - XMax);
						renderer1.setXAxisMax(curX[1]);
					}
					chartView1.repaint();
				}
			});
		}
		public void onAmplitudeReceivedA2(final float a) {
			runOnUiThread(new Runnable() {					
				@Override
				public void run() {
					// TODO Auto-generated method stub
					curX[2]++;
					seriesA2.add(curX[2], a);
					if(curX[2] > XMax) {
						renderer2.setXAxisMin(curX[2] - XMax);
						renderer2.setXAxisMax(curX[2]);
					}
					chartView2.repaint();
				}
			});
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
