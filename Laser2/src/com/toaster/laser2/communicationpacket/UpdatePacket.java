package com.toaster.laser2.communicationpacket;

import java.io.IOException;

import android.location.Location;
import android.util.JsonWriter;

public class UpdatePacket extends CommunicationPacket 
{
	protected static final String NAME_TIME="time";
	protected static final String NAME_ANDROIDID="androidId";
	protected static final String NAME_LOCATION="location";
	protected static final String NAME_ACCURACY="accuracy";
	protected static final String NAME_HEADING="heading";
	protected static final String NAME_ACTION="action";
	protected static final String NAME_STATE="state";
	
	public static final String ACTIONTYPE_HIT="hit";
	public static final String ACTIONTYPE_SHOOT="shoot";
	public static final String ACTIONTYPE_UPDATE="update";
	
	public static final String STATE_ALIVESTAND="alive/stand";
	public static final String STATE_ALIVECRAWL="alive/crawl";
	public static final String STATE_DEADSTAND="dead/stand";
	public static final String STATE_DEADCRAWL="dead/crawl";
	
	public String id;
	public String type;
	public Location location;
	public float heading;
	public long packageTime;
	public String action;
	public String state;
	
	public UpdatePacket()
	{
		super();
		this.type=CommunicationPacket.MESSAGETYPE_UPDATE;
		this.action=ACTIONTYPE_UPDATE;
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
			jsonWriter.endObject();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return stringWriter.toString().getBytes();
	}
		
}
