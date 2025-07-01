package com.orangehrm.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

	private static ExtentReports extent;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static Map<Long, WebDriver> driveMap = new HashMap<>();

	// Initialize extent report
	public synchronized static ExtentReports getReporter() {
		if (extent == null) {
			String reportPath = System.getProperty("user.dir") + "/src/test/resources/ExtentReport/ExtentReport.html";
			// System.out.println(reportPath);

			ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
			spark.config().setReportName("Automation test report");
			spark.config().setDocumentTitle("OrangeHrmReport");
			spark.config().setTheme(Theme.STANDARD);

			extent = new ExtentReports();
			extent.attachReporter(spark);
			// Adding system information
			extent.setSystemInfo("Operating system", System.getProperty("os-name"));
			extent.setSystemInfo("Java version", System.getProperty("java.version"));
			extent.setSystemInfo("User name", System.getProperty("user.name"));
		}
		return extent;
	}

	// start the test
	public synchronized static ExtentTest startTest(String testname) {
		ExtentTest extentTest = getReporter().createTest(testname);
		test.set(extentTest);
		return extentTest;
	}

	// End test
	public synchronized static void endTest() {
		getReporter().flush();
	}

	// Get Current threads Test
	public synchronized static ExtentTest getTest() {
		return test.get();
	}

	// Method to get the name of the current test
	public static String getTestName() {
		ExtentTest currentTest = getTest();
		if (currentTest != null) {
			return currentTest.getModel().getName();
		} else {
			return "No test is currently active for thread";
		}
	}

	// Log the step
	public static void logStep(String logMessage) {
		getTest().info(logMessage);
	}

	// log a step validation with screenshot
	public static void logStepWithScreenshot(WebDriver driver, String logMessage, String screenshotMessage) {
		getTest().pass(logMessage);
		// screenshot method
		attachScreenshot(driver, screenshotMessage);
	}
	
	// log a step validation for API
		public static void logStepValidationAPI( String logMessage) {
			getTest().pass(logMessage);
		}

	// Log Failure
	public static void logFailure(WebDriver driver, String logMessage, String screenshotMessage) {
		String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
		getTest().fail(colorMessage);
		// screenshot method
		attachScreenshot(driver, screenshotMessage);
	}
	
	// Log Failure for API
		public static void logFailureAPI( String logMessage) {
			String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
			getTest().fail(colorMessage);
		}

	// Log a skip
	public static void logSkip(String logMessage) {
		//String colorMessage = String.format("<span style='color:orange;'>%$</span>", logMessage);
		String colorMessage = String.format("<span style='color:orange;'>%s</span>", logMessage);
		getTest().skip(colorMessage);
	}

	// Take a screenshot with date and time in a file
	public synchronized static String takeScreenshot(WebDriver driver, String screenshotName) throws IOException {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File src = ts.getScreenshotAs(OutputType.FILE);
		// Format date and time for file name
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

		// saving screenshot to file
		String destPath = System.getProperty("user.dir") + "/src/test/resources/screenshot/" + screenshotName + "_"
				+ timeStamp + ".png";
		File finalPath = new File(destPath);
		FileUtils.copyFile(src, finalPath);

		// convert screenshot to Base64 for embedding in the report
		String base64format = convertToBase64(src);
		return base64format;
	}

	// Convert screenshot to base64format
	public static String convertToBase64(File screenshotFile) {

		String base64Format = "";
		// Read the file content into a byte array
		try {
			byte[] fileContent = FileUtils.readFileToByteArray(screenshotFile);
			// Convert the byte to a base64 String
			base64Format = Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return base64Format;
	}

	// Attach screenshot to report using base64
	public synchronized static void attachScreenshot(WebDriver driver, String message) {
		try {
			String screenshotBase64 = takeScreenshot(driver, getTestName());
			getTest().info(message, com.aventstack.extentreports.MediaEntityBuilder
					.createScreenCaptureFromBase64String(screenshotBase64).build());
		} catch (IOException e) {
			getTest().fail("Failed to attach screenshot: " + message);
			e.printStackTrace();
		}
	}

	// Register WebDriver for current thread
	public static void registerDriver(WebDriver driver) {
		driveMap.put(Thread.currentThread().getId(), driver);
	}
}
