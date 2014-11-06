package com.toaster.laser2.laser2controller;

import java.util.TimerTask;

public class ConnectionRetryTask extends TimerTask
{
	protected Laser2Controller controller;
	
	public ConnectionRetryTask(Laser2Controller controller)
	{
		this.controller=controller;
	}
	
	@Override
	public void run() {
		this.controller.onConnectionRetry();
	}

}
