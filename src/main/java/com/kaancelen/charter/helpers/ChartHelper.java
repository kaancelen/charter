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
