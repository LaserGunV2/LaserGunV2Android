package com.toaster.laser2;

import java.util.ArrayList;

import com.toaster.laser2.fragment.DebugFragment;
import com.toaster.laser2.fragment.GameFragment;
import com.toaster.laser2.fragment.PreferencesFragment;
import com.toaster.laser2.fragment.RegistrationFragment;
import com.toaster.laser2.laser2controller.Laser2Controller;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements UIHandler
{
	public static final int UIMODE_REGISTRATION=0;
	public static final int UIMODE_GAME=1;
	public static final int UIMODE_PREFERENCES=2;
	public static final int UIMODE_DEBUG=3;
	
	protected ThreadedUIHandler uiHandler;
	//protected TextView debugView;
	protected ViewGroup fragContainer;
	protected Laser2Controller mainController;
	protected RegistrationFragment registrationFragment;
	protected GameFragment gameFragment;
	protected DebugFragment debugFragment;
	protected PreferencesFragment preferencesFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		View mainView=this.getLayoutInflater().inflate(R.layout.activity_main, null);
		setContentView(mainView);
		this.fragContainer=(ViewGroup)this.findViewById(R.id.fragContainer);
		//debugView=(TextView)this.findViewById(R.id.debugView);
		this.uiHandler=new ThreadedUIHandler(this, this.getMainLooper());
		this.mainController=new Laser2Controller(this, uiHandler);
		
		this.registrationFragment=new RegistrationFragment();
		this.gameFragment=new GameFragment();
		this.registrationFragment.setController(mainController);
		this.gameFragment.setController(mainController);
		this.debugFragment=new DebugFragment();
		this.debugFragment.setController(mainController);
		this.preferencesFragment=new PreferencesFragment();
		this.preferencesFragment.setController(mainController);
		
		mainView.setKeepScreenOn(true);
	
		
		FragmentManager fragmentManager=this.getSupportFragmentManager();
		FragmentTransaction fragTrans=fragmentManager.beginTransaction();
		fragTrans.add(R.id.fragContainer, registrationFragment);
		fragTrans.add(R.id.fragContainer, gameFragment);
		fragTrans.add(R.id.fragContainer, debugFragment);
		fragTrans.add(R.id.fragContainer , preferencesFragment);
		fragTrans.commit();
		
		setUIMode(UIMODE_REGISTRATION);
		
		//Bundle registrationBundle=new Bundle();
		//registrationBundle.putSerializable("errorList", mainController.getErrorList());
		//registrationFragment.setArguments(registrationBundle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void setUIMode(int mode)
	{
		FragmentManager fragmentManager=this.getSupportFragmentManager();
		FragmentTransaction fragTrans=fragmentManager.beginTransaction();
		fragTrans.hide(registrationFragment);
		fragTrans.hide(gameFragment);
		fragTrans.hide(debugFragment);
		if (mode==UIMODE_REGISTRATION)
		{
			fragTrans.show(registrationFragment);
		}
		else if (mode==UIMODE_GAME)
		{
			fragTrans.show(gameFragment);
		}
		else if (mode==UIMODE_DEBUG)
		{
			fragTrans.show(debugFragment);
		}
		else if (mode==UIMODE_PREFERENCES)
		{
			fragTrans.show(preferencesFragment);
		}
		fragTrans.commit();
		//untuk hide keyboard sehabis pencet connect
		InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Context.INPUT_METHOD_SERVICE);
	    if (this.getCurrentFocus()!=null)
	    	inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
	    
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			System.runFinalizersOnExit(true);
			System.exit(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void appendDebug(String s) 
	{
		//debugView.setText(debugView.getText()+" "+s);
	}

	@Override
	public void clearDebug() 
	{	
		//debugView.setText("");
	}

	@Override
	public void setAndroidId(String androidId) 
	{
		gameFragment.setAndroidId(androidId);
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode==KeyEvent.KEYCODE_BACK)||(keyCode==KeyEvent.KEYCODE_HOME))
			return true; 
		else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setStatus(String status) {
		this.registrationFragment.setStatus(status);
		this.debugFragment.setStatus(status);
	}

	@Override
	public void setDebugStatus(String debugStatus) {
		debugFragment.setDebugStatus(debugStatus);
		
	}

	@Override
	public void setPlayerAliveStatus(boolean isPlayerAlive)
	{
		gameFragment.setPlayerAliveStatus(isPlayerAlive);
		debugFragment.setPlayerAliveStatus(isPlayerAlive);
	}

	@Override
	public void setFoundBTDevices(ArrayList<BluetoothDevice> deviceList)
	{
		debugFragment.setFoundBTDevices(deviceList);
		Log.v(this.getClass().getName(),"bt scan completed");
	}

	@Override
	public void updateErrorStatus(ArrayList<String> errorList)
	{
		registrationFragment.setErrorStatus(errorList);
		
	}

	@Override
	public void btDeviceFound(BluetoothDevice foundDevice)
	{
		preferencesFragment.onBTDeviceFound(foundDevice);
	}

}
