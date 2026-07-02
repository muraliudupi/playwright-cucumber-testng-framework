package com.framework.utils;

import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExcelReader {

    private ExcelReader() {}

    public static List<Map<String, String>> getSheetData(String excelFilePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(new File(excelFilePath))) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in " + excelFilePath);
            }

            Row headerRow = sheet.getRow(0);
            int totalCols = headerRow.getPhysicalNumberOfCells();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null) continue;

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int j = 0; j < totalCols; j++) {
                    Cell cell = currentRow.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String headerName = headerRow.getCell(j).getStringCellValue().trim();

                    DataFormatter formatter = new DataFormatter();
                    String cellValue = formatter.formatCellValue(cell).trim();

                    rowMap.put(headerName, cellValue);
                }
                dataList.add(rowMap);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file at: " + excelFilePath, e);
        }
        return dataList;
    }
}