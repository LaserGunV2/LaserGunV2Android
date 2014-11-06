package com.toaster.laser2.communicationpacket;

public class KilledPacket extends CommunicationPacket
{
	public KilledPacket()
	{
		this.type=CommunicationPacket.MESSAGETYPE_KILLED;
	}
	
	@Override
	public byte[] getAsByteArray() 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
