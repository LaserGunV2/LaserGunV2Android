package com.toaster.laser2.communicationpacket;

import java.io.IOException;
import android.util.JsonWriter;

public class RegistrationPacket extends CommunicationPacket
{
	protected static final String NAME_GAMEID="gameid";
	protected static final String NAME_NOMORINDUK="nomerInduk";
	
	public String gameId;
	public String nomorInduk;
	
	public RegistrationPacket()
	{
		super();
		this.type=CommunicationPacket.MESSAGETYPE_REGISTER;
	}
	
	public RegistrationPacket(String gameId,String nomorInduk)
	{
		this();
		this.gameId=gameId;
		this.nomorInduk=nomorInduk;
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
			jsonWriter.name(NAME_GAMEID).value(gameId);
			jsonWriter.name(NAME_NOMORINDUK).value(nomorInduk);
			jsonWriter.endObject();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return stringWriter.toString().getBytes();
	}
	
}
