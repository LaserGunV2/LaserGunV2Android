package com.toaster.laser2.laser2controller;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;

import com.bluno.BlunoConnection;
import com.bluno.BlunoConnection.connectionStateEnum;
import com.bluno.BlunoHandler;
import com.google.android.gms.common.ConnectionResult;
import com.toaster.internalsensor.InternalSensorController;
import com.toaster.internalsensor.InternalSensorHandler;
import com.toaster.internalsensor.pose.PoseCalculator;
import com.toaster.laser2.MainActivity;
import com.toaster.laser2.UIHandler;
import com.toaster.laser2.communicationpacket.CommunicationPacket;
import com.toaster.laser2.communicationpacket.ConfirmPacket;
import com.toaster.laser2.communicationpacket.HitPacket;
import com.toaster.laser2.communicationpacket.RegistrationPacket;
import com.toaster.laser2.communicationpacket.UpdatePacket;
import com.toaster.laser2.locationcontroller.LocationController;
import com.toaster.laser2.locationcontroller.LocationControllerHandler;
import com.toaster.laser2.storage.StorageController;
import com.toaster.sound.SoundController;
import com.toaster.udpcommunication.IMessageHandler;
import com.toaster.udpcommunication.UDPPacketManager;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.location.Location;
import android.util.Log;

public class Laser2Controller implements LocationControllerHandler, IMessageHandler, InternalSensorHandler,BlunoHandler
{
	public final static int STATE_DISCONNECTED = 0;
	public final static int STATE_CONNECTED = 1;

	protected final static int UDP_TARGETPORT = 21500;
	protected final static int UDP_RECEIVEPORT = 21501;

	protected final static long MINIMUM_UPDATE_INTERVAL = 1000;
	protected final static long MINIMUM_CONNECTION_RETRY_INTERVAL = 2000;
	protected final static int MAX_CONNECTION_RETRY_ATTEMPT = 5;

	protected final static int BLUNODATAIDX_GUNID=0;
	protected final static int BLUNODATAIDX_COUNTER=1;
	protected final static int BLUNODATAIDX_SENSORID=2;
	
	// protected CommunicationPacket commPacket;
	protected int state;
	protected LocationController locController;
	protected SoundController soundController;
	protected UDPPacketManager commManager;
	protected InternalSensorController internalSensor;
	protected StorageController storageController;
	protected BlunoConnection blunoConnection;
	protected Location currentLocation;
	protected UIHandler ui;
	protected InetAddress serverAddress;
	protected String sensorBTAddress;
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
	protected int[] lastSensorData;
	protected long lastSensorDataTime;
	
	protected StringBuffer debugStringBuffer;
	
	public Laser2Controller(Context context, UIHandler ui)
	{
		this.soundController=new SoundController(context);
		this.commManager = UDPPacketManager.createBroadcastUDPManager(this, context, UDP_TARGETPORT, UDP_RECEIVEPORT);
		this.locController = new LocationController(context, this);
		this.internalSensor = new InternalSensorController(context, this);
		this.storageController=new StorageController(context);
		this.blunoConnection=new BlunoConnection(context, this);
		this.sensorBTAddress=storageController.getSensorAddress();
		this.errorList = new ArrayList<String>();
		this.sensorBTAddress=storageController.getSensorAddress();
		if (this.sensorBTAddress!=null)
			blunoConnection.connect(sensorBTAddress);
		//Log.v(this.getClass().getName(), sensorBTAddress);
		this.ui = ui;
		this.pose = PoseCalculator.POSE_STANDING;
		this.state = STATE_DISCONNECTED;
		this.androidId = -1;
		this.checkPrerequisites();
		this.ui.updateErrorStatus(errorList);
		this.lastSensorData=new int[3];
		this.lastSensorDataTime=-1;
		this.debugStringBuffer=new StringBuffer();
	}

