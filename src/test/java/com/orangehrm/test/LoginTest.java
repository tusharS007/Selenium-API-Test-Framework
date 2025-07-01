package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;

public class LoginTest extends BaseClass {

	private LoginPage loginPage;
	private HomePage homePage;

	@BeforeMethod
	public void setUpPages() {
		loginPage = new LoginPage();
		homePage = new HomePage();

	}

	@Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class)
	public void LoginApp(String username, String password) {

		SoftAssert softAssert = getSoftAssert();

		ExtentManager.logStep("Navigating to login page entering username and password");
		loginPage.login(username, password);

		ExtentManager.logStep("Verifying Admin tab is visible or not");
		boolean isAdminVisible = homePage.isAdminTabVisible();
		if (isAdminVisible) {
			ExtentManager.logStep("Admin tab is visible as expected.");
		} else {
			ExtentManager.logFailure(BaseClass.getDriver(), "Admin Tab Not Visible",
					"Expected Admin tab to be visible, but it was not.");
		}
		softAssert.assertTrue(isAdminVisible, "Admin tab should be visible");

		ExtentManager.logStep("Verifying PIM tab is visible or not");
		boolean isPIMVisible = homePage.isPIMTabVisible();
		if (isPIMVisible) {
			ExtentManager.logStep("PIM tab is visible as expected.");
		} else {
			ExtentManager.logFailure(BaseClass.getDriver(), "PIM Tab Not Visible",
					"Expected PIM tab to be visible, but it was not.");
		}
		softAssert.assertTrue(isPIMVisible, "PIM tab should be visible");

		softAssert.assertAll();

		homePage.logout();
		ExtentManager.logStep("Logged out successfully");
		System.out.println("Test passed: login Successful");
	}

	@Test(dataProvider="inValidLoginData", dataProviderClass=DataProviders.class)
	public void invalidLoginTest(String username, String password) {
		//ExtentManager.startTest("Invalid Login Test"); This has been implemented in test Listener
		ExtentManager.logStep("Navigating to login page entering username and password");
		loginPage.login(username, password);
		String expectedError = "Invalid credentials";
		//ExtentManager.logStep("Validation Successful");
		Assert.assertTrue(loginPage.verifyErrorMessage(expectedError), "Test failed: Invalid error message");
		ExtentManager.logStep("Validation Successful");
	}
}
