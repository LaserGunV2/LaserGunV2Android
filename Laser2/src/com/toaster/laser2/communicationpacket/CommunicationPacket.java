package com.toaster.laser2.communicationpacket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;

import android.location.Location;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

public abstract class CommunicationPacket 
{
	protected static final String NAME_TYPE="type";
	public static final String MESSAGETYPE_UPDATE="update";
	public static final String MESSAGETYPE_ENDGAME="endgame";
	public static final String MESSAGETYPE_REGISTER="register";
	public static final String MESSAGETYPE_CONFIRM="confirm";
	
	protected JsonWriter jsonWriter;
	protected StringWriter stringWriter;
	public String type;
	
	protected static HashMap<String, String> keyValueHashmap=new HashMap<String, String>();
	
	public static CommunicationPacket decodePacket(byte[] buffer)
	{
		keyValueHashmap.clear();
		ByteArrayInputStream inputStream=new ByteArrayInputStream(buffer);
		InputStreamReader reader=new InputStreamReader(inputStream);
		JsonReader jsonReader=new JsonReader(reader);
		try 
		{
			jsonReader.beginObject();
			while (jsonReader.hasNext())
			{
				keyValueHashmap.put(jsonReader.nextName(), jsonReader.nextString());
			}
			jsonReader.endObject();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		String type=keyValueHashmap.get(NAME_TYPE);
		Log.v("CommunicationPacket", "decoded type="+type);
		if (type.equals(MESSAGETYPE_CONFIRM))
		{
			//return new RegistrationPacket(keyValueHashmap.get(RegistrationPacket.NAME_GAMEID), keyValueHashmap.get(RegistrationPacket.NAME_NOMORINDUK));
			return new ConfirmPacket(Integer.valueOf(keyValueHashmap.get(ConfirmPacket.NAME_ANDROIDID).toString()));
		}
		//String type=jsonReader.
		return null;
	}
	
	public CommunicationPacket()
	{
		stringWriter=new StringWriter();	
	}
	
	public abstract byte[] getAsByteArray();
}
