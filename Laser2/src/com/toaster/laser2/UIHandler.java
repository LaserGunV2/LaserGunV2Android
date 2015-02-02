package com.toaster.laser2;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;

public interface UIHandler 
{
	public void appendDebug(String s);
	public void clearDebug();

	public void setAndroidId(String androidId);
	public void setNik(String nik);
	public void setUIMode(int uiMode);
	public void setStatus(String status);
	public void setDebugStatus(String debugStatus);
	public void setPlayerAliveStatus(boolean isPlayerAlive);
	public void setFoundBTDevices(ArrayList<BluetoothDevice> deviceList);
	public void btDeviceFound(BluetoothDevice foundDevice);
	public void updateErrorStatus(ArrayList<String> errorList);
}
