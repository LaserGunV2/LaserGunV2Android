package com.toaster.laser2.laser2controller;

import java.util.TimerTask;

public class PingReplyWaitTask extends TimerTask
{
	protected Laser2LatencyChecker owner;
	
	public PingReplyWaitTask(Laser2LatencyChecker latencyChecker)
	{
		this.owner=latencyChecker;
	}

	@Override
	public void run()
	{
		this.owner.onWaitCompleted();
	}
}
