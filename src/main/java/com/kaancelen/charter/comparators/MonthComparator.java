package com.kaancelen.charter.comparators;

import java.util.Comparator;

import com.kaancelen.charter.constant.ChartConstants;
import com.kaancelen.charter.utils.StringUtil;

public class MonthComparator implements Comparator<Object>{

	
	@Override
	public int compare(Object arg0, Object arg1) {
		String month1 = StringUtil.replaceTurkishChars(((String) arg0));
		String month2 = StringUtil.replaceTurkishChars(((String) arg1));
		
		if(month1.compareTo(month2) == 0){
			return 0;
		}
		
		for (String month : ChartConstants.MONTHS) {
			if(month1.compareTo(month) == 0){
				return -1;
			}
			if(month2.compareTo(month) == 0){
				return 1;
			}
		}
		throw new IllegalArgumentException(month1 + " or " + month2 + " is not appripiote for this method");
	}

}
