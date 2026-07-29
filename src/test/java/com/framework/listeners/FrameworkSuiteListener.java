package com.framework.listeners;

import com.framework.core.MobileDriverFactory;
import com.framework.core.WebDriverFactory;
import com.framework.utils.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public final class FrameworkSuiteListener implements ISuiteListener {

    private static final Logger LOG = LoggerFactory.getLogger(FrameworkSuiteListener.class);

    @Override
    public void onFinish(ISuite suite) {
        LOG.info("Suite '{}' complete — tearing down framework infrastructure.", suite.getName());
        WebDriverFactory.quitAllDrivers();
        MobileDriverFactory.quitAllDrivers();
        DatabaseUtil.closePool();
    }
}
