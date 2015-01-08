package com.kaancelen.charter.controller;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ComponentSystemEvent;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.kaancelen.charter.constant.FileConstants;
import com.kaancelen.charter.helpers.ChartHelper;
import com.kaancelen.charter.helpers.DocumentHelper;
import com.kaancelen.charter.helpers.FileHelper;
import com.kaancelen.charter.models.Consolidated;
import com.kaancelen.charter.models.Record;
import com.kaancelen.charter.utils.PrimefacesUtils;

@ManagedBean(name="mainController")
@SessionScoped
public class MainController implements Serializable{

	private static final long serialVersionUID = 3704152148206888251L;
	
	private Consolidated consolidated;
	private BarChartModel nakitRisk;
	private BarChartModel limitRisk;
	
	@PostConstruct
	public void init(){
		System.out.println("MainController#init");
		this.resetCharts();
	}
	@PreDestroy
	public void destroy(){
		System.out.println("MainController#destroy");
	}
	/**
	 * @param componentSystemEvent
	 */
	public void onPageLoad(ComponentSystemEvent componentSystemEvent){
		if(PrimefacesUtils.isAjaxRequest())
			return;
		System.out.println("MainController#onPageLoad");
	}
	/**
	 * run on file upload event
	 * @param event
	 */
	public void onUploadFile(FileUploadEvent event){
		try {
			System.out.println("MainController#onUploadFile");
			consolidated = new Consolidated();
			consolidated.setFilename(new String(event.getFile().getFileName().getBytes(Charset.defaultCharset()), "UTF-8"));
			consolidated.setFilepath(FileConstants.ROOT_PATH + consolidated.getFilename());
			consolidated.setUploadedDate(new Date());
			//copy file to server
			if(FileHelper.copyFile(consolidated.getFilepath(), event.getFile().getInputstream())){
				PrimefacesUtils.showMessage(FacesMessage.SEVERITY_INFO, "Dosya baþarý ile yüklendi!", consolidated.getFilename());
			}else{
				PrimefacesUtils.showMessage(FacesMessage.SEVERITY_ERROR, "Dosya yüklemesi baþarýsýz!", consolidated.getFilename());
			}
		} catch (UnsupportedEncodingException e) {
			PrimefacesUtils.showMessage(FacesMessage.SEVERITY_ERROR, "Dosya ismi çevrilemedi!", "");
			System.err.println("MainController : " + e.getLocalizedMessage());
		} catch (IOException e) {
			PrimefacesUtils.showMessage(FacesMessage.SEVERITY_ERROR, "Dosya yüklenemedi!", "");
			System.err.println("MainController : " + e.getLocalizedMessage());
		}
		
	}
	/**
	 * run on submit file upload dialog form
	 */
	public void onCompleteFileUpload(){
		System.out.println("MainController#onCompleteFileUpload");
		List<Record> records = DocumentHelper.getDatasFromExcelFile(consolidated.getFilepath());
		Collections.sort(records);//sort records
		consolidated.setRecords(records);
	}
	/**
	 * run on draw chart action
	 */
	public void drawCharts(){
		nakitRisk = ChartHelper.drawNakitRiskChart(consolidated.getRecords());
		limitRisk = ChartHelper.drawLimitRiskChart(consolidated.getRecords());
	}
	/**
	 * reset charts
	 */
	private void resetCharts(){
		BarChartModel model = new BarChartModel();
		 
        ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);
 
        model.addSeries(boys);
         
        limitRisk = nakitRisk = model;//PREVENET NULL POINTER EXCEPTION
	}
	
	//GETTERS AND SETTERS
	public Consolidated getConsolidated() {
		return consolidated;
	}
	public void setConsolidated(Consolidated consolidated) {
		this.consolidated = consolidated;
	}
	public BarChartModel getNakitRisk() {
		return nakitRisk;
	}
	public void setNakitRisk(BarChartModel nakitRisk) {
		this.nakitRisk = nakitRisk;
	}
	public BarChartModel getLimitRisk() {
		return limitRisk;
	}
	public void setLimitRisk(BarChartModel limitRisk) {
		this.limitRisk = limitRisk;
	}
}
