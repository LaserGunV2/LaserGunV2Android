package com.toaster.laser2.laser2controller;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;

import com.google.android.gms.common.ConnectionResult;
import com.toaster.internalsensor.InternalSensorController;
import com.toaster.internalsensor.InternalSensorHandler;
import com.toaster.internalsensor.PoseCalculator;
import com.toaster.laser2.MainActivity;
import com.toaster.laser2.UIHandler;
import com.toaster.laser2.communicationpacket.CommunicationPacket;
import com.toaster.laser2.communicationpacket.ConfirmPacket;
import com.toaster.laser2.communicationpacket.HitPacket;
import com.toaster.laser2.communicationpacket.RegistrationPacket;
import com.toaster.laser2.communicationpacket.UpdatePacket;
import com.toaster.laser2.locationcontroller.LocationController;
import com.toaster.laser2.locationcontroller.LocationControllerHandler;
import com.toaster.udpcommunication.IMessageHandler;
import com.toaster.udpcommunication.UDPPacketManager;

import android.content.Context;
import android.location.Location;

public class Laser2Controller implements LocationControllerHandler, IMessageHandler, InternalSensorHandler
{
	protected final static int STATE_DISCONNECTED = 0;
	protected final static int STATE_CONNECTED = 1;

	protected final static int UDP_TARGETPORT = 21500;
	protected final static int UDP_RECEIVEPORT = 21501;

	protected final static long MINIMUM_UPDATE_INTERVAL = 1000;
	protected final static long MINIMUM_CONNECTION_RETRY_INTERVAL = 2000;
	protected final static int MAX_CONNECTION_RETRY_ATTEMPT = 5;

	// protected CommunicationPacket commPacket;
	protected int state;
	protected LocationController locController;
	protected UDPPacketManager commManager;
	protected InternalSensorController internalSensor;
	protected Location currentLocation;
	protected UIHandler ui;
	protected InetAddress serverAddress;
	protected String gameId;
	protected String nomorInduk;
	protected int androidId;
	protected int pose;
	protected float azimuth;
	protected ArrayList<String> errorList;
	protected Timer updateTimer;
	protected Timer connectionRetryTimer;
	protected int connectionAttemptCount;
	protected boolean playerAlive;
	protected boolean debugMode = false;

	public Laser2Controller(Context context, UIHandler ui)
	{
		this.commManager = UDPPacketManager.createBroadcastUDPManager(this, context, UDP_TARGETPORT, UDP_RECEIVEPORT);
		this.locController = new LocationController(context, this);
		this.internalSensor = new InternalSensorController(context, this);
		this.errorList = new ArrayList<String>();
		if (!this.locController.getStatus())
		{
			if (this.locController.getErrorCode() == ConnectionResult.SERVICE_MISSING)
			{
				this.errorList.add("Google Play Service tidak tersedia");
			}
			else
				if (this.locController.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED)
				{
					this.errorList.add("Google Play Service perlu diupdate");
				}
				else
				{
					this.errorList.add(Integer.toString(this.locController.getErrorCode()));
				}
		}
		if (!this.internalSensor.getStatus())
		{
			if (!this.internalSensor.getStatus(InternalSensorController.SENSORSTATUS_GRAVITY))
			{
				this.errorList.add("Tipe sensor GRAVITY tidak tersedia");
			}
			if (!this.internalSensor.getStatus(InternalSensorController.SENSORSTATUS_MAGNETIC))
			{
				this.errorList.add("Tipe sensor MAGNETIC tidak tersedia");
			}
		}
		this.ui = ui;
		this.pose = PoseCalculator.POSE_STANDING;
		this.state = STATE_DISCONNECTED;
		this.androidId = -1;
	}

	protected String createConnectionAttemptStatusString(boolean failed, int count)
	{
		if (failed)
		{
			return "Proses koneksi gagal";
		}
		else
		{
			return "Percobaan koneksi ke-" + count;
		}
	}

	public void enableDebugMode()
	{
		this.debugMode=true;
		this.ui.setUIMode(MainActivity.UIMODE_DEBUG);
	}

