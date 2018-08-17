package com.mycompany.myapp4;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataUtil
{
	public static Data getData(Context c){
		Data data=new Data();
		data.TAG="Touch";
		data.Data=new CopyOnWriteArrayList<Data>();
		data.Time=getTime();
        //原代码
		//		data.Process=getLauncherTopApp(c,c.getSystemService(ActivityManager.class));
        //改动后
        data.Process=getLauncherTopApp(c, (ActivityManager) c.getSystemService(String.valueOf(ActivityManager.class)));
        if (data.Process.isEmpty())
        {
            String EmptyChar = "main";
            data.Process = EmptyChar;
        }


        return data;
	}
	private static String getTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");//设置日期格式
		return(df.format(new Date()));
	}
	public static String getLauncherTopApp(Context context, ActivityManager activityManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            //5.0以后需要用这方法
            //原来代码
//            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            //我的改动后
            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");

            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }
}