package com.example.sm9m2cds11;

import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jnidriver.*;

public class MainActivity extends Activity {

	ReceiveThread mReceiveThread;
	FNDThread mFNDThread;
	
	boolean mStart;
	boolean mThreadRun = true;
	
	int in_cda, mMaxCount;
	
	JNIDriver mDriver = new JNIDriver();
	
	byte[] data1 = {1,0,0,0,0,0,0,0};
	byte[] data2 = {0,1,0,0,0,0,0,0};
	byte[] data3 = {0,0,0,0,0,0,0,0};
	byte[] data_emergency = {1,1,1,1,1,1,1,1};
	
	boolean stop_flg = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		mDriver.write(data3); // LED 상태 초기화
		
		Button btn1 = (Button) findViewById(R.id.button1);
		btn1.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				mReceiveThread = new ReceiveThread();
				mReceiveThread.start();
				
				mFNDThread = new FNDThread();
				mFNDThread.start();
				
				mThreadRun = true;
				stop_flg = false;
				
			}
		});
		Button btn2 = (Button) findViewById(R.id.button2);
		btn2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				stop_flg = true; // 스레드 종료, LED OFF, 조도 출력 지우기
			}
		});
	}
	private class ReceiveThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (mThreadRun) {
				Message text = Message.obtain();
				handler.sendMessage(text);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private class FNDThread extends Thread {
		@Override
		public void run() {
			FileReader in;
			int in_cda;
			
			super.run();
			while (mThreadRun) {
				byte[] n= {0,0,0,0,0,0,0};
				
				try {
					in = new FileReader("/sys/devices/12d10000.adc/iio:device0/in_voltage3_raw");
					BufferedReader br = new BufferedReader(in);
					String data = br.readLine();
					
					in_cda = Integer.parseInt(data);	
					
					if(mStart==false) { mDriver.writeFND(n); }
					else {
						n[0] = (byte)(in_cda % 1000000 / 100000);
						n[1] = (byte)(in_cda % 100000 / 10000);
						n[2] = (byte)(in_cda % 10000 / 1000);
						n[3] = (byte)(in_cda % 1000 / 100);
						n[4] = (byte)(in_cda % 100 / 10);
						n[5] = (byte)(in_cda % 10);
						mDriver.writeFND(n);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			TextView tv;
			FileReader in;
			int in_cda;
			try {
				in = new FileReader("/sys/devices/12d10000.adc/iio:device0/in_voltage3_raw");
				BufferedReader br = new BufferedReader(in);
				String data = br.readLine();
				
				tv = (TextView) findViewById(R.id.textView1);
				in_cda = Integer.parseInt(data);		
				
				mStart = true;
				
				if (stop_flg == true) {
					tv.setText("CDS: ");
					mDriver.write(data3);
					mThreadRun = false;
					mStart = false;
				} else if (in_cda < 3000) {
					tv.setText("CDS: " + data + " (Street 0 - ON)");
					mDriver.write(data1);
				} else if (in_cda >= 3000 && in_cda < 3500) {
					tv.setText("CDS: " + data + " (Street 1 - ON)");
					mDriver.write(data2);
				} else if (in_cda >= 3500) {
					tv.setText("CDS: " + data + " (스프링쿨러 작동 시작)");
					mDriver.write(data_emergency);
					mDriver.setBuzzer((byte) 0x08);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	protected void onPause() {
		mDriver.close();
		mDriver.closeFND();
		mDriver.closePZ();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if (mDriver.open("/dev/sm9s5422_led") < 0 || mDriver.openFND("/dev/sm9s5422_segment") < 0 || mDriver.openPZ("/dev/sm9s5422_piezo") < 0){
			Toast.makeText(MainActivity.this, "Driver Open Failed", Toast.LENGTH_SHORT).show();
		}
		
		mReceiveThread = new ReceiveThread();
		mReceiveThread.start();
		mFNDThread = new FNDThread();
		mFNDThread.start();
		mThreadRun = true;
		
		super.onResume();
	}
}

















