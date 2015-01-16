package com.toaster.internalsensor;

import java.util.List;

import com.toaster.internalsensor.azimuth.AzimuthCalculator;
import com.toaster.internalsensor.azimuth.Kompas2;
import com.toaster.internalsensor.pose.PoseCalculator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class InternalSensorController implements SensorEventListener,InternalSensorHandler
{
	public static final int SENSORSTATUS_MAGNETIC=0;
	public static final int SENSORSTATUS_GRAVITY=1;
	
	protected InternalSensorHandler handler;
    protected AzimuthCalculator azimuth;
    protected Kompas2 azimuth2;
    protected PoseCalculator pose;
	public boolean[] sensorStatus;
	protected int activeAzimuthCalculatorId=1;
    
	public InternalSensorController(Context context,InternalSensorHandler handler)
	{
		this.handler=handler;
		SensorManager sensorManager=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		this.checkSensorStatus(sensorManager);
		if (this.getStatus())
		{
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
			this.azimuth=new AzimuthCalculator(this,0);
			this.pose=new PoseCalculator(this);
			this.azimuth2=new Kompas2(this,1);
		}
	}
	
	public void setActiveAzimuthCalculatorId(int id)
	{
		this.activeAzimuthCalculatorId=id;
	}
	
	protected void checkSensorStatus(SensorManager sensorManager)
	{
		sensorStatus=new boolean[2];
		List<Sensor> result=sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if (result.size()>0)
			 sensorStatus[SENSORSTATUS_MAGNETIC]=true;
		else 
			sensorStatus[SENSORSTATUS_MAGNETIC]=false;
		result=sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
		if (result.size()>0)
			 sensorStatus[SENSORSTATUS_GRAVITY]=true;
		 else
			 sensorStatus[SENSORSTATUS_GRAVITY]=false;
	}

	public boolean getStatus()
	{
		boolean result=true;
		for (int i=0;i<sensorStatus.length;i++)
			result=result&&sensorStatus[i];
		return result;
	}
	
	public boolean getStatus(int sensorType)
	{
		return this.sensorStatus[sensorType];
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		azimuth.onSensorChanged(event);
		pose.onSensorChanged(event);
		azimuth2.onSensorChanged(event);
	}

	@Override
	public void onAzimuthUpdated(double azimuth,int id) {
		if (this.activeAzimuthCalculatorId==id)
			handler.onAzimuthUpdated(azimuth,id);
		
	}

	@Override
	public void onPoseUpdated(int pose) {
		handler.onPoseUpdated(pose);
	}
}
