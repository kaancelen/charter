package com.kaancelen.charter.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Performance implements Serializable{

	private static final long serialVersionUID = -3503100992795941956L;
	private String filename;
	private String filepath;
	private Date uploadedDate;
	private List<JobRecord > jobRecords;
	private List<String> personels;
	
	public Performance(){
		super();
	}
	
	@Override
	public String toString() {
		return "Performance [filename=" + filename + ", filepath=" + filepath
				+ ", uploadedDate=" + uploadedDate + ", jobRecords="
				+ jobRecords + ", personels=" + personels + "]";
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
	public List<JobRecord> getJobRecords() {
		return jobRecords;
	}
	public void setJobRecords(List<JobRecord> jobRecords) {
		this.jobRecords = jobRecords;
	}
	public List<String> getPersonels() {
		return personels;
	}
	public void setPersonels(List<String> personels) {
		this.personels = personels;
	}
}
