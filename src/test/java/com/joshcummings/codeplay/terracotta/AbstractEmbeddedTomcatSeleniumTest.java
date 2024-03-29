package com.joshcummings.codeplay.terracotta;

import com.joshcummings.codeplay.terracotta.testng.HttpSupport;
import com.joshcummings.codeplay.terracotta.testng.SeleniumSupport;
import com.joshcummings.codeplay.terracotta.testng.TestConstants;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.concurrent.TimeUnit;

public class AbstractEmbeddedTomcatSeleniumTest {
    static WebDriver driver;

    protected SeleniumSupport selenium = new SeleniumSupport();
    protected HttpSupport http = new HttpSupport();

    @BeforeTest(alwaysRun = true)
    public void startSelenium() {
        driver = selenium.start(System.getProperty("driver"));
    }

    @AfterTest(alwaysRun = true)
    public void shutdownSelenium() {
        selenium.stop(driver);
    }

    protected void goToPage(String page) {
        driver.get("http://" + TestConstants.host + page);
    }

    protected void login(String username, String password) {
        goToPage("/");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("login")).submit();
        FluentWait<WebDriver> wait = new WebDriverWait(driver, 2).pollingEvery(100, TimeUnit.MILLISECONDS);
        wait.until(driver -> driver.findElement(By.id("service")) != null);
    }

    protected void employeeLogin(String username, String password) {
        goToPage("/employee.jsp");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.name("login")).submit();
        FluentWait<WebDriver> wait = new WebDriverWait(driver, 2).pollingEvery(100, TimeUnit.MILLISECONDS);
        wait.until(driver -> driver.findElement(By.id("service")) != null);
    }

    protected void logout() {
        goToPage("/logout");
    }

    protected String getTextThenDismiss(Alert alert) {
        String text = alert.getText();
        alert.dismiss();
        return text;
    }

    protected void ignoreErrors(Runnable r) {
        try {
            r.run();
        } catch (Exception e) {
            // eat
        }
    }

    protected Alert switchToAlertEventually(WebDriver driver, long timeoutInMilliseconds) throws NoAlertPresentException {
        long now = System.currentTimeMillis();
        try {
            return driver.switchTo().alert();
        } catch (NoAlertPresentException e) {
            if (timeoutInMilliseconds <= 0) {
                throw e;
            }
        }

        try {
            Thread.sleep(100);
            return switchToAlertEventually(driver, timeoutInMilliseconds - (System.currentTimeMillis() - now));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NoAlertPresentException(e);
        }
    }

    protected WebElement findElementEventually(WebDriver driver, By by, long timeoutInMilliseconds) throws NoSuchElementException {
        long now = System.currentTimeMillis();
        try {
            return driver.findElement(by);
        } catch (NoSuchElementException e) {
            if (timeoutInMilliseconds <= 0) {
                throw e;
            }
        }

        try {
            Thread.sleep(100);
            return findElementEventually(driver, by, timeoutInMilliseconds - (System.currentTimeMillis() - now));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NoSuchElementException(e.getMessage());
        }
    }
}
