package ExocytosisAnalyzer.GUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import ExocytosisAnalyzer.datasets.Parameters;
import ExocytosisAnalyzer.datasets.Secretion;
import ExocytosisAnalyzer.datasets.Vesicle;
import ij.io.SaveDialog;

public class SaveUtils {
	public static void saveSecretionList(	

			Vector<Secretion> detected_secretions,
			Parameters parameter
			) 
	
					throws IOException {

		String sep = ",";
		SaveDialog saveFileDialog = new SaveDialog("Save file", parameter.filename + "_secretions", ".csv");
		String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();
		File file = new File(saveFilePatch + ".csv");		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "unicode");
		
		String[] columnTitle = new String[] {
				"Ref", // 1
				"Property", // 2
				"Begin Frame", // 3
				"Duration", // 4
				"Peak Frame", // 5
				"Position (x)",
				"Position (y)", // 6
				"Max. Displacement (pixels)", // 7
				"Tau (" + parameter.timeUnit + ")", // 8
				"R Square for Decay Est.", // 9
				"dF/Sigma (MAD)", // 10
				"Decay Function", // 11
				"F0",//13
				"Delta F",//14
				"R Square Gaussian fit",//15
				"Est. Size (FWHM " + parameter.pixelUnit + ")" // 16

		};

		for(String title : columnTitle){
			osw.write(title);
			osw.write(sep);
		}
		osw.write("\r\n");
		

