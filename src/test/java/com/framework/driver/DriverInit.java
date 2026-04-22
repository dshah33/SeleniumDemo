package com.framework.driver;

import com.framework.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.Map;

public class DriverInit {

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless").toLowerCase());
        int pageLoadTimeout = Integer.parseInt(ConfigReader.get("page.load.timeout").toLowerCase());
        int implicitWait = Integer.parseInt(ConfigReader.get("implicit.wait").toLowerCase());

        WebDriver driver = switch (browser) {
            case "chrome" -> buildChrome(headless);
            case "firefox" -> buildFirefox(headless);
            case "edge" -> buildEdge(headless);
            default -> throw new IllegalArgumentException(
                    "Unsupported browser, Please select from chrome, firefox or edge browser");
        };

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

        return driver;
    }

    private static WebDriver buildChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        //disable update password popup
		/*
		 * options.addArguments("--disable-save-password-bubble");
		 * options.addArguments("--disable-notifications");
		 */
        
        options.setExperimentalOption("prefs", Map.of(
        	    "credentials_enable_service", false,
        	    "profile.password_manager_enabled", false,
        	    "profile.password_manager_leak_detection", false
        	));
        
        if (headless) options.addArguments("--headless=new");
        return new ChromeDriver(options);
    }

    private static WebDriver buildFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) options.addArguments("--headless");
        return new FirefoxDriver(options);
    }

    private static WebDriver buildEdge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        if (headless) options.addArguments("--headless=new");
        return new EdgeDriver(options);
    }
}