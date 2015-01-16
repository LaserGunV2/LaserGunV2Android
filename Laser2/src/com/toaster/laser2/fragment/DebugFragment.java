package com.toaster.laser2.fragment;

import java.util.ArrayList;

import com.google.android.gms.internal.bt;
import com.toaster.laser2.MainActivity;
import com.toaster.laser2.R;
import com.toaster.laser2.laser2controller.Laser2Controller;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class DebugFragment extends Fragment implements OnClickListener,OnItemClickListener
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
	protected TextView textViewPairAddress;
	protected Laser2Controller laserController;
	protected Button buttonSimulateHit;
	protected Button buttonPing;
	protected Button buttonScan;
	protected Button buttonBack;
	//protected ListView listViewFoundDevices;
	//protected ArrayAdapter<String> foundDevicesAdapter;
	
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
		//textViewPingResult=(TextView)mainView.findViewById(R.id.textviewPingResult);
		//textViewPairAddress=(TextView)mainView.findViewById(R.id.textViewCurrentBTPairAddress);
		buttonConnect=(Button)mainView.findViewById(R.id.buttonConnect_debug);
		buttonDisconnect=(Button)mainView.findViewById(R.id.buttonDisconnect_debug);
		buttonSimulateHit=(Button)mainView.findViewById(R.id.buttonSimulateShotBy);
		buttonBack=(Button)mainView.findViewById(R.id.debug_buttonBack);
		//buttonPing=(Button)mainView.findViewById(R.id.buttonPing);
		//buttonScan=(Button)mainView.findViewById(R.id.buttonScan);
		//listViewFoundDevices=(ListView)mainView.findViewById(R.id.listViewBTAddress);
		//foundDevicesAdapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1);
		//listViewFoundDevices.setAdapter(foundDevicesAdapter);
		buttonConnect.setOnClickListener(this);
		buttonDisconnect.setOnClickListener(this);
		buttonSimulateHit.setOnClickListener(this);
		//buttonPing.setOnClickListener(this);
		//buttonScan.setOnClickListener(this);
		buttonBack.setOnClickListener(this);
		
		//listViewFoundDevices.setOnItemClickListener(this);
		//foundDevicesAdapter.add("abc");
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
		else if (objButton==buttonScan)
		{
			laserController.startBTScan();
		}
		else if (objButton==buttonBack)
		{
			if (this.laserController.getState()==Laser2Controller.STATE_DISCONNECTED)
				this.laserController.setUIMode(MainActivity.UIMODE_REGISTRATION);
			else if ((this.laserController.getState()==Laser2Controller.STATE_CONNECTED))
				this.laserController.setUIMode(MainActivity.UIMODE_GAME);
		}
	}
	
	public void setPlayerAliveStatus(boolean isPlayerAlive)
	{
		if (isPlayerAlive)
			textViewDebugOutput.setBackgroundColor(Color.WHITE);
		else
			textViewDebugOutput.setBackgroundColor(Color.RED);
	}

	public void setFoundBTDevices(ArrayList<BluetoothDevice> deviceList) 
	{
		for (BluetoothDevice device:deviceList)
		{
			//foundDevicesAdapter.add(device.getAddress());
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> listView, View item, int index, long id)
	{
		//laserController.setBTPairAddress(foundDevicesAdapter.getItem(index));
		System.out.println("saving bt address");
	}
}
