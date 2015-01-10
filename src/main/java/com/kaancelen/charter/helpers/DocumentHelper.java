package com.kaancelen.charter.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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

import com.kaancelen.charter.comparators.TermComparator;
import com.kaancelen.charter.models.Record;

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
	
	
}
