package com.spixy.muni.is;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver
{
	@Override
    public void onReceive(Context context, Intent intent)
	{
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {     
        	SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);

        	if (settings.getBoolean("Autostart", true) == false)
        	{
            	settings = null;
        		return;
        	}
        	
        	String id = settings.getString("ID", "").toString();
        	int freq = settings.getInt("Frequency", 1);	
        	boolean sw1 = settings.getBoolean("Switch 1", false);
        	boolean sw2 = settings.getBoolean("Switch 2", false);
        	boolean sw3 = settings.getBoolean("Switch 3", false);
        	boolean sw4 = settings.getBoolean("Switch 4", false);

        	settings = null;
        	
            Intent mServiceIntent = new Intent(context, MyService.class);
            mServiceIntent.putExtra("ID", id);
	        mServiceIntent.putExtra("timer", freq);
            mServiceIntent.putExtra("mails", sw1);
        	mServiceIntent.putExtra("grades", sw2);
            mServiceIntent.putExtra("notepad", sw3);
            mServiceIntent.putExtra("exams", sw4);
	        context.startService(mServiceIntent);
        }
    }
}
