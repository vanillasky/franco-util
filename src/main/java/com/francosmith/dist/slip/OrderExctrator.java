package com.francosmith.dist.slip;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.francosmith.dist.model.ColumnKey;
import com.francosmith.dist.model.PurchaseOrder;
import com.francosmith.util.Config;
import com.francosmith.util.excel.ColumnDataFilter;
import com.francosmith.util.excel.DataFilter;
import com.francosmith.util.excel.ExcelReader;

public class OrderExctrator {
	
	final Logger logger = LoggerFactory.getLogger(OrderExctrator.class);
	
	public static final SimpleDateFormat SDF_SHIP_DATE = new SimpleDateFormat("yyyy/MM/dd");
	public static final SimpleDateFormat SDF_ORDER_DATE = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	private final List<PurchaseOrder> orders = new ArrayList<PurchaseOrder>();
	private final Map<String, PurchaseOrder> orderMap = new HashMap<String, PurchaseOrder>();
	
	private List<String> orderKeys = new ArrayList<String>();
	
	public OrderExctrator() {
		
	}
	
	
	public List<PurchaseOrder> extract(String filePath, String matchDate) throws IOException {
		DataFilter filter = new ColumnDataFilter(Config.getInt("filter_date_column_index"), matchDate);
		return extract(filePath, ExcelReader.LAST_SHEET, filter);
	}
	
	public List<PurchaseOrder> extract(String filePath, int sheetNo, DataFilter filter) throws IOException {
		ExcelReader reader = new ExcelReader();
		
		JSONObject excelData = reader.filter(filePath, sheetNo, filter);
		JSONArray records = excelData.getJSONArray("result");
		
		for (int i=0, len=records.length(); i < len; i++) {
			buildPurchaseOrder(records.getJSONObject(i));
		}
		
		for(int i=0; i < orderKeys.size(); i++) {
			orders.add(orderMap.get(orderKeys.get(i)));
		}
		
		return orders;
	}
	

//	private boolean isDirectShipping(Row targetRow) {
//		Cell cell = targetRow.getCell(colNameMap.get(ColumnKey.SHIPPING_MEMO));
//		if (cell != null && cell.getStringCellValue().equals("Á÷¼Û")) {
//			return true;
//		}
//		return false;
//	}

//	private void mapHeaderName(XSSFSheet sheet) {
//		int colNum = sheet.getRow(0).getLastCellNum();
//		
//		if (sheet.getRow(0).cellIterator().hasNext()) {
//			for (int i=0; i < colNum; i++) {
//				colNameMap.put(sheet.getRow(0).getCell(i).getStringCellValue(), i);
//			}
//		}
//		
//	}

	private void buildPurchaseOrder(JSONObject lineItem) {
		
		String key = getOrderGroupKey(lineItem);
		
		if (!orderKeys.contains(key)) {
			orderKeys.add(key);
		}
		
		if (orderMap.containsKey(key)) {
			addOrder(key, lineItem);	
		} else {
			createOrder(key, lineItem);
		}
	}
	
	
	private String getOrderGroupKey(JSONObject lineItem) throws IllegalArgumentException {
		if (lineItem.get(ColumnKey.NAME) == null) {
			throw new IllegalArgumentException("Cannot organize order-key:" + ColumnKey.NAME + " should not be null" );
		}
	
		if(lineItem.get(ColumnKey.TEL1)	== null ) {
			throw new IllegalArgumentException("Cannot organize order-key:" + ColumnKey.TEL1 + " should not be null" );
		}
		
		if(lineItem.get(ColumnKey.ADDRESS) == null ) {
			throw new IllegalArgumentException("Cannot organize order-key:" + ColumnKey.ADDRESS + " should not be null" );
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(lineItem.getString(ColumnKey.NAME))
		  .append(lineItem.getString(ColumnKey.TEL1))
		  .append(lineItem.getString(ColumnKey.ADDRESS));
		
		
		return sb.toString().trim();
	}


	private void createOrder(String key, JSONObject lineItem) {
		PurchaseOrder order = PurchaseOrder.create(lineItem);
		orderMap.put(key, order);
	}


	private void addOrder(String key, JSONObject lineItem) {
		PurchaseOrder order = orderMap.get(key);
		order.addItem(lineItem);
	}
}
