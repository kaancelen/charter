package com.kaancelen.charter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Consolidated implements Serializable{
	
	private static final long serialVersionUID = -4183756345179436115L;
	private String firmName;
	private String filename;
	private String filepath;
	private Date uploadedDate;
	private List<Record> records;
	private List<String> terms;
	
	public Consolidated() {
		super();
		terms = new ArrayList<String>();
	}

	@Override
	public String toString() {
		return "Consolidated [firmName=" + firmName + ", filename=" + filename
				+ ", filepath=" + filepath + ", uploadedDate=" + uploadedDate
				+ ", records=" + records + ", terms=" + terms + "]";
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public Date getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(Date uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	public List<String> getTerms() {
		return terms;
	}

	public void setTerms(List<String> terms) {
		this.terms = terms;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}
}
