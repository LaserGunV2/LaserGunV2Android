package com.toaster.internalsensor;

public interface InternalSensorHandler 
{
	public void onAzimuthUpdated(double azimuth,int id);
	public void onPoseUpdated(int pose);
}
