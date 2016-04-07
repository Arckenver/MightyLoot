package com.arckenver.mightyloot.object;

import org.apache.commons.lang3.RandomUtils;

public class Interval
{
	private int a;
	private int b;
	
	public Interval(int a, int b)
	{
		this.a = a;
		this.b = b;
	}
	
	public int getRandom()
	{
		return RandomUtils.nextInt(a, b+1);
	}
	
	public String toString()
	{
		return "[" + a + ";" + b + "]";
	}
}
