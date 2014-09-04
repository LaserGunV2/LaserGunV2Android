package com.toaster.laser2.communicationpacket;

public class ConfirmPacket extends CommunicationPacket
{
	protected static final String NAME_ANDROIDID="androidId";
	
	public int androidId;
	
	public ConfirmPacket()
	{
		super();
		this.type=CommunicationPacket.MESSAGETYPE_CONFIRM;
	}
	
	public ConfirmPacket(int androidId)
	{
		this();
		this.androidId=androidId;
	}
	
	@Override
	public byte[] getAsByteArray() {
		//gak akan ngirim tipe ini mestinya, jd gk perlu diimplement method yg ini
		// TODO Auto-generated method stub
		return null;
	}

}
