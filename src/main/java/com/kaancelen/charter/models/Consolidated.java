package com.kaancelen.charter.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Consolidated implements Serializable{
	
	private String filename;
	private String filepath;
	private Date uploadedDate;
	private List<Record> records;
	
	@Override
	public String toString() {
		return "Consolidated [filename=" + filename + ", filepath=" + filepath
				+ ", uploadedDate=" + uploadedDate
				+ ", records=" + records + "]";
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
}
