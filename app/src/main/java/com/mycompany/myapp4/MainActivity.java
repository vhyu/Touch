package com.mycompany.myapp4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lzg.strongservice.service.Service1;
import com.lzg.strongservice.service.Service2;

public class MainActivity extends Activity 
{
	//private BufferedWriter w;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Intent i1 = new Intent(MainActivity.this, Service1.class);
		startService(i1);

		Intent i2 = new Intent(MainActivity.this, Service2.class);
		startService(i2);
		finish();

    }
}
