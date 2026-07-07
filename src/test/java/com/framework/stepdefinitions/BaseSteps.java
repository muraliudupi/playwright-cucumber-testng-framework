package com.framework.stepdefinitions;

import com.framework.utils.ConfigReader;
import com.framework.utils.ExcelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class BaseSteps {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected static final String EXCEL_FILE_PATH = ConfigReader.getExcelPath();

    protected Map<String, String> getExcelRow(String sheetName, String rowNumber) {
        List<Map<String, String>> testData = ExcelReader.getSheetData(EXCEL_FILE_PATH, sheetName);
        int rowIndex = Integer.parseInt(rowNumber) - 1;

        if (rowIndex < 0 || rowIndex >= testData.size()) {
            org.testng.Assert.fail(String.format(
                    "DATA PROFILE ERROR: Requested Row %s not found in spreadsheet partition '%s' (sheet contains %d rows). " +
                            "Verify the row index bounds specified within your Feature File Scenario Outline Examples.",
                    rowNumber, sheetName, testData.size()));
        }
        return testData.get(rowIndex);
    }
}
