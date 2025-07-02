package com.orangehrm.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
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
	private boolean isHeadless = false;

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
	@Parameters("browser")
	public synchronized void setup(String browser) throws IOException {
		System.out.println("Setting up WebDriver for: " + this.getClass().getSimpleName());
		launchBrowser(browser);
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

	/*
	 * Initialize the WebDriver based on browser defined in config.properties file
	 */
	private synchronized void launchBrowser(String browser) {

		// String browser = prop.getProperty("browser");
		boolean seleniumGrid = Boolean.parseBoolean(prop.getProperty("seleniumGrid"));
		String gridURL = prop.getProperty("gridURL");
		if (seleniumGrid) {
			try {
				if (browser.equalsIgnoreCase("chrome")) {
					ChromeOptions options = new ChromeOptions();
					options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else if (browser.equalsIgnoreCase("firefox")) {
					FirefoxOptions options = new FirefoxOptions();
					options.addArguments("-headless");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else if (browser.equalsIgnoreCase("edge")) {
					EdgeOptions options = new EdgeOptions();
					options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
					driver.set(new RemoteWebDriver(new URL(gridURL), options));
				} else {
					throw new IllegalArgumentException("Browser Not Supported: " + browser);
				}
				log.info("RemoteWebDriver instance created for Grid in headless mode");
			} catch (MalformedURLException e) {
				throw new RuntimeException("Invalid Grid URL", e);
			}
		} else {
			if (browser.equalsIgnoreCase("chrome")) {

				// Create ChromeOptions
				ChromeOptions options = new ChromeOptions();
				// added new argument and boolean condition to handle window size issue in

				options.setBinary(System.getenv("CHROME_BIN"));
				options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
				isHeadless = true;
				// options.addArguments("--disable-gpu");
				// options.addArguments("--window-size=1920,1080");
				options.addArguments("--disable-notifications");
				options.addArguments("--no-sandbox");
				options.addArguments("--disable-dev-shm-usage");
				options.addArguments("--remote-allow-origins=*");
//			options.addArguments("--headless=new"); // Run Chrome in headless mode
//			options.addArguments("--disable-gpu"); // Disable GPU for headless mode
//			options.addArguments("--window-size=1920,1080"); // Set window size
//			options.addArguments("--disable-notifications"); // Disable browser notifications
//			options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
//			options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource-limited environments

				// driver = new ChromeDriver();
				driver.set(new ChromeDriver(options)); // new changes as per thread local
				getDriver().manage().window().setSize(new Dimension(1920, 1080)); // Critical!
				ExtentManager.registerDriver(getDriver());
				log.info("ChromeDriver launched");
			} else if (browser.equalsIgnoreCase("edge")) {

				EdgeOptions options = new EdgeOptions();
				options.addArguments("--headless"); // Run Edge in headless mode
				isHeadless = true;
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
				isHeadless = true;
				options.addArguments("--disable-gpu"); // Disable GPU rendering (useful for headless mode)
				options.addArguments("--window-size=1920,1080");
				// options.addArguments("--width=1920"); // Set browser width
				// options.addArguments("--height=1080"); // Set browser height
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
	}

	private void configureBrowser() {
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		// getDriver().manage().window().maximize(); // Comment this line whenever want
		// to run in headless as this causing screen size issues.
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
