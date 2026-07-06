package com.framework.stepdefinitions;

import com.framework.utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseSteps {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected static final String EXCEL_FILE_PATH = ConfigReader.getExcelPath();
}
