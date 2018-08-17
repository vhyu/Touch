package com.mycompany.myapp4;
import android.content.*;
import com.lzg.strongservice.service.*;

public  class BootReceiver extends BroadcastReceiver {  
   // private PendingIntent mAlarmSender;  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        // 在这里干你想干的事（启动一个Service，Activity等），本例是启动一个定时调度程序，每30分钟启动一个Service去更新数据  
        
		Intent i1 = new Intent(context, Service1.class);
		context.startService(i1);

		Intent i2 = new Intent(context, Service2.class);
		context.startService(i2);
		}  
}  
