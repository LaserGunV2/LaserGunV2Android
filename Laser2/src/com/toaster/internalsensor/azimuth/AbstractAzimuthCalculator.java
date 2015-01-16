package com.toaster.internalsensor.azimuth;

import com.toaster.internalsensor.InternalSensorHandler;

public class AbstractAzimuthCalculator
{
	protected int id;
	protected InternalSensorHandler handler;
	
	public AbstractAzimuthCalculator(InternalSensorHandler handler,int id)
	{
		this.id=id;
		this.handler=handler;
	}
}
