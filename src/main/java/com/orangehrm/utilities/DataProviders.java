package com.orangehrm.utilities;

import java.util.List;

import org.testng.annotations.DataProvider;

public class DataProviders {

	public static final String File_Path = System.getProperty("user.dir")+"/src/test/resources/testdata/TestData.xlsx";
	
	@DataProvider(name="validLoginData")
	public static Object[][] validLoginData(){
		return getSheetData("validLoginData");
	}
	
	@DataProvider(name="inValidLoginData")
	public static Object[][] inValidLoginData(){
		return getSheetData("inValidLoginData");
	}
	
	private static Object[][] getSheetData(String sheetName) {
		List<String[]> sheetdata = ExcelReaderUtility.getSheetData(File_Path, sheetName);
		 
		 Object[][] data = new Object[sheetdata.size()][sheetdata.get(0).length];
	
		 for(int i=0;i<sheetdata.size();i++) {
			 data[i] = sheetdata.get(i);
		 }
		 return data;
	}
}
