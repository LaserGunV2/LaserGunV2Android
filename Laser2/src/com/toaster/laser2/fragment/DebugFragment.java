package com.toaster.laser2.fragment;

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

public class DebugFragment extends Fragment implements OnClickListener
{
	protected EditText editTextGameId;
	protected EditText editTextNIK;
	protected EditText editTextSimulatedHitIdSenjata;
	protected EditText editTextSimulatedHitCounter;
	protected Button buttonConnect;
	protected Button buttonDisconnect;
	protected TextView textViewStatus;
	protected TextView textViewDebugOutput;
	protected TextView textViewPingResult;
	protected Laser2Controller laserController;
	protected Button buttonSimulateHit;
	protected Button buttonPing;
	
	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
	{
		View mainView=inflater.inflate(R.layout.debug_fragment_layout, null);
		editTextGameId=(EditText)mainView.findViewById(R.id.editTextGameId_debug);
		editTextNIK=(EditText)mainView.findViewById(R.id.editTextNIK_debug);
		editTextSimulatedHitIdSenjata=(EditText)mainView.findViewById(R.id.editTextShooterGunId);
		editTextSimulatedHitCounter=(EditText)mainView.findViewById(R.id.editTextShooterBulletCounter);
		textViewDebugOutput=(TextView)mainView.findViewById(R.id.textViewDebugOutput);
		textViewStatus=(TextView)mainView.findViewById(R.id.textViewStatus_debug);
		textViewPingResult=(TextView)mainView.findViewById(R.id.textviewPingResult);
		buttonConnect=(Button)mainView.findViewById(R.id.buttonConnect_debug);
		buttonDisconnect=(Button)mainView.findViewById(R.id.buttonDisconnect_debug);
		buttonSimulateHit=(Button)mainView.findViewById(R.id.buttonSimulateShotBy);
		buttonPing=(Button)mainView.findViewById(R.id.buttonPing);
		
		buttonConnect.setOnClickListener(this);
		buttonDisconnect.setOnClickListener(this);
		buttonSimulateHit.setOnClickListener(this);
		buttonPing.setOnClickListener(this);
		
		return mainView;
	}
	
	public void setController(Laser2Controller controller)
	{
		this.laserController=controller;
	}
	
	public void setDebugStatus(String debugStatus)
	{
		this.textViewDebugOutput.setText(debugStatus);
	}
	
	public void setStatus(String status)
	{
		this.textViewDebugOutput.setText(status);
	}

	@Override
	public void onClick(View v) 
	{
		Button objButton=(Button)v;
		if (objButton==buttonConnect)
		{
			if ((this.editTextGameId.getText().toString().isEmpty()==false)&&(this.editTextNIK.getText().toString().isEmpty()==false))
				this.laserController.startRegistration(editTextGameId.getText().toString(), editTextNIK.getText().toString());
		}
		else if (objButton==buttonDisconnect)
		{
			
		}
		else if (objButton==buttonSimulateHit)
		{
			if ((this.editTextSimulatedHitIdSenjata.getText().toString().isEmpty()==false)&&(this.editTextSimulatedHitIdSenjata.getText().toString().isEmpty()==false))
				this.laserController.simulateHit(Integer.parseInt(editTextSimulatedHitIdSenjata.getText().toString()), Integer.parseInt(editTextSimulatedHitCounter.getText().toString()));
		}
		else if (objButton==buttonPing)
		{
			
		}
	}
	
	public void setPlayerAliveStatus(boolean isPlayerAlive)
	{
		if (isPlayerAlive)
			textViewDebugOutput.setBackgroundColor(Color.WHITE);
		else
			textViewDebugOutput.setBackgroundColor(Color.RED);
	}
}