package com.toaster.laser2.fragment;

import com.toaster.laser2.MainActivity;
import com.toaster.laser2.R;
import com.toaster.laser2.laser2controller.Laser2Controller;

import android.bluetooth.BluetoothDevice;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PreferencesFragment extends Fragment implements OnClickListener,OnItemClickListener
{
	protected Button buttonScan;
	protected Button buttonBack;
	protected Button buttonToggleAzimuthCalcId;
	protected ListView listViewFoundDevices;
	protected ArrayAdapter<String> foundDevicesAdapter;
	protected TextView textViewPairAddress;
	protected Laser2Controller laserController;
	//hrsny diambil dari preferences
	protected int curAzimuthCalcId=1;
	
	@Override
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
	{
		View mainView=inflater.inflate(R.layout.preferences_fragment_layout, null);
		
		textViewPairAddress=(TextView)mainView.findViewById(R.id.preferences_textViewCurrentBTPairAddress);
		buttonScan=(Button)mainView.findViewById(R.id.preferences_buttonScan);
		buttonBack=(Button)mainView.findViewById(R.id.preferences_buttonBack);
		buttonToggleAzimuthCalcId=(Button)mainView.findViewById(R.id.preferences_buttonToggleAzimuthCalcId);
		listViewFoundDevices=(ListView)mainView.findViewById(R.id.preferences_listViewBTAddress);
		foundDevicesAdapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1);
		listViewFoundDevices.setAdapter(foundDevicesAdapter);
		buttonScan.setOnClickListener(this);		
		buttonBack.setOnClickListener(this);
		buttonToggleAzimuthCalcId.setOnClickListener(this);
		listViewFoundDevices.setOnItemClickListener(this);
		showPairedBTAddress();
		return mainView;
	}

	public void setController(Laser2Controller controller)
	{
		this.laserController=controller;
	}
	
	protected void showPairedBTAddress()
	{
		textViewPairAddress.setText("Pair Address = "+laserController.getBTPairAddress());
	}
	
	@Override
	public void onItemClick(AdapterView<?> listView, View item, int index, long id)
	{
		laserController.setBTPairAddress(foundDevicesAdapter.getItem(index));
		Toast notificationToast=Toast.makeText(this.getActivity(), "Pair Saved : "+foundDevicesAdapter.getItem(index), Toast.LENGTH_SHORT);
		notificationToast.show();
		showPairedBTAddress();
		//ganti pake toast
		//System.out.println("saving bt address");
	}

	@Override
	public void onClick(View v)
	{
		Button btn=(Button)v;
		if (btn==buttonScan)
		{
			this.foundDevicesAdapter.clear();
			this.laserController.startBTScan();
		}
		else if (btn==buttonBack)
		{
			this.laserController.setUIMode(MainActivity.UIMODE_REGISTRATION);
		}
		else if (btn==buttonToggleAzimuthCalcId)
		{
			this.curAzimuthCalcId=(this.curAzimuthCalcId+1)%2;
			this.buttonToggleAzimuthCalcId.setText("Azimuth Calc "+this.curAzimuthCalcId);
			this.laserController.setAzimuthCalculatorId(this.curAzimuthCalcId);
		}
	}
	
	public void onBTDeviceFound(BluetoothDevice foundDevice)
	{
		foundDevicesAdapter.add(foundDevice.getAddress());
	}
}
