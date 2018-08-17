package com.mycompany.myapp4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AndroidRuntimeException;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Job implements Shell.Callback
{
	Shell shell;
	Data All;
	Boolean isF = TRUE;

	Handler han=new Handler(){

		@SuppressLint("WrongConstant")
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 1:
//					try
//					{
//						w.write( msg.obj.toString());
//						w.flush();
//					System.out.print("case 1 中的msg 是什么");
//					System.out.println(msg.obj.toString());
//					System.out.print("case 1 中的d 是什么");
//					System.out.println(Share_send_d.send_d.toString());


//					}
//					catch (IOException e)
//					{
//						throw new AndroidRuntimeException(e);
//					}
					break;
				case 2:
					System.out.print("case 2 中的msg 是什么:");
					System.out.println(msg.obj.toString());
					Toast.makeText(context,msg.obj.toString(),2).show();
					break;
			}
			super.handleMessage(msg);
		}

	};

	private static Thread myThread;

//	新的线程，传送获取到的数据
	private static Sendthread sendThread;
	private static Recethread receThread;

//	private BufferedWriter w;
	@Override
	public void onRuesult(String StrResult) {

		String line = StrResult;

		if (line.indexOf("EV") == -1) {
			return;
		}
		String[] arg = sort(line.split(" "));
		Data d = DataUtil.getData(context);

		d.type = arg[1];
		String value = toHex.toHex(arg[2]);
		if (d.type.indexOf("POSITION_X") != -1) {
			d.Value = Integer.parseInt(value);
		} else if (d.type.indexOf("POSITION_Y") != -1) {
			d.Value = Integer.parseInt(value);
		} else {
			d.Value = value;
		}
		//		设置当前的静态变量
//		synchronized(Share_send_d.send_d)
//		{
		Share_send_d.send_d = d;
//		}


//		启动发送消息的线程
		if (isF) {
			sendThread = new Sendthread();
			new Thread(sendThread).start();
//			receThread = new Recethread();
//			new Thread(receThread).start();
			isF = FALSE;
		}

//		8.15
//		System.out.print("++++++++++++");
//		System.out.print(Share_send_d.send_d);
//		System.out.print("++++++++++++\n");



//		vhyu注释掉
//		没有太大作用
//		mMessage.send(1, d, handler);


//		d 存储的信息就是要发送的信息
//		开一个线程发送信息
//		发送完信息之后，服务器端将接收到的消息存储到文件中去
//		当写入文件达到一定的大小之后，换下一个文件，这样把耗时的io操作转移到了服务器上，总共有几个这样的文件进行轮盘。
//		接收也可以在这里

//		System.out.print("the value of the new d：");
//		System.out.println(d.toString());
	}

	@Override
	public void onisok(boolean isok)
	{
		// TODO: Implement this method
	}
	
	private static Job job=null;
	Context context; //存储的信息是android.app.Application@4336665，与当前操纵的app无关
	Handler handler;
	
	private Job(Context con){
	
		try
		{
			
//				File f=new File("/storage/emulated/0/test.csv");
//				w = new BufferedWriter(new FileWriter(f,true));

			All=DataUtil.getData(con);
			context=con;
			handler=han;
			shell = new Shell();
			shell.setCallback(Job.this);

//			SensorMonitor sensorMonitor=new SensorMonitor (context,"SensorData.csv");
//			sensorMonitor.start ();
			
		}
		catch (Throwable e)
		{
			throw new AndroidRuntimeException(e.toString());
		}
	}
	private String[] sort(String[] str){
		List<String>list=new ArrayList<String>();
		for(String s:str){
			if(!s.equals("")){
				list.add(s);
			}
		}
		list.remove(0);
		return list.toArray(new String[]{});
	}
	
	public void Run() throws IOException {
		myThread=new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						shell.EnterSu();
						shell.shell("getevent -l");
					}
					catch (Throwable e)
					{
						throw new AndroidRuntimeException(e.toString());
					}
				}
			});
		myThread.start();
	}
	
	public static boolean isRun(){
		return (job!=null)&&(myThread.isAlive());
	}
	public static Job getJob(Context c){
		if(job==null){
			return job=new Job(c);
		}else{
			return job;
		}
	}
}