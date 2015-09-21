package com.kaancelen.charter.controller;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

import com.kaancelen.charter.comparators.LabelComparator;
import com.kaancelen.charter.comparators.MonthComparator;
import com.kaancelen.charter.comparators.StringComparator;
import com.kaancelen.charter.constant.ChartConstants;
import com.kaancelen.charter.constant.FileConstants;
import com.kaancelen.charter.helpers.ChartHelper;
import com.kaancelen.charter.helpers.DocumentHelper;
import com.kaancelen.charter.helpers.FileHelper;
import com.kaancelen.charter.helpers.PDFHelper;
import com.kaancelen.charter.models.JobRecord;
import com.kaancelen.charter.models.Performance;
import com.kaancelen.charter.utils.PrimefacesUtils;

@ManagedBean(name="performanceController")
@SessionScoped
public class PerformanceController implements Serializable{

	private static final long serialVersionUID = -8608336698009131718L;
	private Performance performance;
	private BarChartModel personelChart;
	private BarChartModel departmentChart;
	private LineChartModel monthlyChart;
	private boolean isChartsDrow;
	private List<Map<Object, Number>> personelData;
	private List<Map<Object, Number>> departmentData; 
	private List<Map<Object, Number>> monthlyData;
	private String performanceHidden;
	private String departmentHidden;
	private String monthlyHidden;
	private boolean isReportReady;
	
