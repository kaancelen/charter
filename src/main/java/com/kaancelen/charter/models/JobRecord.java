package com.kaancelen.charter.models;

import java.io.Serializable;
import java.util.Date;

public class JobRecord implements Serializable{

	private static final long serialVersionUID = -1322933813592733102L;
	private String personel;//important
	private String firm;
	private String group;
	private double offerLimit;
	private String segment;
	private String branch;
	private String requester;
	private Date requestDate;
	private Date completeDate;
	private String month;
	private String result;//important
	private int dayDiff;
	private String offerGrounds;
	private String desc;//important
	
	@Override
	public String toString() {
		return "JobRecord [personel=" + personel + ", firm=" + firm
				+ ", group=" + group + ", offerLimit=" + offerLimit
				+ ", segment=" + segment + ", branch=" + branch
				+ ", requester=" + requester + ", requestDate=" + requestDate
				+ ", completeDate=" + completeDate + ", month=" + month
				+ ", result=" + result + ", dayDiff=" + dayDiff
				+ ", offerGrounds=" + offerGrounds + ", desc=" + desc + "]";
	}
	public String getPersonel() {
		return personel;
	}
	public void setPersonel(String personel) {
		this.personel = personel;
	}
	public String getFirm() {
		return firm;
	}
	public void setFirm(String firm) {
		this.firm = firm;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public double getOfferLimit() {
		return offerLimit;
	}
	public void setOfferLimit(double offerLimit) {
		this.offerLimit = offerLimit;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getRequester() {
		return requester;
	}
	public void setRequester(String requester) {
		this.requester = requester;
	}
	public Date getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
	public Date getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getDayDiff() {
		return dayDiff;
	}
	public void setDayDiff(int dayDiff) {
		this.dayDiff = dayDiff;
	}
	public String getOfferGrounds() {
		return offerGrounds;
	}
	public void setOfferGrounds(String offerGrounds) {
		this.offerGrounds = offerGrounds;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
