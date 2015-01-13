package com.kaancelen.charter.controller;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ComponentSystemEvent;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.kaancelen.charter.constant.FileConstants;
import com.kaancelen.charter.helpers.ChartHelper;
import com.kaancelen.charter.helpers.DocumentHelper;
import com.kaancelen.charter.helpers.FileHelper;
import com.kaancelen.charter.models.JobRecord;
import com.kaancelen.charter.models.Performance;
import com.kaancelen.charter.utils.PrimefacesUtils;

@ManagedBean(name="performanceController")
@SessionScoped
public class PerformanceController implements Serializable{

	private static final long serialVersionUID = -8608336698009131718L;
	private Performance performance;
	private int activeTab;
	private BarChartModel personelChart;
	private BarChartModel departmentChart;
	private boolean isChartsDrow;
	
	@PostConstruct
	public void init(){
		System.out.println("PerformanceController#init");
		this.resetCharts();
	}
	@PreDestroy
	public void destroy(){
		System.out.println("PerformanceController#destroy");
	}
	/**
	 * @param componentSystemEvent
	 */
	public void onPageLoad(ComponentSystemEvent componentSystemEvent){
		if(PrimefacesUtils.isAjaxRequest())
			return;
		System.out.println("PerformanceController#onPageLoad");
	}
	/**
	 * run on file upload event
	 * @param event
	 */
	public void onUploadFile(FileUploadEvent event){
		try {
			System.out.println("PerformanceController#onUploadFile");
			performance = new Performance();
			performance.setFilename(new String(event.getFile().getFileName().getBytes(Charset.defaultCharset()), "UTF-8"));
			performance.setFilepath(FileConstants.ROOT_PATH + performance.getFilename());
			performance.setUploadedDate(new Date());
			isChartsDrow = false;
			//copy file to server
			if(FileHelper.copyFile(performance.getFilepath(), event.getFile().getInputstream())){
				PrimefacesUtils.showMessage(FacesMessage.SEVERITY_INFO, "Dosya baþarý ile yüklendi!", performance.getFilename());
			}else{
				PrimefacesUtils.showMessage(FacesMessage.SEVERITY_ERROR, "Dosya yüklemesi baþarýsýz!", performance.getFilename());
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
		System.out.println("PerformanceController#onCompleteFileUpload");
		List<JobRecord> jobRecords = DocumentHelper.getDatasFromExcelPerformanceFile(performance.getFilepath());
		performance.setJobRecords(jobRecords);
		performance.setPersonels(DocumentHelper.getPersonels(jobRecords));
		PrimefacesUtils.executeScript("PF('uploadPerfFile').hide()");
	}
	/**
	 * run on tabchange event
	 */
	public void onTabChange(TabChangeEvent event){
		System.out.println("PerformanceController#onTabChange "+event.getTab().getTitle());
		activeTab = Integer.parseInt(event.getTab().getTitle());
	}
	public void drawCharts(){
		if(isChartsDrow){//eger chartlar zaten cizilmisse tekrar cizmene gerek yok
			return;
		}
		System.out.println("PerformanceController#drawCharts");
		personelChart = ChartHelper.drawPerformans(performance.getJobRecords(), 1);
		this.checkPersonels(personelChart);
		departmentChart = ChartHelper.drawPerformans(performance.getJobRecords(), 2);
	}
	//PRIVATE METHODS
	/**
	 * reset charts
	 */
	private void resetCharts(){
		BarChartModel model = new BarChartModel();
        model.addSeries(new ChartSeries("PREVENT NULL POINTER EXCEPTION"));
         
        personelChart = departmentChart = model;//PREVENET NULL POINTER EXCEPTION
        isChartsDrow = false;
	}
	/**
	 * Check if chartseries contains all terms
	 * if not place 0
	 * @param model
	 */
	private void checkPersonels(BarChartModel model){
		for (ChartSeries chartSeries : model.getSeries()) {
			for (String personel : performance.getPersonels()) {
				if(!chartSeries.getData().containsKey(personel))
					chartSeries.getData().put(personel, 0);
			}
		}
	}
	
	//GETTERS AND SETTERS
	public Performance getPerformance() {
		return performance;
	}
	public void setPerformance(Performance performance) {
		this.performance = performance;
	}
	public int getActiveTab() {
		return activeTab;
	}
	public void setActiveTab(int activeTab) {
		this.activeTab = activeTab;
	}
	public BarChartModel getPersonelChart() {
		return personelChart;
	}
	public void setPersonelChart(BarChartModel personelChart) {
		this.personelChart = personelChart;
	}
	public BarChartModel getDepartmentChart() {
		return departmentChart;
	}
	public void setDepartmentChart(BarChartModel departmentChart) {
		this.departmentChart = departmentChart;
	}
}
