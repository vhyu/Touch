package com.mycompany.myapp4;
import android.os.*;

public class mMessage
{
	public static void send(int what,Object text,Handler han){
		Message m=new Message();
		m.what=what;
		m.obj=text;
		han.sendMessage(m);
	}
}
//这个message指的是service1和service2互相调用，并不指的是发送到服务气的message