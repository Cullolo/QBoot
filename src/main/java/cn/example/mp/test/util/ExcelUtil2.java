package cn.example.mp.test.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author xianpei.qin
 * @date 2021/01/06 09:52
 */
public class ExcelUtil2 {

    public static byte[] buildExcelSXSS(Object[] columnTitles, List<Map> contents) throws Exception {
        XSSFWorkbook xssfWb = null;
        SXSSFWorkbook sxssfWorkbook = null;
        SXSSFSheet sxssSheet = null;
        ByteArrayOutputStream outputStream = null;
        byte[] bytes = null;
        try {
            outputStream = new ByteArrayOutputStream();
            xssfWb = new XSSFWorkbook();
            //默认读取1000行
            sxssfWorkbook = new SXSSFWorkbook(xssfWb, 1000);
            //分页名称
            sxssSheet = (SXSSFSheet) sxssfWorkbook.createSheet("Sheet1");

            //列头样式
            XSSFFont columnTitleFont = (XSSFFont) sxssfWorkbook.createFont();
            CellStyle columnTitleStyle = sxssfWorkbook.createCellStyle();
            columnTitleFont.setFontHeightInPoints((short) 14);
            columnTitleFont.setFontName("宋体");
            columnTitleFont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
            //columnTitleFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            columnTitleStyle.setFont(columnTitleFont);
            //先输出第一行注释
            int beginRow = 0;
            if (columnTitles.length > 1) {
                SXSSFRow row = (SXSSFRow) sxssSheet.createRow(beginRow);
                for (int i = 0; i < columnTitles.length; ++i) {
                    String value = (String)columnTitles[i];
                    //sxssSheet.setColumnWidth(beginRow,30*256);
                    SXSSFCell cell = (SXSSFCell) row.createCell(i);
                    cell.setCellValue(value);
                    cell.setCellStyle(columnTitleStyle);
                }
                beginRow++;
            }
            //内容样式
            XSSFFont contentfont = (XSSFFont) sxssfWorkbook.createFont();
            XSSFCellStyle contentStyle = (XSSFCellStyle) sxssfWorkbook.createCellStyle();
            contentfont.setFontHeightInPoints((short) 12);
            contentfont.setFontName("宋体");
            contentfont.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
            //contentfont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
            contentStyle.setFont(contentfont);
            //这里要注意下，本来格式转换代码是写在下面循环里的，有残留，这里拿出来单独处理，也可以按照自己想法做
            XSSFDataFormat df = (XSSFDataFormat) sxssfWorkbook.createDataFormat();
            XSSFCellStyle contentStyleInteger = (XSSFCellStyle) sxssfWorkbook.createCellStyle();
            contentStyleInteger.setFont(contentfont);
            contentStyleInteger.setDataFormat(df.getFormat("#,#0"));//数据格式只显示整数
            XSSFCellStyle contentStyleDecimal = (XSSFCellStyle) sxssfWorkbook.createCellStyle();
            contentStyleDecimal.setFont(contentfont);
            contentStyleDecimal.setDataFormat(df.getFormat("#,##0.00"));//保留两位小数点

            //记录总数
            int listNum = contents.size();
            //查询数据库最大记录数
            int sourceMaxCache = 10000;
            //总共查询次数
            int findNum = 0;
            if (0 == (listNum % sourceMaxCache)){
                findNum = listNum / sourceMaxCache;
            }else {
                findNum = listNum / sourceMaxCache + 1;
            }
            //此次批量写入数据
            List<Map> listBatch = null;
            //已处理到第几位
            int nowNum = 0;
            for (int j = 0; j < findNum; j++){
                listBatch = new ArrayList<>();
                //把查询结果分批写入map list
                for (int a=0;a<sourceMaxCache;a++) {
                    listBatch.add(contents.get(nowNum));
                    nowNum++;
                    //这里主要是防止数组越界
                    if(nowNum == listNum) {
                        break;
                    }
                }
                //数据写入excel
                for (Map rowContent : listBatch) {
                    int begincolumn = 0;
                    SXSSFRow row = (SXSSFRow) sxssSheet.createRow(beginRow);
                    for (Object key : rowContent.keySet()) {
                        String value = rowContent.get(key) + "";
                        SXSSFCell cell = (SXSSFCell) row.createCell(begincolumn);
                        if(begincolumn==0 || begincolumn==1) {
                            cell.setCellStyle(contentStyle);
                            cell.setCellValue(value);
                        }else if(NumberUtils.isNumber(value)){
                            //XSSFDataFormat df = (XSSFDataFormat) sxssfWorkbook.createDataFormat();
                            if (NumberUtils.isDigits(value)) {
                                //contentStyle.setDataFormat(df.getFormat("#,#0"));//数据格式只显示整数
                                cell.setCellStyle(contentStyleInteger);
                            }else{
                                //contentStyle.setDataFormat(df.getFormat("#,##0.00"));//保留两位小数点
                                cell.setCellStyle(contentStyleDecimal);
                            }
                            cell.setCellValue(NumberUtils.toDouble(value));
                        }else {
                            cell.setCellStyle(contentStyle);
                            cell.setCellValue(value);
                        }
                        begincolumn++;
                    }
                    beginRow++;
                }
                //帮助gc回收内存
                listBatch.clear();
            }
            sxssfWorkbook.write(outputStream);
            bytes = outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return bytes;
    }
}
