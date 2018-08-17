package com.lzg.strongservice.service;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import com.lzg.strongservice.utils.*;
import com.mycompany.myapp4.*;

import java.io.IOException;

/**
 * http://blog.csdn.net/liuzg1220
 * 
 * @author henry
 *
 */
public class Service2 extends Service {

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				startService1();
				break;

			default:
				break;
			}

		};
	};

	/**
	 * 使用aidl 启动Service1
	 */
	private StrongService startS1 = new StrongService.Stub() {

		@Override
		public void stopService() throws RemoteException {
			Intent i = new Intent(getBaseContext(), Service1.class);
			getBaseContext().stopService(i);
		}

		@Override
		public void startService() throws RemoteException {
			Intent i = new Intent(getBaseContext(), Service1.class);
			getBaseContext().startService(i);

		}
	};

	/**
	 * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service1
	 */
	@Override
	public void onTrimMemory(int level) {
		startService1();
	}

	@SuppressLint("NewApi")
	public void onCreate() {
		if(Job.isRun()){
			try {
				Job.getJob(getApplicationContext()).Run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Toast.makeText(Service2.this, "Service2 启动中...", Toast.LENGTH_SHORT)
				.show();
		startService1();
		/*
		 * 此线程用监听Service2的状态
		 */
		new Thread() {
			public void run() {
				while (true) {
					boolean isRun = Utils.isServiceWork(Service2.this,
							"com.lzg.strongservice.service.Service1");
					if (!isRun) {
						Message msg = Message.obtain();
						msg.what = 1;
						handler.sendMessage(msg);
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	/**
	 * 判断Service1是否还在运行，如果不是则启动Service1
	 */
	private void startService1() {
		boolean isRun = Utils.isServiceWork(Service2.this,
				"com.lzg.strongservice.service.Service1");
		if (isRun == false) {
			try {
				startS1.startService();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (IBinder) startS1;
	}

}
