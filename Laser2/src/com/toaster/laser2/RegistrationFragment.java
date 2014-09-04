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

public class RegistrationFragment extends Fragment implements OnClickListener
{
	protected EditText editTextGameId;
	protected EditText editTextNIK;
	protected TextView textViewStatus;
	protected Button buttonConnect;
	protected Laser2Controller laserController;
	
	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
	{
		View mainView=inflater.inflate(R.layout.registration_fragment_layout, null);
		editTextGameId=(EditText)mainView.findViewById(R.id.editTextGameId);
		editTextNIK=(EditText)mainView.findViewById(R.id.editTextNIK);
		textViewStatus=(TextView)mainView.findViewById(R.id.textViewStatus);
		buttonConnect=(Button)mainView.findViewById(R.id.buttonConnect);
		buttonConnect.setOnClickListener(this);
		return mainView;
	}
	
	public void setController(Laser2Controller controller)
	{
		this.laserController=controller;
	}

	@Override
	public void onClick(View v) 
	{
		Button objButton=(Button)v;
		if (objButton==buttonConnect)
		{
			this.laserController.register(editTextGameId.getText().toString(), editTextNIK.getText().toString());
		}
	}
	
	
}
