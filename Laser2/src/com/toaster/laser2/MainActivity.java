package com.toaster.laser2;

import com.toaster.laser2.laser2controller.Laser2Controller;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements UIHandler
{
	public static final int UIMODE_REGISTRATION=0;
	public static final int UIMODE_GAME=1;
	
	
	protected ThreadedUIHandler uiHandler;
	//protected TextView debugView;
	protected ViewGroup fragContainer;
	protected Laser2Controller mainController;
	protected RegistrationFragment registrationFragment;
	protected GameFragment gameFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		View mainView=this.getLayoutInflater().inflate(R.layout.activity_main, null);
		setContentView(mainView);
		fragContainer=(ViewGroup)this.findViewById(R.id.fragContainer);
		//debugView=(TextView)this.findViewById(R.id.debugView);
		uiHandler=new ThreadedUIHandler(this, this.getMainLooper());
		mainController=new Laser2Controller(this, uiHandler);
		
		registrationFragment=new RegistrationFragment();
		gameFragment=new GameFragment();
		registrationFragment.setController(mainController);
		gameFragment.setController(mainController);
		mainView.setKeepScreenOn(true);
	
		
		FragmentManager fragmentManager=this.getSupportFragmentManager();
		FragmentTransaction fragTrans=fragmentManager.beginTransaction();
		fragTrans.add(R.id.fragContainer, registrationFragment);
		fragTrans.add(R.id.fragContainer, gameFragment);
		fragTrans.commit();
	
		
		setUIMode(UIMODE_REGISTRATION);
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
		if (mode==UIMODE_REGISTRATION)
		{
			fragTrans.show(registrationFragment);
		}
		else if (mode==UIMODE_GAME)
		{
			fragTrans.show(gameFragment);
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
}
