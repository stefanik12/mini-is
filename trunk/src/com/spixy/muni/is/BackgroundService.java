package com.spixy.muni.is;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundService extends Service
{
	private boolean mails = false, grades = false, notepad = false, exams = false;
    private int timer = 3600;
    private String id = "";
    private MyWork work = null;
    
	public BackgroundService() {
		work = new MyWork(this);
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
	    	int timerID = intent.getIntExtra("timer", 1);
	    	
	    	switch (timerID)
			{
				case 0: timer = 1800; break; // 30 minutes
				case 1: timer = 3600; break; // 1 hour
				case 2: timer = 7200; break; // 2 hours
				case 3: timer = 10800; break; // 3 hours
				case 4: timer = 21600; break; // 6 hours
				case 5: timer = 43200; break; // 12 hours
				case 6: timer = 86400; break; // 24 hours
			}
        }
	    
	    if (id.length() != 10)
	    {
	    	this.stopSelf();
    		return -1;
	    }
    	
    	if (mails==false && grades==false && notepad==false && exams==false)
    	{
    		this.stopSelf();
    		return -1;
    	}
    	
    	if (work.isAlive())
    	{
    		work.running = false;
    		work.interrupt();
    	}
    	
    	work = new MyWork(this);
    	work.id = id;
    	work.mails = mails;
    	work.grades = grades;
    	work.notepad = notepad;;
    	work.exams = exams;
    	work.timer = timer;
    	work.start();

	    return Service.START_STICKY;
	}
	
	public void Interrupt()
	{
		if (work.isAlive())
			work.interrupt();
	}

	@Override
	public void onDestroy()
	{
		if (work.isAlive())
    	{
    		work.running = false;
    		work.interrupt();
    	}
		
		super.onDestroy();
	}

}
