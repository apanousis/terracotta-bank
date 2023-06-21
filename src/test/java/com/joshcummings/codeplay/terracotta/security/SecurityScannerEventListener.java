package com.joshcummings.codeplay.terracotta.security;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zaproxy.clientapi.core.Alert;

import java.util.ArrayList;
import java.util.List;

public class SecurityScannerEventListener implements WebDriverEventListener {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityScannerEventListener.class);
    private int currentScanID;
    private ZapProxyScanner zapScanner;

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateTo(String s, WebDriver webDriver) {
        //scan(s);

    }

    @Override
    public void afterNavigateTo(String url, WebDriver webDriver) {
        scan(url);
    }

    @Override
    public void beforeNavigateBack(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateBack(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateForward(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateForward(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void beforeScript(String s, WebDriver webDriver) {

    }

    @Override
    public void afterScript(String s, WebDriver webDriver) {

    }

    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {

    }

    public ZapProxyScanner getZapScanner() {
        return zapScanner;
    }

    public void setZapScanner(ZapProxyScanner zapScanner) {
        this.zapScanner = zapScanner;
    }

    private void logAlerts(List<Alert> alerts) {
        for (Alert alert : alerts) {
            LOG.info("Alert: " + alert.getAlert() + " at URL: " + alert.getUrl() + " Parameter: " + alert.getParam() + " CWE ID: " + alert.getCweId());
        }
    }

    /*
        Remove false positives, filter based on risk and reliability
     */
    private List<Alert> filterAlerts(List<Alert> alerts) {
        List<Alert> filtered = new ArrayList<>();
        for (Alert alert : alerts) {
            if (alert.getRisk() == Alert.Risk.High && alert.getConfidence() != Alert.Confidence.Low) {
                filtered.add(alert);
            }
        }
        return filtered;
    }

    void setAlertAndAttackStrength() {
        for (Policy policy : Policy.values()) {
            String ids = enableZapPolicy(policy);
            for (String id : ids.split(",")) {
                zapScanner.setScannerAlertThreshold(id, "LOW");
                zapScanner.setScannerAlertThreshold(id, "MEDIUM");
                zapScanner.setScannerAttackStrength(id, "HIGH");
            }
        }
    }

    private void scanWithZap(String url) {
        LOG.info("Scanning...");
        zapScanner.scan(url);
        currentScanID = zapScanner.getLastScannerScanId();
        int complete = 0;
        while (complete < 100) {
            complete = zapScanner.getScanProgress(currentScanID);
            LOG.info("Scan is " + complete + "% complete.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Thread was interrupted {}", e.getMessage());
            }
        }
        LOG.info("Scanning done.");
    }

    private String enableZapPolicy(Policy policy) {
        String scannerIds = policy.getScannerIds();
        zapScanner.setEnableScanners(scannerIds, true);
        return scannerIds;
    }

    private void spiderWithZap(String baseUrl) {
        zapScanner.setThreadCount(5);
        zapScanner.setMaxDepth(5);
        zapScanner.setPostForms(false);
        zapScanner.spider(baseUrl);
        int spiderID = zapScanner.getLastSpiderScanId();
        int complete = 0;
        while (complete < 100) {
            complete = zapScanner.getSpiderProgress(spiderID);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Thread was interrupted {}", e.getMessage());
            }
        }
        for (String url : zapScanner.getSpiderResults(spiderID)) {
            LOG.info("Found URL: " + url);
        }
    }

    private void scan(String url) {
        LOG.info("Spidering...");
        spiderWithZap(url);
        LOG.info("Spider done.");
//        setAlertAndAttackStrength();
        scanWithZap(url);
        List<Alert> alerts = filterAlerts(zapScanner.getAlerts());
        logAlerts(alerts);
    }
}
