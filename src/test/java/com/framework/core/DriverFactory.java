package com.framework.core;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.ViewportSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DriverFactory

 * Central authority for Playwright object lifecycles. Playwright, Browser, BrowserContext and Page are all NOT thread-safe — a single instance of any
 * of them must never be accessed from more than one thread. TestNG's parallel="methods" execution model spins up scenarios on a pool of worker
 * threads, so every one of these objects is wrapped in its own ThreadLocal.

 * Lifecycle strategy (per thread):
 *   Playwright (1x per thread, expensive)  -> created once, reused across scenarios
 *   Browser    (1x per thread, expensive)  -> created once, reused across scenarios
 *   BrowserContext (1x per SCENARIO)       -> fresh isolated cookies/storage per test
 *   Page       (1x per SCENARIO)           -> fresh tab per test
 *
 * Context/Page are scenario-scoped (created in @Before, destroyed in @After) so tests never bleed cookies, localStorage or auth state into each other,
 * even when they share the same underlying Browser process on that thread.
 */

public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private static final ThreadLocal<Playwright> PLAYWRIGHT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE_THREAD_LOCAL = new ThreadLocal<>();

    // Cross-thread registry (keyed by owning thread id) purely for guaranteed
    // teardown from a single @AfterSuite thread. ThreadLocal.remove() can
    // only ever be called BY the owning thread, but TestNG worker threads
    // are pooled/recycled and may already be gone by the time the suite
    // finishes — so @AfterSuite closes instances via this map instead of
    // relying on each worker thread to clean up after itself.
    private static final Map<Long, Playwright> PLAYWRIGHT_REGISTRY = new ConcurrentHashMap<>();
    private static final Map<Long, Browser> BROWSER_REGISTRY = new ConcurrentHashMap<>();

    private DriverFactory() {
        // static utility — never instantiated
    }

    // -------------------------------------------------------------------
    // Playwright / Browser — created lazily, ONE per worker thread
    // -------------------------------------------------------------------

    private static Playwright getPlaywright() {
        if (PLAYWRIGHT_THREAD_LOCAL.get() == null) {
            LOG.info("[Thread-{}] Creating new Playwright instance", threadId());
            Playwright playwright = Playwright.create();
            PLAYWRIGHT_THREAD_LOCAL.set(playwright);
            PLAYWRIGHT_REGISTRY.put(threadId(), playwright);
        }
        return PLAYWRIGHT_THREAD_LOCAL.get();
    }

    private static Browser getBrowser() {
        if (BROWSER_THREAD_LOCAL.get() == null) {
            String browserName = resolveSetting("browser", "chromium").toLowerCase();
            boolean headless = Boolean.parseBoolean(resolveSetting("headless", "true"));

            LOG.info("[Thread-{}] Launching {} (headless={})", threadId(), browserName, headless);

            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(headless)
                    .setSlowMo(0);

            BrowserType browserType = switch (browserName) {
                case "firefox" -> getPlaywright().firefox();
                case "webkit"  -> getPlaywright().webkit();
                default        -> getPlaywright().chromium();
            };

            Browser browser = browserType.launch(launchOptions);
            BROWSER_THREAD_LOCAL.set(browser);
            BROWSER_REGISTRY.put(threadId(), browser);
        }
        return BROWSER_THREAD_LOCAL.get();
    }

    // -------------------------------------------------------------------
    // BrowserContext / Page — created PER SCENARIO by Hooks
    // -------------------------------------------------------------------

    /**
     * Creates a fresh, isolated BrowserContext + Page for the current thread.
     * Must be called from an @Before hook. Idempotent-safe: if a stale
     * context somehow exists on this thread it is closed first.
     */
    public static void createNewPageForScenario(String scenarioName) {
        if (CONTEXT_THREAD_LOCAL.get() != null) {
            LOG.warn("[Thread-{}] Stale BrowserContext detected before '{}' — closing it first",
                    threadId(), scenarioName);
            closeContextAndPage();
        }

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(new ViewportSize(1920, 1080))
                .setIgnoreHTTPSErrors(true);

        BrowserContext context = getBrowser().newContext(contextOptions);
        context.setDefaultTimeout(30_000);
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        Page page = context.newPage();

        CONTEXT_THREAD_LOCAL.set(context);
        PAGE_THREAD_LOCAL.set(page);

        LOG.info("[Thread-{}] New BrowserContext + Page ready for scenario '{}'", threadId(), scenarioName);
    }

    /**
     * Returns the Page bound to the CURRENT thread. Step definitions and
     * Page Objects call this instead of ever holding a Page reference as a
     * field, which is what makes the whole model thread-safe.
     */
    public static Page getPage() {
        Page page = PAGE_THREAD_LOCAL.get();
        if (page == null) {
            throw new IllegalStateException(
                    "No Page bound to thread " + threadId() +
                    ". Was createNewPageForScenario() called in a @Before hook?");
        }
        return page;
    }

    public static BrowserContext getContext() {
        BrowserContext context = CONTEXT_THREAD_LOCAL.get();
        if (context == null) {
            throw new IllegalStateException("No BrowserContext bound to thread " + threadId());
        }
        return context;
    }

    // -------------------------------------------------------------------
    // Teardown
    // -------------------------------------------------------------------

    /**
     * Closes Context + Page for the current thread. Called from @After on
     * every scenario. Browser and Playwright instances are intentionally
     * left alive and reused for the next scenario on this same thread.
     */
    public static void closeContextAndPage() {
        try {
            BrowserContext context = CONTEXT_THREAD_LOCAL.get();
            if (context != null) {
                // NOTE: do NOT call tracing().stop() here. Hooks.attachTrace()
                // already stops (and saves) tracing explicitly for FAILED
                // scenarios. Calling stop() twice throws. For passed
                // scenarios we deliberately never save the trace — closing
                // the context with an active trace simply discards it, which
                // is the desired behavior (no wasted disk I/O on green runs).
                context.close();
            }
        } catch (Exception e) {
            LOG.error("[Thread-{}] Error closing BrowserContext", threadId(), e);
        } finally {
            CONTEXT_THREAD_LOCAL.remove();
            PAGE_THREAD_LOCAL.remove();
        }
    }

    /**
     * Suite-wide teardown. Called ONCE from a single @AfterSuite method
     * (see AbstractTestNGCucumberRunner) after every worker thread has
     * finished executing scenarios. Iterates the cross-thread registry
     * rather than relying on ThreadLocal.remove(), because by this point
     * the worker threads that originally created these instances may
     * already be idle/recycled/destroyed by TestNG's thread pool — this is
     * precisely the gap that causes silent browser-process leaks if you
     * only ever call ThreadLocal.remove() from @After.
     */
    public static void quitAllDrivers() {
        BROWSER_REGISTRY.forEach((tid, browser) -> {
            try {
                browser.close();
                LOG.info("[Thread-{}] Browser closed during suite teardown", tid);
            } catch (Exception e) {
                LOG.error("[Thread-{}] Error closing Browser during suite teardown", tid, e);
            }
        });
        BROWSER_REGISTRY.clear();

        PLAYWRIGHT_REGISTRY.forEach((tid, playwright) -> {
            try {
                playwright.close();
                LOG.info("[Thread-{}] Playwright closed during suite teardown", tid);
            } catch (Exception e) {
                LOG.error("[Thread-{}] Error closing Playwright during suite teardown", tid, e);
            }
        });
        PLAYWRIGHT_REGISTRY.clear();

        LOG.info("Suite-wide Playwright/Browser teardown complete.");
    }

    private static long threadId() {
        return Thread.currentThread().threadId();
    }

    /**
     * Resolution order (handled inside ConfigReader): explicit -D system
     * property (highest priority, for CI/local overrides) → config.properties
     * → the hardcoded default passed here. Used so browser/headless settings
     * behave identically whether the run is launched via `./gradlew test`
     * (which forwards -D system properties) or directly via IntelliJ's
     * native TestNG runner (which does not).
     */
    private static String resolveSetting(String key, String hardcodedDefault) {
        String value = ConfigReader.get(key);
        return value != null && !value.isBlank() ? value : hardcodedDefault;
    }
}
