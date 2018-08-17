package com.mycompany.myapp4;

import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vhyu on 2018/7/2.
 */

public class Recethread implements Runnable,KillPidShell.Callback {
    String ServerIp = "202.199.6.212";
    int ServerPort = 2048;
    Socket s;
    int flag = 1;
    String strResultHeader = "";
    String strResultContent = "";
    KillPidShell aKillshell;

    @Override
    public void run() {
        BufferedReader in = null;

        while (true) {
            try {
                if (flag ==1)
                {
                    s= new Socket(ServerIp, ServerPort);
                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    flag = 2;
                }
                if (in != null) {
                    System.out.println("接收到信息了");
                    System.out.println(in.read());
                }

                //                                strResultHeader存储的是服务器是否收到header，正常情况下应该是header OK
                strResultHeader = in.readLine();

//                                strResultContent存储的是服务器的预测结果，正常情况下应该是legal user或者是illgeal user
                strResultContent = in.readLine();

            } catch (Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("收信息抛异常");
            }

                if (strResultContent.toString().equals("legal user")) {
                        //admit
                    }
                if (strResultContent.toString().equals("illegal user")) {
                    Date curDate = new Date(System.currentTimeMillis());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Message msg1 = new Message();
                    msg1.obj = "Illeagl user,program will be closed";
                    System.out.print("!!!!!!不合法！！！！！");
                    //                    杀死进程
                    try {
                        aKillshell = new KillPidShell();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    aKillshell.setCallback(Recethread.this);
                    String package_name = Share_send_d.send_d.Process.toString();
                    String cmd_str = "am force-stop " + "com.tencent.mm";
                    aKillshell.EnterSu();
                    aKillshell.doShell(cmd_str);
                    System.out.print("、、、、、kill the process、、、、、");
                }
            }
        }
    @Override
    public void onRuesult(String StrResult) {

    }

    @Override
    public void onisok(boolean isok) {

    }
}