		for (Secretion s : detected_secretions) {
				
			
			osw.write(
					String.valueOf(s.getRef()) +sep+
					s.proprety +sep+
					String.valueOf(s.getStartSlice()) +sep+
					String.valueOf(s.getDuration()) +sep+
					String.valueOf(s.peakTime) +sep+
					String.valueOf(s.getStartX()) +sep+
					String.valueOf(s.getStartY()) +sep+
					String.valueOf(s.getMaxDisplacement()) +sep+

					String.valueOf(s.Decay_tau * parameter.timeInterval) +sep+
					String.valueOf(s.Decay_R2) +sep+
					String.valueOf(s.getSNR()) +sep+				
					"y=" + s.Decay_bk + "+" + s.Decay_peak + "*" + "exp(-x/" + s.Decay_tau * parameter.timeInterval + ")" +sep+
					String.valueOf(s.getF_zero()) +sep+
					String.valueOf(s.getMax()-s.getF_zero())+sep+
					String.valueOf(s.getPeakGaussfitterRsquare2D())  +sep+
					String.valueOf(s.getEstimatedPeakSize2D() * parameter.pixelSize)
					);
			osw.write("\r\n");
		}
		osw.flush();
		osw.close();
	}
	public static void saveTrackingList(	

			Vector<Secretion> detected_secretions,
			Parameters parameter
			) 
	
					throws IOException {

		String sep = ",";
		SaveDialog saveFileDialog = new SaveDialog("Save file", parameter.filename + "_tracking", ".csv");
		String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();
		File file = new File(saveFilePatch + ".csv");		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "unicode");
		
		String[] columnTitle = new String[] {
				"Ref", // 1
				"Begin Frame", // 2
				"Duration", // 3
				"Position (x)",
				"Position (y)", // 4
				"Max. Displacement (pixels)", // 5
				"dF/Sigma (MAD)", // 6
				"F0",//7
				"Delta F" //8
		};

		for(String title : columnTitle){
			osw.write(title);
			osw.write(sep);
		}
		osw.write("\r\n");
		

		for (Secretion s : detected_secretions) {
				
			
			osw.write(
					String.valueOf(s.getRef()) +sep+
					String.valueOf(s.getStartSlice()) +sep+
					String.valueOf(s.getDuration()) +sep+
					String.valueOf(s.getStartX()) +sep+
					String.valueOf(s.getStartY()) +sep+
					String.valueOf(s.getMaxDisplacement()) +sep+										
					String.valueOf(s.getSNR()) +sep+				
					String.valueOf(s.getF_zero()) +sep+
					String.valueOf(s.getMax()-s.getF_zero())
					);
			osw.write("\r\n");
		}
		osw.flush();
		osw.close();
	}
	public static void saveVesicleList_secretion(
			Vector<Vesicle> detected_vesicle,
			Parameters parameter,
			int ref
			) 
					throws IOException {
		
		String sep = ",";
		SaveDialog saveFileDialog = new SaveDialog("Save file",  parameter.filename + "_evt_" + String.valueOf(ref), ".csv");
		String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();

		File file = new File(saveFilePatch + ".csv");		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "Unicode");
		
		String[] columnTitle = new String[] { 
				"Ref", // 0
				"Property", // 1
				"Frame", // 2
				"Intensity", // 3
				"x", // 4
				"y", // 5
				"R² for Gaussian fit" ,// 6
				"Apparent Size (FWHM " + parameter.pixelUnit + ")" // 7
		};
		
		for(String title : columnTitle){
			osw.write(title);
			osw.write(sep);
		}
		osw.write("\r\n");
		
		int i = 1;
		for (Vesicle v : detected_vesicle) {
			
			osw.write(
					String.valueOf(i) +sep+ 
					v.property +sep+ 
					String.valueOf(v.slice) +sep+ 
					String.valueOf(v.getMaxDen()) +sep+ 
					String.valueOf(v.x) +sep+ 
					String.valueOf(v.y) +sep+
					String.valueOf(v.get2DFitRsqr())+sep+ 
					String.valueOf(v.EstimateSize2D() * parameter.pixelSize) 
					);
			osw.write("\r\n");
			i++;
		}
		osw.flush();
		osw.close();
	}
	public static void saveVesicleList_tracking(
			Vector<Vesicle> detected_vesicle,
			Parameters parameter,
			int ref
			) 
					throws IOException {
		
		String sep = ",";
		SaveDialog saveFileDialog = new SaveDialog("Save file",  parameter.filename + "_tracking_evt_" + String.valueOf(ref), ".csv");
		String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();

		File file = new File(saveFilePatch + ".csv");		
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "Unicode");
		
		String[] columnTitle = new String[] { 
				"Ref", // 1
				"Frame", // 2
				"Intensity", // 3
				"x", // 4
				"y", // 5
		};
		
		for(String title : columnTitle){
			osw.write(title);
			osw.write(sep);
		}
		osw.write("\r\n");
		
		int i = 1;
		for (Vesicle v : detected_vesicle) {
			
			osw.write(
					String.valueOf(i) +sep+ 
					String.valueOf(v.slice) +sep+ 
					String.valueOf(v.getMaxDen()) +sep+ 
					String.valueOf(v.x) +sep+ 
					String.valueOf(v.y)
					);
			osw.write("\r\n");
			i++;
		}
		osw.flush();
		osw.close();
	}
	
	public static void saveAllVesicleList(String[] columnTitle,Vector<Secretion> secretions,
			Parameters parameter
			) 
					throws IOException {
		
		Vector<Secretion> detected_secretions = secretions;

		String sep = ",";
		SaveDialog saveFileDialog = new SaveDialog("Export All Table",  parameter.filename + "_evt_" , "");
		String saveFilePatch;
		
		for (Secretion s : detected_secretions) {

			saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName() + s.ref_ID;

			File file = new File(saveFilePatch + ".csv");		
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "Unicode");

			for(String title : columnTitle){
				osw.write(title);
				osw.write(sep);
			}
			osw.write("\r\n");
			
			int i = 1;
			for (Vesicle v : s.secretion_event) {
				
				osw.write(
						String.valueOf(i) +sep+ 
						v.property +sep+ 
						String.valueOf(v.slice) +sep+ 
						String.valueOf(v.getMaxDen()) +sep+ 
						String.valueOf(v.x) +sep+ 
						String.valueOf(v.y) +sep+
						String.valueOf(v.get2DFitRsqr()) +sep+ 
						String.valueOf(v.EstimateSize2D() * parameter.pixelSize)
						);
				osw.write("\r\n");
				i++;
			}
			osw.flush();
			osw.close();
		}
		
			
			
			
			}
		
		
		
		
		
