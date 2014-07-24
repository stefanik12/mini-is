package com.spixy.muni.is;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyWork extends Thread
{
	public String id;
	public Context parent;
    public boolean mails, grades, notepad, exams, running;
    public int timer;
    
    public MyWork(Context parent)
    {
    	super("Worker Thread");
    	this.parent = parent;
    }
	
	@Override
	public void run()
	{
		running = true;
		MailsFeed mailsFeed = null;
		GradesFeed gradesFeed = null;
		NotesFeed notesFeed = null;
		ExamsFeed examsFeed = null;
		List<String> list = new LinkedList<String>();
		int flag;
			
		if (mails)
		{
			try {
				mailsFeed = new MailsFeed(id);
			} catch (MalformedURLException e) { }
		}
		
		if (grades)
		{
			try {
				gradesFeed = new GradesFeed(id);
			} catch (MalformedURLException e) { }
		}
		
		if (notepad)
		{
			try {
				notesFeed = new NotesFeed(id);
			} catch (MalformedURLException e) { }
		}
		
		if (exams)
		{
			try {
				examsFeed = new ExamsFeed(id);
			} catch (MalformedURLException e) { }
		}

		LoadSettings(mailsFeed, gradesFeed, notesFeed, examsFeed);
		
		while (running)
		{
			flag = 0;
			list.clear();
			
			if (mails)
			{
				try {
					list.addAll( mailsFeed.Run() );
					if (mailsFeed.GetItems() > 0)
						flag += 1;
				} catch (Exception e1) {
					Log.e("MailsFeed", e1.getMessage());
				}
			}
			
			if (grades)
			{
				try {
					list.addAll( gradesFeed.Run() );
					if (gradesFeed.GetItems() > 0)
						flag += 2;
				} catch (Exception e1) {
					Log.e("GradesFeed", e1.getMessage());
				}
			}
			
			if (notepad)
			{
				try {
					list.addAll( notesFeed.Run() );
					if (notesFeed.GetItems() > 0)
						flag += 4;
				} catch (Exception e1) {
					Log.e("NotesFeed", e1.getMessage());
				}
			}
			
			if (exams)
			{
				try {
					list.addAll( examsFeed.Run() );
					if (examsFeed.GetItems() > 0)
						flag += 8;
				} catch (Exception e1) {
					Log.e("ExamsFeed", e1.getMessage());
				}
			}

			if (parent == null)
				return;
			
			if (list.size() > 0)
			{
				CreateNotification(list, flag);
				SaveSettings(mailsFeed, gradesFeed, notesFeed, examsFeed);
			}
			
			try {
				Thread.sleep(timer*1000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private void SaveSettings(MailsFeed mailsFeed, GradesFeed gradesFeed, NotesFeed notesFeed, ExamsFeed examsFeed)
    {
		SharedPreferences settings = parent.getSharedPreferences("HistoryData", 0);
    	SharedPreferences.Editor editor = settings.edit();
    	
    	if (mailsFeed != null)
    		editor.putStringSet("Mails", mailsFeed.GetHistory());
    	
    	if (gradesFeed != null)
    		editor.putStringSet("Grades", gradesFeed.GetHistory());
    	
    	if (notesFeed != null)
    		editor.putStringSet("Notes", notesFeed.GetHistory());

    	if (examsFeed != null)
    		editor.putStringSet("Exams", examsFeed.GetHistory());
    	
    	editor.apply();
    }
	
	private void LoadSettings(MailsFeed mailsFeed, GradesFeed gradesFeed, NotesFeed notesFeed, ExamsFeed examsFeed)
    {
    	SharedPreferences settings = parent.getSharedPreferences("HistoryData", 0);
    	
    	if (mailsFeed != null)
    		mailsFeed.SetHistory( settings.getStringSet("Mails", new HashSet<String>()) );
    	
    	if (gradesFeed != null)
    		gradesFeed.SetHistory( settings.getStringSet("Grades", new HashSet<String>()) );
    	
    	if (notesFeed != null)
    		notesFeed.SetHistory( settings.getStringSet("Notes", new HashSet<String>()) );
    	
    	if (examsFeed != null)
    		examsFeed.SetHistory( settings.getStringSet("Exams", new HashSet<String>()) );
    }
	
	private void CreateNotification(List<String> list, int flag)
	{
		String text, url = "https://is.muni.cz";
		
		switch (flag)
		{
			case 1:  // new messages only
				if (list.size() == 1) text = list.get(0);
				else text = parent.getResources().getString(R.string.new_msgs);
				url += "/m/posta.pl?m=" + id + ";a=52";
				break;
				
			case 2:  // new grades only
				if (list.size() == 1) text = list.get(0);
				else text = parent.getResources().getString(R.string.new_grades);
				url += "/m/?m=" + id + ";a=4";
				break;
				
			case 4:  // new notes only
				if (list.size() == 1) text = list.get(0);
				else text = parent.getResources().getString(R.string.new_notes);
				url += "/m/?m=" + id + ";a=8";
				break;
				
			case 8:  // new exams only
				if (list.size() == 1) text = list.get(0);
				else text = parent.getResources().getString(R.string.new_exams);
				url += "/m/?m=" + id + ";a=14";
				break;
				
			default:  // new events
				text = parent.getResources().getString(R.string.new_events);
				url += "/m/?" + id;
				break;
		}
		
		/*
		// notification cleared
		Intent clearedIntent = new Intent(parent, NotificationCleared.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(parent.getApplicationContext(), 0, clearedIntent, 0);
		*/	
	    
	    // notification clicked
	    Intent targetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	    PendingIntent contentIntent = PendingIntent.getActivity(parent, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);	    
	    
	    // setup
	    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(parent)
	    .setSmallIcon(R.drawable.ic_launcher)
	    .setContentTitle("IS Muni Notifications")
	    .setContentText(text)
	    .setAutoCancel(true)
	    //.setDeleteIntent(pendingIntent)
	    .setContentIntent(contentIntent);
	    
	    // fill lines to extended notification
	 	if (list.size() > 1)
	 	{
	 		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
	 		inboxStyle.setBigContentTitle(text);
	 		
	 		for (int i=0; i < list.size(); i++)
	 			inboxStyle.addLine(list.get(i));
	 			
	 		mBuilder.setStyle(inboxStyle);
	 	}
	    
	    NotificationManager nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
	    nManager.notify(65535, mBuilder.build());
	}
}
