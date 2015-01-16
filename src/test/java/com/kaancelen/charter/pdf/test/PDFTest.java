package com.kaancelen.charter.pdf.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.kaancelen.charter.helpers.PDFHelper;

public class PDFTest {
	public static void main(String[] args) {
		Random random = new Random();
		String[] names = {"kaan","meliha","sercan","nurullah","zeybel","eþref","hüseyin","merve","hasan","bulut","seçkin","cingöz","zafer"};
		
		//personeldata array
		List<Map<Object, Number>> personelData = new ArrayList<Map<Object,Number>>();
		//create personel data
		for(int i=0; i<3; i++){
			Map<Object, Number> map = new HashMap<Object, Number>();
			System.out.println();
			for(int j=0; j<names.length; j++){
				map.put(names[j], random.nextInt(1000000000));
				System.out.println(names[j] + " = " + map.get(names[j]));
			}
			personelData.add(map);
		}
		
		PDFHelper.createPerformanceReport(personelData, null);
	}
}
