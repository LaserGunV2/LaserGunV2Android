package com.toaster.laser2.communicationpacket;

import java.io.IOException;

import android.util.JsonWriter;

public class PingPongPacket extends CommunicationPacket{
	protected static final String NAME_SENTTIME="sentTime";
	protected static final String NAME_PACKETID="packetId";
	
	public long sentTime;
	public int packetId;
	
	public PingPongPacket(String type,long sentTime,int packetId)
	{
		super();
		this.type=type;
		this.sentTime=sentTime;
		this.packetId=packetId;
	}
	
	@Override
	public byte[] getAsByteArray() 
	{
		stringWriter.getBuffer().setLength(0);
		//gk bs gt yah gw reset si jsonwriternya?
		jsonWriter=new JsonWriter(stringWriter);
		try 
		{
			jsonWriter.beginObject();
			jsonWriter.name(NAME_TYPE).value(type);
			jsonWriter.name(NAME_SENTTIME).value(sentTime);
			jsonWriter.name(NAME_PACKETID).value(packetId);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return stringWriter.toString().getBytes();
	}
}
