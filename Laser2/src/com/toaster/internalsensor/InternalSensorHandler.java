package com.toaster.internalsensor;

public interface InternalSensorHandler 
{
	public void onAzimuthUpdated(float azimuth);
	public void onPoseUpdated(int pose);
}
