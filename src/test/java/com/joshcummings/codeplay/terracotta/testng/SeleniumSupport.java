package com.joshcummings.codeplay.terracotta.testng;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import com.joshcummings.codeplay.terracotta.security.SecurityScannerEventListener;
import com.joshcummings.codeplay.terracotta.security.ZapProxyScanner;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumSupport {

    private static String ZAP_HOST = "localhost";
    private static Integer ZAP_PORT = 8090;

    public WebDriver start(String driverType) throws Exception {
        if ("chrome".equals(driverType)) {
            return startChrome();
        } else if ("firefox".equals(driverType)) {
            return startFirefox();
        } else {
             throw new Exception("Incompatible driver type");
        }
    }

    private WebDriver startFirefox() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxProfile profile = new FirefoxProfile();

//        profile.setPreference("network.proxy.type", 1);
//        profile.setPreference("network.proxy.http", ZAP_HOST);
//        profile.setPreference("network.proxy.http_port", ZAP_PORT);
//        profile.setPreference("network.proxy.ssl", ZAP_HOST);
//        profile.setPreference("network.proxy.ssl_port", ZAP_PORT);

        FirefoxOptions options = new FirefoxOptions();
        options.setProfile(profile);

        return new FirefoxDriver(options);
    }

    public void stop(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebDriver startChrome() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("disable-geolocation");

        Proxy proxy = new Proxy();
        proxy.setHttpProxy("owasp-zap:8090");
        proxy.setFtpProxy("owasp-zap:8090");
        proxy.setSslProxy("owasp-zap:8090");
        capabilities.setCapability(CapabilityType.PROXY, proxy);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        try {
            EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(new RemoteWebDriver(new URL("http://localhost:4444"), capabilities));
            SecurityScannerEventListener securityScannerEventListener = new SecurityScannerEventListener();
            securityScannerEventListener.setZapScanner(new ZapProxyScanner("127.0.0.1", 8090));
            eventFiringWebDriver.register(securityScannerEventListener);
            return eventFiringWebDriver;
        } catch (MalformedURLException e) {

        }
        return null;
    }
}
