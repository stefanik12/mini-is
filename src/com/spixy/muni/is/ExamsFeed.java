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

public class ExamsFeed {
	private static final String prefix = "https://is.muni.cz/m/?m=";
	private static final String postfix = ";a=14";
	private String[] oldExams = new String[10];
	private URL www;

	public ExamsFeed(String ID) throws MalformedURLException {
		www = new URL(prefix + ID + postfix);
	}
	
	public void SetHistory(Set<String> data)
	{
		oldExams = data.toArray(new String[data.size()]);
	}
	
	public Set<String> GetHistory()
	{
		return new HashSet<String>(Arrays.asList(oldExams));
	}

	public List<String> Run() throws IOException
	{
		String[] newExams = new String[10];
		String inputLine = null;
        String fullText = "";
			
		URLConnection yc = www.openConnection();
	    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        
	    while ((inputLine = in.readLine()) != null)
	    	fullText += inputLine + "\n";
	    
	    in.close();
	    
	    if (fullText.length() == 0)
	    	throw new IOException("Input empty");
	    
	    int index = fullText.indexOf("<div id=\"aplikace\">");
	    
	    if (index == -1)
	    	throw new IOException("Content not found");
	    
	    index += 24;
	    
	    fullText = fullText.substring(index);
	    
	    int i = 0;
	    
	    while ((index = fullText.indexOf("\n")) != -1 && i < 10)
	    {
	    	if (fullText.indexOf("<P>") < index) break;
	    	
	    	newExams[i] = fullText.substring(0, index);
	    	
	    	newExams[i] = newExams[i].replace("</I>" , "");
	    	newExams[i] = newExams[i].replace("<I>" , "");
	    	
	    	fullText = fullText.substring(index);
	    	
	    	i++;
	    }
	    
	    List<String> results = Utility.CompareL(newExams, oldExams, i);
	    oldExams = newExams;
	    newExams = null;
	    
	    return results;
	}

}
