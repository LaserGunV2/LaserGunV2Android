package com.toaster.laser2.laser2controller;

import java.util.TimerTask;

public class UpdateTask extends TimerTask
{
	protected Laser2Controller controller;
	
	public UpdateTask(Laser2Controller controller)
	{
		this.controller=controller;
	}
	
	
	@Override
	public void run() 
	{
		this.controller.onUpdateEvent();
		
	}

}
