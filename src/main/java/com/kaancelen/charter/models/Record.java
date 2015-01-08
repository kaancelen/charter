package com.kaancelen.charter.models;

import java.io.Serializable;

public class Record implements Comparable<Record>,Serializable{
	
	private String term;	//term of record
	private String memberType;
	private String memberCode;
	private String riskCode;
	private double limit;
	private double shortTermRisk;
	private double middleTermRisk;
	private double longTermRisk;
	private double tahakkuk;
	private double reeskont;
	private String finansCode;
	
	@Override
	public int compareTo(Record arg0) {
		String[] split = this.term.split("/");
		String[] oSplit = arg0.term.split("/");
		int month = Integer.parseInt(split[0]);
		int year = Integer.parseInt(split[1]);
		int oMonth = Integer.parseInt(oSplit[0]);
		int oYear = Integer.parseInt(oSplit[1]);
		
		if(year > oYear){
			return 1;
		}else if (year < oYear) {
			return -1;
		}else{
			if(month > oMonth){
				return 1;
			}else if(month < oMonth){
				return -1;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return "Record [term=" + term + ", memberType=" + memberType
				+ ", memberCode=" + memberCode + ", riskCode=" + riskCode
				+ ", limit=" + limit + ", shortTermRisk=" + shortTermRisk
				+ ", middleTermRisk=" + middleTermRisk + ", longTermRisk="
				+ longTermRisk + ", tahakkuk=" + tahakkuk + ", reeskont="
				+ reeskont + ", finansCode=" + finansCode + "]";
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getRiskCode() {
		return riskCode;
	}

	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}

	public double getLimit() {
		return limit;
	}

	public void setLimit(double limit) {
		this.limit = limit;
	}

	public double getShortTermRisk() {
		return shortTermRisk;
	}

	public void setShortTermRisk(double shortTermRisk) {
		this.shortTermRisk = shortTermRisk;
	}

	public double getMiddleTermRisk() {
		return middleTermRisk;
	}

	public void setMiddleTermRisk(double middleTermRisk) {
		this.middleTermRisk = middleTermRisk;
	}

	public double getLongTermRisk() {
		return longTermRisk;
	}

	public void setLongTermRisk(double longTermRisk) {
		this.longTermRisk = longTermRisk;
	}

	public double getTahakkuk() {
		return tahakkuk;
	}

	public void setTahakkuk(double tahakkuk) {
		this.tahakkuk = tahakkuk;
	}

	public double getReeskont() {
		return reeskont;
	}

	public void setReeskont(double reeskont) {
		this.reeskont = reeskont;
	}

	public String getFinansCode() {
		return finansCode;
	}

	public void setFinansCode(String finansCode) {
		this.finansCode = finansCode;
	}
}
