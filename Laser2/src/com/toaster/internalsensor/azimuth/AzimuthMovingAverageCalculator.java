package com.toaster.internalsensor.azimuth;

public class AzimuthMovingAverageCalculator
{
	//inputnya dalam radian
	protected static final int IDX_X=0;
	protected static final int IDX_Y=1;
	
	protected double[][] queue;
	protected int enqIdx;
	protected int dataCount;
	protected double sumX;
	protected double sumY;
	
	public AzimuthMovingAverageCalculator(int windowSize)
	{
		queue=new double[windowSize][2];
	}
	
	public boolean insertData(double newData)
	{
		if (dataCount==queue.length)
		{
			sumX=sumX-queue[enqIdx][IDX_X];
			sumY=sumY-queue[enqIdx][IDX_Y];
			//curTotal=curTotal-queue[enqIdx];
		}
		
		//queue[enqIdx]=newData;
		//curTotal=curTotal+newData;
		double curX=Math.cos(newData);
		double curY=Math.sin(newData);
		queue[enqIdx][IDX_X]=curX;
		queue[enqIdx][IDX_Y]=curY;
		sumX=sumX+curX;
		sumY=sumY+curY;
		enqIdx=(enqIdx+1)%queue.length;
		if (dataCount<queue.length)
			dataCount++;
		if (dataCount==queue.length)
			return true;
		else 
			return false;
	} 
	
	public double getCurrentValue()
	{
		return Math.atan2(this.sumY, this.sumX);
	}

}
