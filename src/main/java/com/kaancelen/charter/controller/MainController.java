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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.kaancelen.charter.constant.FileConstants;
import com.kaancelen.charter.helpers.ChartHelper;
import com.kaancelen.charter.helpers.ChartSeriesCalculator;
import com.kaancelen.charter.helpers.DocumentHelper;
import com.kaancelen.charter.helpers.FileHelper;
import com.kaancelen.charter.helpers.PDFHelper;
import com.kaancelen.charter.models.Consolidated;
import com.kaancelen.charter.models.Record;
import com.kaancelen.charter.utils.PrimefacesUtils;

@ManagedBean(name="mainController")
@SessionScoped
public class MainController implements Serializable{

	private static final long serialVersionUID = 3704152148206888251L;
	
	private Consolidated consolidated;
	private boolean isChartsDrow;//chartlar zaten cizilmis mi? Dosya degismis mi ?
	private int activeTab;
	private List<String> terms = new ArrayList<String>();//columns
	private BarChartModel nakitRisk;//charts
	private BarChartModel limitRisk;
	private BarChartModel termNakitRisk;
	private BarChartModel facLeaRisk;
	private BarChartModel gnakitRisk;
	private BarChartModel glimitRisk;
	private BarChartModel termGnakitRisk;
	private String[] hiddenInputs;
	private boolean isReportReady;
	
	@PostConstruct
	public void init(){
		System.out.println("MainController#init");
		consolidated = new Consolidated();//PREVENT NULL POINTER EXCEPTION
		activeTab = 1;
		this.resetCharts();
	}
	@PreDestroy
	public void destroy(){
		System.out.println("MainController#destroy");
		FileHelper.removeDirectory(FileConstants.ROOT_PATH);//remove all files on exit
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
				isChartsDrow = false;//Yeni dosya yuklendi
				isReportReady = false;
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
		terms = DocumentHelper.getTerms(records);
		Collections.sort(records);//sort records
		consolidated.setRecords(records);
		isChartsDrow = false;//yeni dosya yuklendi
		PrimefacesUtils.executeScript("PF('uploadFile').hide()");
	}
	/**
	 * run on draw chart action
	 */
	public void drawCharts(){
		if(isChartsDrow){//eger chartlar zaten cizilmisse tekrar cizmene gerek yok
			return;
		}
		System.out.println("MainController#drawCharts");
		nakitRisk = ChartHelper.draw(consolidated.getRecords(), 1, consolidated.getFirmName());
		limitRisk = ChartHelper.draw(consolidated.getRecords(), 2, consolidated.getFirmName());
		termNakitRisk = ChartHelper.draw(consolidated.getRecords(), 3, consolidated.getFirmName());
		facLeaRisk = ChartHelper.draw(consolidated.getRecords(), 4, consolidated.getFirmName());
		gnakitRisk = ChartHelper.draw(consolidated.getRecords(), 5, consolidated.getFirmName());
		glimitRisk = ChartHelper.draw(consolidated.getRecords(), 6, consolidated.getFirmName());
		termGnakitRisk = ChartHelper.draw(consolidated.getRecords(), 7, consolidated.getFirmName());
		isChartsDrow = true;
		//Check terms
		checkTerms(nakitRisk);
		checkTerms(limitRisk);
		checkTerms(termNakitRisk);
		checkTerms(facLeaRisk);
		checkTerms(gnakitRisk);
		checkTerms(glimitRisk);
		checkTerms(termGnakitRisk);
	}
	/**
	 * run on tabchange event
	 */
	public void onTabChange(TabChangeEvent event){
		System.out.println("MainController#onTabChange "+event.getTab().getTitle());
		activeTab = Integer.parseInt(event.getTab().getTitle());
	}
	/**
	 * @return table data according to active tab
	 */
	public List<Map<Object, Number>> getTableData(){
		List<Map<Object, Number>> tableData = new ArrayList<Map<Object, Number>>();
		if(activeTab == 1){
			for (ChartSeries seri : nakitRisk.getSeries()) {
				tableData.add(seri.getData());
			}
		}else if(activeTab == 2){
			for (ChartSeries seri : limitRisk.getSeries()) {
				tableData.add(seri.getData());
			}
			tableData.add(ChartSeriesCalculator.usePercantage(tableData.get(1), tableData.get(0)));
		}if(activeTab == 3){
			for (ChartSeries seri : termNakitRisk.getSeries()) {
				tableData.add(seri.getData());
			}
		}else if(activeTab == 4){
			for (ChartSeries seri : facLeaRisk.getSeries()) {
				tableData.add(seri.getData());
			}
		}if(activeTab == 5){
			for (ChartSeries seri : gnakitRisk.getSeries()) {
				tableData.add(seri.getData());
			}
		}else if(activeTab == 6){
			for (ChartSeries seri : glimitRisk.getSeries()) {
				tableData.add(seri.getData());
			}
			tableData.add(ChartSeriesCalculator.usePercantage(tableData.get(1), tableData.get(0)));
		}if(activeTab == 7){
			for (ChartSeries seri : termGnakitRisk.getSeries()) {
				tableData.add(seri.getData());
			}
		}
		return tableData;
	}
	/**
	 * create and return file content
	 * @return
	 */
	public StreamedContent getFile(){
		try {
			//get datas
			List<List<Map<Object, Number>>> datas = new ArrayList<List<Map<Object, Number>>>();
			activeTab = 1;
			datas.add(getTableData());
			activeTab = 2;
			datas.add(getTableData());
			activeTab = 3;
			datas.add(getTableData());
			activeTab = 4;
			datas.add(getTableData());
			activeTab = 5;
			datas.add(getTableData());
			activeTab = 6;
			datas.add(getTableData());
			activeTab = 7;
			datas.add(getTableData());
			activeTab = 8;
			//Create file
			PDFHelper.createMemzucReport(datas);
			//get stream
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
			InputStream inputStream;
			inputStream = new FileInputStream(FileConstants.MEMZUC_REPORT_NAME);
			//return it
			return new DefaultStreamedContent(inputStream, "application/pdf", dateFormat.format(now)+"_memzuc.pdf");
		} catch (FileNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
			PrimefacesUtils.showMessage(FacesMessage.SEVERITY_ERROR, "Dosya indirme hatasý!", "dosya oluþturulamadý!");
			return null;
		}
	}
	/**
	 * convert b64png files to image file
	 */
	public void exportFilesToServer(){
		System.out.println("MainController#exportFilesToServer");
		this.saveBase64AsImage(hiddenInputs[0], FileConstants.CHART_0);
		this.saveBase64AsImage(hiddenInputs[1], FileConstants.CHART_1);
		this.saveBase64AsImage(hiddenInputs[2], FileConstants.CHART_2);
		this.saveBase64AsImage(hiddenInputs[3], FileConstants.CHART_3);
		this.saveBase64AsImage(hiddenInputs[4], FileConstants.CHART_4);
		this.saveBase64AsImage(hiddenInputs[5], FileConstants.CHART_5);
		this.saveBase64AsImage(hiddenInputs[6], FileConstants.CHART_6);
		isReportReady = true;
	}
	
