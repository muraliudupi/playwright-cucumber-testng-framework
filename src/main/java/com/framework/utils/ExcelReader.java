package com.framework.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ExcelReader {

    private ExcelReader() {
    }

    private static final Map<String, List<Map<String, String>>> CACHE = new ConcurrentHashMap<>();

    public static List<Map<String, String>> getSheetData(String excelFilePath, String sheetName) {
        String cacheKey = excelFilePath + "::" + sheetName;
        return CACHE.computeIfAbsent(cacheKey, key -> readSheetFromDisk(excelFilePath, sheetName));
    }

    private static List<Map<String, String>> readSheetFromDisk(String excelFilePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(new File(excelFilePath), null, true)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in " + excelFilePath);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Header row (row 0) is missing in sheet '" + sheetName + "' of " + excelFilePath);
            }
            int totalCols = headerRow.getPhysicalNumberOfCells();

            List<String> headerNames = new ArrayList<>(totalCols);
            Set<String> seenHeaders = new HashSet<>();
            for (int j = 0; j < totalCols; j++) {
                Cell headerCell = headerRow.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String headerName = headerCell.getStringCellValue().trim();

                if (headerName.isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Blank header cell at column index %d in sheet '%s' of %s. Every column must have a non-empty header.",
                            j, sheetName, excelFilePath));
                }
                if (!seenHeaders.add(headerName)) {
                    throw new RuntimeException(String.format(
                            "Duplicate header '%s' in sheet '%s' of %s. Column headers must be unique - " +
                                    "a repeated header silently overwrites a prior column's value when rows are read.",
                            headerName, sheetName, excelFilePath));
                }
                headerNames.add(headerName);
            }

            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null) continue;

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int j = 0; j < totalCols; j++) {
                    Cell cell = currentRow.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = formatter.formatCellValue(cell).trim();
                    rowMap.put(headerNames.get(j), cellValue);
                }
                dataList.add(rowMap);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file at: " + excelFilePath, e);
        }
        return dataList;
    }
}