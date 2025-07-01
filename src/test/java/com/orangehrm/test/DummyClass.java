package com.orangehrm.test;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;

public class DummyClass extends BaseClass {
	@Test
	public void dummyTEst() {
		//ExtentManager.startTest("Dummy Test"); - This has been implemented in test Listener
		String title = getDriver().getTitle();
		ExtentManager.logStep("Verifying title");
		Assert.assertEquals(title, "OrangeHRM");
		System.out.println("Test passed title is matching");
		//ExtentManager.logSkip("This Test is skipped");
		//throw new SkipException("Skipping the test as part of testing");
	}

}
