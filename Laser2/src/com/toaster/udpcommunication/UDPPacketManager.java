package com.toaster.udpcommunication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.google.android.gms.drive.query.internal.HasFilter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


public class UDPPacketManager implements IMessageSender
{
	protected UDPPacketReceiver receiver;
	protected UDPPacketSender sender;
	//protected DatagramSocket socket;
	protected boolean isIntentionallyClosed;
	//boolean hasFixedTarget;
	protected InetAddress broadcastAddress; 
	protected int targetPort;
	
	public static UDPPacketManager createBroadcastUDPManager(IMessageHandler handler,Context context,int targetPort,int receivePort)
	{
		
		UDPPacketManager result=null;
		WifiManager wifi=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo=wifi.getDhcpInfo();
		if (dhcpInfo!=null)
		{
			
			DatagramSocket receiveSocket;
			DatagramSocket sendSocket;
			int broadCastAddressInt=dhcpInfo.ipAddress&dhcpInfo.netmask|~dhcpInfo.netmask;
			byte[] quad=new byte[4];
			for (int i=0;i<4;i++)
			{
				quad[i]=(byte)((broadCastAddressInt>>(8*i))&0xFF);
			}
			try
			{
				//Log.v("UDPPacketManager", "broadcast inet address="+  dhcpInfo.ipAddress);
				//Log.v("UDPPacketManager", "broadcast address="+InetAddress.getByAddress(quad).getHostAddress());
				sendSocket=new DatagramSocket();
				receiveSocket=new DatagramSocket(receivePort);
				result=new UDPPacketManager(sendSocket,receiveSocket, handler);
				result.broadcastAddress=InetAddress.getByAddress(quad);
				result.targetPort=targetPort;
				//result.hasFixedTarget=false;
			}
			catch (Exception e)
			{
				Log.v("UDPPacketManager", e.toString());
			}
			
			return result;
		}
		else
			return null;
	}
	
	/*
	public static UDPPacketManager createTargetedUDPManager(IMessageHandler handler,InetAddress targetAddress,int targetPort,int receivePort)
	{
		DatagramSocket targetSocket=null;
		DatagramSocket receiveSocket=null;
		try 
		{
			receiveSocket=new DatagramSocket(receivePort);
			targetSocket.connect(new InetSocketAddress(targetAddress,targetPort));
		} 
		catch (SocketException e) 
		{	
		}
		UDPPacketManager result=new UDPPacketManager(targetSocket,receiveSocket,handler);
		result.hasFixedTarget=true;
		return result;
	}*/
	
	private UDPPacketManager(DatagramSocket sendSocket, DatagramSocket receiveSocket,IMessageHandler handler)
	{
		//this.socket=targetSocket;
		this.receiver=new UDPPacketReceiver(this, receiveSocket, handler);
		this.sender=new UDPPacketSender(this, sendSocket);
	}
	
	
	public void onSocketClosedHandler()
	{
		//gk ngapa2in..da udp..
	}

	
	public void cleanup()
	{
		isIntentionallyClosed=true;
		sender.cleanup();
		receiver.cleanup();
	}

//	@Override
//	public boolean send(byte[] buffer, int offset, int length, int option) 
//	{
//		if (hasFixedTarget)
//		{
//			Log.v("UDPPacketManager", "send "+length);
//			sender.send(new DatagramPacket(buffer, length));
//			return true;
//		}
//		else
//			return false;
//	}

	@Override
	public boolean send(byte[] buffer, int offset, int length, int option,
			InetAddress targetAddr) 
	{
		sender.send(new DatagramPacket(buffer, length,targetAddr,targetPort));
		return true;
	}

	@Override
	public boolean broadcast(byte[] buffer, int offset, int length, int option) 
	{
		sender.send(new DatagramPacket(buffer, length,broadcastAddress,targetPort));
		return true;
		
		/*
	
		if (!hasFixedTarget)
		{
			Log.v("UDPPacketManager", "broadcast "+length);
			sender.send(new DatagramPacket(buffer, length,broadcastAddress,socket.getLocalPort()));
			//kyny blm tentu local port deh. kudu gw pikirin lg
			return true;
		}
		else
			return false;
			*/
	}
}
