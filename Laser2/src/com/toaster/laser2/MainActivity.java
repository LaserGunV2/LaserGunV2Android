package com.toaster.laser2;

import com.toaster.laser2.laser2controller.Laser2Controller;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements UIHandler
{
	protected ThreadedUIHandler uiHandler;
	protected TextView debugView;
	protected Laser2Controller mainController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		debugView=(TextView)this.findViewById(R.id.debugView);
		uiHandler=new ThreadedUIHandler(this, this.getMainLooper());
		mainController=new Laser2Controller(this, uiHandler);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		debugView.setText(debugView.getText()+" "+s);
	}

	@Override
	public void clearDebug() 
	{	
		debugView.setText("");
	}
}
