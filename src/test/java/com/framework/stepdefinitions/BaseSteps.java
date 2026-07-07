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

    protected Map<String, String> getExcelRowByKey(String uniqueTestCaseId, String sheetName) {
        List<Map<String, String>> allRows = ExcelReader.getSheetData(EXCEL_FILE_PATH, sheetName);

        return allRows.stream()
                .filter(row -> uniqueTestCaseId.equalsIgnoreCase(row.get("TestCaseID")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Data Key Verification Error: TestCaseID matching value '%s' was not resolved inside sheet '%s'.",
                        uniqueTestCaseId, sheetName)));
    }
}