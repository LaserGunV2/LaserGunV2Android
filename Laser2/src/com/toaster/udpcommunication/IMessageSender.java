package com.toaster.udpcommunication;

import java.net.InetAddress;

public interface IMessageSender 
{
	//boolean send(byte[] buffer,int offset,int length);
	boolean send(byte[] buffer,int offset,int length,int option,InetAddress targetAddr);
	//boolean send(byte[] buffer,int offset,int length,int option);
	boolean broadcast(byte[] buffer,int offset,int length,int option);
}
