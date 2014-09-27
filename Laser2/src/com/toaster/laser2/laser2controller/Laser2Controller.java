package com.toaster.laser2.laser2controller;


import java.net.InetAddress;

import com.toaster.internalsensor.InternalSensorController;
import com.toaster.internalsensor.InternalSensorHandler;
import com.toaster.internalsensor.PoseCalculator;
import com.toaster.laser2.MainActivity;
import com.toaster.laser2.UIHandler;
import com.toaster.laser2.communicationpacket.CommunicationPacket;
import com.toaster.laser2.communicationpacket.ConfirmPacket;
import com.toaster.laser2.communicationpacket.RegistrationPacket;
import com.toaster.laser2.communicationpacket.UpdatePacket;
import com.toaster.laser2.locationcontroller.LocationController;
import com.toaster.laser2.locationcontroller.LocationControllerHandler;
import com.toaster.udpcommunication.IMessageHandler;
import com.toaster.udpcommunication.UDPPacketManager;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class Laser2Controller implements LocationControllerHandler,IMessageHandler,InternalSensorHandler
{
	protected final static int STATE_DISCONNECTED=0;
	protected final static int STATE_CONNECTED=1;
	
	protected final static int UDP_TARGETPORT=21500;
	protected final static int UDP_RECEIVEPORT=21501;
	
	//protected CommunicationPacket commPacket;
	protected int state;
	protected LocationController locController;
	protected UDPPacketManager commManager;
	protected InternalSensorController internalSensor;
	protected Location currentLocation;
	protected UIHandler ui;
	protected InetAddress serverAddress;
	protected int androidId;
	protected int pose;
	protected float azimuth;
	
	public Laser2Controller(Context context,UIHandler ui)
	{
		//Log.v("DEBUG", "test");
		//commPacket=new CommunicationPacket();
		this.commManager=UDPPacketManager.createBroadcastUDPManager(this, context, UDP_TARGETPORT,UDP_RECEIVEPORT);
		this.locController=new LocationController(context,this);
		this.internalSensor=new InternalSensorController(context, this);
		this.ui=ui;
		this.state=STATE_DISCONNECTED;
		this.androidId=-1;
	}

	@Override
	public void onLocationUpdate(Location newLocation) 
	{
		if (this.state==STATE_CONNECTED)
		{
			this.currentLocation=newLocation;
			ui.clearDebug();
			ui.appendDebug(Location.convert(newLocation.getLatitude(), Location.FORMAT_DEGREES)+" "+Location.convert(newLocation.getLongitude(),Location.FORMAT_DEGREES)+" "+newLocation.getAccuracy());
			UpdatePacket commPacket=new UpdatePacket();
			commPacket.location=newLocation;
			commPacket.id=Integer.toString(this.androidId);
			commPacket.action=UpdatePacket.ACTIONTYPE_UPDATE;
			commPacket.heading=Math.round(azimuth);
			if (this.pose==PoseCalculator.POSE_PRONE)
				commPacket.state=UpdatePacket.STATE_ALIVECRAWL;
			else
				commPacket.state=UpdatePacket.STATE_ALIVESTAND;
			byte[] buffer=commPacket.getAsByteArray();
			commManager.send(buffer, 0, buffer.length, 0, serverAddress);
			//commManager.broadcast(buffer, 0, buffer.length, 0);
		}
	}

	@Override
	public void onMessageReceived(InetAddress senderAddress,byte[] buffer, int length) 
	{
		CommunicationPacket decodedPacket=CommunicationPacket.decodePacket(buffer);
		Log.v("Laser2Controller", decodedPacket.type);
		if (decodedPacket.type.equals(CommunicationPacket.MESSAGETYPE_CONFIRM))
		{
			serverAddress=senderAddress;
			androidId=((ConfirmPacket)decodedPacket).androidId;
			this.state=STATE_CONNECTED;
			Log.v("Laser2Controller", "connected. androidId="+this.androidId+",server:"+serverAddress.getHostAddress());
			ui.setAndroidId(Integer.toString(this.androidId));
			ui.setUIMode(MainActivity.UIMODE_GAME);
		}
		else if (decodedPacket.type.equals(CommunicationPacket.MESSAGETYPE_ENDGAME))
		{
			serverAddress=null;
			androidId=-1;
			this.state=STATE_DISCONNECTED;
			ui.setUIMode(MainActivity.UIMODE_REGISTRATION);
		}
		// TODO Auto-generated method stub
		
	}
	
	public void register(String gameId,String nomorInduk)
	{
		RegistrationPacket regPacket=new RegistrationPacket();
		regPacket.gameId=gameId;
		regPacket.nomorInduk=nomorInduk;
		byte[] buffer=regPacket.getAsByteArray();
		//commManager.send(buffer, 0, buffer.length, 0);
		commManager.broadcast(buffer, 0, buffer.length, 0);
	}

	@Override
	public void onAzimuthUpdated(float azimuth) {
		this.azimuth=azimuth;
	}

	@Override
	public void onPoseUpdated(int pose) {
		this.pose=pose;		
	}
	
	
}
