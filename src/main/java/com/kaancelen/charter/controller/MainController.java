package com.kaancelen.charter.controller;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	}
	/**
	 * run on draw chart action
	 */
	public void drawCharts(){
		if(isChartsDrow){//eger chartlar zaten cizilmisse tekrar cizmene gerek yok
			return;
		}
		System.out.println("MainController#onCompleteFileUpload");
		nakitRisk = ChartHelper.draw(consolidated.getRecords(), 1);
		limitRisk = ChartHelper.draw(consolidated.getRecords(), 2);
		termNakitRisk = ChartHelper.draw(consolidated.getRecords(), 3);
		facLeaRisk = ChartHelper.draw(consolidated.getRecords(), 4);
		gnakitRisk = ChartHelper.draw(consolidated.getRecords(), 5);
		glimitRisk = ChartHelper.draw(consolidated.getRecords(), 6);
		termGnakitRisk = ChartHelper.draw(consolidated.getRecords(), 7);
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
		}if(activeTab == 7){
			for (ChartSeries seri : termGnakitRisk.getSeries()) {
				tableData.add(seri.getData());
			}
		}
		return tableData;
	}
	
	/**
	 * reset charts
	 */
	private void resetCharts(){
		BarChartModel model = new BarChartModel();
        model.addSeries(new ChartSeries("PREVENT NULL POINTER EXCEPTION"));
         
        limitRisk = nakitRisk = facLeaRisk = glimitRisk = gnakitRisk = termGnakitRisk = termNakitRisk = model;//PREVENET NULL POINTER EXCEPTION
        isChartsDrow = false;
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
}
