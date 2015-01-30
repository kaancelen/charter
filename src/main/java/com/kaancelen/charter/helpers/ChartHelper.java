package com.kaancelen.charter.helpers;

import java.util.List;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.kaancelen.charter.models.JobRecord;
import com.kaancelen.charter.models.Record;

public class ChartHelper {
	
	/**
	 * return bar chart model due to type
	 * @param type, 1=>Nakit risk degisimi, 2=>Nakit/Limit risk degisimi, 3=>nakit kisa,orta,uzun vade risk degisimi
	 * 			4=>faktoring ve leasing degisimi, 5=>Gayrinakit risk degisimi, 6=>Gayrinakit limit/risk degisimi
	 * 			7=>Gayrinakit kisa,orta,uzun vade risk degisimi
	 * @return
	 */
	public static BarChartModel draw(List<Record> records, int type, String firmTitle){
		BarChartModel barChartModel = new BarChartModel();
		barChartModel.setLegendPosition("ne");
		String title = null;
		
		switch (type) {
		case 1:
			title = firmTitle+" MEMZU� D�NEMLER�NE G�RE NAK�T R�SK DE����M�"; 
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitRisk(records, true));
			barChartModel.addSeries(ChartSeriesCalculator.sekerbankNakitRisk(records, true));
			break;
		case 2:
			title = firmTitle+" TCMB NAK�T L�M�T/R�SK DE����M�";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitLimit(records, true));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitRisk(records, true));
			break;
		case 3:
			title = firmTitle+" MEMZU� D�NEMLER�NE G�RE NAK�T KISA,ORTA,UZUN VADE R�SK DE����M�";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbShortTermNakitRisk(records, true));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbMiddleTermNakitRisk(records, true));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbLongTermNakitRisk(records, true));
			break;
		case 4:
			title = firmTitle+" FAKTOR�NG VE LEAS�NG R�SK DE����M� ";
			barChartModel.addSeries(ChartSeriesCalculator.facNakitRisk(records));
			barChartModel.addSeries(ChartSeriesCalculator.leaNakitRisk(records));
			break;
		case 5:
			title = firmTitle+" MEMZU� D�NEMLER�NE G�RE G.NAK�T R�SK DE����M� ";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitRisk(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.sekerbankNakitRisk(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNTYTM(records));
			break;
		case 6:
			title = firmTitle+" TCMB G.NAK�T L�M�T/R�SK DE����M�";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitLimit(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbNakitRisk(records, false));
			break;
		case 7:
			title = firmTitle+" MEMZU� D�NEMLER�NE G�RE G.NAK�T KISA,ORTA,UZUN VADE R�SK DE����M�";
			barChartModel.addSeries(ChartSeriesCalculator.tcmbShortTermNakitRisk(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbMiddleTermNakitRisk(records, false));
			barChartModel.addSeries(ChartSeriesCalculator.tcmbLongTermNakitRisk(records, false));
			break;
		
		}
		
		barChartModel.setTitle(title);
		return barChartModel;
	}

	public static BarChartModel drawPerformans(List<JobRecord> jobRecords, int type) {
		BarChartModel barChartModel = new BarChartModel();
		String title = null;
		
		switch (type) {
		case 1:
			title = "�ALI�AN PERFORMANS";
			barChartModel.setLegendPosition("ne");
			barChartModel.addSeries(ChartSeriesCalculator.PersonelReport(jobRecords));
			barChartModel.addSeries(ChartSeriesCalculator.PersonelCek(jobRecords));
			barChartModel.addSeries(ChartSeriesCalculator.PersonelMemzuc(jobRecords));
			break;
		case 2:
			title = "B�L�M PERFORMANS";
			barChartModel.setStacked(true);
			barChartModel.addSeries(ChartSeriesCalculator.DepartmentReport(jobRecords, 2));//Rapor
			barChartModel.addSeries(ChartSeriesCalculator.DepartmentReport(jobRecords, 3));//�ek
			barChartModel.addSeries(ChartSeriesCalculator.DepartmentReport(jobRecords, 4));//Memzu�
			barChartModel.addSeries(ChartSeriesCalculator.DepartmentReport(jobRecords, 1));//Toplam
			break;
		}
		
		barChartModel.setTitle(title);
		return barChartModel;
	}

}
