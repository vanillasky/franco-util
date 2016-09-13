package com.francosmith.dist.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.json.JSONObject;

public class OrderItem {

	private String productCode;
	private String prodcutName;
	private String spec;
	private String optionName;
	private String trackingNumber;
	
	private Boolean isShipBySupplier;
	private int orderQuantity;
	private int shipQuantity;
	private BigDecimal price;
	private BigDecimal shipCost;
	private BigDecimal discount;
	
	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getExtension() {
		if (price == null) {
			return new BigDecimal(0);
		}
		
		return price.multiply(new BigDecimal(getOrderQuantity()));
	}
	
	public static OrderItem createFrom(JSONObject lineItem) {
		OrderItem item = new OrderItem();
		
		item.setProdcutName(lineItem.getString(ColumnKey.ITEM));
		item.setOptionName(PurchaseOrder.parseString(lineItem.get(ColumnKey.OPTION)));
		
		item.setOrderQuantity(lineItem.getInt(ColumnKey.ORDER_QTY));
		
		if (lineItem.get(ColumnKey.SHIP_QTY) == null) {
			item.setShipQuantity(item.getOrderQuantity());
		} else {
			item.setShipQuantity(lineItem.getInt(ColumnKey.SHIP_QTY));
		}
		
		if (lineItem.get(ColumnKey.SHIPPING_MEMO) == null || lineItem.get(ColumnKey.SHIPPING_MEMO).getClass() == JSONObject.NULL.getClass()) {
			item.setIsShipBySupplier(new Boolean(false));
		} else {
			if ("Á÷¼Û".equals(lineItem.getString(ColumnKey.SHIPPING_MEMO).trim())) {
				item.setIsShipBySupplier(new Boolean(true));
			} else {
				item.setIsShipBySupplier(new Boolean(false));
			}
		}
		
		item.setPrice(PurchaseOrder.parseBigDecimal(lineItem.get(ColumnKey.UNIT_PRICE)));
		item.setShipCost(PurchaseOrder.parseBigDecimal(lineItem.get(ColumnKey.SHIPPING_COST)));
		item.setTrackingNumber(PurchaseOrder.parseString(lineItem.get(ColumnKey.TRACKING)));
		item.setDiscount(PurchaseOrder.parseBigDecimal(lineItem.get(ColumnKey.DISCOUNT)));
		return item;
	}
	
	
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProdcutName() {
		return prodcutName;
	}
	public void setProdcutName(String prodcutName) {
		this.prodcutName = prodcutName;
	}
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public String getOptionName() {
		return optionName;
	}
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public int getShipQuantity() {
		return shipQuantity;
	}
	public void setShipQuantity(int shipQuantity) {
		this.shipQuantity = shipQuantity;
	}
	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public BigDecimal getShipCost() {
		return shipCost;
	}
	public void setShipCost(BigDecimal shipCost) {
		this.shipCost = shipCost;
	}

	public Boolean isShipBySupplier() {
		return isShipBySupplier;
	}

	public void setIsShipBySupplier(Boolean isShipBySupplier) {
		this.isShipBySupplier = isShipBySupplier;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	
}
