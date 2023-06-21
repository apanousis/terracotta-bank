package com.joshcummings.codeplay.terracotta.testng;

import com.joshcummings.codeplay.terracotta.security.SecurityScannerEventListener;
import com.joshcummings.codeplay.terracotta.security.ZapProxyScanner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.zaproxy.clientapi.core.ClientApiException;

import java.net.MalformedURLException;
import java.net.URL;

public class SeleniumSupport {

    private final static String ZAP_HOST = "owasp-zap";
    private final static Integer ZAP_PORT = 8090;

    public WebDriver start(String driverType) {
        if ("firefox".equals(driverType)) {
            return startFirefox();
        }
        return startChrome();
    }

    private WebDriver startFirefox() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxProfile profile = new FirefoxProfile();

        profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.http", ZAP_HOST);
        profile.setPreference("network.proxy.http_port", ZAP_PORT);
        profile.setPreference("network.proxy.ssl", ZAP_HOST);
        profile.setPreference("network.proxy.ssl_port", ZAP_PORT);

        FirefoxOptions capabilities = new FirefoxOptions();
        capabilities.setProfile(profile);

        return buildWebDriver(capabilities);
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
        proxy.setHttpProxy(ZAP_HOST + ":" + ZAP_PORT);
        proxy.setFtpProxy(ZAP_HOST + ":" + ZAP_PORT);
        proxy.setSslProxy(ZAP_HOST + ":" + ZAP_PORT);
        capabilities.setCapability(CapabilityType.PROXY, proxy);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        return buildWebDriver(capabilities);
    }

    private EventFiringWebDriver buildWebDriver(MutableCapabilities capabilities) {
        try {
            EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(
                    new RemoteWebDriver(new URL("http://selenium-hub:4444"), capabilities)
            );
            SecurityScannerEventListener securityScannerEventListener = new SecurityScannerEventListener();
            securityScannerEventListener.setZapScanner(new ZapProxyScanner(ZAP_HOST, ZAP_PORT, false));
            eventFiringWebDriver.register(securityScannerEventListener);
            return eventFiringWebDriver;
        } catch (MalformedURLException | ClientApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
