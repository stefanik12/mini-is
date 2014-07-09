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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyWork implements Runnable
{
	private String id;
	public Context parent;
	
    public boolean mails, grades, notepad, exams;
    public int timer;

	public MyWork(String ID) {
		id = ID;
	}
	
	@Override
	public void run()
	{
		// kvoli zmene UI a tak
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			return;
		}
        
		Log.i("IS Muni Background Service", "started");
        
		MailsFeed mailsFeed = null;
		GradesFeed gradesFeed = null;
		NotesFeed notesFeed = null;
		ExamsFeed examsFeed = null;
		List<String> list = new LinkedList<String>();
			
		if (mails)
		{
			try {
				mailsFeed = new MailsFeed(id);
			} catch (MalformedURLException e) {
				Log.e("MailsFeed - MalformedURLException", e.getMessage());
			}
		}
		
		if (grades)
		{
			try {
				gradesFeed = new GradesFeed(id);
			} catch (MalformedURLException e) {
				Log.e("GradesFeed - MalformedURLException", e.getMessage());
			}
		}
		
		if (notepad)
		{
			try {
				notesFeed = new NotesFeed(id);
			} catch (MalformedURLException e) {
				Log.e("NotesFeed - MalformedURLException", e.getMessage());
			}
		}
		
		if (exams)
		{
			try {
				examsFeed = new ExamsFeed(id);
			} catch (MalformedURLException e) {
				Log.e("NotesFeed - MalformedURLException", e.getMessage());
			}
		}

		LoadSettings(mailsFeed, gradesFeed, notesFeed, examsFeed);
		
		while (true)
		{
			list.clear();
			
			if (mails)
			{
				try {
					list.addAll( mailsFeed.Run() );
				} catch (Exception e1) {
					Log.e("mailsFeed Exception", e1.getMessage());
				}
			}
			
			if (grades)
			{
				try {
					list.addAll( gradesFeed.Run() );					
				} catch (Exception e1) {
					Log.e("gradesFeed Exception", e1.getMessage());
				}
			}
			
			if (notepad)
			{
				try {
					list.addAll( notesFeed.Run() );
				} catch (Exception e1) {
					Log.e("notesFeed Exception", e1.getMessage());
				}
			}
			
			if (exams)
			{
				try {
					list.addAll( examsFeed.Run() );
				} catch (Exception e1) {
					Log.e("notesFeed Exception", e1.getMessage());
				}
			}

			if (parent == null)
				return;
			
			if (list.size() > 0)
			{
				CreateNotification(list);
				SaveSettings(mailsFeed, gradesFeed, notesFeed, examsFeed);
			}
			
			try {
				Thread.sleep(timer*1000);
			} catch (InterruptedException e) {
				return;
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
    	
    	editor.commit(); 	
    	settings = null;
    	editor = null;
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
    	
    	settings = null;
    }
	
	private void CreateNotification(List<String> list)
	{
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(parent)
	    .setSmallIcon(R.drawable.ic_launcher)
	    .setContentTitle("IS Muni Notifications")
	    .setContentText("New events")
	    .setAutoCancel(true);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle("Lasts events:");
		
		for (int i=0; i < list.size(); i++)
			inboxStyle.addLine(list.get(i));
		mBuilder.setStyle(inboxStyle);

	    Intent targetIntent = new Intent(parent, MainActivity.class);
	    PendingIntent contentIntent = PendingIntent.getActivity(parent, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    mBuilder.setContentIntent(contentIntent);
	    NotificationManager nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
	    nManager.notify(12345, mBuilder.build());
	    
	    mBuilder = null;
	    inboxStyle = null;
	    targetIntent = null;
	    nManager = null;
	}
}
