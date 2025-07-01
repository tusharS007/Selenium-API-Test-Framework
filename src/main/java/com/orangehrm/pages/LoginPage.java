package com.orangehrm.pages;

import org.openqa.selenium.By;

import com.orangehrm.actionDriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class LoginPage {

	private ActionDriver action;

	private By usernameField = By.xpath("//input[@name='username']");
	private By paaswordField = By.cssSelector("input[type='password']");
	private By loginButton = By.xpath("//button[@type='submit']");
	private By errorMessage = By.xpath("//p[text()='Invalid credentials']");

//	public LoginPage(WebDriver driver) {
//		this.action = new ActionDriver(driver);
//	}

	public LoginPage() {
		this.action = BaseClass.getActionDriver();// get singleton instance
	}

	public void login(String username, String password) {

		action.enterText(usernameField, username);
		action.enterText(paaswordField, password);
		action.click(loginButton);
	}

	public void getErrorMessage() {
		action.getText(errorMessage);
	}

	public boolean verifyErrorMessage(String expectedError) {
		return action.compareText(errorMessage, expectedError);
	}
}
