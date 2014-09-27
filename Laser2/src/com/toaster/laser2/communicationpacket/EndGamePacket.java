package com.toaster.laser2.communicationpacket;

public class EndGamePacket extends CommunicationPacket
{
	public EndGamePacket()
	{
		this.type=CommunicationPacket.MESSAGETYPE_ENDGAME;
	}
	
	@Override
	public byte[] getAsByteArray() 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
