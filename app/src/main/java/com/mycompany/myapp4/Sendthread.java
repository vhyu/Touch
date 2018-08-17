package com.mycompany.myapp4;

import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by vhyu on 2018/6/25.
 */

public class Sendthread implements Runnable{
//    public volatile static Data d;

    Data LastD = new Data();
    Socket s;
    public int i = 0;
    boolean isFirst=true;
    String ServerIp="202.199.6.212";
    int ServerPort=2049;
    OutputStream outputStream;
    BufferedReader in = null;
    BufferedWriter out = null;
    String UserId="test";

    public void run() {
        String strResultHeader = "";
        String strResultContent = "";
        Message msg = new Message();

        //System.exit(0);
        while (true) {
//            try {
//                sleep(100); //暂停，每一秒输出一次
//            }catch (InterruptedException e) {
//                System.out.println("error");
//            }
            try {
                if (isFirst) {
                    s = new Socket(ServerIp, ServerPort);
//      store the content
                    outputStream = s.getOutputStream();
//                      because when we receive the respond, we only receive the content, we don't need to get the header.
                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
//      store the size of the content and userId
                    out = new BufferedWriter(new OutputStreamWriter(outputStream));
                    isFirst = false;
                }
                if (!(LastD.equals(Share_send_d.send_d))) {
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    LastD = Share_send_d.send_d;
                    System.out.println("if 之后的Share:");
                    System.out.println(Share_send_d.send_d);

                    StringBuilder sendcontent=new StringBuilder(Share_send_d.send_d.toString());
//                  send the length of the stream
                    out.write(String.valueOf(sendcontent.toString().getBytes().length) + "\r\n" + UserId);
                    out.flush();
                    int lensize = sendcontent.toString().getBytes().length;
                    String strbuffer = in.readLine();
                    System.out.println(strbuffer);
                    System.out.println("the length of the file is ok!");

                    outputStream.write(sendcontent.toString().getBytes(), 0, (int) lensize);
                    outputStream.flush();
//                    用来测试的，输出发送结果
//                    String strbuffer = in.readLine();
//                    System.out.println("***************sendthread**********");
//                    System.out.println(send_str_len);
//                    i++;
//                    System.out.print("发送结果:");
//                    System.out.println(i);

                    msg.what = 1;
                    msg.obj = "";
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.out.println("发送信息抛异常1");
//            } catch (IOException e) {
//                e.printStackTrace();
//                msg.what = 0;
//                msg.obj = "文件读写错误";
//                System.out.println("文件读写错误");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("发送信息抛异常2");
            }
        }
    }
}