package com.kaancelen.charter.pdf.test;

import com.kaancelen.charter.utils.StringUtil;

public class MonthlyComparatorTest {
	public static void main(String[] args) {
		String month = "EYLüL";
		
		System.out.println(StringUtil.replaceTurkishChars(month.toUpperCase()));
		
//		MonthComparator comparator = new MonthComparator();
//		
//		if(comparator.compare(month1, month2) > 0){
//			System.out.println(month1 + " > " + month2);
//		}else if(comparator.compare(month1, month2) < 0){
//			System.out.println(month1 + " < " + month2);
//		}else{
//			System.out.println(month1 + " = " + month2);
//		}
	}
}
