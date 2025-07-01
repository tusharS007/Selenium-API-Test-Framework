package com.orangehrm.pages;

import org.openqa.selenium.By;

import com.orangehrm.actionDriver.ActionDriver;
import com.orangehrm.base.BaseClass;

public class HomePage {

	private ActionDriver action;

	private By adminTab = By.xpath("//span[text()='Admin']");
	private By PIMTab = By.xpath("//span[text()='PIM']");
	private By userIdButton = By.className("oxd-userdropdown-tab");
	private By logoutButton = By.xpath("//a[text()='Logout']");
	private By OrangeHRMLogo = By.xpath("//div[@class='oxd-brand-banner']/img");

//	public HomePage(WebDriver driver) {
//		this.action = new ActionDriver(driver);
//	}
	
	public HomePage() {
		this.action=BaseClass.getActionDriver();
	}

	public boolean isAdminTabVisible() {
		return action.isDisplayed(adminTab);
	}
	
	public boolean isPIMTabVisible() {
		return action.isDisplayed(PIMTab);
	}

	public boolean verifyOrnageHRMLogo() {
		return action.isDisplayed(OrangeHRMLogo);
	}

	public void logout() {
		action.click(userIdButton);
		action.click(logoutButton);
	}

}
