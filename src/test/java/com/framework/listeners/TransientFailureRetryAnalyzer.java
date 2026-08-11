package com.framework.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed Cucumber/TestNG scenario once before letting it report as
 * failed. Applied globally via {@link RetryAnnotationTransformer} so step
 * definitions and hooks never need to know it exists — this only covers
 * transient/infrastructure flakiness (e.g. the documented ParaBank rate-limit
 * risk), not a mechanism for hiding genuine assertion failures.
 */
public class TransientFailureRetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 1;
    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        return attempt++ < MAX_RETRIES;
    }
}