	/**
	 * reset charts
	 */
	private void resetCharts(){
		BarChartModel model = new BarChartModel();
        model.addSeries(new ChartSeries("PREVENT NULL POINTER EXCEPTION"));
         
        limitRisk = nakitRisk = facLeaRisk = glimitRisk = gnakitRisk = termGnakitRisk = termNakitRisk = model;//PREVENET NULL POINTER EXCEPTION
        isChartsDrow = false;
        isReportReady = false;
        hiddenInputs = new String[7];
	}
	/**
	 * Check if chartseries contains all terms
	 * if not place 0
	 * @param model
	 */
	private void checkTerms(BarChartModel model){
		for (ChartSeries chartSeries : model.getSeries()) {
			for (String term : terms) {
				if(!chartSeries.getData().containsKey(term))
					chartSeries.getData().put(term, 0);
			}
		}
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
	public BarChartModel getTermNakitRisk() {
		return termNakitRisk;
	}
	public void setTermNakitRisk(BarChartModel termNakitRisk) {
		this.termNakitRisk = termNakitRisk;
	}
	public BarChartModel getFacLeaRisk() {
		return facLeaRisk;
	}
	public void setFacLeaRisk(BarChartModel facLeaRisk) {
		this.facLeaRisk = facLeaRisk;
	}
	public BarChartModel getGnakitRisk() {
		return gnakitRisk;
	}
	public void setGnakitRisk(BarChartModel gnakitRisk) {
		this.gnakitRisk = gnakitRisk;
	}
	public BarChartModel getGlimitRisk() {
		return glimitRisk;
	}
	public void setGlimitRisk(BarChartModel glimitRisk) {
		this.glimitRisk = glimitRisk;
	}
	public BarChartModel getTermGnakitRisk() {
		return termGnakitRisk;
	}
	public void setTermGnakitRisk(BarChartModel termGnakitRisk) {
		this.termGnakitRisk = termGnakitRisk;
	}
	public int getActiveTab() {
		return activeTab;
	}
	public void setActiveTab(int activeTab) {
		this.activeTab = activeTab;
	}
	public List<String> getTerms() {
		return terms;
	}
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	public String[] getHiddenInputs() {
		return hiddenInputs;
	}
	public void setHiddenInputs(String[] hiddenInputs) {
		this.hiddenInputs = hiddenInputs;
	}
	public boolean isReportReady() {
		return isReportReady;
	}
	public void setReportReady(boolean isReportReady) {
		this.isReportReady = isReportReady;
	}
}
