package com.francosmith.dist.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.francosmith.dist.slip.OrderExctrator;

public class PurchaseOrder {
	public static final Logger logger = LoggerFactory.getLogger(PurchaseOrder.class);
	
	public static final String FREIGHT_COMPANY = "CJ≈√πË";
	public static final String CHANNEL_NAVER = "N";
	public static final String CHANNEL_SHOP = "F";
	public static final String CHANNEL_11ST = "11ST";
	public static final String CHANNEL_INT = "I";
	
	private String shipToAddress;
	private String shipToName;
	private String orderNnumber;
	private String channel;
	private String shipToPhone;
	private String freightCompany;
	private String shipDate;
	private String orderDate;
	private List<String> notices;
	private List<String> messages;
	private String trackingNo;
	
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();
	
	
	
	public void addItem(JSONObject lineItem) {
		synchronized (orderItems) {
			OrderItem item = OrderItem.createFrom(lineItem);
			orderItems.add(item);
		}
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public static PurchaseOrder create(JSONObject lineItem) {
		PurchaseOrder order = new PurchaseOrder();
		
		order.setShipToName(lineItem.getString(ColumnKey.NAME));
		order.setShipToPhone(lineItem.getString(ColumnKey.TEL1));
		order.setShipToAddress(lineItem.getString(ColumnKey.ADDRESS));
		order.setFreightCompany(FREIGHT_COMPANY);
		order.setShipDate(OrderExctrator.SDF_SHIP_DATE.format(new Date()));
		
		order.setOrderNnumber(parseString(lineItem.get(ColumnKey.ORDER_NO)));
		order.setOrderDate(dateToString(lineItem.get(ColumnKey.ORDER_DATE)));
		order.setTrackingNo(parseString(lineItem.get(ColumnKey.TRACKING)));
		order.setChannel(parseString(lineItem.get(ColumnKey.CHANNEL)));
		
		String noticeText = parseString(lineItem.get(ColumnKey.NOTICE));
		if (noticeText != null && noticeText.length() > 0) {
			order.notices = Arrays.asList(noticeText.split(";"));
		}
		
		
		String messageText = parseString(lineItem.get(ColumnKey.MEMO));
		if (messageText != null && messageText.length() > 0) {
			order.messages = Arrays.asList(messageText.split(";"));
		}
		
		
		order.addItem(lineItem);
		return order;
	}
	
	public String getTrackingNo() {
		return trackingNo;
	}

	public void setTrackingNo(String trackingNo) {
		this.trackingNo = trackingNo;
	}

	public static String parseString(Object obj) {
		if (obj == null || obj.getClass() == JSONObject.NULL.getClass()) {
			return null;
		}
		
		return String.valueOf(obj);
	}

	public static BigDecimal parseBigDecimal(Object obj) {
		try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            
        }
		return new BigDecimal("0");
	}


	public static String dateToString(Object obj) {
		if (obj == null || obj.getClass() == JSONObject.NULL.getClass()) {
			return null;
		}
		
		String result;
		
		try {
			result = OrderExctrator.SDF_ORDER_DATE.format(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
//			System.out.println("Obj:" + obj.getClass());
//			System.out.println("Obj toString:" + obj.toString());
			result = obj.toString();
		}
		return result;
	}
	
	
	public String getShipToAddress() {
		return shipToAddress;
	}

	public void setShipToAddress(String shipToAddress) {
		this.shipToAddress = shipToAddress;
	}

	public String getShipToName() {
		return shipToName;
	}

	public void setShipToName(String shipToName) {
		this.shipToName = shipToName;
	}

	public String getOrderNnumber() {
		return orderNnumber;
	}

	public void setOrderNnumber(String orderNnumber) {
		this.orderNnumber = orderNnumber;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getShipToPhone() {
		return shipToPhone;
	}

	public void setShipToPhone(String shipToPhone) {
		this.shipToPhone = shipToPhone;
	}

	public String getFreightCompany() {
		return freightCompany;
	}

	public void setFreightCompany(String freightCompany) {
		this.freightCompany = freightCompany;
	}

	public String getShipDate() {
		return shipDate;
	}

	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}


	public List<String> getNotices() {
		return notices;
	}

	public void setNotices(List<String> notices) {
		this.notices = notices;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	
	
}
