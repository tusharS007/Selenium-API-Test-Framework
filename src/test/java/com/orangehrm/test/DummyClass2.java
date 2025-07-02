package com.orangehrm.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class DummyClass2 extends BaseClass {
	@Test
	public void dummyTEst() {
		// ExtentManager.startTest("Dummy Test2"); - This has been implemented in test Listener
		//Test Checking last
		String title = getDriver().getTitle();
		ExtentManager.logStep("Verifying title");
		Assert.assertEquals(title, "OrangeHRM");
		System.out.println("Test passed title is matching");
		ExtentManager.logStep("Validation Successful");
	}

}
