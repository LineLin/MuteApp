package com.line.alermapp.util;

import java.text.DecimalFormat;


public class FormatUtils {
	
	private static DecimalFormat format = new DecimalFormat();
	
	public static String formatInt(int source,String pattern){
		format.applyPattern(pattern);
		return format.format(source);
	}
	
	public static void main(String[] args){
		int a = 0;
		System.out.println(formatInt(a,"##"));
	}
}
