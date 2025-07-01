package com.orangehrm.actionDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class ActionDriver {

	private WebDriver driver;
	private WebDriverWait wait;
	public static final Logger log = BaseClass.log;
	// private Properties prop;

//initialize driver and wait in constructor
	public ActionDriver(WebDriver driver) {
		// public ActionDriver(WebDriver driver, Properties prop)
		this.driver = driver;
		// this.prop = prop;
		// int explicitwait = Integer.parseInt(prop.getProperty("explicitwait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		log.info("WebDriver instance is created");
	}

	// click method
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		try {
			waitForElementToBeClcikable(by);
			applyBorder(by, "green");
			driver.findElement(by).click();
			ExtentManager.logStep("Clicked an element: " + elementDescription);
			log.info("Element is clicked-->" + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			System.out.println("Unable to click element: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to click an element: " + elementDescription,
					"_unable to click");
			log.error("Element is not clicked");
		}
	}

	public void enterText(By by, String value) {
		waitForElementToBeVisible(by);
		try {
			WebElement element = driver.findElement(by);
			applyBorder(by, "green");
			element.clear();
			element.sendKeys(value);
			log.info("Entered text :" + getElementDescription(by) + "-->" + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to input text: " + e.getMessage());
		}
	}

	public String getText(By by) {

		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "green");
			return driver.findElement(by).getText();
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to get text: " + e.getMessage());
			return "";
		}
	}

	public boolean isDisplayed(By by) {

		try {
			waitForElementToBeVisible(by);
			WebElement element = driver.findElement(by);

			if (element.isDisplayed()) {
				applyBorder(by, "green");
				String description = getElementDescription(by);
				log.info("Element is displayed: " + description);
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is displayed",
						"Element is displayed: " + description);
				return true;
			} else {
				applyBorder(by, "red");
				String description = getElementDescription(by);
				log.warn("Element is NOT displayed: " + description);
				ExtentManager.logFailure(BaseClass.getDriver(), "Element is NOT Displayed",
						"Element is NOT displayed: " + description);
				return false;
			}
		} catch (Exception e) {
			applyBorder(by, "red");
			String description = getElementDescription(by);
			log.error("Exception while checking display status: " + e.getMessage());
			ExtentManager.logFailure(BaseClass.getDriver(), "Element Is not Displayed",
					"Element Is not Displayed: " + description);
			return false;
		}

