package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class HomePageTest extends BaseClass {

	private LoginPage login;
	private HomePage home;

	@BeforeMethod
	public void setUpPages() {
		login = new LoginPage();
		home = new HomePage();

	}

	@Test(dataProvider="validLoginData", dataProviderClass=DataProviders.class)
	public void verifyOrangeHRMLogo(String username, String password) {
		// ExtentManager.startTest("Home Page logo Test"); -This has been implemented in test Listener
		ExtentManager.logStep("Navigating to login page entering username and password");
		login.login(username, password);
		ExtentManager.logStep("Verify Logo is visible or not");
		Assert.assertTrue(home.verifyOrnageHRMLogo(), "Logo is not visible");
		ExtentManager.logStep("Validation Successful");
		ExtentManager.logStep("Logged out successfully");
	}
}