	@PostConstruct
	public void init(){
		System.out.println("PerformanceController#init");
		this.resetCharts();
	}
	@PreDestroy
	public void destroy(){
		System.out.println("PerformanceController#destroy");
		FileHelper.removeDirectory(FileConstants.ROOT_PATH);//remove all files on exit
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
			isReportReady = false;
			//copy file to server
			if(FileHelper.copyFile(performance.getFilepath(), event.getFile().getInputstream())){
				PrimefacesUtils.showMessage(FacesMessage.SEVERITY_INFO, "Dosya başarı ile yüklendi!", performance.getFilename());
			}else{
				PrimefacesUtils.showMessage(FacesMessage.SEVERITY_ERROR, "Dosya yüklemesi başarısız!", performance.getFilename());
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
		performance.setMonths(DocumentHelper.getMonths(jobRecords));
		PrimefacesUtils.executeScript("PF('uploadPerfFile').hide()");
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
		monthlyChart = ChartHelper.drawPerformansMonthly(performance.getJobRecords());
		this.checkMonths(monthlyChart);
		this.calculateMonthsData();
	}
	/**
	 * convert b64png files to image file
	 */
	public void exportFilesToServer(){
		System.out.println("PerformanceController#exportFilesToServer");
		this.saveBase64AsImage(performanceHidden, FileConstants.PERF_CHART);
		this.saveBase64AsImage(departmentHidden, FileConstants.DEPT_CHART);
		this.saveBase64AsImage(monthlyHidden, FileConstants.MONTH_CHART);
		isReportReady = true;
	}
	/**
	 * @return
	 */
	public String[] getDepartmentColumns(){
		return ChartConstants.DEPARTMENT_LABELS;
	}
	/**
	 * create and return file content
	 * @return
	 */
	public StreamedContent getFile(){
		try {
			//Create file
			PDFHelper.createPerformanceReport(personelData, departmentData, monthlyData);
			//get stream
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
			InputStream inputStream;
			inputStream = new FileInputStream(FileConstants.PERF_REPORT_NAME);
			//return it
			return new DefaultStreamedContent(inputStream, "application/pdf", dateFormat.format(now)+"_performance.pdf");
		} catch (FileNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
			PrimefacesUtils.showMessage(FacesMessage.SEVERITY_ERROR, "Dosya indirme hatası!", "dosya oluşturulamadı!");
			return null;
		}
	}
	//PRIVATE METHODS
	/**
	 * reset charts
	 */
	private void resetCharts(){
		BarChartModel model = new BarChartModel();
        model.addSeries(new ChartSeries("PREVENT NULL POINTER EXCEPTION"));
         
        personelChart = departmentChart = model;//PREVENET NULL POINTER EXCEPTION
        monthlyChart = new LineChartModel();//PREVENET NULL POINTER EXCEPTION
        isChartsDrow = false;
        isReportReady = false;
	}
	/**
	 * Check if chartseries contains all personels
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
	 * Check if chartseries contains all months
	 * if not place 0
	 * @param model
	 */
	private void checkMonths(LineChartModel model){
		for (ChartSeries chartSeries : model.getSeries()) {
			for (String month : performance.getMonths()) {
				if(!chartSeries.getData().containsKey(month))
					chartSeries.getData().put(month, 0);
			}
		}
	}
	/**
	 * produce personelData
	 */
	private void calculatePersonelData(){
		personelData = new ArrayList<Map<Object, Number>>();
		Map<Object, Number> report = personelChart.getSeries().get(0).getData();
		Map<Object, Number> cek = personelChart.getSeries().get(1).getData();
		Map<Object, Number> memzuc = personelChart.getSeries().get(2).getData();
		Map<Object, Number> total = new TreeMap<>(new StringComparator());
		
		for (Entry<Object, Number> entry : personelChart.getSeries().get(0).getData().entrySet()){
			total.put(entry.getKey(), (Integer)report.get(entry.getKey()) + (Integer)memzuc.get(entry.getKey()) + (Integer)cek.get(entry.getKey()));
		}
		
		personelData.add(report);//report
		personelData.add(cek);//çek
		personelData.add(memzuc);//memzuç
		personelData.add(total);//total
	}
	/**
	 * produce departmentData
	 */
	private void calculateDepartmentData(){
		departmentData = new ArrayList<Map<Object, Number>>();
		Map<Object, Number> report = departmentChart.getSeries().get(0).getData();//rapor
		Map<Object, Number> cek = departmentChart.getSeries().get(1).getData();//çek
		Map<Object, Number> memzuc = departmentChart.getSeries().get(2).getData();//memzuç
		Map<Object, Number> total = departmentChart.getSeries().get(3).getData();//toplam
		Map<Object, Number> row = new TreeMap<Object, Number>(new LabelComparator());
		
		
		row.put(ChartConstants.DEPARTMENT_LABELS[0], report.get(ChartConstants.DEPARTMENT_LABELS[0]));//Rapor
		row.put(ChartConstants.DEPARTMENT_LABELS[1], report.get(ChartConstants.DEPARTMENT_LABELS[1]));//Olumlu rapor
		row.put(ChartConstants.DEPARTMENT_LABELS[2], report.get(ChartConstants.DEPARTMENT_LABELS[2]));//Olumsuz rapor
		row.put(ChartConstants.DEPARTMENT_LABELS[3], cek.get(ChartConstants.DEPARTMENT_LABELS[3]));//çek
		row.put(ChartConstants.DEPARTMENT_LABELS[4], cek.get(ChartConstants.DEPARTMENT_LABELS[4]));//Olumlu çek
		row.put(ChartConstants.DEPARTMENT_LABELS[5], cek.get(ChartConstants.DEPARTMENT_LABELS[5]));//Olumsuz çek
		row.put(ChartConstants.DEPARTMENT_LABELS[6], memzuc.get(ChartConstants.DEPARTMENT_LABELS[6]));//Memzuç
		row.put(ChartConstants.DEPARTMENT_LABELS[7], total.get(ChartConstants.DEPARTMENT_LABELS[7]));//Toplam
		
		departmentData.add(row);
	}
	/**
	 * produce monthly performance data
	 */
	private void calculateMonthsData(){
		monthlyData = new ArrayList<Map<Object,Number>>();
		for(int i=0; i<4; i++){
			monthlyData.add(monthlyChart.getSeries().get(i).getData());
		}
		//monthlyChart.getSeries().remove(3);//remove total line because i need it for only data table and i take that
	}
	/**
	 * save base64 string as a image
	 * @param base64
	 * @param path
	 */
	private void saveBase64AsImage(String base64, String path){
		if(base64.split(",").length > 1){
	        String encoded = base64.split(",")[1];
	        byte[] decoded = Base64.decodeBase64(encoded);
	        // Write to a .png file
	        try {
	            RenderedImage renderedImage = ImageIO.read(new ByteArrayInputStream(decoded));
	            ImageIO.write(renderedImage, "png", new File(path)); // use a proper path & file name here.
	        } catch (IOException e) {
	        	System.err.println(e.getMessage());
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
	public boolean isReportReady() {
		return isReportReady;
	}
	public void setReportReady(boolean isReportReady) {
		this.isReportReady = isReportReady;
	}
	public LineChartModel getMonthlyChart() {
		return monthlyChart;
	}
	public void setMonthlyChart(LineChartModel monthlyChart) {
		this.monthlyChart = monthlyChart;
	}
	public List<Map<Object, Number>> getMonthlyData() {
		return monthlyData;
	}
	public void setMonthlyData(List<Map<Object, Number>> monthlyData) {
		this.monthlyData = monthlyData;
	}
	public String getMonthlyHidden() {
		return monthlyHidden;
	}
	public void setMonthlyHidden(String monthlyHidden) {
		this.monthlyHidden = monthlyHidden;
	}
}
