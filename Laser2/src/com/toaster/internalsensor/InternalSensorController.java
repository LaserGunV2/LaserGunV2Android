package com.toaster.internalsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class InternalSensorController implements SensorEventListener,InternalSensorHandler
{
	protected InternalSensorHandler handler;
    protected AzimuthCalculator azimuth;
    protected PoseCalculator pose;
	
	public InternalSensorController(Context context,InternalSensorHandler handler)
	{
		this.handler=handler;
		SensorManager sensorManager=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
		this.azimuth=new AzimuthCalculator(this);
		this.pose=new PoseCalculator(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		azimuth.onSensorChanged(event);
		pose.onSensorChanged(event);
		
	}

	@Override
	public void onAzimuthUpdated(float azimuth) {
		handler.onAzimuthUpdated(azimuth);
		
	}

	@Override
	public void onPoseUpdated(int pose) {
		handler.onPoseUpdated(pose);
	}
}
