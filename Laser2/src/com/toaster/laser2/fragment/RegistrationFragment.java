package com.toaster.laser2.fragment;

import java.util.ArrayList;
import java.util.ResourceBundle.Control;

import com.toaster.laser2.R;
import com.toaster.laser2.R.id;
import com.toaster.laser2.R.layout;
import com.toaster.laser2.laser2controller.Laser2Controller;

import android.os.Bundle;
import android.provider.Telephony.TextBasedSmsColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
	protected EditText editTextDebugMode;
	protected Button buttonDebugMode;
	protected Laser2Controller laserController;
	
	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
	{
		View mainView=inflater.inflate(R.layout.registration_fragment_layout, null);
		this.editTextGameId=(EditText)mainView.findViewById(R.id.editTextGameId);
		this.editTextNIK=(EditText)mainView.findViewById(R.id.editTextNIK);
		this.textViewStatus=(TextView)mainView.findViewById(R.id.textViewStatus);
		this.editTextDebugMode=(EditText)mainView.findViewById(R.id.editTextDebugVerification);
		this.buttonConnect=(Button)mainView.findViewById(R.id.buttonConnect);
		this.buttonDebugMode=(Button)mainView.findViewById(R.id.buttonDebugMode);
		this.buttonConnect.setOnClickListener(this);
		this.buttonDebugMode.setOnClickListener(this);
		//Bundle registrationBundle=this.getArguments();
		//this.setErrorStatus((ArrayList<String>)registrationBundle.get("errorList"));
		return mainView;
	}
	
	public void setController(Laser2Controller controller)
	{
		this.laserController=controller;
	}
	
	public void setErrorStatus(ArrayList<String> errorList)
	{
		if (errorList.size()>0)
		{
			StringBuffer buffer=new StringBuffer();
			for (int i=0;i<errorList.size();i++)
			{
				buffer.append(errorList.get(i)+"\n");
			}
			textViewStatus.setText(buffer.toString());
			//this.buttonConnect.setEnabled(false);
			
		}
		else
		{
			textViewStatus.setText("");
		}
	}
	
	public void setStatus(String status)
	{
		this.textViewStatus.setText(status);
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
		else if (objButton==buttonDebugMode)
		{
			if (this.editTextDebugMode.getText().toString().toLowerCase().equals("debug"))
				this.laserController.enableDebugMode();
		}
	}
	
	
}
