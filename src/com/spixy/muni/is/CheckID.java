package com.spixy.muni.is;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

public class CheckID implements Runnable {
	private Activity parent;
	
	public String ID;
	
	public CheckID(Activity parent)
	{
		this.parent = parent;
	}
	
	public void run()
    {		
		if (Check())
		{
			parent.runOnUiThread(new Runnable() {
			    public void run() {
				    TextView tv = (TextView) parent.findViewById(R.id.textView1);
			    	tv.setText(R.string.stat_OK);
			    	tv.setTextColor(Color.GREEN);
			    }
			});
		}
		else
		{
			parent.runOnUiThread(new Runnable() {
			    public void run() {
				    TextView tv = (TextView) parent.findViewById(R.id.textView1);
			    	tv.setText(R.string.stat_NOK);
			    	tv.setTextColor(Color.RED);
			    }
			});
		}
    }
	
	private boolean Check()
	{
		try {
			URL www = new URL("https://is.muni.cz/m/?" + ID);
			URLConnection yc = www.openConnection();
		    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		    String inputLine = null;
		    String fullText = "";
		    
		    while ((inputLine = in.readLine()) != null)
		    	fullText += inputLine + "\n";  
		    
		    in.close();		    
		    return ! (fullText.contains("Chybné muèo."));

		} catch (MalformedURLException e) {
			return false;
			
		} catch (IOException e) {
			return true;
		}	    
	}
}
