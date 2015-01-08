package com.kaancelen.charter.comparators;

import java.util.Comparator;

public class TermComparator implements Comparator<Object>{

	@Override
	public int compare(Object o1, Object o2) {
		String[] split1 = ((String)o1).split("/");
		String[] split2 = ((String)o2).split("/");
		
		int month1 = Integer.parseInt(split1[0]);
		int year1 = Integer.parseInt(split1[1]);
		int month2 = Integer.parseInt(split2[0]);
		int year2 = Integer.parseInt(split2[1]);
		
		if(year1 > year2){
			return 1;
		}else if (year1 < year2) {
			return -1;
		}else{
			if(month1 > month2){
				return 1;
			}else if(month1 < month2){
				return -1;
			}
		}
		return 0;
	}

}
