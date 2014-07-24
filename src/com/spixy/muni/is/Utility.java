package com.spixy.muni.is;

import java.util.LinkedList;
import java.util.List;

public class Utility {
	
	public static List<String> CompareL(String[] newNS, String[] oldNS, int length)
	{
		List<String> list = new LinkedList<String>();
		
		for (int i=0; i<length; i++)
		{
			if (IsInArray(newNS[i], oldNS)) continue;
			list.add(newNS[i]);
		}
		
		return list;
	}
	
	public static boolean IsInArray(String newNS, String[] oldNS)
	{
		if (oldNS == null)
			return false;
		
		int length = oldNS.length;
		
		for (int i=0; i<length; i++)
		{
			if (newNS.equals(oldNS[i]))
				return true;
		}
		
		return false;
	}

}
