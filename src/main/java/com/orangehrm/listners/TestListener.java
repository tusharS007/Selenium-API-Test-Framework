package com.orangehrm.listners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

public class TestListener implements ITestListener, IAnnotationTransformer {

	// Triggered when test start
	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		// Start logging in extent reports
		ExtentManager.startTest(testName);
		ExtentManager.logStep("Test Started: " + testName);
	}

	@Override
	public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
		annotation.setRetryAnalyzer(RetryAnalyzer.class);
	}

	// Triggered when test success
	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		if (!result.getTestClass().getName().toLowerCase().contains("api")) {
			ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test Passed Successfully!",
					"Test End: " + testName + "- ✅ Test Passed");
		} else {

			ExtentManager.logStepValidationAPI("Test End: " + testName + "- ✅ Test Passed");
		}
	}

	// Triggered when test fails
	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		String faillureMessage = result.getThrowable().getMessage();
		ExtentManager.logStep(faillureMessage);
		if (!result.getTestClass().getName().toLowerCase().contains("api")) {
			ExtentManager.logFailure(BaseClass.getDriver(), "Test Failled!",
					"Test End: " + testName + "- ❌ Test Failed");

		} else {
			ExtentManager.logFailureAPI("Test End: " + testName + "- ❌ Test Failed");
		}
	}

	// Triggered when test is skipped
	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		ExtentManager.logSkip("Test Skipped: " + testName);
	}

	// Triggered when a suite start
	@Override
	public void onStart(ITestContext context) {
		// Initialize Extent report
		ExtentManager.getReporter();
	}

	// Triggered when the suite ends
	@Override
	public void onFinish(ITestContext context) {
		// Flush the extent report
		ExtentManager.endTest();

	}

}
