package com.toaster.internalsensor.pose;

public class MovingAverageCalculator 
{
	protected float[] queue;
	protected int enqIdx;
	protected int dataCount;
	protected float curTotal;
	
	public MovingAverageCalculator(int windowSize)
	{
		queue=new float[windowSize];
		enqIdx=0;
		dataCount=0;
	}
	//to do: insertdata dgn getcurrentvalue hrs synchronized ke 1 object
	public boolean insertData(float newData)
	{
		if (dataCount==queue.length)
		{
			curTotal=curTotal-queue[enqIdx];
		}
		queue[enqIdx]=newData;
		curTotal=curTotal+newData;
		enqIdx=(enqIdx+1)%queue.length;
		if (dataCount<queue.length)
			dataCount++;
		if (dataCount==queue.length)
			return true;
		else 
			return false;
	}
	
	public float getCurrentValue()
	{
		return curTotal/queue.length;
	}
}
