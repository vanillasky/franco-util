package com.francosmith.util.excel;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.json.JSONObject;

public interface DataFilter {
	
	public List<JSONObject> process(XSSFSheet sheet);
}
