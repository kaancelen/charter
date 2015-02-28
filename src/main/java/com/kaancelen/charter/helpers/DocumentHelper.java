package com.kaancelen.charter.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kaancelen.charter.comparators.MonthComparator;
import com.kaancelen.charter.comparators.StringComparator;
import com.kaancelen.charter.comparators.TermComparator;
import com.kaancelen.charter.models.JobRecord;
import com.kaancelen.charter.models.Record;
import com.kaancelen.charter.utils.StringUtil;

public class DocumentHelper {
	
	/**
	 * get datas from excel file
	 * @param filepath input filename
	 * @param termList output, file uniq terms
	 * @return
	 */
	public static List<Record> getDatasFromExcelFile(String filepath){
		List<Record> records = new ArrayList<Record>();
		try {
			FileInputStream inputStream = new FileInputStream(new File(filepath));
			String mimeType = FileHelper.mimeType(filepath);
			Workbook workbook = null;
			Sheet sheet = null;
			if(mimeType.compareTo(".xls") == 0){
				workbook = new HSSFWorkbook(inputStream);
			}else{
				workbook = new XSSFWorkbook(inputStream);
			}
			sheet = workbook.getSheetAt(0);
			//For each row
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();//skip headers
			while(rowIterator.hasNext()){
				Row row = rowIterator.next();
				
				//For each cell
				Record record = new Record();
				int cellIndex = 0;
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext()){
					Cell cell = cellIterator.next();
					String value = null;
					if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
						value = cell.getNumericCellValue()+"";
					else
						value = cell.getStringCellValue().replace(".", "");
					try{
						switch (cellIndex) {
							case 0: record.setTerm(value);break;
							case 1: record.setMemberType(value);break;
							case 2: record.setMemberCode(value);break;
							case 3: record.setRiskCode(value.substring(0, 3));break;
							case 4: record.setLimit(Double.parseDouble(value));break;
							case 5: record.setShortTermRisk(Double.parseDouble(value));break;
							case 6: record.setMiddleTermRisk(Double.parseDouble(value));break;
							case 7: record.setLongTermRisk(Double.parseDouble(value));break;
							case 8: record.setTahakkuk(Double.parseDouble(value));break;
							case 9: record.setReeskont(Double.parseDouble(value));break;
							case 10: record.setFinansCode(value);break;
						}
					}catch(IllegalStateException e){//if catch exception just keep going
						System.err.println(e.getLocalizedMessage());
					}
					cellIndex++;
				}
				//add record to list
				records.add(record);
			}
		} catch (FileNotFoundException e) {
			System.err.println("DocumentHelper : " + e.getLocalizedMessage());
			return null;
		} catch (IOException e) {
			System.err.println("DocumentHelper : " + e.getLocalizedMessage());
			return null;
		}
		
		return records;
	}
	/**
	 * return uniq terms
	 * @param records
	 * @return
	 */
	public static List<String> getTerms(List<Record> records) {
		Set<String> termSet = new TreeSet<>(new TermComparator());
		for (Record record : records) {
			termSet.add(record.getTerm());
		}
		return new ArrayList<String>(termSet);
	}
	
	/**
	 * @param filepath
	 * @return
	 */
	public static List<JobRecord> getDatasFromExcelPerformanceFile(String filepath){
		List<JobRecord> records = new ArrayList<JobRecord>();
		try {
			FileInputStream inputStream = new FileInputStream(new File(filepath));
			String mimeType = FileHelper.mimeType(filepath);
			Workbook workbook = null;
			Sheet sheet = null;
			if(mimeType.compareTo(".xls") == 0){
				workbook = new HSSFWorkbook(inputStream);
			}else{
				workbook = new XSSFWorkbook(inputStream);
			}
			sheet = workbook.getSheetAt(0);
			//For each row
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();//skip headers
			while(rowIterator.hasNext()){
				Row row = rowIterator.next();
				
				//For each cell
				JobRecord record = new JobRecord();
				int cellIndex = 0;
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext()){
					Cell cell = cellIterator.next();
					try{
						switch (cellIndex) {
							case 0: record.setPersonel(StringUtil.replaceTurkishChars(cell.getStringCellValue().split(" ")[0]).toUpperCase()); break;//too risky!!!
							case 1: record.setFirm(cell.getStringCellValue()); break;
							case 2: record.setGroup(cell.getStringCellValue()); break;
							case 3: record.setOfferLimit(cell.getNumericCellValue()); break;
							case 4: record.setSegment(cell.getStringCellValue()); break;
							case 5: record.setBranch(cell.getStringCellValue()); break;
							case 6: record.setRequester(cell.getStringCellValue()); break;
							case 7: record.setRequestDate(cell.getDateCellValue()); break;
							case 8: record.setCompleteDate(cell.getDateCellValue()); break;
							case 9: record.setMonth(StringUtil.replaceTurkishChars(cell.getStringCellValue().toUpperCase())); break;
							case 10: record.setResult(cell.getStringCellValue().toLowerCase()); break;
							case 11: record.setDayDiff((int)cell.getNumericCellValue()); break;
							case 12: record.setOfferGrounds(cell.getStringCellValue()); break;
							case 13: record.setDesc(cell.getStringCellValue()); break;
							case 14: record.setType(cell.getStringCellValue().toLowerCase());break;
						}
					}catch(IllegalStateException e){//if catch exception just keep going
						System.err.println(e.getLocalizedMessage());
					}
					cellIndex++;
				}
				//add record to list
				if(record.getPersonel() == null || record.getPersonel().compareTo("") == 0)
					break;
				records.add(record);
			}
		} catch (FileNotFoundException e) {
			System.err.println("DocumentHelper : " + e.getLocalizedMessage());
			return null;
		} catch (IOException e) {
			System.err.println("DocumentHelper : " + e.getLocalizedMessage());
			return null;
		}
		
		return records;
	}
	/**
	 * @param jobRecords
	 * @return
	 */
	public static List<String> getPersonels(List<JobRecord> jobRecords){
		Set<String> personelSet = new TreeSet<>(new StringComparator());
		for (JobRecord record : jobRecords) {
			personelSet.add(record.getPersonel());
		}
		return new ArrayList<String>(personelSet);
	}
	
	/**
	 * @param jobRecords
	 * @return
	 */
	public static List<String> getMonths(List<JobRecord> jobRecords){
		Set<String> monthSet = new TreeSet<>(new MonthComparator());
		for (JobRecord record : jobRecords) {
			monthSet.add(record.getMonth());
		}
		return new ArrayList<String>(monthSet);
	}
}
