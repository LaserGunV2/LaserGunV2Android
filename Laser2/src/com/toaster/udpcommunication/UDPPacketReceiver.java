package com.toaster.udpcommunication;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;

public class UDPPacketReceiver implements Runnable
{
	public static String LOGTAG="UDPPacketReceiver";
	protected static int BUFFERSIZE=20000;
	
	protected UDPPacketManager udpManager;
	protected IMessageHandler handler;
	protected boolean isAlive;
	protected Thread workerThread;
	protected DatagramSocket socket;
	
	
	public UDPPacketReceiver(UDPPacketManager udpManager,DatagramSocket socket,IMessageHandler handler)
	{
		this.handler=handler;
		this.udpManager=udpManager;
		this.socket=socket;
		
		isAlive=true;
		workerThread=new Thread(this);
		workerThread.start();
	}


	@Override
	public void run() 
	{
		byte[] buffer=new byte[BUFFERSIZE];
		DatagramPacket receivedPacket=new DatagramPacket(buffer,buffer.length);
		while (isAlive)
		{
			try 
			{
				//Log.v("UDPReceiver", "waiting for packet");
				socket.receive(receivedPacket);
				//Log.v("UDPReceiver", "got packet:"+receivedPacket.getLength());
				handler.onMessageReceived(receivedPacket.getAddress(),buffer, receivedPacket.getLength());
			} 
			catch (IOException e) 
			{
				udpManager.onSocketClosedHandler();
			}
		}
		
	}
	
	public void cleanup()
	{
		isAlive=false;
		socket.close();
		udpManager.onSocketClosedHandler();
		try 
		{
			workerThread.join();
		} 
		catch (InterruptedException e) 
		{
		}
	}
}
