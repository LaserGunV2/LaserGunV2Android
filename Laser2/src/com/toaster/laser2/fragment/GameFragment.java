package com.toaster.laser2.fragment;

import com.toaster.laser2.MainActivity;
import com.toaster.laser2.R;
import com.toaster.laser2.laser2controller.Laser2Controller;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GameFragment extends Fragment implements OnClickListener
{
	protected TextView textViewAndroidId;
	protected Laser2Controller laserController;
	protected Button buttonDebug;
	protected EditText editTextDebug;
	

	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
	{
		View mainView=inflater.inflate(R.layout.game_fragment_layout, null);
		textViewAndroidId=(TextView)mainView.findViewById(R.id.androidIdLabel);
		buttonDebug=(Button)mainView.findViewById(R.id.game_buttonDebugMode);
		editTextDebug=(EditText)mainView.findViewById(R.id.game_editTextDebugVerification);
		buttonDebug.setOnClickListener(this);
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

	@Override
	public void onClick(View v)
	{
		if (v==buttonDebug)
		{
			if (editTextDebug.getText().toString().equalsIgnoreCase("debug"))
			{
				laserController.setUIMode(MainActivity.UIMODE_DEBUG);
		
			}
		}
		
	}
}
