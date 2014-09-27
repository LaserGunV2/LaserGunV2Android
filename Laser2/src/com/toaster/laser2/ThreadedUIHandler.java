package com.toaster.laser2;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ThreadedUIHandler extends Handler implements UIHandler
{
	protected static final int MSG_appendDebug=0;
	protected static final int MSG_clearDebug=1;
	protected static final int MSG_setandroidid=2;
	protected static final int MSG_setuimode=3;
	
	
	protected UIHandler actualUI;
	
	public ThreadedUIHandler(UIHandler actualUI,Looper looper)
	{
		super(looper);
		this.actualUI=actualUI;
	}

	@Override
	public void handleMessage(Message msg) 
	{
		switch (msg.what)
		{
			case MSG_appendDebug:
			{
				String par=(String)msg.obj;
				actualUI.appendDebug(par);
			}
			break;
			case MSG_clearDebug:
			{
				actualUI.clearDebug();
			}
			break;
			case MSG_setandroidid:
			{
				String par=(String)msg.obj;
				actualUI.setAndroidId(par);
			}
			break;
			case MSG_setuimode:
			{
				Integer par=(Integer)msg.obj;
				actualUI.setUIMode(par.intValue());
			}
			break;
		}
	}

	@Override
	public void appendDebug(String s) 
	{
		(this.obtainMessage(MSG_appendDebug, s)).sendToTarget();	
	}

	@Override
	public void clearDebug() 
	{
		(this.obtainMessage(MSG_clearDebug)).sendToTarget();
	}

	@Override
	public void setAndroidId(String androidId) {
		(this.obtainMessage(MSG_setandroidid, androidId)).sendToTarget();
	}

	@Override
	public void setUIMode(int uiMode) {
		(this.obtainMessage(MSG_setuimode, new Integer(uiMode))).sendToTarget();
		
	}
}
