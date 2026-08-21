package com.framework.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import java.util.concurrent.atomic.AtomicInteger;

public class TransientFailureRetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 1;
    private final AtomicInteger attempt = new AtomicInteger();

    @Override
    public boolean retry(ITestResult result) {
        return attempt.getAndIncrement() < MAX_RETRIES;
    }
}
