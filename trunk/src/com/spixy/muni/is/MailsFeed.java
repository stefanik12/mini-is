package com.spixy.muni.is;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MailsFeed {
	private static final String prefix = "https://is.muni.cz/m/posta.pl?m=";
	private static final String postfix = ";a=52";
	private String[] oldMails = new String[10];
	private URL www;
	private int items = 0;
	
	public MailsFeed(String ID) throws MalformedURLException {
		www = new URL(prefix + ID + postfix);
	}
	
	public void SetHistory(Set<String> data)
	{
		oldMails = data.toArray(new String[data.size()]);
	}
	
	public Set<String> GetHistory()
	{
		return new HashSet<String>(Arrays.asList(oldMails));
	}
	
	public int GetItems()
	{
		return items;
	}
	
	public List<String> Run() throws IOException
	{
		String[] newMails = new String[10];
		String inputLine = null;
        String fullText = "";
        items = 0;
			
		URLConnection yc = www.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        
	    while ((inputLine = in.readLine()) != null)
	    	fullText += inputLine + "\n";
	    
	    in.close();
	    
	    if (fullText.length() == 0)
	    	throw new IOException("Input empty");
	    
	    int index = fullText.indexOf("<OL>");
	    
	    if (index == -1)
	    	throw new IOException("Content not found");
	    
	    fullText = fullText.substring(index);
	    
	    int i = 0;
	    int startI, endI;
	    String txt1, txt2;
	    
	    while ((index = fullText.indexOf("<LI>")) != -1 && i < 10)
	    {
	    	startI = fullText.indexOf("\">") + 2; // 2 za ">
	    	endI = fullText.indexOf("</A>");
	    	txt1 = fullText.substring(startI, endI);
	
	    	startI = endI + 6; // "</A>: " = 6
	    	endI = fullText.indexOf("</LI>");
	    	txt2 = fullText.substring(startI, endI);
	    	
	    	newMails[i] = txt1 + " - " + txt2;	    	
	    	fullText = fullText.substring(endI + 5);	    	
	    	i++;
	    }
	    
	    List<String> results = Utility.CompareL(newMails, oldMails, i);
	    oldMails = newMails;
	    newMails = null;
	    
	    items = results.size();	    
	    return results;
	}
}
