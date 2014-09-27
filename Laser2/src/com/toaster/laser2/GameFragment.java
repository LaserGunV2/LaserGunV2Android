package com.toaster.laser2;

import com.toaster.laser2.laser2controller.Laser2Controller;

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
	protected EditText editTextDebugConfirmation;
	protected Button buttonDebugConfirmation;
	protected Laser2Controller laserController;
	
	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
	{
		View mainView=inflater.inflate(R.layout.game_fragment_layout, null);
		textViewAndroidId=(TextView)mainView.findViewById(R.id.androidIdLabel);
		editTextDebugConfirmation=(EditText)mainView.findViewById(R.id.editTextDebugVerification);
		buttonDebugConfirmation=(Button)mainView.findViewById(R.id.buttonDebugVerification);
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
	
	@Override
	public void onClick(View v) 
	{
		Button objButton=(Button)v;
		if (objButton==buttonDebugConfirmation)
		{
			if (editTextDebugConfirmation.getText().toString().equals(buttonDebugConfirmation.getText().toString()))
			{
				
			}
		}
	}
}
