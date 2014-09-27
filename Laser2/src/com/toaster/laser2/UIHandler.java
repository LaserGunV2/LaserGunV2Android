package com.toaster.laser2;

public interface UIHandler 
{
	public void appendDebug(String s);
	public void clearDebug();

	public void setAndroidId(String androidId);
	public void setUIMode(int uiMode);
}
