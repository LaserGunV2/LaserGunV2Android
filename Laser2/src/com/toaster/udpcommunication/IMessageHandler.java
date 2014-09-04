package com.toaster.udpcommunication;

import java.net.InetAddress;

public interface IMessageHandler 
{
	void onMessageReceived(InetAddress senderAddress,byte[] buffer,int length);
}
