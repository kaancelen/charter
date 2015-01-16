package com.kaancelen.charter.helpers;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import com.kaancelen.charter.constant.FileConstants;

public class PDFHelper {

	/**
	 * create pdf report for performance
	 * save it to under CHARTER_FILES
	 */
	public static void createPerformanceReport(List<Map<Object, Number>> personelData, List<Map<Object, Number>> departmentData){
		try {
			PDDocument document = new PDDocument();
			
			createPage(document, FileConstants.PERF_CHART, personelData, Arrays.asList("Rapor cikar", "Kullanim orani", "250-251 R.Kodu"));
			createPage(document, FileConstants.DEPT_CHART, departmentData, Arrays.asList("Toplam", "Rapor", "Olumlu", "Olumsuz", "Memzuç"));
			
			document.save(FileConstants.PERF_REPORT_NAME);
			document.close();
		
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (COSVisitorException e) {
			System.err.println(e.getLocalizedMessage());
		}
	}
	/**
	 * create pdf report for memzuç
	 * save it under CHARTER_FILES
	 */
	public static void createMemzucReport(List<List<Map<Object, Number>>> datas){
		try {
			PDDocument document = new PDDocument();
			
			createPage(document, FileConstants.CHART_0, datas.get(0), Arrays.asList("TCMB", "Sekerbank"));
			createPage(document, FileConstants.CHART_1, datas.get(1), Arrays.asList("Toplam Limit", "Toplam Risk", "Kullanim Orani"));
			createPage(document, FileConstants.CHART_2, datas.get(2), Arrays.asList("0-12 Vade", "12-24 Vade", "24+ Vade"));
			createPage(document, FileConstants.CHART_3, datas.get(3), Arrays.asList("Factory", "Leasing"));
			createPage(document, FileConstants.CHART_4, datas.get(4), Arrays.asList("TCMB", "Sekerbank", "250-251 R.Kodu"));
			createPage(document, FileConstants.CHART_5, datas.get(5), Arrays.asList("Toplam Limit", "Toplam Risk", "Kullanim Orani"));
			createPage(document, FileConstants.CHART_6, datas.get(6), Arrays.asList("0-12 Vade", "12-24 Vade", "24+ Vade"));
			
			document.save(FileConstants.MEMZUC_REPORT_NAME);
			document.close();
		
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (COSVisitorException e) {
			System.err.println(e.getLocalizedMessage());
		}
	}
	
	/**
	 * create page
	 * @param image
	 * @param data
	 * @param rowLabels
	 * @throws IOException 
	 */
	private static void createPage(PDDocument document, String imagePath, List<Map<Object, Number>> data, List<String> rowLabels) throws IOException{
		int heightBuffer = 400;//image buffers
		int widthBuffer = 200;
		//convert image
		PDXObjectImage image = new PDPixelMap(document, ImageIO.read(new File(imagePath)));
		//Create page due to image dimensions
		PDPage page = new PDPage(new PDRectangle(image.getWidth() + widthBuffer, image.getHeight() + heightBuffer));
		document.addPage(page);//add page to document
		//draw image
		PDPageContentStream contentStream = new PDPageContentStream(document, page);
		contentStream.drawImage(image, widthBuffer/3, 2*heightBuffer/3);
		//draw table
		drawTable(page, contentStream, heightBuffer/2, widthBuffer/15, convertDataToString(data, rowLabels));
		//save file
		contentStream.close();
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	private static String[][] convertDataToString(List<Map<Object, Number>> data, List<String> labels){
		String[][] content = new String[data.size()+1][data.get(0).size()+1];
		content[0][0] = "Tip";
		//Set column headers
		List<Object> keys = new ArrayList<Object>(data.get(0).keySet());
		for(int i=1; i< data.get(0).size()+1; i++){
			content[0][i] = (String) keys.get(i-1);//set column header
		}
		//Set row headers
		for(int i=1; i< data.size()+1; i++){
			content[i][0] = labels.get(i-1);//Set row header
		}
		DecimalFormat doubleFormat = new DecimalFormat("");
		DecimalFormat persFormat = new DecimalFormat("#0.00");
		//Set datas
		for(int i=1; i< data.size()+1; i++){//row
			Map<Object, Number> map = data.get(i-1);//get row datas
			
			int j = 1;
			for (Entry<Object, Number> entry : map.entrySet()){
				if(entry.getValue() instanceof Double){
					if((Double)entry.getValue() < 1 && (Double)entry.getValue() > 0){
						content[i][j++] = "%"+persFormat.format(entry.getValue());
						System.out.println("["+i+"]["+j+"] = " + persFormat.format(entry.getValue()));
					}else{
						content[i][j++] = doubleFormat.format(entry.getValue());
					}
				}else{
					content[i][j++] = entry.getValue()+"";
				}
			}
		}
		
		return content;
	}
	
	/**
	 * @source http://fahdshariff.blogspot.com.tr/2010/10/creating-tables-with-pdfbox.html
	 * @param page
	 * @param contentStream
	 * @param y the y-coordinate of the first row
	 * @param margin the padding on left and right of table
	 * @param content a 2d array containing the table data
	 * @throws IOException
	 */
	private static void drawTable(PDPage page, PDPageContentStream contentStream,
	                            float y, float margin,
	                            String[][] content) throws IOException {
	    final int rows = content.length;
	    final int cols = content[0].length;
	    final float rowHeight = 22f;
	    final float tableWidth = page.findMediaBox().getWidth()-(2*margin);
	    final float tableHeight = rowHeight * rows;
	    final float colWidth = tableWidth/(float)cols;
	    final float cellMargin=5f;
	 
	    //draw the rows
	    float nexty = y ;
	    for (int i = 0; i <= rows; i++) {
	        contentStream.drawLine(margin,nexty,margin+tableWidth,nexty);
	        nexty-= rowHeight;
	    }
	 
	    //draw the columns
	    float nextx = margin;
	    for (int i = 0; i <= cols; i++) {
	        contentStream.drawLine(nextx,y,nextx,y-tableHeight);
	        nextx += colWidth;
	    }
	 
	    PDFont header = PDType1Font.HELVETICA_BOLD;
	    PDFont data = PDType1Font.HELVETICA;
	    int fontSize = 12;
	    if(cols > 8)
	    	fontSize = 11;
	    else if(cols > 12)
	    	fontSize = 10;
	    else if(cols > 15)
	    	fontSize = 9;
	 
	    float textx = margin+cellMargin;
	    float texty = y-15;
	    for(int i = 0; i < content.length; i++){
	        for(int j = 0 ; j < content[i].length; j++){
	        	//set font if it is content or header
	        	if(i==0 || j==0)
	        		contentStream.setFont(header, fontSize);
	        	else
	        		contentStream.setFont(data, fontSize);
	            String text = content[i][j];
	            contentStream.beginText();
	            contentStream.moveTextPositionByAmount(textx,texty);
	            contentStream.drawString(text);
	            contentStream.endText();
	            textx += colWidth;
	        }
	        texty-=rowHeight;
	        textx = margin+cellMargin;
	    }
	}
	
}
