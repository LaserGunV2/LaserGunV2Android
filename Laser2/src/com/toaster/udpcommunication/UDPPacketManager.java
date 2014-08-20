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
	protected DatagramSocket socket;
	protected boolean isIntentionallyClosed;
	boolean hasFixedTarget;
	protected InetAddress broadcastAddress; 
	
	public static UDPPacketManager createBroadcastUDPManager(IMessageHandler handler,Context context,int port)
	{
		
		UDPPacketManager result=null;
		WifiManager wifi=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo=wifi.getDhcpInfo();
		if (dhcpInfo!=null)
		{
			
			DatagramSocket socket;
			int broadCastAddressInt=dhcpInfo.ipAddress&dhcpInfo.netmask|~dhcpInfo.netmask;
			byte[] quad=new byte[4];
			for (int i=0;i<4;i++)
			{
				quad[i]=(byte)((broadCastAddressInt>>(8*i))&0xFF);
			}
			try
			{
				Log.v("UDPPacketManager", "broadcast inet address="+  dhcpInfo.ipAddress);
				//Log.v("UDPPacketManager", "broadcast netmask ="+InetAddress.getByAddress(quad).getHostAddress());
				Log.v("UDPPacketManager", "broadcast address="+InetAddress.getByAddress(quad).getHostAddress());
				socket=new DatagramSocket(port);
				//socket.connect(new InetSocketAddress(InetAddress.getByAddress(quad),port));
				result=new UDPPacketManager(socket, handler);
				result.broadcastAddress=InetAddress.getByAddress(quad);
				result.hasFixedTarget=false;
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
	
	public static UDPPacketManager createTargetedUDPManager(IMessageHandler handler,InetAddress targetAddress,int port)
	{
		DatagramSocket socket=null;
		try 
		{
			socket=new DatagramSocket(port);
			socket.connect(new InetSocketAddress(targetAddress,port));
		} 
		catch (SocketException e) 
		{	
		}
		UDPPacketManager result=new UDPPacketManager(socket,handler);
		result.hasFixedTarget=true;
		return result;
	}
	
	private UDPPacketManager(DatagramSocket socket,IMessageHandler handler)
	{
		this.socket=socket;
		this.receiver=new UDPPacketReceiver(this, socket, handler);
		this.sender=new UDPPacketSender(this, socket);
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

	@Override
	public boolean send(byte[] buffer, int offset, int length, int option) 
	{
		if (hasFixedTarget)
		{
			Log.v("UDPPacketManager", "send "+length);
			sender.send(new DatagramPacket(buffer, length));
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean send(byte[] buffer, int offset, int length, int option,
			InetAddress targetAddr) 
	{
		return false;
	}

	@Override
	public boolean broadcast(byte[] buffer, int offset, int length, int option) 
	{
		if (!hasFixedTarget)
		{
			Log.v("UDPPacketManager", "broadcast "+length);
			sender.send(new DatagramPacket(buffer, length,broadcastAddress,socket.getLocalPort()));
			//kyny blm tentu local port deh. kudu gw pikirin lg
			return true;
		}
		else
			return false;
	}
}
