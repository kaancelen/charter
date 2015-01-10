package com.kaancelen.charter.helpers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.kaancelen.charter.comparators.TermComparator;
import com.kaancelen.charter.constant.BankConstants;
import com.kaancelen.charter.models.Record;

public class ChartHelper {
	
	/**
	 * return bar chart model due to type
	 * @param type, 1=>Nakit risk degisimi, 2=>Nakit/Limit risk degisimi, 3=>nakit kisa,orta,uzun vade risk degisimi
	 * 			4=>faktoring ve leasing degisimi, 5=>Gayrinakit risk degisimi, 6=>Gayrinakit limit/risk degisimi
	 * 			7=>Gayrinakit kisa,orta,uzun vade risk degisimi
	 * @return
	 */
	public static BarChartModel draw(List<Record> records, int type){
		BarChartModel barChartModel = new BarChartModel();
		barChartModel.setLegendPosition("ne");
		String title = null;
		
		switch (type) {
		case 1:
			title = "MEMZUÇ DÖNEMLERÝNE GÖRE NAKÝT RÝSK DEÐÝÞÝMÝ"; 
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitRisk(records, true));
			barChartModel.addSeries(ChartSeriesCalculator.sekerbankNakitRisk(records, true));
			break;
		case 2:
			title = "TCMB NAKÝT LÝMÝT/RÝSK DEÐÝÞÝMÝ";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitLimit(records, true));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitRisk(records, true));
			break;
		case 3:
			title = "MEMZUÇ DÖNEMLERÝNE GÖRE NAKÝT KISA,ORTA,UZUN VADE RÝSK DEÐÝÞÝMÝ";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbShortTermNakitRisk(records, true));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbMiddleTermNakitRisk(records, true));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbLongTermNakitRisk(records, true));
			break;
		case 4:
			title = "FAKTORÝNG VE LEASÝNG RÝSK DEÐÝÞÝMÝ ";
			barChartModel.addSeries(ChartSeriesCalculator.facNakitRisk(records));
			barChartModel.addSeries(ChartSeriesCalculator.leaNakitRisk(records));
			break;
		case 5:
			title = "MEMZUÇ DÖNEMLERÝNE GÖRE G.NAKÝT RÝSK DEÐÝÞÝMÝ ";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitRisk(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.sekerbankNakitRisk(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNTYTM(records));
			break;
		case 6:
			title = "TCMB G.NAKÝT LÝMÝT/RÝSK DEÐÝÞÝMÝ";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitLimit(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitRisk(records, false));
			break;
		case 7:
			title = "MEMZUÇ DÖNEMLERÝNE GÖRE G.NAKÝT KISA,ORTA,UZUN VADE RÝSK DEÐÝÞÝMÝ";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbShortTermNakitRisk(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbMiddleTermNakitRisk(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbLongTermNakitRisk(records, false));
			break;
		
		}
		
		barChartModel.setTitle(title);
		return barChartModel;
	}
	
	public static BarChartModel drawNakitRiskChart(List<Record> records) {
		Map<Object, Number> tcmbMap = new TreeMap<Object, Number>(new TermComparator());
		Map<Object, Number> sekerbankMap = new TreeMap<Object, Number>(new TermComparator());
		
		for (Record record : records) {//look for all records
			if(StringHelper.inArray(BankConstants.nakitSeries, record.getRiskCode())){// if it is nakit record
				Double termTotal = record.getShortTermRisk() + record.getMiddleTermRisk() + record.getLongTermRisk()
							+ record.getTahakkuk() + record.getReeskont();
				Double oldValue = (Double)tcmbMap.get(record.getTerm());
				tcmbMap.put(record.getTerm(), termTotal + (oldValue==null?0:oldValue));//add this records total value to map
				if(record.getMemberCode().contains(BankConstants.sekerbankCode)){//if it is also sekerbank value
					Double oldValue2 = (Double)sekerbankMap.get(record.getTerm());
					sekerbankMap.put(record.getTerm(), termTotal + (oldValue2==null?0:oldValue2));//add this 
				}
			}
		}
		
		ChartSeries tcmbNakitRisk = new ChartSeries("TCMB Nakit Risk");
		tcmbNakitRisk.setData(tcmbMap);
		ChartSeries sekerbankNakitRisk = new ChartSeries("Þekerbank Nakit Risk");
		sekerbankNakitRisk.setData(sekerbankMap);
		BarChartModel barChartModel = new BarChartModel();
		barChartModel.addSeries(tcmbNakitRisk);
		barChartModel.addSeries(sekerbankNakitRisk);
		barChartModel.setTitle("Memzuç dönemlerine göre nakit-risk deðiþimi");
		barChartModel.setLegendPosition("ne");
		return barChartModel;
	}
	
	public static BarChartModel drawLimitRiskChart(List<Record> records){
		Map<Object, Number> limit = new TreeMap<Object, Number>(new TermComparator());
		Map<Object, Number> risk = new TreeMap<Object, Number>(new TermComparator());
		
		for (Record record : records) {//look for all records
			if(StringHelper.inArray(BankConstants.nakitSeries, record.getRiskCode())){// if it is nakit record
				Double totalLimit = record.getLimit();
				Double totalRisk = record.getShortTermRisk() + record.getMiddleTermRisk() + record.getLongTermRisk()
						+ record.getTahakkuk() + record.getReeskont();
				
				Double oldLimit = (Double) limit.get(record.getTerm());
				Double oldRisk = (Double) risk.get(record.getTerm());
				
				limit.put(record.getTerm(), totalLimit + (oldLimit==null?0:oldLimit));
				risk.put(record.getTerm(), totalRisk + (oldRisk==null?0:oldRisk));
			}
		}
		
		ChartSeries limitSeri = new ChartSeries("TCMB Nakit Limit");
		limitSeri.setData(limit);
		ChartSeries riskSeri = new ChartSeries("TCMB Nakit Risk");
		riskSeri.setData(risk);
		BarChartModel barChartModel = new BarChartModel();
		barChartModel.addSeries(limitSeri);
		barChartModel.addSeries(riskSeri);
		barChartModel.setTitle("TCMB Nakit Limit/Risk Deðiþimi");
		barChartModel.setLegendPosition("ne");
		return barChartModel;
	}

	

}