//		try {
//			waitForElementToBeVisible(by);
//			applyBorder(by, "green");
//			log.info("Element is displayed" + getElementDescription(by));
//			//ExtentManager.logStep("Element Is Displayed" + getElementDescription(by));
//			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Element is displayed",
//					"Element is displayed: " + getElementDescription(by));
//			return driver.findElement(by).isDisplayed();
//
//		} catch (Exception e) {
//			applyBorder(by, "red");
//			log.error("Elemetn is not displayed");
//			ExtentManager.logFailure(BaseClass.getDriver(), "Element Is not Displayed",
//					"Element Is not Displayed" + getElementDescription(by));
//			return false;
//		}
	}

	public void waitForPageLoad(int timeOutinSec) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeOutinSec)).until(WebDriver -> ((JavascriptExecutor) WebDriver)
					.executeScript("return document.readyState").equals("complete"));
			log.info("Page loaded succesfully");
		} catch (Exception e) {
			log.error("Page did not load within " + timeOutinSec + " seconds.Exception " + e.getMessage());
		}
	}

	public void scrollToElement(By by) {
		try {
			applyBorder(by, "green");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement element = driver.findElement(by);
			js.executeScript("arguments[0],scrollIntoView(true);", element);
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to locate element: " + e.getMessage());
		}
	}

	// wait for element to be clickable
	public void waitForElementToBeClcikable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			log.error("Element is not clickable: " + e.getMessage());
		}
	}

	// wait for element to be visible
	public void waitForElementToBeVisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			log.error("Element is not visible: " + e.getMessage());
		}
	}

	public boolean compareText(By by, String expectedText) {
		try {
			waitForElementToBeVisible(by);
			String actualText = driver.findElement(by).getText();
			if (expectedText.equals(actualText)) {
				applyBorder(by, "green");
				log.info("Text are matching " + actualText + " equals " + expectedText);
				ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Comapre Text",
						"Text Verified" + actualText + " equals " + expectedText);
				return true;
			} else {
				applyBorder(by, "red");
				log.error("Text are not matching " + actualText + " not equals to " + expectedText);
				ExtentManager.logFailure(BaseClass.getDriver(), "Text Comparision failed",
						"Text Comparision failed" + actualText + " not equals " + expectedText);
				return false;
			}
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to compare text" + e.getMessage());
		}
		return false;
	}

	public String getElementDescription(By locator) {
		// check for null driver or locator for null pointer exception
		if (driver == null)
			return "Driver is null";
		if (locator == null)
			return "Locator is null";

		try {
			// find the element using locator
			WebElement element = driver.findElement(locator);
			// get element attributes
			String name = element.getDomAttribute("name");
			String id = element.getDomAttribute("id");
			String text = element.getText();
			String className = element.getDomAttribute("class");
			String placeHolder = element.getDomAttribute("placeholder");
			// return the description based on element attributes
			if (isNotEmpty(name))
				return "Element with name: " + name;
			else if (isNotEmpty(id))
				return "Element with id: " + id;
			else if (isNotEmpty(text))
				return "Element with text: " + truncate(text, 50);
			else if (isNotEmpty(className))
				return "Element with class: " + className;
			else if (isNotEmpty(placeHolder))
				return "Element with placeHolder: " + placeHolder;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Unable to describe the element" + e.getMessage());
			;
		}
		return "Locator: " + locator.toString();
	}

	private boolean isNotEmpty(String value) {
		return value != null && !value.isEmpty();
	}

	private String truncate(String value, int maxLength) {
		if (value == null || value.length() < maxLength)
			return value;

		return value.substring(0, maxLength) + "...";
	}

	// Utility Method to Border an element
	public void applyBorder(By by, String color) {
		try {
			// Locate the element
			WebElement element = driver.findElement(by);
			// apply border
			String script = "arguments[0].style.border='3px solid " + color + "'";
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(script, element);
			log.info("Applied the border with color " + color + " to element " + getElementDescription(by));
		} catch (Exception e) {
			log.warn("Failed to apply the color: " + getElementDescription(by), e);
		}
	}

	// ===================== Select Methods =====================

	// Method to select a dropdown by visible text
	public void selectByVisibleText(By by, String value) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByVisibleText(value);
			applyBorder(by, "green");
			log.info("Selected dropdown value: " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to select dropdown value: " + value, e);
		}
	}

	// Method to select a dropdown by value
	public void selectByValue(By by, String value) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByValue(value);
			applyBorder(by, "green");
			log.info("Selected dropdown value by actual value: " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to select dropdown by value: " + value, e);
		}
	}

	// Method to select a dropdown by index
	public void selectByIndex(By by, int index) {
		try {
			WebElement element = driver.findElement(by);
			new Select(element).selectByIndex(index);
			applyBorder(by, "green");
			log.info("Selected dropdown value by index: " + index);
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to select dropdown by index: " + index, e);
		}
	}

	// Method to get all options from a dropdown
	public List<String> getDropdownOptions(By by) {
		List<String> optionsList = new ArrayList<>();
		try {
			WebElement dropdownElement = driver.findElement(by);
			Select select = new Select(dropdownElement);
			for (WebElement option : select.getOptions()) {
				optionsList.add(option.getText());
			}
			applyBorder(by, "green");
			log.info("Retrieved dropdown options for " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to get dropdown options: " + e.getMessage());
		}
		return optionsList;
	}

	// ===================== JavaScript Utility Methods =====================

	// Method to click using JavaScript
	public void clickUsingJS(By by) {
		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			applyBorder(by, "green");
			log.info("Clicked element using JavaScript: " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to click using JavaScript", e);
		}
	}

	// Method to scroll to the bottom of the page
	public void scrollToBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
		log.info("Scrolled to the bottom of the page.");
	}

	// Method to highlight an element using JavaScript
	public void highlightElementJS(By by) {
		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid yellow'", element);
			log.info("Highlighted element using JavaScript: " + getElementDescription(by));
		} catch (Exception e) {
			log.error("Unable to highlight element using JavaScript", e);
		}
	}

	// ===================== Window and Frame Handling =====================

	// Method to switch between browser windows
	public void switchToWindow(String windowTitle) {
		try {
			Set<String> windows = driver.getWindowHandles();
			for (String window : windows) {
				driver.switchTo().window(window);
				if (driver.getTitle().equals(windowTitle)) {
					log.info("Switched to window: " + windowTitle);
					return;
				}
			}
			log.warn("Window with title " + windowTitle + " not found.");
		} catch (Exception e) {
			log.error("Unable to switch window", e);
		}
	}

	// Method to switch to an iframe
	public void switchToFrame(By by) {
		try {
			driver.switchTo().frame(driver.findElement(by));
			log.info("Switched to iframe: " + getElementDescription(by));
		} catch (Exception e) {
			log.error("Unable to switch to iframe", e);
		}
	}

	// Method to switch back to the default content
	public void switchToDefaultContent() {
		driver.switchTo().defaultContent();
		log.info("Switched back to default content.");
	}

	// ===================== Alert Handling =====================

	// Method to accept an alert popup
	public void acceptAlert() {
		try {
			driver.switchTo().alert().accept();
			log.info("Alert accepted.");
		} catch (Exception e) {
			log.error("No alert found to accept", e);
		}
	}

	// Method to dismiss an alert popup
	public void dismissAlert() {
		try {
			driver.switchTo().alert().dismiss();
			log.info("Alert dismissed.");
		} catch (Exception e) {
			log.error("No alert found to dismiss", e);
		}
	}

	// Method to get alert text
	public String getAlertText() {
		try {
			return driver.switchTo().alert().getText();
		} catch (Exception e) {
			log.error("No alert text found", e);
			return "";
		}
	}

	// ===================== Browser Actions =====================

	public void refreshPage() {
		try {
			driver.navigate().refresh();
			ExtentManager.logStep("Page refreshed successfully.");
			log.info("Page refreshed successfully.");
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to refresh page", "refresh_page_failed");
			log.error("Unable to refresh page: " + e.getMessage());
		}
	}

	public String getCurrentURL() {
		try {
			String url = driver.getCurrentUrl();
			ExtentManager.logStep("Current URL fetched: " + url);
			log.info("Current URL fetched: " + url);
			return url;
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to fetch current URL", "get_current_url_failed");
			log.error("Unable to fetch current URL: " + e.getMessage());
			return null;
		}
	}

	public void maximizeWindow() {
		try {
			driver.manage().window().maximize();
			ExtentManager.logStep("Browser window maximized.");
			log.info("Browser window maximized.");
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to maximize window", "maximize_window_failed");
			log.error("Unable to maximize window: " + e.getMessage());
		}
	}

	// ===================== Advanced WebElement Actions =====================

	public void moveToElement(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(driver.findElement(by)).perform();
			ExtentManager.logStep("Moved to element: " + elementDescription);
			log.info("Moved to element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to move to element",
					elementDescription + "_move_failed");
			log.error("Unable to move to element: " + e.getMessage());
		}
	}

	public void dragAndDrop(By source, By target) {
		String sourceDescription = getElementDescription(source);
		String targetDescription = getElementDescription(target);
		try {
			Actions actions = new Actions(driver);
			actions.dragAndDrop(driver.findElement(source), driver.findElement(target)).perform();
			ExtentManager.logStep("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
			log.info("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to drag and drop",
					sourceDescription + "_drag_failed");
			log.error("Unable to drag and drop: " + e.getMessage());
		}
	}

	public void doubleClick(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.doubleClick(driver.findElement(by)).perform();
			ExtentManager.logStep("Double-clicked on element: " + elementDescription);
			log.info("Double-clicked on element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to double-click element",
					elementDescription + "_doubleclick_failed");
			log.error("Unable to double-click element: " + e.getMessage());
		}
	}

	public void rightClick(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.contextClick(driver.findElement(by)).perform();
			ExtentManager.logStep("Right-clicked on element: " + elementDescription);
			log.info("Right-clicked on element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to right-click element",
					elementDescription + "_rightclick_failed");
			log.error("Unable to right-click element: " + e.getMessage());
		}
	}

	public void sendKeysWithActions(By by, String value) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(driver);
			actions.sendKeys(driver.findElement(by), value).perform();
			ExtentManager.logStep("Sent keys to element: " + elementDescription + " | Value: " + value);
			log.info("Sent keys to element --> " + elementDescription + " | Value: " + value);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to send keys",
					elementDescription + "_sendkeys_failed");
			log.error("Unable to send keys to element: " + e.getMessage());
		}
	}

	public void clearText(By by) {
		String elementDescription = getElementDescription(by);
		try {
			driver.findElement(by).clear();
			ExtentManager.logStep("Cleared text in element: " + elementDescription);
			log.info("Cleared text in element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Unable to clear text",
					elementDescription + "_clear_failed");
			log.error("Unable to clear text in element: " + e.getMessage());
		}
	}

	// Method to upload a file
	public void uploadFile(By by, String filePath) {
		try {
			driver.findElement(by).sendKeys(filePath);
			applyBorder(by, "green");
			log.info("Uploaded file: " + filePath);
		} catch (Exception e) {
			applyBorder(by, "red");
			log.error("Unable to upload file: " + e.getMessage());
		}
	}

}
