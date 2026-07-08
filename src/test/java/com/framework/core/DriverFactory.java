package com.framework.core;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Browser>    BROWSER_THREAD_LOCAL    = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Page>       PAGE_THREAD_LOCAL       = new ThreadLocal<>();

    private static final Map<Long, Playwright> PLAYWRIGHT_REGISTRY = new ConcurrentHashMap<>();
    private static final Map<Long, Browser>    BROWSER_REGISTRY    = new ConcurrentHashMap<>();

    private static final int DEFAULT_VIEWPORT_WIDTH = 1280;
    private static final int DEFAULT_VIEWPORT_HEIGHT = 720;

    private DriverFactory() {}

    private static Playwright getPlaywright() {
        if (PLAYWRIGHT_THREAD_LOCAL.get() == null) {
            LOG.info("[Thread-{}] Instantiating unique thread-isolated Playwright instance.", threadId());
            Playwright playwright = Playwright.create();
            PLAYWRIGHT_THREAD_LOCAL.set(playwright);
            PLAYWRIGHT_REGISTRY.put(threadId(), playwright);
        }
        return PLAYWRIGHT_THREAD_LOCAL.get();
    }

    private static Browser getBrowser() {
        if (BROWSER_THREAD_LOCAL.get() == null) {
            String browserType = resolveSetting("browser", "chromium").trim().toLowerCase();
            boolean headless   = Boolean.parseBoolean(resolveSetting("headless", "true"));

            LOG.info("[Thread-{}] Spawning isolated {} OS process (Headless={}).", threadId(), browserType, headless);

            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);

            Browser browser = switch (browserType) {
                case "firefox" -> getPlaywright().firefox().launch(options);
                case "webkit"  -> getPlaywright().webkit().launch(options);
                default        -> getPlaywright().chromium().launch(options);
            };
            BROWSER_THREAD_LOCAL.set(browser);
            BROWSER_REGISTRY.put(threadId(), browser);
        }
        return BROWSER_THREAD_LOCAL.get();
    }

    private static Browser.NewContextOptions buildStandardizedContextOptions() {
        return new Browser.NewContextOptions()
                .setViewportSize(DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT);
    }

    public static void createNewPageForScenario(String scenarioName) {
        LOG.info("[Thread-{}] Allocating fresh workflow execution context for: {}", threadId(), scenarioName);

        if (PAGE_THREAD_LOCAL.get() != null || CONTEXT_THREAD_LOCAL.get() != null) {
            LOG.warn("[Thread-{}] Found stale un-cleared target driver states. Forcing baseline reset context.", threadId());
            closeContextAndPage();
        }

        BrowserContext context = getBrowser().newContext(buildStandardizedContextOptions());

        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        Page page = context.newPage();

        CONTEXT_THREAD_LOCAL.set(context);
        PAGE_THREAD_LOCAL.set(page);
    }

    public static BrowserContext getContext() {
        BrowserContext context = CONTEXT_THREAD_LOCAL.get();
        if (context == null) {
            throw new IllegalStateException(
                    "[Thread-" + threadId() + "] No BrowserContext bound. " +
                            "Was createNewPageForScenario() called from a @Before hook?");
        }
        return context;
    }

    public static Page getPage() {
        Page page = PAGE_THREAD_LOCAL.get();
        if (page == null) {
            throw new IllegalStateException(
                    "[Thread-" + threadId() + "] No Page bound. " +
                            "Was createNewPageForScenario() called from a @Before hook?");
        }
        return page;
    }

    public static void closeContextAndPage() {
        long tid = threadId();
        LOG.info("[Thread-{}] Terminating scenario-scoped isolated sandboxes (Page & Context). Keeping browser alive.", tid);

        try {
            Page page = PAGE_THREAD_LOCAL.get();
            if (page != null) page.close();
        } catch (Exception e) {
            LOG.error("[Thread-{}] Exception caught during scenario-scoped Page close.", tid, e);
        } finally {
            PAGE_THREAD_LOCAL.remove();
        }

        try {
            BrowserContext context = CONTEXT_THREAD_LOCAL.get();
            if (context != null) context.close();
        } catch (Exception e) {
            LOG.error("[Thread-{}] Exception caught during scenario-scoped BrowserContext close.", tid, e);
        } finally {
            CONTEXT_THREAD_LOCAL.remove();
        }
    }

    public static void quitAllDrivers() {
        LOG.info("Suite complete — closing all Browser and Playwright instances across {} thread(s).",
                BROWSER_REGISTRY.size());

        BROWSER_REGISTRY.forEach((tid, browser) -> {
            try {
                if (browser != null) browser.close();
                LOG.info("Suite Cleanup: Recycled Browser OS subprocess allocated on Thread-{}", tid);
            } catch (Exception e) {
                LOG.error("Suite Teardown Alert: Anomaly discarding Browser process on Thread-{}", tid, e);
            }
        });
        BROWSER_REGISTRY.clear();

        PLAYWRIGHT_REGISTRY.forEach((tid, playwright) -> {
            try {
                if (playwright != null) playwright.close();
                LOG.info("Suite Cleanup: Destroyed core Playwright channel loop mapping on Thread-{}", tid);
            } catch (Exception e) {
                LOG.error("Suite Teardown Alert: Anomaly discarding Playwright instance on Thread-{}", tid, e);
            }
        });
        PLAYWRIGHT_REGISTRY.clear();

        // Safe cleanup of any lingering ThreadLocals on the master runner thread
        PLAYWRIGHT_THREAD_LOCAL.remove();
        BROWSER_THREAD_LOCAL.remove();
        PAGE_THREAD_LOCAL.remove();
        CONTEXT_THREAD_LOCAL.remove();

        LOG.info("Suite Infrastructure Teardown Complete: All shared OS processes harvested successfully.");
    }

    private static long threadId() {
        return Thread.currentThread().threadId();
    }

    private static String resolveSetting(String key, String hardcodedDefault) {
        String value = ConfigReader.get(key);
        return (value != null && !value.isBlank()) ? value : hardcodedDefault;
    }
}