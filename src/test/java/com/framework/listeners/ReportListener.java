package com.framework.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.framework.config.ConfigReader;
import com.framework.driver.DriverDesign;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    @Override
    public synchronized void onStart(ITestContext context) {
        if (extent != null) return;

        String reportDir = ConfigReader.get("report.path");
        String reportName = ConfigReader.get("report.name");
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filePath = reportDir + reportName + "_" + timestamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(filePath);
        spark.config().setDocumentTitle(reportName);
        spark.config().setReportName(reportName);
        spark.config().setTheme(Theme.DARK);
        spark.config().setEncoding("UTF-8");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java", System.getProperty("java.version"));
        extent.setSystemInfo("Browser", ConfigReader.get("browser"));
        extent.setSystemInfo("URL", ConfigReader.get("base.url"));
        extent.setSystemInfo("Headless", ConfigReader.get("headless"));
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        ExtentTest test = extent.createTest(
                description != null && !description.isEmpty() ? description : methodName);

        test.info("Browser: " + ConfigReader.get("browser").toUpperCase());
        test.info("URL    : " + ConfigReader.get("base.url"));
        // Test login activity obeject : testNode
        testNode.set(test);
        console("TEST START", methodName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String base64 = captureScreenshot();
        testNode.get().log(
                Status.PASS,
                "Test PASSED in " + elapsedSeconds(result) + "s",
                MediaEntityBuilder.createScreenCaptureFromBase64String(base64, "Pass Screenshot").build());
        console("PASS", result.getMethod().getMethodName() + " (" + elapsedSeconds(result) + "s)");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String base64 = captureScreenshot();
        ExtentTest test = testNode.get();
        test.log(Status.FAIL,
                "Test FAILED in " + elapsedSeconds(result) + "s - "
                        + result.getThrowable().getMessage());
        test.log(Status.FAIL,
                result.getThrowable(),
                //convert a Image to base64 strem
                MediaEntityBuilder.createScreenCaptureFromBase64String(base64, "Failure Screenshot").build());
        console("FAIL", result.getMethod().getMethodName() + " - " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Throwable cause = result.getThrowable();
        testNode.get().log(Status.SKIP,
                "Test SKIPPED" + (cause != null ? " - " + cause.getMessage() : ""));
        console("SKIP", result.getMethod().getMethodName());
    }

    public static void log(Status status, String message) {
        ExtentTest test = testNode.get();
        if (test != null) test.log(status, message);
        console(status.name(), message);
    }

    public static void info(String message) {
        log(Status.INFO, message);
    }

    private static void console(String level, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + timestamp + "] [" + level + "] " + message);
    }

    private static String captureScreenshot() {
        WebDriver driver = DriverDesign.getDriver();
        if (driver == null) return "";
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            System.err.println("[ExtentReport] Screenshot failed: " + e.getMessage());
            return "";
        }
    }

    private static long elapsedSeconds(ITestResult result) {
        return (result.getEndMillis() - result.getStartMillis()) / 1000;
    }
}