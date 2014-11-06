package com.toaster.locationservicelistener;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationServiceListener implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener
{
	public static final int STATUS_ERROR=-1;
	public static final int STATUS_DISCONNECTED=0;
	public static final int STATUS_CONNECTING=1;
	public static final int STATUS_CONNECTED=2;
	
	protected LocationClient locationClient;
	protected long updateInterval;
	protected int status;
	protected int errorType;

	protected LocationListener listener;
	
	public LocationServiceListener(Context context,LocationListener listener,long updateInterval)
	{
		locationClient=new LocationClient(context,this,this);
		this.listener=listener;
		this.updateInterval=updateInterval;
		locationClient.connect();
		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) 
	{
		this.status=STATUS_ERROR;
		this.errorType=result.getErrorCode();
		// TODO Auto-generated method stub
	}
	
	public int getStatus()
	{
		return this.status;
	}
	
	public int getErrorType()
	{
		return this.errorType;
	}

	@Override
	public void onConnected(Bundle arg0) 
	{
		this.status=STATUS_CONNECTED;
		LocationRequest locRequest=LocationRequest.create();
		locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locRequest.setInterval(this.updateInterval);
		locRequest.setFastestInterval(500);
		locationClient.requestLocationUpdates(locRequest, listener);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
