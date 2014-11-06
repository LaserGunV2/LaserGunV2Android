package com.toaster.laser2.locationcontroller;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationListener;
import com.toaster.locationservicelistener.LocationServiceListener;

public class LocationController implements LocationListener
{
	protected final static int UPDATE_INTERVAL=1000;
	
	protected LocationServiceListener locSrvListener;
	protected LocationControllerHandler handler;
	
	public LocationController(Context context,LocationControllerHandler handler)
	{
		this.locSrvListener=new LocationServiceListener(context, this, UPDATE_INTERVAL);
		this.handler=handler;
	}

	public boolean getStatus()
	{
		if (this.locSrvListener.getStatus()==LocationServiceListener.STATUS_ERROR)
			return false;
		else 
			return true;
	}
	
	public int getErrorCode()
	{
		return this.locSrvListener.getErrorType();
	}
	
	@Override
	public void onLocationChanged(Location newLocation) 
	{
		handler.onLocationUpdate(newLocation);
	}
}
