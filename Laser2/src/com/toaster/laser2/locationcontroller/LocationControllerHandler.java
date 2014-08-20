package com.toaster.laser2.locationcontroller;

import android.location.Location;

public interface LocationControllerHandler 
{
	public void onLocationUpdate(Location newLocation);
}