	protected void checkPrerequisites()
	{
		this.errorList.clear();
		//Log.v(this.getClass().getName(), "btstate="+blunoConnection.getConnectionState());
		//Log.v(this.getClass().getName(), "bterr="+(blunoConnection.getConnectionState()!=connectionStateEnum.isConnected));
		if (sensorBTAddress==null)
		{
			this.errorList.add("Belum dipair dengan sensor");
		}
		else if (blunoConnection.getConnectionState()!=connectionStateEnum.isConnected)
		{
			this.errorList.add("Belum terkoneksi dengan sensor");
		}
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

//	public void enableDebugMode()
//	{
//		this.debugMode=true;
//		this.ui.setUIMode(MainActivity.UIMODE_DEBUG);
//	}
//	
	public void setUIMode(int uiMode)
	{
		if (uiMode==MainActivity.UIMODE_DEBUG)
		{
			this.debugMode=true;
		}
		this.ui.setUIMode(uiMode);
	}
	
	

	protected String generateDebugString()
	{
		debugStringBuffer.setLength(0);
		debugStringBuffer.append("AndroidId = " + this.androidId + "\n");
		debugStringBuffer.append("ServerAddress = " + this.serverAddress + "\n");
		if (this.currentLocation != null)
		{
			debugStringBuffer.append("Latitude = " + Location.convert(this.currentLocation.getLatitude(), Location.FORMAT_DEGREES) + "\n");
			debugStringBuffer.append("Longitude = " + Location.convert(this.currentLocation.getLongitude(), Location.FORMAT_DEGREES) + "\n");
			debugStringBuffer.append("Accuracy = " + this.currentLocation.getAccuracy() + "\n");
		}
		debugStringBuffer.append("Azimuth = " + this.azimuth + "\n");
		debugStringBuffer.append("Pose = " + this.pose + "\n");
		debugStringBuffer.append("Last sensor Data: "+lastSensorDataTime+"\n");
		if (sensorBTAddress==null)
		{
			//this.errorList.add("Belum dipair dengan sensor");
			debugStringBuffer.append("Belum dipair dengan sensor"+"\n");
		}
		else if (blunoConnection.getConnectionState()!=connectionStateEnum.isConnected)
		{
			//this.errorList.add("Belum terkoneksi dengan sensor");
			debugStringBuffer.append("Belum terkoneksi dengan sensor"+"\n");
		}
		else
		{
			debugStringBuffer.append("Terkoneksi dengan sensor"+"\n");
		}
		debugStringBuffer.append("SensorData-GunId:"+lastSensorData[BLUNODATAIDX_GUNID]+"\n");
		debugStringBuffer.append("SensorData-Counter:"+lastSensorData[BLUNODATAIDX_COUNTER]+"\n");
		debugStringBuffer.append("SensorData-SensorId:"+lastSensorData[BLUNODATAIDX_SENSORID]+"\n");
		return debugStringBuffer.toString();
	}

	public ArrayList<String> getErrorList()
	{
		return this.errorList;
	}

	@Override
	public void onAzimuthUpdated(double azimuth,int id)
	{
		this.azimuth = (float)azimuth;
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
			this.ui.setNik(this.nomorInduk);
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
	
	/*
	protected void sendHitMessage()
	{
		HitPacket hitPacket=new HitPacket();
		hitPacket.location = this.currentLocation;
		hitPacket.id = Integer.toString(this.androidId);
		hitPacket.action = UpdatePacket.ACTIONTYPE_UPDATE;
		hitPacket.heading = Math.round(this.azimuth);
		if (this.playerAlive==true)
		{
			if (this.pose == PoseCalculator.POSE_PRONE)
			{
				hitPacket.state = UpdatePacket.STATE_ALIVECRAWL;
			}
			else
			{
				hitPacket.state = UpdatePacket.STATE_ALIVESTAND;
			}
		}
		else 
		{
			if (this.pose == PoseCalculator.POSE_PRONE)
			{
				hitPacket.state = UpdatePacket.STATE_DEADCRAWL;
			}
			else
			{
				hitPacket.state = UpdatePacket.STATE_DEADSTAND;
			}
		}
		hitPacket.idSenjata=lastSensorData[BLUNODATAIDX_GUNID];
		hitPacket.idSenjata=lastSensorData[BLUNODATAIDX_GUNID];
		hitPacket.idSenjata=lastSensorData[BLUNODATAIDX_GUNID];
		byte[] buffer = hitPacket.getAsByteArray();
		this.commManager.send(buffer, 0, buffer.length, 0, this.serverAddress);
	}*/

	protected void sendUpdate()
	{
		// ui.clearDebug();
		// ui.appendDebug(Location.convert(this.currentLocation.getLatitude(),
		// Location.FORMAT_DEGREES)+" "+Location.convert(this.currentLocation.getLongitude(),Location.FORMAT_DEGREES)+" "+this.currentLocation.getAccuracy());
		UpdatePacket commPacket = new UpdatePacket();
		commPacket.location = this.currentLocation;
		commPacket.id = Integer.toString(this.androidId);
		commPacket.gameId=this.gameId;
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
		Log.v(this.getClass().getCanonicalName(), "hit");
		this.sendHitUpdate(idSenjata, counter,0);
	}

	protected void sendHitUpdate(int idSenjata, int counter,int sensorId)
	{
		
		soundController.playSound(SoundController.SOUND_HIT);
		HitPacket commPacket = new HitPacket();
		commPacket.location = this.currentLocation;
		commPacket.id = Integer.toString(this.androidId);
		commPacket.gameId=this.gameId;
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
		if (this.state==Laser2Controller.STATE_CONNECTED)
			this.commManager.send(buffer, 0, buffer.length, 0, this.serverAddress);
	}

	@Override
	public void onDataReceived(String strGunId, String strCounter, String strSensorId)
	{
		try
		{
			this.lastSensorData[BLUNODATAIDX_GUNID]=Integer.parseInt(strGunId);
			this.lastSensorData[BLUNODATAIDX_COUNTER]=Integer.parseInt(strCounter);
			this.lastSensorData[BLUNODATAIDX_SENSORID]=Integer.parseInt(strSensorId);
			this.lastSensorDataTime=System.currentTimeMillis();
			if (this.state == STATE_CONNECTED)
			{
				this.sendHitUpdate(this.lastSensorData[BLUNODATAIDX_GUNID], this.lastSensorData[BLUNODATAIDX_COUNTER], this.lastSensorData[BLUNODATAIDX_SENSORID]);
			}
		}
		catch (Exception e)
		{
			Log.v(this.getClass().getCanonicalName(), "btparseerror"+e.toString());
		}
	}

	@Override
	public void onConnectionStateChange(connectionStateEnum theconnectionStateEnum)
	{
		this.checkPrerequisites();
		this.ui.updateErrorStatus(errorList);
	}

	@Override
	public void onScanCompleted(ArrayList<BluetoothDevice> deviceList)
	{
		ui.setFoundBTDevices(deviceList);
		Log.v(this.getClass().getName(), "scan completed");
	}
	
	public void setBTPairAddress(String address)
	{
		this.sensorBTAddress=address;
		this.storageController.saveSensorAddress(address);
		this.sensorBTAddress=address;
		if (this.sensorBTAddress!=null)
			blunoConnection.connect(sensorBTAddress);
	}
	
	public String getBTPairAddress()
	{
		return this.storageController.getSensorAddress();
	}
	
	public void startBTScan()
	{
		blunoConnection.scanForBleDevices();
	}

	@Override
	public void onBTDeviceFound(BluetoothDevice foundDevice)
	{
		ui.btDeviceFound(foundDevice);
		
	}
	
	public int getState()
	{
		return this.state;
	}
	
	public void setAzimuthCalculatorId(int id)
	{
		this.internalSensor.setActiveAzimuthCalculatorId(id);
	}
}