	protected String generateDebugString()
	{
		StringBuffer result = new StringBuffer();
		result.append("AndroidId = " + this.androidId + "\n");
		result.append("ServerAddress = " + this.serverAddress + "\n");
		if (this.currentLocation != null)
		{
			result.append("Latitude = " + Location.convert(this.currentLocation.getLatitude(), Location.FORMAT_DEGREES) + "\n");
			result.append("Longitude = " + Location.convert(this.currentLocation.getLongitude(), Location.FORMAT_DEGREES) + "\n");
			result.append("Accuracy = " + this.currentLocation.getAccuracy() + "\n");
		}
		result.append("Azimuth = " + this.azimuth + "\n");
		result.append("Pose = " + this.pose + "\n");
		return result.toString();
	}

	public ArrayList<String> getErrorList()
	{
		return this.errorList;
	}

	@Override
	public void onAzimuthUpdated(float azimuth)
	{
		this.azimuth = azimuth;
		this.ui.setDebugStatus(this.generateDebugString());
	}

	public void onConnectionRetry()
	{
		this.connectionAttemptCount++;
		if (this.connectionAttemptCount > MAX_CONNECTION_RETRY_ATTEMPT)
		{
			this.stopConnectionRetryTimer();
			this.ui.setStatus(this.createConnectionAttemptStatusString(true, this.connectionAttemptCount));

		}
		else
		{
			this.ui.setStatus(this.createConnectionAttemptStatusString(false, this.connectionAttemptCount));
			this.sendRegistrationPacket();
		}
	}

	@Override
	public void onLocationUpdate(Location newLocation)
	{
		this.currentLocation = newLocation;
		this.ui.setDebugStatus(this.generateDebugString());
		// this.sendUpdate();
	}

	@Override
	public void onMessageReceived(InetAddress senderAddress, byte[] buffer, int length)
	{
		CommunicationPacket decodedPacket = CommunicationPacket.decodePacket(buffer);
		// Log.v("Laser2Controller", decodedPacket.type);
		if (decodedPacket.type.equals(CommunicationPacket.MESSAGETYPE_CONFIRM))
		{
			this.serverAddress = senderAddress;
			this.androidId = ((ConfirmPacket) decodedPacket).androidId;
			this.state = STATE_CONNECTED;
			this.ui.setAndroidId(Integer.toString(this.androidId));
			this.stopConnectionRetryTimer();
			this.startUpdateTimer();
			this.ui.setDebugStatus(this.generateDebugString());
			if (!this.debugMode)
				this.ui.setUIMode(MainActivity.UIMODE_GAME);
			this.playerAlive=true;
			this.ui.setPlayerAliveStatus(this.playerAlive);
		}
		else if (decodedPacket.type.equals(CommunicationPacket.MESSAGETYPE_ENDGAME))
		{
			this.serverAddress = null;
			this.androidId = -1;
			this.state = STATE_DISCONNECTED;
			this.stopUpdateTimer();
			this.ui.setUIMode(MainActivity.UIMODE_REGISTRATION);
			this.ui.setDebugStatus(this.generateDebugString());
		}
		else if (decodedPacket.type.equals(CommunicationPacket.MESSAGETYPE_KILLED))
		{
			this.playerAlive=false;
			this.ui.setPlayerAliveStatus(this.playerAlive);
		}
		else if (decodedPacket.type.equals(CommunicationPacket.MESSAGETYPE_PONG))
		{

		}

		// TODO Auto-generated method stub
	}

	@Override
	public void onPoseUpdated(int pose)
	{
		this.pose = pose;
		this.ui.setDebugStatus(this.generateDebugString());
	}

	public void onUpdateEvent()
	{
		// Log.v("test", "update event");
		if (this.state == STATE_CONNECTED)
		{
			this.sendUpdate();
		}
	}

	protected void sendRegistrationPacket()
	{
		RegistrationPacket regPacket = new RegistrationPacket();
		regPacket.gameId = this.gameId;
		regPacket.nomorInduk = this.nomorInduk;
		byte[] buffer = regPacket.getAsByteArray();
		this.commManager.broadcast(buffer, 0, buffer.length, 0);
	}

