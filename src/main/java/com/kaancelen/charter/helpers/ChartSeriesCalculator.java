package com.kaancelen.charter.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

import com.kaancelen.charter.comparators.LabelComparator;
import com.kaancelen.charter.comparators.MonthComparator;
import com.kaancelen.charter.comparators.StringComparator;
import com.kaancelen.charter.comparators.TermComparator;
import com.kaancelen.charter.constant.BankConstants;
import com.kaancelen.charter.constant.ChartConstants;
import com.kaancelen.charter.models.JobRecord;
import com.kaancelen.charter.models.Record;

/**
 * Calculator chart series
 * @author kaancelen
 */
public class ChartSeriesCalculator {
	/**
	 * TCMB nakit/gayrinakit risk toplamlarýný hesaplar
	 * @param records
	 * @param nakit = true nakit hesabý, nakit = false gayrinakit hesabý 
	 * @return
	 */
	public static ChartSeries tcmbNakitRisk(List<Record> records, boolean nakit){
		Map<Object, Number> tcmbNakitRiskMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(StringHelper.inArray((nakit?BankConstants.nakitSeries:BankConstants.gnakitSeries), record.getRiskCode())){//eger nakit risk kodu ise
				Double termTotal = record.getShortTermRisk() + record.getMiddleTermRisk() + record.getLongTermRisk()
						+ record.getTahakkuk() + record.getReeskont();
				Double oldValue = (Double)tcmbNakitRiskMap.get(record.getTerm());
				tcmbNakitRiskMap.put(record.getTerm(), termTotal + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries tcmbNakitRisk = new ChartSeries("TCMB "+(nakit?"Nakit":"Gayrinakit")+" Risk");
		tcmbNakitRisk.setData(tcmbNakitRiskMap);
		return tcmbNakitRisk;
	}
	
	/**
	 * Sekerbank nakit/gayrinakit risk toplamlarýný hesaplar
	 * @param records
	 * @return
	 */
	public static ChartSeries sekerbankNakitRisk(List<Record> records, boolean nakit){
		Map<Object, Number> sekerbankNakitRiskMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(StringHelper.inArray((nakit?BankConstants.nakitSeries:BankConstants.gnakitSeries), record.getRiskCode()) && record.getMemberCode().contains(BankConstants.sekerbankCode)){//eger nakit risk kodu ise
				Double termTotal = record.getShortTermRisk() + record.getMiddleTermRisk() + record.getLongTermRisk()
						+ record.getTahakkuk() + record.getReeskont();
				Double oldValue = (Double)sekerbankNakitRiskMap.get(record.getTerm());
				sekerbankNakitRiskMap.put(record.getTerm(), termTotal + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries sekerbankNakitRisk = new ChartSeries("Þekerbank "+(nakit?"Nakit":"Gayrinakit")+" Risk");
		sekerbankNakitRisk.setData(sekerbankNakitRiskMap);
		return sekerbankNakitRisk;
	}
	
	/**
	 * Her dönem nakit/gayrinakit risk kayýdý bulunan kurum sayýsý
	 * @param records
	 * @return
	 */
	public static Map<String, Integer> corpNakitRisk(List<Record> records, boolean nakit){
		Map<String, Set<String>> corpNakitRiskMap = new TreeMap<String, Set<String>>(new TermComparator());
		
		for(Record record : records){
			if(StringHelper.inArray((nakit?BankConstants.nakitSeries:BankConstants.gnakitSeries), record.getRiskCode())){//eger nakit risk kodu ise
				if(corpNakitRiskMap.get(record.getTerm()) == null){//if there are no corp record in this term
					corpNakitRiskMap.put(record.getTerm(), new HashSet<String>());
				}
				corpNakitRiskMap.get(record.getTerm()).add(record.getMemberCode());//add this member code to this term's set
			}
		}
		Map<String, Integer> corpNakitRisk = new TreeMap<String, Integer>(new TermComparator());//convert sets to numbers
		for (Map.Entry<String, Set<String>> entry : corpNakitRiskMap.entrySet()){
			corpNakitRisk.put(entry.getKey(), entry.getValue().size());
		}
		return corpNakitRisk;
	}
	
	/**
	 * TCMB nakit/gayrinakit limit toplamlarýný hesaplar
	 * @param records
	 * @return
	 */
	public static ChartSeries tcmbNakitLimit(List<Record> records, boolean nakit){
		Map<Object, Number> tcmbNakitLimitMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(StringHelper.inArray((nakit?BankConstants.nakitSeries:BankConstants.gnakitSeries), record.getRiskCode())){//eger nakit risk kodu ise
				Double termLimit = record.getLimit();
				Double oldValue = (Double)tcmbNakitLimitMap.get(record.getTerm());
				tcmbNakitLimitMap.put(record.getTerm(), termLimit + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries tcmbNakitLimit = new ChartSeries("TCMB "+(nakit?"Nakit":"Gayrinakit")+" Limit");
		tcmbNakitLimit.setData(tcmbNakitLimitMap);
		return tcmbNakitLimit;
	}
	
	/**
	 * TCMB kisa vadeli nakit/gayrinakit risk toplamý
	 * @param records
	 * @return
	 */
	public static ChartSeries tcmbShortTermNakitRisk(List<Record> records, boolean nakit){
		Map<Object, Number> tcmbShortTermNakitRiskMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(StringHelper.inArray((nakit?BankConstants.nakitSeries:BankConstants.gnakitSeries), record.getRiskCode())){//eger nakit risk kodu ise
				Double shortTermNakitRisk = record.getShortTermRisk();
				Double oldValue = (Double)tcmbShortTermNakitRiskMap.get(record.getTerm());
				tcmbShortTermNakitRiskMap.put(record.getTerm(), shortTermNakitRisk + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries tcmbShortTermNakitRisk = new ChartSeries("TCMB 0-12 Ay "+(nakit?"Nakit":"Gayrinakit")+" Risk");
		tcmbShortTermNakitRisk.setData(tcmbShortTermNakitRiskMap);
		return tcmbShortTermNakitRisk;
	}
	
	/**
	 * TCMB orta vadeli nakit/gayrinakit risk toplamý
	 * @param records
	 * @return
	 */
	public static ChartSeries tcmbMiddleTermNakitRisk(List<Record> records, boolean nakit){
		Map<Object, Number> tcmbMiddleTermNakitRiskMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(StringHelper.inArray((nakit?BankConstants.nakitSeries:BankConstants.gnakitSeries), record.getRiskCode())){//eger nakit risk kodu ise
				Double middleTermNakitRisk = record.getMiddleTermRisk();
				Double oldValue = (Double)tcmbMiddleTermNakitRiskMap.get(record.getTerm());
				tcmbMiddleTermNakitRiskMap.put(record.getTerm(), middleTermNakitRisk + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries tcmbMiddleTermNakitRisk = new ChartSeries("TCMB 12-24 Ay "+(nakit?"Nakit":"Gayrinakit")+" Risk");
		tcmbMiddleTermNakitRisk.setData(tcmbMiddleTermNakitRiskMap);
		return tcmbMiddleTermNakitRisk;
	}
	
	/**
	 * TCMB uzun vadeli nakit/gayrinakit risk toplamý
	 * @param records
	 * @return
	 */
	public static ChartSeries tcmbLongTermNakitRisk(List<Record> records, boolean nakit){
		Map<Object, Number> tcmbLongTermNakitRiskMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(StringHelper.inArray((nakit?BankConstants.nakitSeries:BankConstants.gnakitSeries), record.getRiskCode())){//eger nakit risk kodu ise
				Double longTermNakitRisk = record.getLongTermRisk();
				Double oldValue = (Double)tcmbLongTermNakitRiskMap.get(record.getTerm());
				tcmbLongTermNakitRiskMap.put(record.getTerm(), longTermNakitRisk + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries tcmbLongTermNakitRisk = new ChartSeries("TCMB 24+ Ay "+(nakit?"Nakit":"Gayrinakit")+" Risk");
		tcmbLongTermNakitRisk.setData(tcmbLongTermNakitRiskMap);
		return tcmbLongTermNakitRisk;
	}
	
	/**
	 * TCMB nakit teminine yönelik teminat mektubu (201, 251)
	 * @param records
	 * @return
	 */
	public static ChartSeries tcmbNTYTM(List<Record> records){
		Map<Object, Number> tcmbNTYTMMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(StringHelper.inArray(BankConstants.ntytm, record.getRiskCode())){//eger nakit risk kodu ise
				Double ntytm = record.getShortTermRisk() + record.getMiddleTermRisk() + record.getLongTermRisk()
						+ record.getTahakkuk() + record.getReeskont();
				Double oldValue = (Double)tcmbNTYTMMap.get(record.getTerm());
				tcmbNTYTMMap.put(record.getTerm(), ntytm + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries tcmbNTYTM = new ChartSeries("Nakit Teminine Yönelik Tem. Mek.(201, 251)");
		tcmbNTYTM.setData(tcmbNTYTMMap);
		return tcmbNTYTM;
	}

	/**
	 * 
	 * @param records
	 * @return
	 */
	public static ChartSeries facNakitRisk(List<Record> records) {
		Map<Object, Number> facNakitRiskMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(record.getRiskCode().startsWith("6")){//eger 6xx factoring kodu ise
				Double facRisk = record.getShortTermRisk() + record.getMiddleTermRisk() + record.getLongTermRisk()
						+ record.getTahakkuk() + record.getReeskont();
				Double oldValue = (Double)facNakitRiskMap.get(record.getTerm());
				facNakitRiskMap.put(record.getTerm(), facRisk + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries facNakitRisk = new ChartSeries("Faktoring Nakit risk (6xx)");
		facNakitRisk.setData(facNakitRiskMap);
		return facNakitRisk;
	}

	/**
	 * 
	 * @param records
	 * @return
	 */
	public static ChartSeries leaNakitRisk(List<Record> records) {
		Map<Object, Number> leaNakitRiskMap = new TreeMap<Object, Number>(new TermComparator());
		
		for(Record record : records){
			if(record.getRiskCode().startsWith("7")){//eger 7xx factoring kodu ise
				Double leaRisk = record.getShortTermRisk() + record.getMiddleTermRisk() + record.getLongTermRisk()
						+ record.getTahakkuk() + record.getReeskont();
				Double oldValue = (Double)leaNakitRiskMap.get(record.getTerm());
				leaNakitRiskMap.put(record.getTerm(), leaRisk + (oldValue==null?0:oldValue));//add this records total value to map
			}
		}
		
		ChartSeries leaNakitRisk = new ChartSeries("Leasing Nakit risk (7xx)");
		leaNakitRisk.setData(leaNakitRiskMap);
		return leaNakitRisk;
	}
	
	/**
	 * create usage percantage map for given share and denom maps,
	 * Create share/denom map
	 * @param share
	 * @param denom
	 * @return
	 */
	public static Map<Object, Number> usePercantage(Map<Object, Number> share, Map<Object, Number> denom){
		Map<Object, Number> percantage = new TreeMap<Object, Number>(new TermComparator());
		for (Entry<Object, Number> entry : share.entrySet()){
			percantage.put(entry.getKey(), (entry.getValue().doubleValue()/denom.get(entry.getKey()).doubleValue())*100);
		}
		
		return percantage;
	}

	/**
	 * @param jobRecords
	 * @return
	 */
	public static ChartSeries PersonelReport(List<JobRecord> jobRecords) {
		Map<Object, Number> personelReportMap = new TreeMap<Object, Number>(new StringComparator());
		
		for (JobRecord jobRecord : jobRecords) {
			//if it is not null and RAPOR than it is rapor
			if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.rapor)){
				//if there are no records before it should be '0'
				Integer oldValue = (Integer) personelReportMap.get(jobRecord.getPersonel());
				personelReportMap.put(jobRecord.getPersonel(), 1 + (oldValue==null?0:oldValue));//increase counter 1 for this personel
			}
		}
		
		ChartSeries chartSeries = new ChartSeries("Rapor Sayýsý");
		chartSeries.setData(personelReportMap);
		return chartSeries;
	}

	/**
	 * @param jobRecords
	 * @return
	 */
	public static ChartSeries PersonelMemzuc(List<JobRecord> jobRecords) {
		Map<Object, Number> personelMemzucMap = new TreeMap<Object, Number>(new StringComparator());
		
		for (JobRecord jobRecord : jobRecords) {
			//if desc contains 'memzu' then it is memzu record
			if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.memzu)){
				//if there are no records before it should be '0'
				Integer oldValue = (Integer) personelMemzucMap.get(jobRecord.getPersonel());
				personelMemzucMap.put(jobRecord.getPersonel(), 1 + (oldValue==null?0:oldValue));//increase counter 1 for this personel
			}
		}
		
		ChartSeries chartSeries = new ChartSeries("Memzuç Sayýsý");
		chartSeries.setData(personelMemzucMap);
		return chartSeries;
	}
	
	/**
	 * @param jobRecords
	 * @return
	 */
	public static ChartSeries PersonelCek(List<JobRecord> jobRecords) {
		Map<Object, Number> personelCekMap = new TreeMap<Object, Number>(new StringComparator());
		
		for (JobRecord jobRecord : jobRecords) {
			//if desc contains 'memzu' then it is memzu record
			if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.cek)){
				//if there are no records before it should be '0'
				Integer oldValue = (Integer) personelCekMap.get(jobRecord.getPersonel());
				personelCekMap.put(jobRecord.getPersonel(), 1 + (oldValue==null?0:oldValue));//increase counter 1 for this personel
			}
		}
		
		ChartSeries chartSeries = new ChartSeries("Çek Sayýsý");
		chartSeries.setData(personelCekMap);
		return chartSeries;
	}

	/**
	 * 1=> Toplam
	 * 2=> Rapor
	 * 3=> Çek
	 * 4=> Memzuç
	 * @param jobRecords
	 * @param type
	 * @return
	 */
	public static ChartSeries DepartmentReport(List<JobRecord> jobRecords, int type) {
		Map<Object, Number> departmentMap = new TreeMap<Object, Number>(new LabelComparator());
		departmentMap.put(ChartConstants.DEPARTMENT_LABELS[0], 0);//toplam
		departmentMap.put(ChartConstants.DEPARTMENT_LABELS[1], 0);//rapor
		departmentMap.put(ChartConstants.DEPARTMENT_LABELS[2], 0);//olumlu rapor
		departmentMap.put(ChartConstants.DEPARTMENT_LABELS[3], 0);//olumsuz rapor
		departmentMap.put(ChartConstants.DEPARTMENT_LABELS[4], 0);//Çek
		departmentMap.put(ChartConstants.DEPARTMENT_LABELS[5], 0);//olumlu çek
		departmentMap.put(ChartConstants.DEPARTMENT_LABELS[6], 0);//olumsuz çek
		departmentMap.put(ChartConstants.DEPARTMENT_LABELS[7], 0);//memzuç
		
		for (JobRecord jobRecord : jobRecords) {
			switch (type) {
			case 1:
				//Toplam
				departmentMap.put(ChartConstants.DEPARTMENT_LABELS[7], 1 + (Integer)departmentMap.get(ChartConstants.DEPARTMENT_LABELS[7]));
				break;
			case 2:
				//Rapor
				if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.rapor)){	//Toplam rapor
					departmentMap.put(ChartConstants.DEPARTMENT_LABELS[0], 1 + (Integer)departmentMap.get(ChartConstants.DEPARTMENT_LABELS[0]));
					if(jobRecord.getResult() != null && !jobRecord.getResult().contains(BankConstants.olumsuz)){ //olumlu rapor
						departmentMap.put(ChartConstants.DEPARTMENT_LABELS[1], 1 + (Integer)departmentMap.get(ChartConstants.DEPARTMENT_LABELS[1]));
					}
					if(jobRecord.getResult() != null && jobRecord.getResult().contains(BankConstants.olumsuz)){ //olumsuz rapor
						departmentMap.put(ChartConstants.DEPARTMENT_LABELS[2], 1 + (Integer)departmentMap.get(ChartConstants.DEPARTMENT_LABELS[2]));
					}
				}
				break;
			case 3:
				//Çek
				if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.cek)){	//Toplam çek
					departmentMap.put(ChartConstants.DEPARTMENT_LABELS[3], 1 + (Integer)departmentMap.get(ChartConstants.DEPARTMENT_LABELS[3]));
					if(jobRecord.getResult() != null && !jobRecord.getResult().contains(BankConstants.olumsuz)){ //olumlu çek
						departmentMap.put(ChartConstants.DEPARTMENT_LABELS[4], 1 + (Integer)departmentMap.get(ChartConstants.DEPARTMENT_LABELS[4]));
					}
					if(jobRecord.getResult() != null && jobRecord.getResult().contains(BankConstants.olumsuz)){ //olumsuz çek
						departmentMap.put(ChartConstants.DEPARTMENT_LABELS[5], 1 + (Integer)departmentMap.get(ChartConstants.DEPARTMENT_LABELS[5]));
					}
				}
				break;
			case 4:
				if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.memzu)){
					departmentMap.put(ChartConstants.DEPARTMENT_LABELS[6], 1 + (Integer)departmentMap.get(ChartConstants.DEPARTMENT_LABELS[6]));
				}
				break;
			}
		}
		
		ChartSeries chartSeries = new ChartSeries();
		chartSeries.setData(departmentMap);
		return chartSeries;
	}
	
	/**
	 * 
	 * @param jobRecords
	 * @param type
	 * @return
	 */
	public static LineChartSeries MonthlyReport(List<JobRecord> jobRecords, int type){
		Map<Object, Number> monthlyMap = new TreeMap<Object, Number>(new MonthComparator());
		String label = "";
		Integer oldValue = null;
		
		for (JobRecord jobRecord : jobRecords) {
			switch (type) {
			case 1:	//Toplam
				oldValue = (Integer) monthlyMap.get(jobRecord.getMonth());
				monthlyMap.put(jobRecord.getMonth(), 1 + (oldValue==null?0:oldValue));
				break;
			case 2://Rapor
				if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.rapor)){
					oldValue = (Integer) monthlyMap.get(jobRecord.getMonth());
					monthlyMap.put(jobRecord.getMonth(), 1 + (oldValue==null?0:oldValue));
				}
				break;
			case 3://Çek
				if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.cek)){
					oldValue = (Integer) monthlyMap.get(jobRecord.getMonth());
					monthlyMap.put(jobRecord.getMonth(), 1 + (oldValue==null?0:oldValue));
				}
				break;
			case 4://Memzuç
				if(jobRecord.getType() != null && jobRecord.getType().contains(BankConstants.memzu)){
					oldValue = (Integer) monthlyMap.get(jobRecord.getMonth());
					monthlyMap.put(jobRecord.getMonth(), 1 + (oldValue==null?0:oldValue));
				}
				break;
			}
		}
		
		switch (type) {
			case 1: label="Toplam";break;
			case 2: label="Rapor Sayýsý";break;
			case 3: label="Çek Sayýsý";break;
			case 4: label="Memzuç Sayýsý";break;
		}
		
		LineChartSeries lineChartSeries = new LineChartSeries(label);
		lineChartSeries.setData(monthlyMap);
		lineChartSeries.setFill(type!=1);
		return lineChartSeries;
	}
}
