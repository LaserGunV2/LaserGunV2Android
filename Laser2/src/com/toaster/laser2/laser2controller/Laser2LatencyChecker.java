package com.toaster.laser2.laser2controller;

import java.util.Timer;

import com.toaster.laser2.communicationpacket.PingPongPacket;
import com.toaster.udpcommunication.UDPPacketManager;

public class Laser2LatencyChecker {
	protected final static int PACKET_COUNT=10;
	protected final static int PACKET_DELAY=0;
	
	protected UDPPacketManager commManager;
	protected long[][] packetTime;
	protected Timer replyWaitTimer;
	protected int packetCount;
	
	public Laser2LatencyChecker(UDPPacketManager commManager)
	{
		this.commManager=commManager;
		packetTime=new long[PACKET_COUNT][2];
	}
	
	public void runTest()
	{
		for (int i=0;i<packetTime.length;i++)
		{
			long curTime=System.nanoTime();
			packetTime[i][0]=curTime;
			PingPongPacket curPacket=new PingPongPacket(PingPongPacket.MESSAGETYPE_PING, curTime, i);
			byte[] buffer=curPacket.getAsByteArray();
			this.commManager.broadcast(buffer, 0, buffer.length, 0);
			try
			{
				Thread.sleep(PACKET_DELAY);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void onPacketReceived(PingPongPacket replyPacket)
	{
		packetTime[replyPacket.packetId][1]=System.nanoTime();
	}
	
	public void onWaitCompleted()
	{
		
	}
}
