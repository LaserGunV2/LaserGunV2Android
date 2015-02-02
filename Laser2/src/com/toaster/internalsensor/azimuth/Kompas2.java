package com.toaster.internalsensor.azimuth;

import com.toaster.internalsensor.InternalSensorHandler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Kompas2 extends AbstractAzimuthCalculator implements SensorEventListener
{
	protected AzimuthMovingAverageCalculator aziMovingAverage;
	protected Sensor gravSensor;
	protected Sensor magSensor;
	//protected MainActivity ui;
	protected double[] gravSensorResult=new double[3];
	protected double[] normalizedNormalVector=new double[3];
	protected double[] magneticSensorResult=new double[3];
	protected double[] fwdVector=new double[3];
	protected double[] projectedMagVector=new double[3];
	protected double betaRadian;
	protected double azimuth;
	//protected InternalSensorHandler handler;
	
	/*
	public Kompas2(Context context,MainActivity ui)
	{
		aziMovingAverage=new AzimuthMovingAverageCalculator(50);
		this.ui=ui;
		SensorManager sensorManager=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		
		gravSensor=sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		magSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, gravSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, magSensor,SensorManager.SENSOR_DELAY_GAME);
	}*/
	
	public Kompas2(InternalSensorHandler handler,int id)
	{
		super(handler,id);
		this.aziMovingAverage=new AzimuthMovingAverageCalculator(50);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub
		
	}

	protected String createDebugString()
	{
		double lenSquared=0;
		for (int i=0;i<gravSensorResult.length;i++)
		{
			lenSquared+=gravSensorResult[i]*gravSensorResult[i];
		}
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append("Gravity:\n");
		sBuffer.append("x:"+gravSensorResult[0]+"\n");
		sBuffer.append("y:"+gravSensorResult[1]+"\n");
		sBuffer.append("z:"+gravSensorResult[2]+"\n");
		sBuffer.append("length:"+Math.sqrt(lenSquared)+"\n");
		sBuffer.append("FwdVector:\n");
		sBuffer.append("beta:"+ Math.toDegrees(betaRadian)+"\n");
		sBuffer.append("x:"+fwdVector[0]+"\n");
		sBuffer.append("y:"+fwdVector[1]+"\n");
		sBuffer.append("z:"+fwdVector[2]+"\n");
		
		lenSquared=0;
		for (int i=0;i<magneticSensorResult.length;i++)
		{
			lenSquared+=magneticSensorResult[i]*magneticSensorResult[i];
		}
		sBuffer.append("Magnetic:\n");
		sBuffer.append("x:"+magneticSensorResult[0]+"\n");
		sBuffer.append("y:"+magneticSensorResult[1]+"\n");
		sBuffer.append("z:"+magneticSensorResult[2]+"\n");
		
		sBuffer.append("length:"+Math.sqrt(lenSquared)+"\n");
		sBuffer.append("Projected Magnetic:\n");
		sBuffer.append("x:"+projectedMagVector[0]+"\n");
		sBuffer.append("y:"+projectedMagVector[1]+"\n");
		sBuffer.append("z:"+projectedMagVector[2]+"\n");
		sBuffer.append("\nAzimuth:"+azimuth);
		return sBuffer.toString();
	}
	
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType()==Sensor.TYPE_GRAVITY)
		{
			for (int i=0;i<gravSensorResult.length;i++)
			{
				gravSensorResult[i]=event.values[i];
			}
			double gravLen=0;
			for (int i=0;i<gravSensorResult.length;i++)
			{
				gravLen+=gravSensorResult[i]*gravSensorResult[i];
			}
			gravLen=Math.sqrt(gravLen);
			for (int i=0;i<gravSensorResult.length;i++)
			{
				gravSensorResult[i]/=gravLen;
				normalizedNormalVector[i]=-gravSensorResult[i];
			}
			
			
			betaRadian=findBetaRadian(gravSensorResult[0], gravSensorResult[1], gravSensorResult[2]);
			fwdVector[0]=0;
			fwdVector[1]=Math.cos(betaRadian);
			fwdVector[2]=Math.sin(betaRadian);
					
			
		}
		else if (event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
		{
			for (int i=0;i<magneticSensorResult.length;i++)
			{
				magneticSensorResult[i]=event.values[i];
			}
			double magLen=0;
			for (int i=0;i<magneticSensorResult.length;i++)
			{
				magLen+=magneticSensorResult[i]*magneticSensorResult[i];
			}
			magLen=Math.sqrt(magLen);
			for (int i=0;i<magneticSensorResult.length;i++)
			{
				magneticSensorResult[i]/=magLen;
			}
		}
		projectMagVector(magneticSensorResult, gravSensorResult, projectedMagVector);
		double azimuthRad=calculateAzimuth(projectedMagVector, fwdVector,normalizedNormalVector);
		//if (azimuth!=Double.NaN)
//		if ((azimuthRad<=-Math.PI)||(azimuthRad>=Math.PI))
//		{
//			Log.v(this.getClass().getName(),"azimuth"+ azimuthRad);
//		}
		this.aziMovingAverage.insertData(azimuthRad);
		//this.azimuth=this.aziMovingAverage.getCurrentValue();
		this.azimuth=Math.toDegrees(this.aziMovingAverage.getCurrentValue());
		this.azimuth-=180; 
		//msh belum ketemu kenapa kebalik. kemungkinan di dot productnya
		//kalo yang diganti vektor normalnya dia bkln arahny yg kebalik (jd ccw) 
		if (this.azimuth<0)
		{
			this.azimuth=360+this.azimuth;
		}
		this.handler.onAzimuthUpdated(this.azimuth, this.id);
		//Log.v(this.getClass().getCanonicalName(), "azi="+this.azimuth);
		//ui.setDebugText(createDebugString());
	
	}
	
	public double calculateAzimuth(double[] projectedMagVect,double fwdVect[],double normalizedNormVect[])
	{
//		double magDotFwd=0;
//		double magVectLenSquared=0;
//		double fwdVectLenSquared=0;
//		for (int i=0;i<projectedMagVect.length;i++)
//		{
//			magDotFwd+=projectedMagVect[i]*fwdVect[i];
//			magVectLenSquared+=projectedMagVect[i]*projectedMagVect[i];
//			fwdVectLenSquared+=fwdVect[i]*fwdVect[i];
//		}
//		double result=Math.acos(magDotFwd/(Math.sqrt(magVectLenSquared))*Math.sqrt(fwdVectLenSquared));
//		return Math.toDegrees(result);
		double dotProduct=(projectedMagVect[0]*fwdVect[0])+(projectedMagVect[1]*fwdVect[1])+(projectedMagVect[2]*fwdVect[2]);
		double crossProduct=
				(projectedMagVect[0]*fwdVect[1]*normalizedNormVect[2])
				+(projectedMagVect[2]*fwdVect[0]*normalizedNormVect[1])
				+(projectedMagVect[1]*fwdVect[2]*normalizedNormVect[0])
				-(projectedMagVect[2]*fwdVect[1]*normalizedNormVect[0])
				-(projectedMagVect[0]*fwdVect[2]*normalizedNormVect[1])
				-(projectedMagVect[1]*fwdVect[0]*normalizedNormVect[2]);
		double azimuthRad=Math.atan2(crossProduct, dotProduct);
		//azimuthRad-=Math.PI;
		//if (azimuthRad<0)
		//	azimuthRad=(Math.PI*2)+azimuthRad;
		if (Double.isNaN(azimuthRad))
			return 0;
		else
			return azimuthRad;
	}
	
	public void projectMagVector(double[] magVect,double[] normVect,double[] projectedMagVector)
	{
		double normProjectionMult=0;
		double normLengthSquared=0;
		for (int i=0;i<magVect.length;i++)
		{
			normProjectionMult+=magVect[i]*normVect[i];
			normLengthSquared+=normVect[i]*normVect[i];
		}
		normProjectionMult/=normLengthSquared;
		for (int i=0;i<projectedMagVector.length;i++)
		{
			projectedMagVector[i]=magVect[i]-(normVect[i]*normProjectionMult);
		}
	}
	
	
	
	public final double findBetaRadian(double gravX,double gravY,double gravZ)
	{
		double gravZSquared=gravZ*gravZ;
		double gravYSquared=gravY*gravY;
		double betaRad[]=new double[4]; 
		
		double acosBeta=Math.sqrt(1/((gravYSquared/gravZSquared)+1));
		betaRad[0]=Math.acos(acosBeta);
		betaRad[1]=betaRad[0]+(Math.PI/2);
		betaRad[2]=Math.acos(-acosBeta);
		betaRad[3]=betaRad[2]+(Math.PI/2);
		
		int minResultIdx=0;
		double minResult=Math.abs((gravY*Math.cos(betaRad[minResultIdx]))+(gravZ*Math.sin(betaRad[minResultIdx])));
		for (int i=1;i<betaRad.length;i++)
		{
			double curResult=Math.abs((gravY*Math.cos(betaRad[i]))+(gravZ*Math.sin(betaRad[i])));
			if (minResult>curResult)
			{
				minResult=curResult;
				minResultIdx=i;
			}
		}
		return betaRad[minResultIdx];
	}
}
