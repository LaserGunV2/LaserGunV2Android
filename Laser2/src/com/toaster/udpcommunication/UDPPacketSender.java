package com.toaster.udpcommunication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import android.util.Log;

public class UDPPacketSender implements Runnable
{
	protected Semaphore semaphore;
	protected Thread workerThread;
	protected Queue<DatagramPacket> outQueue;
	protected UDPPacketManager udpPacketManager;
	protected DatagramSocket socket;
	protected boolean isAlive;
	
	public UDPPacketSender(UDPPacketManager packetManager,DatagramSocket socket)
	{
		this.socket=socket;
		outQueue=new ArrayBlockingQueue<DatagramPacket>(50);
		semaphore=new Semaphore(0);
		isAlive=true;
		workerThread=new Thread(this);
		workerThread.start();
	}

	public void send(DatagramPacket dataPacket)
	{
		outQueue.add(dataPacket);
		semaphore.release();
	}
	
	@Override
	public void run() 
	{
		DatagramPacket packet;
		while (isAlive)
		{
			try 
			{
				semaphore.acquire();
			} 
			catch (InterruptedException e) 
			{
				
			}
			if (outQueue.isEmpty())
				continue;
			packet=outQueue.remove();
			try 
			{
				Log.v("UDPPacketSender", "send:"+packet.getAddress().getHostAddress()+":"+packet.getPort());
				socket.send(packet);
			} 
			catch (IOException e) 
			{
				System.out.println(e);
			}
		}
	}
	
	public void cleanup()
	{
		isAlive=false;
		outQueue.clear();
		semaphore.release();
		try 
		{
			workerThread.join();
		} 
			catch (InterruptedException e) 
		{
			e.printStackTrace();
		}	
	}
}
