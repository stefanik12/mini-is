package com.spixy.muni.is;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class NotesFeed {
	private static final String prefix = "https://is.muni.cz/m/?m=";
	private static final String postfix = ";a=8";
	private String[] oldNotes;
	private String id;
	private URL www;
	private int items = 0;
	
	public NotesFeed(String ID) throws MalformedURLException {
		www = new URL(prefix + id + postfix);
		id = ID;
	}
	
	public void SetHistory(Set<String> data)
	{
		oldNotes = data.toArray(new String[data.size()]);
	}

	public Set<String> GetHistory()
	{
		return new HashSet<String>(Arrays.asList(oldNotes));
	}

	public int GetItems()
	{
		return items;
	}
	
	private int parse(String page, List<String> notes) throws IOException
	{
		www = new URL(prefix + id + postfix + page);
		String inputLine = null;
        String fullText = "";
			
		URLConnection yc = www.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        
	    while ((inputLine = in.readLine()) != null)
	    	fullText += inputLine + "\n";
	    
	    in.close();
	    
	    if (fullText.length() == 0)
	    	throw new IOException("Input empty");
	    
	    if (fullText.indexOf("další stránka") == -1)
	    {
	    	throw new IOException("No more pages");
	    }
	    
	    int index = fullText.indexOf("<div id=\"aplikace\">");
	    
	    if (index == -1)
	    	throw new IOException("Content not found");
	    
	    index += 24;
	    
	    fullText = fullText.substring(index);
	    
	    int i = 0;
	    
	    while ((index = fullText.indexOf("<HR>")) != -1 && i < 3)
	    {
	    	if (fullText.indexOf("<A") < index) break;
	    	
	    	String newNote = fullText.substring(0, index);
	    	
	    	newNote = newNote.replace("</B>" , ":");
	    	newNote = newNote.replace("<BR>" , ": ");
	    	newNote = newNote.replace("\n" , " ");
	    	
	    	notes.add(newNote);
	    	
	    	fullText = fullText.substring(index + 7);
	    	
	    	i++;
	    }
	    
		return i;
	}
	
	public List<String> Run() throws IOException
	{
		List<String> notes = new LinkedList<String>();

        items = 0;
		int s = 0;
		int page = 0;
		
		while (s < 30)
		{
			try
			{
				s += parse(";st=" + page, notes);
				page++;
			}
			catch (IOException ex)
			{
				break;
			}
		}

		String[] array = notes.toArray(new String[notes.size()]);
	    
		notes = Utility.CompareL(array, oldNotes, array.length);
	    oldNotes = array;
	    array = null;

        items = notes.size();
	    return notes;
	}
	
}
