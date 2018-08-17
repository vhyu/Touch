package com.mycompany.myapp4;

import java.util.*;

public class Data
{
	public String TAG;
	public String type;
	public Object Value;
	public String Time;
	public String Process;
	public List<Data> Data;

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return TAG+","+Time+","+Value+","+type+","+Process+"\n";
	}
	
}
