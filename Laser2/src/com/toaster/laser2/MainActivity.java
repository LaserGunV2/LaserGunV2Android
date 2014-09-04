package com.toaster.laser2;

import com.toaster.laser2.laser2controller.Laser2Controller;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fragContainer=(ViewGroup)this.findViewById(R.id.fragContainer);
		//debugView=(TextView)this.findViewById(R.id.debugView);
		uiHandler=new ThreadedUIHandler(this, this.getMainLooper());
		mainController=new Laser2Controller(this, uiHandler);
		
		registrationFragment=new RegistrationFragment();
		registrationFragment.setController(mainController);
		
		FragmentManager fragmentManager=this.getSupportFragmentManager();
		FragmentTransaction fragTrans=fragmentManager.beginTransaction();
		fragTrans.add(R.id.fragContainer, registrationFragment);
		fragTrans.commit();
	
		
		setUIMode(UIMODE_REGISTRATION, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void setUIMode(int mode, boolean debug)
	{
		FragmentManager fragmentManager=this.getSupportFragmentManager();
		FragmentTransaction fragTrans=fragmentManager.beginTransaction();
		fragTrans.hide(registrationFragment);
		if (mode==UIMODE_REGISTRATION)
		{
			fragTrans.show(registrationFragment);
		}
		else if (mode==UIMODE_GAME)
		{
			
		}
		fragTrans.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
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
}
