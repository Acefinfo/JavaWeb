/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;


import java.io.OutputStream;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;

/**
 *
 * @author DELL
 */
public final class ExcelExportUtil {
    
    private ExcelExportUtil(){}
    
    public static void export(String sheetName, String[] headers, List<String[]> rows, String fileName){
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
        
        XSSFWorkbook workbook = null;
        try{
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(sheetName);
            
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(headerFont);
            
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i <headers.length; i++){
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
             int rowIdx = 1;
            if (rows != null) {
                for (String[] rowData : rows) {
                    XSSFRow row = sheet.createRow(rowIdx++);
                    for (int c = 0; c < rowData.length; c++) {
                        row.createCell(c).setCellValue(rowData[c] == null ? "" : rowData[c]);
                    }
                }
            }
            
            for (int i = 0; i < headers.length; i++){
                sheet.autoSizeColumn(i);
            }
            
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName +"\"");
            
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            
        } catch (Exception e){
            throw new RuntimeException("Cound not generate Excell support", e);
        } finally{
            if (workbook != null){
                try{
                    workbook.close();
                }catch (Exception ignored){
                }
            }
        }
        fc.responseComplete();
    }
    
}
