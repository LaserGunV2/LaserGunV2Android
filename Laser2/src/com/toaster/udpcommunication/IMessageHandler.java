package com.toaster.udpcommunication;

public interface IMessageHandler 
{
	void onMessageReceived(byte[] buffer,int length);
	int getMessageTag();
}
