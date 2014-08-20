package com.toaster.laser2.laser2controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.toaster.laser2.UIHandler;
import com.toaster.laser2.communicationpacket.CommunicationPacket;
import com.toaster.laser2.locationcontroller.LocationController;
import com.toaster.laser2.locationcontroller.LocationControllerHandler;
import com.toaster.udpcommunication.IMessageHandler;
import com.toaster.udpcommunication.UDPPacketManager;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class Laser2Controller implements LocationControllerHandler,IMessageHandler
{
	protected final static int UDP_PORT=21500;
	
	
	protected CommunicationPacket commPacket;
	protected LocationController locController;
	protected UDPPacketManager commManager;
	protected Location currentLocation;
	protected UIHandler ui;
	
	public Laser2Controller(Context context,UIHandler ui)
	{
		//Log.v("DEBUG", "test");
		commPacket=new CommunicationPacket();
		commManager=UDPPacketManager.createBroadcastUDPManager(this, context, UDP_PORT);
		locController=new LocationController(context,this);
		this.ui=ui;
	}

	@Override
	public void onLocationUpdate(Location newLocation) 
	{
		this.currentLocation=newLocation;
		ui.clearDebug();
		ui.appendDebug(Location.convert(newLocation.getLatitude(), Location.FORMAT_DEGREES)+" "+Location.convert(newLocation.getLongitude(),Location.FORMAT_DEGREES)+" "+newLocation.getAccuracy());
		commPacket.location=newLocation;
		commPacket.id="test";
		commPacket.action=CommunicationPacket.ACTIONTYPE_UPDATE;
		commPacket.type=CommunicationPacket.MESSAGETYPE_UPDATE;
		commPacket.heading=0;
		byte[] buffer=commPacket.getAsByteArray();
		
		commManager.broadcast(buffer, 0, buffer.length, 0);
	}

	@Override
	public void onMessageReceived(byte[] buffer, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMessageTag() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
