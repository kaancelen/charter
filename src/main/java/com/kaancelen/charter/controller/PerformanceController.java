package com.kaancelen.charter.controller;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ComponentSystemEvent;
import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.kaancelen.charter.comparators.LabelComparator;
import com.kaancelen.charter.constant.ChartConstants;
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
	private List<Map<Object, Number>> personelData;
	private List<Map<Object, Number>> departmentData; 
	private String performanceHidden;
	private String departmentHidden;
	
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
	/**
	 * 
	 */
	public void drawCharts(){
		if(isChartsDrow){//eger chartlar zaten cizilmisse tekrar cizmene gerek yok
			return;
		}
		System.out.println("PerformanceController#drawCharts");
		personelChart = ChartHelper.drawPerformans(performance.getJobRecords(), 1);
		this.checkPersonels(personelChart);
		this.calculatePersonelData();
		departmentChart = ChartHelper.drawPerformans(performance.getJobRecords(), 2);
		this.calculateDepartmentData();
	}
	/**
	 * convert b64png files to image file
	 */
	public void exportFilesToServer(){
		System.out.println("PerformanceController#exportFilesToServer");
		System.out.println(departmentHidden);
		System.out.println(performanceHidden);
		if(departmentHidden.split(",").length > 1){
	        String encoded = departmentHidden.split(",")[1];
	        byte[] decoded = Base64.decodeBase64(encoded);
	        // Write to a .png file
	        try {
	            RenderedImage renderedImage = ImageIO.read(new ByteArrayInputStream(decoded));
	            ImageIO.write(renderedImage, "png", new File(FileConstants.ROOT_PATH + "out.png")); // use a proper path & file name here.
	        } catch (IOException e) {
	        	System.err.println(e.getMessage());
	        }
	    }
	}
	/**
	 * @return
	 */
	public String[] getDepartmentColumns(){
		return ChartConstants.DEPARTMENT_LABELS;
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
	/**
	 * produce personelData
	 */
	private void calculatePersonelData(){
		personelData = new ArrayList<Map<Object, Number>>();
		Map<Object, Number> report = personelChart.getSeries().get(0).getData();
		Map<Object, Number> memzuc = personelChart.getSeries().get(1).getData();
		Map<Object, Number> total = new HashMap<Object, Number>();
		
		for (Entry<Object, Number> entry : personelChart.getSeries().get(0).getData().entrySet()){
			total.put(entry.getKey(), (Integer)report.get(entry.getKey()) + (Integer)memzuc.get(entry.getKey()));
		}
		
		personelData.add(report);//report
		personelData.add(memzuc);//memzuç
		personelData.add(total);//total
	}
	/**
	 * produce departmentData
	 */
	private void calculateDepartmentData(){
		departmentData = new ArrayList<Map<Object, Number>>();
		Map<Object, Number> total = departmentChart.getSeries().get(0).getData();//total
		Map<Object, Number> report = departmentChart.getSeries().get(1).getData();//report
		Map<Object, Number> memzuc = departmentChart.getSeries().get(2).getData();//memzuç
		Map<Object, Number> row = new TreeMap<Object, Number>(new LabelComparator());
		
		row.put(ChartConstants.DEPARTMENT_LABELS[0], total.get(ChartConstants.DEPARTMENT_LABELS[0]));//Toplam
		row.put(ChartConstants.DEPARTMENT_LABELS[1], report.get(ChartConstants.DEPARTMENT_LABELS[1]));//Rapor
		row.put(ChartConstants.DEPARTMENT_LABELS[2], report.get(ChartConstants.DEPARTMENT_LABELS[2]));//Olumlu
		row.put(ChartConstants.DEPARTMENT_LABELS[3], report.get(ChartConstants.DEPARTMENT_LABELS[3]));//Olumsuz
		row.put(ChartConstants.DEPARTMENT_LABELS[4], memzuc.get(ChartConstants.DEPARTMENT_LABELS[4]));//Memzuç
		
		departmentData.add(row);
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
	public List<Map<Object, Number>> getPersonelData() {
		return personelData;
	}
	public void setPersonelData(List<Map<Object, Number>> personelData) {
		this.personelData = personelData;
	}
	public List<Map<Object, Number>> getDepartmentData() {
		return departmentData;
	}
	public void setDepartmentData(List<Map<Object, Number>> departmentData) {
		this.departmentData = departmentData;
	}
	public String getPerformanceHidden() {
		return performanceHidden;
	}
	public void setPerformanceHidden(String performanceHidden) {
		this.performanceHidden = performanceHidden;
	}
	public String getDepartmentHidden() {
		return departmentHidden;
	}
	public void setDepartmentHidden(String departmentHidden) {
		this.departmentHidden = departmentHidden;
	}
}