/***
	public static void saveSecretionListExcel(String[] columnTitle, Vector<Secretion> detected_secretions, double pixelSize, double timeInterval,String pixelUnit,
			String timeUnit, String filename ) throws IOException {

		SaveDialog saveFileDialog = new SaveDialog("Save file", filename + "_secretions", ".xlsx");
		String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();

		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a Sheet
		XSSFSheet sheet = workbook.createSheet("Results");
		
		// Create a Font for styling header cells
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);

		// Create a CellStyle with the font
		XSSFCellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		XSSFRow headerRow = sheet.createRow(0);

		// Create cells
		for (int j = 0; j < columnTitle.length; j++) {
			XSSFCell cell = headerRow.createCell(j);
			cell.setCellValue(columnTitle[j]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Cell Style for formatting Date
		//CellStyle dateCellStyle = workbook.createCellStyle();
		// dateCellStyle.setDataFormat();
		int j = 1;
		for (Secretion s : detected_secretions) {
			XSSFRow row = sheet.createRow(j);
			row.createCell(0).setCellValue(s.ref_ID);
			row.createCell(1).setCellValue(s.start_slice);
			row.createCell(2).setCellValue(s.fin_slice);
			row.createCell(3).setCellValue(s.peakTime);
			row.createCell(4).setCellValue(s.start_x);
			row.createCell(5).setCellValue(s.start_y);			
			row.createCell(6).setCellValue(s.getEstimatedPeakRadius2D() * pixelSize);
			row.createCell(7).setCellValue(s.Decay_tau * timeInterval);
			row.createCell(8).setCellValue(s.function);
			row.createCell(9).setCellValue(s.Decay_R2);
			row.createCell(10).setCellValue(s.getSNR());
			j++;

		}
		// Resize all columns to fit the content size
		for (int k = 0; k < columnTitle.length; k++) {
			sheet.autoSizeColumn(k);
		}

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream(saveFilePatch);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();
	}

	public static void saveVesicleListExcel(String[] columnTitle, Vector<Vesicle> detected_vesicle, double pixelSize, String pixelUnit, String filename, int ref)
			throws IOException {

		SaveDialog saveFileDialog = new SaveDialog("Save file", filename + "_evt_" + String.valueOf(ref), ".xlsx");
		String saveFilePatch = saveFileDialog.getDirectory() + saveFileDialog.getFileName();

		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a Sheet
		XSSFSheet sheet = workbook.createSheet("Results");


		// Create a Font for styling header cells
		XSSFFont headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);

		// Create a CellStyle with the font
		XSSFCellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		// Create a Row
		XSSFRow headerRow = sheet.createRow(0);

		// Create cells
		for (int j = 0; j < columnTitle.length; j++) {
			XSSFCell cell = headerRow.createCell(j);
			cell.setCellValue(columnTitle[j]);
			cell.setCellStyle(headerCellStyle);
		}

		// Create Cell Style for formatting Date
		// CellStyle dateCellStyle = workbook.createCellStyle();
		// dateCellStyle.setDataFormat();
		int j = 1;
		for (Vesicle v : detected_vesicle) {
			XSSFRow row = sheet.createRow(j);
			row.createCell(0).setCellValue(v.getRef());
			row.createCell(1).setCellValue(v.property);
			row.createCell(2).setCellValue(v.slice);
			row.createCell(3).setCellValue(v.getMaxDen());
			row.createCell(4).setCellValue(v.x);
			row.createCell(5).setCellValue(v.y);
			row.createCell(6).setCellValue(v.EstimateSize2D() * pixelSize);
			row.createCell(7).setCellValue(v.get2DFitRsqr());
			j++;

		}
		// Resize all columns to fit the content size
		for (int k = 0; k < columnTitle.length; k++) {
			sheet.autoSizeColumn(k);
		}

		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream(saveFilePatch);
		workbook.write(fileOut);
		fileOut.close();
		workbook.close();
	}
***/	
}

