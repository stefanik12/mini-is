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

public class GradesFeed {
	private static final String prefix = "https://is.muni.cz/m/?m=";
	private static final String postfix = ";a=4";
	private String[] oldGrades = new String[10];
	private URL www;
	
	public GradesFeed(String ID) throws MalformedURLException {
		www = new URL(prefix + ID + postfix);
	}
	
	public void SetHistory(Set<String> data)
	{
		oldGrades = data.toArray(new String[data.size()]);
	}
	
	public Set<String> GetHistory()
	{
		return new HashSet<String>(Arrays.asList(oldGrades));
	}
	
	public List<String> Run() throws IOException
	{
		String[] newGrades = new String[10];
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
	    
	    index += 23; // "<div id=\"aplikace\">\n\n" = 23
	    
	    fullText = fullText.substring(index);
	    
	    int i = 0;
	    
	    while ((index = fullText.indexOf("<BR>")) != -1 && i < 10)
	    {
	    	if (fullText.indexOf("<P>") < index) break;
	    	
	    	newGrades[i] = fullText.substring(0, index);
	    	
	    	fullText = fullText.substring(index + 5); // "<BR>\n" = 5
	    	
	    	i++;
	    }
	    
	    List<String> results = Utility.CompareL(newGrades, oldGrades, i);
	    oldGrades = newGrades;
	    newGrades = null;
	    
	    return results;
	}

}
