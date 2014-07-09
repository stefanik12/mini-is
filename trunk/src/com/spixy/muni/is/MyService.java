package com.spixy.muni.is;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service
{
	private boolean mails = false, grades = false, notepad = false, exams = false;
    private int timer = 3600;
    private String id = "";
    private Thread t2 = null;
    private MyWork work;
    
	public MyService() {
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (intent == null)
		{
    		this.stopSelf();
    		return -1;
		}
			
		if (intent.hasExtra("ID"))
	    {
	    	id = intent.getStringExtra("ID");
        }
		
		mails = intent.getBooleanExtra("mails", mails);
    	grades = intent.getBooleanExtra("grades", grades);
    	notepad = intent.getBooleanExtra("notepad", notepad);
    	exams = intent.getBooleanExtra("exams", exams);

	    if (intent.hasExtra("timer"))
	    {
	    	timer = intent.getIntExtra("timer", -1);
	    	
	    	switch (timer)
			{
				case 0: timer = 1800; break; // 30 minutes
				case 1: timer = 3600; break; // 1 hour
				case 2: timer = 7200; break; // 2 hours
				case 3: timer = 10800; break; // 3 hours
				case 4: timer = 21600; break; // 6 hours
				case 5: timer = 43200; break; // 12 hours
				case 6: timer = 86400; break; // 24 hours
				
				default: timer = 3600; break; // 1 hour
			}
        }

    	if (id.length() == 0)
    	{
    		this.stopSelf();
    		return -1;
    	}
    	
    	if (mails==false && grades==false && notepad==false)
    	{
    		this.stopSelf();
    		return -1;
    	}
    	
		work = new MyWork(id);
    	work.mails = mails;
    	work.grades = grades;
    	work.notepad = notepad;;
    	work.exams = exams;
    	work.timer = timer;
    	work.parent = this;
    	
        if (t2 != null && t2.isAlive())
        	t2.interrupt();
        
		t2 = new Thread(work);
		t2.start();

	    return Service.START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		if (t2 != null && t2.isAlive())
        	t2.interrupt();
		
		super.onDestroy();
	}

}