	protected void sendUpdate()
	{
		// ui.clearDebug();
		// ui.appendDebug(Location.convert(this.currentLocation.getLatitude(),
		// Location.FORMAT_DEGREES)+" "+Location.convert(this.currentLocation.getLongitude(),Location.FORMAT_DEGREES)+" "+this.currentLocation.getAccuracy());
		UpdatePacket commPacket = new UpdatePacket();
		commPacket.location = this.currentLocation;
		commPacket.id = Integer.toString(this.androidId);
		commPacket.action = UpdatePacket.ACTIONTYPE_UPDATE;
		commPacket.heading = Math.round(this.azimuth);
		if (this.playerAlive==true)
		{
			if (this.pose == PoseCalculator.POSE_PRONE)
			{
				commPacket.state = UpdatePacket.STATE_ALIVECRAWL;
			}
			else
			{
				commPacket.state = UpdatePacket.STATE_ALIVESTAND;
			}
		}
		else 
		{
			if (this.pose == PoseCalculator.POSE_PRONE)
			{
				commPacket.state = UpdatePacket.STATE_DEADCRAWL;
			}
			else
			{
				commPacket.state = UpdatePacket.STATE_DEADSTAND;
			}
		}
		byte[] buffer = commPacket.getAsByteArray();
		this.commManager.send(buffer, 0, buffer.length, 0, this.serverAddress);
		// Log.v("out", "update");
	}

	protected void startConnectionRetryTimer()
	{
		this.stopConnectionRetryTimer();
		this.connectionRetryTimer = new Timer();
		this.connectionRetryTimer.scheduleAtFixedRate(new ConnectionRetryTask(this), MINIMUM_CONNECTION_RETRY_INTERVAL, MINIMUM_CONNECTION_RETRY_INTERVAL);
	}

	public void startRegistration(String gameId, String nomorInduk)
	{
		this.gameId = gameId;
		this.nomorInduk = nomorInduk;
		this.connectionAttemptCount = 1;
		this.sendRegistrationPacket();
		this.ui.setStatus(this.createConnectionAttemptStatusString(false, this.connectionAttemptCount));
		this.startConnectionRetryTimer();
	}

	protected void startUpdateTimer()
	{
		this.stopUpdateTimer();
		this.updateTimer = new Timer();
		this.updateTimer.scheduleAtFixedRate(new UpdateTask(this), 0, MINIMUM_UPDATE_INTERVAL);
	}

	protected void stopConnectionRetryTimer()
	{
		if (this.connectionRetryTimer != null)
		{
			this.connectionRetryTimer.cancel();
			this.connectionRetryTimer.purge();
			this.connectionRetryTimer = null;
		}
	}

	public void stopRegistration()
	{
		this.stopConnectionRetryTimer();
	}

	protected void stopUpdateTimer()
	{
		if (this.updateTimer != null)
		{
			this.updateTimer.cancel();
			this.updateTimer.purge();
			this.updateTimer = null;
		}
	}

	public void simulateHit(int idSenjata, int counter)
	{
		this.sendHitUpdate(idSenjata, counter);
	}

	protected void sendHitUpdate(int idSenjata, int counter)
	{
		HitPacket commPacket = new HitPacket();
		commPacket.location = this.currentLocation;
		commPacket.id = Integer.toString(this.androidId);
		commPacket.action = UpdatePacket.ACTIONTYPE_HIT;
		commPacket.heading = Math.round(this.azimuth);
		if (this.pose == PoseCalculator.POSE_PRONE)
		{
			commPacket.state = UpdatePacket.STATE_ALIVECRAWL;
		}
		else
		{
			commPacket.state = UpdatePacket.STATE_ALIVESTAND;
		}
		commPacket.idSenjata = idSenjata;
		commPacket.counter = counter;
		byte[] buffer = commPacket.getAsByteArray();
		this.commManager.send(buffer, 0, buffer.length, 0, this.serverAddress);
	}

}
