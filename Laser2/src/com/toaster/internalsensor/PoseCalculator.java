package com.toaster.internalsensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class PoseCalculator implements SensorEventListener
{
	public static final int POSE_STANDING=0;
    public static final int POSE_PRONE=1;
	
	protected final static double DOMINANT_AXIS_THRESHOLD=8.0;
	protected final static double STABLE_VECTOR_LENGTH_TOLERANCE=0.2;
	protected final static double STABLE_VECTOR_LENGTH=9.80665;
	
	protected InternalSensorHandler handler;
	protected double squaredStableVectorLength=STABLE_VECTOR_LENGTH*STABLE_VECTOR_LENGTH;
	protected double squaredStableVectorLengthTolerance=STABLE_VECTOR_LENGTH_TOLERANCE*STABLE_VECTOR_LENGTH_TOLERANCE;
	protected MovingAverageCalculator[] axisMAvg;
	
	
	public PoseCalculator(InternalSensorHandler handler)
	{
		this.handler=handler;
		this.axisMAvg=new MovingAverageCalculator[3];
		for (int i=0;i<this.axisMAvg.length;i++)
		{
			this.axisMAvg[i]=new MovingAverageCalculator(100);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType()==Sensor.TYPE_GRAVITY)
		{  
			int dominantAxis=determineDominantAxis(event.values);
			if (dominantAxis>=0)
			{
				for (int i=0;i<axisMAvg.length;i++)
				{
					if (i==dominantAxis)
					{
						axisMAvg[i].insertData(1);
					}
					else
					{
						axisMAvg[i].insertData(0);
					}
				}
			}
           //float m_Norm_Gravity = (float)Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1] + event.values[2]*event.values[2]);
           //this.debug.debug("0:"+event.values[0]+"\n1:"+event.values[1]+"\n2:"+event.values[2]+"\nlen:"+m_Norm_Gravity);
			int maxIdx=1;
			float maxValue=axisMAvg[maxIdx].getCurrentValue();
			String s="";
			for (int i=0;i<axisMAvg.length;i++)
			{ 
				float curValue=axisMAvg[i].getCurrentValue();
				s=s+curValue+" ";
				if (maxValue<curValue)
				{
					maxIdx=i;
					maxValue=curValue;
				}
			}
			//Log.v("out", s);
			if (maxIdx==1)
				handler.onPoseUpdated(PoseCalculator.POSE_STANDING);
			else
				handler.onPoseUpdated(PoseCalculator.POSE_PRONE);
		}
		
	}
	
	protected int determineDominantAxis(float[] gravityVector)
	{
		//float m_Norm_Gravity = (float)Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1] + event.values[2]*event.values[2]);
		float squaredGVectorLength=gravityVector[0]*gravityVector[0] + gravityVector[1]*gravityVector[1] + gravityVector[2]*gravityVector[2];
		//System.out.println(squaredStableVectorLength-squaredGVectorLength);
		int result=-1;
		if (Math.abs(squaredStableVectorLength-squaredGVectorLength)<this.squaredStableVectorLengthTolerance)
		{
			//System.out.println(squaredGVectorLength);
			for (int i=0;i<gravityVector.length;i++)
			{
				if (Math.abs(gravityVector[i])>DOMINANT_AXIS_THRESHOLD)
				{
					if ((result==-1)||(Math.abs(gravityVector[result])<Math.abs(gravityVector[i])))
						result=i;			
				}
					
			}
		}
		//System.out.println(result);
		return result;
	}

}
