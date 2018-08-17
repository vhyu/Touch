package com.mycompany.myapp4;

public class toHex
{
	static StringBuffer buf;
	
	public static String toHex(String str)
	{
		buf = new StringBuffer();
		if (str.indexOf("0") == -1)
		{
			buf.append(str);
			return buf.toString();
		}
		buf.append(Integer.parseInt(str, 16));
//		System.out.println(buf.toString());
		return buf.toString();
	}
}
