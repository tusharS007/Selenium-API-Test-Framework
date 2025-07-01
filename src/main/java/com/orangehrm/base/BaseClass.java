package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.orangehrm.actionDriver.ActionDriver;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

public class BaseClass {

	protected static Properties prop;
	// Protected static WebDriver driver;
	// Private static ActionDriver action;
	// To perform parallel testing we created thread local
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static ThreadLocal<ActionDriver> action = new ThreadLocal<>();
	public static final Logger log = LoggerManager.getLogger(BaseClass.class);
	protected ThreadLocal<SoftAssert> sofAssert = ThreadLocal.withInitial(SoftAssert::new);

	public SoftAssert getSoftAssert() {
		return sofAssert.get();
	}

	@BeforeSuite
	public void loadConfig() throws IOException {
		// load configuration file
		prop = new Properties();
		FileInputStream fis = new FileInputStream(
				System.getProperty("user.dir") + "/src/main/resources/config.properties");
		prop.load(fis);
		log.info("Properties file loaded");

		// start extent reporter
		// ExtentManager.getReporter(); - This has been implemented in test Listener
	}

	@BeforeMethod
	public synchronized void setup() throws IOException {
		System.out.println("Setting up WebDriver for: " + this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(2);
		log.info("Webdriver initialized and Browser Maximized");
//		log.trace("This is trace");
//		log.debug("This is debug");
//		log.warn("This is warn");
//		log.error("This is error");
//		log.fatal("This is fatal");

		/*
		 * initialize the actionDriver only once if (action == null) { action = new
		 * ActionDriver(driver);
		 * log.info("Action driver instance is created"+Thread.currentThread().getId());
		 * }
		 */
		// Initialize actionDriver for the current thread
		action.set(new ActionDriver(getDriver()));
		log.info("Action driver is initialized for thread: " + Thread.currentThread().getId());
	}

	private synchronized void launchBrowser() {

		// initialize webdriver
		String browser = prop.getProperty("browser");
		if (browser.equalsIgnoreCase("chrome")) {

			// Create ChromeOptions
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless"); // Run Chrome in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU for headless mode
			// options.addArguments("--window-size=1920,1080"); // Set window size
			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
			options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource-limited environments

			// driver = new ChromeDriver();
			driver.set(new ChromeDriver(options)); // new changes as per thread local
			ExtentManager.registerDriver(getDriver());
			log.info("ChromeDriver launched");
		} else if (browser.equalsIgnoreCase("edge")) {
			
			EdgeOptions options = new EdgeOptions();
			options.addArguments("--headless"); // Run Edge in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU acceleration
			options.addArguments("--window-size=1920,1080"); // Set window size
			options.addArguments("--disable-notifications"); // Disable pop-up notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD
			options.addArguments("--disable-dev-shm-usage"); // Prevent resource-limited crashes
			
			// driver = new EdgeDriver();
			driver.set(new EdgeDriver(options));
			ExtentManager.registerDriver(getDriver());
			log.info("EdgeDriver launched");
		} else if (browser.equalsIgnoreCase("firefox")) {

			// Create FirefoxOptions
			FirefoxOptions options = new FirefoxOptions();
			options.addArguments("--headless"); // Run Firefox in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU rendering (useful for headless mode)
			options.addArguments("--width=1920"); // Set browser width
			options.addArguments("--height=1080"); // Set browser height
			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD environments
			options.addArguments("--disable-dev-shm-usage"); // Prevent crashes in low-resource environments

			// driver = new FirefoxDriver();
			driver.set(new FirefoxDriver(options));
			ExtentManager.registerDriver(getDriver());
			log.info("FirefoxDriver launched");
		} else {
			throw new IllegalArgumentException("Browser not supported:" + browser);
		}
	}

	private void configureBrowser() {
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		getDriver().manage().window().maximize();
		try {
			getDriver().get(prop.getProperty("url"));
		} catch (Exception e) {
			log.info("Failed to navigate to the URL:" + e.getMessage());
		}
	}

	@AfterMethod // synchronized keyword is used so that paralle testind dont have any issue with
					// methods
	public synchronized void tearDown() {
		if (getDriver() != null) {
			getDriver().quit();
		}
		log.info("WebDriver instance is closed");
		driver.remove();
		action.remove();
		// driver = null;
		// action = null;
		// ExtentManager.endTest(); - This has been implemented in test Listener
	}

	// Getter Method for WebDriver
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			log.info("WebDriver is not initialized");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get();
	}

	// Getter Method for ActionDriver
	public static ActionDriver getActionDriver() {
		if (action.get() == null) {
			log.info("ActionDriver is not initialized");
			throw new IllegalStateException("ActionDriver is not initialized");
		}
		return action.get();
	}

	// Static wait for pause
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}

}
