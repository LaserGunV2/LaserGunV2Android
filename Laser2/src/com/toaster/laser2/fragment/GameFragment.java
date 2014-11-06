package com.toaster.laser2.fragment;

import com.toaster.laser2.R;
import com.toaster.laser2.laser2controller.Laser2Controller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameFragment extends Fragment 
{
	protected TextView textViewAndroidId;
	protected Laser2Controller laserController;
	

	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
	{
		View mainView=inflater.inflate(R.layout.game_fragment_layout, null);
		textViewAndroidId=(TextView)mainView.findViewById(R.id.androidIdLabel);
		return mainView;
	}
	
	public void setController(Laser2Controller controller)
	{
		this.laserController=controller;
	}

	public void setAndroidId(String androidId)
	{
		this.textViewAndroidId.setText(androidId);
	}
	
	public void setPlayerAliveStatus(boolean isPlayerAlive)
	{
		if (isPlayerAlive)
			textViewAndroidId.setBackgroundColor(Color.WHITE);
		else
			textViewAndroidId.setBackgroundColor(Color.RED);
	}
}
