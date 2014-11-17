package com.toaster.laser2.communicationpacket;

import java.io.IOException;

import android.location.Location;
import android.util.JsonWriter;

public class HitPacket extends UpdatePacket {
	protected static final String NAME_IDSENJATA="idsenjata";
	protected static final String NAME_COUNTER="counter";
	protected static final String NAME_SENSORID="sensor";
	
	public int idSenjata;
	public int counter;
	public int sensorId;
	
	public HitPacket()
	{
		super();
		this.action=UpdatePacket.ACTIONTYPE_HIT;
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
			jsonWriter.name(NAME_TYPE).value(CommunicationPacket.MESSAGETYPE_UPDATE);
			jsonWriter.name(NAME_ANDROIDID).value(id);
			jsonWriter.name(NAME_LOCATION).value(Location.convert(location.getLatitude(), Location.FORMAT_DEGREES)+","+Location.convert(location.getLongitude(), Location.FORMAT_DEGREES));
			jsonWriter.name(NAME_ACCURACY).value(location.getAccuracy());
			jsonWriter.name(NAME_HEADING).value(heading);
			jsonWriter.name(NAME_ACTION).value(action);
			jsonWriter.name(NAME_STATE).value(state);
			jsonWriter.name(NAME_IDSENJATA).value(idSenjata);
			jsonWriter.name(NAME_COUNTER).value(counter);
			jsonWriter.endObject();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return stringWriter.toString().getBytes();
	}
}
