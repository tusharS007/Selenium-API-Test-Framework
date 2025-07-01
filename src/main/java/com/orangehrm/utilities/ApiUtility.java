package com.orangehrm.utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiUtility {

	// Method to send the GET Request
	public static Response sendGetRequest(String endPoint) {
		return RestAssured.get(endPoint);
	}

//Method to send the POST Request
	public static Response sendPostRequest(String endPoint, String payload) {
		return RestAssured.given().header("Content-Type", "application/json").body(payload).post();

	}

//Method to validate the response status
	public static boolean validateStatusCode(Response response, int statuscode) {
		return response.getStatusCode() == statuscode;
	}

//Method to extract value from JSON response
	public static String getJsonValue(Response response, String value) {

		return response.jsonPath().getString(value);
	}
}
