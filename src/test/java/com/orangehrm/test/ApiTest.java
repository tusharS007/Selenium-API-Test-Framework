package com.orangehrm.test;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.orangehrm.utilities.ApiUtility;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;

import io.restassured.response.Response;

public class ApiTest {
	//@Test(retryAnalyzer = RetryAnalyzer.class) - Retry logic at class level
	@Test(retryAnalyzer = RetryAnalyzer.class)
	public void verifyGetUserAPI() {

		SoftAssert softAssert = new SoftAssert();

		// Step1: Define API Endpoint
		String endpoint = "https://jsonplaceholder.typicode.com/users/1";
		ExtentManager.logStep("API EndPoint: " + endpoint);

		// Step3: Send GET Request
		ExtentManager.logStep("Sending Get Request to the API");
		Response response = ApiUtility.sendGetRequest(endpoint);

		// Step3: Validate status code
		ExtentManager.logStep("Validating API Response Status code");
		boolean isStatusCodeValid = ApiUtility.validateStatusCode(response, 200);
		softAssert.assertTrue(isStatusCodeValid, "Status code is not as Expected");
		if (isStatusCodeValid) {
			ExtentManager.logStepValidationAPI("Status code Validation Passed!");
		} else {
			ExtentManager.logFailureAPI("Status code Validation Failed!");
		}

		// Step4: Validate user name
		ExtentManager.logStep("Validating response body for username");
		String username = ApiUtility.getJsonValue(response, "username");
		boolean isUsernameValid = "Bret".equals(username);
		softAssert.assertTrue(isUsernameValid, "Username is invalid");
		if (isUsernameValid) {
			ExtentManager.logStepValidationAPI("Username is Valid!");
		} else {
			ExtentManager.logFailureAPI("Username Validation Failed!");
		}

		// Step4: Validate email id
		ExtentManager.logStep("Validating response body for emailID");
		String emailID = ApiUtility.getJsonValue(response, "email");
		boolean isEmailValid = "Sincere@april.biz".equals(emailID);
		softAssert.assertTrue(isUsernameValid, "Email is invalid");
		if (isEmailValid) {
			ExtentManager.logStepValidationAPI("Email is Valid!");
		} else {
			ExtentManager.logFailureAPI("Email Validation Failed!");
		}
		softAssert.assertAll();
	}
}
