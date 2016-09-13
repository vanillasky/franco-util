package com.francosmith.dist.slip;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.francosmith.dist.model.OrderItem;
import com.francosmith.dist.model.PurchaseOrder;
import com.francosmith.util.Config;
import com.francosmith.util.excel.DataFilter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;


public class TriplePageBuilder implements SlipBuilder {

	private static final Logger logger = LoggerFactory.getLogger(TriplePageBuilder.class);
	private static final Map<String, String> channelMap;
	private static final String DEFAULT_CHANNEL = Config.getString("shopping_channels.default"); 
	private static final float FONT_SIZE = 7.2f;		
	
	
	private OrderExctrator orderExtractor = new OrderExctrator();
	private int pageHeight = Config.getInt("slip.page.height");
	private Paragraph headerCompany;
	
	private PdfFont fontKor;
    private PdfFont fontVerdana;
	
    static {
    	channelMap = new HashMap<String, String>();
    	String[] channels = Config.getStringArray("shopping_channels.map");
    	for (String each : channels) {
    		String[] pair = each.split(":");
    		channelMap.put(pair[0], pair[1]);
    	}
    }
    
    public TriplePageBuilder() {
    	try {
			fontKor = PdfFontFactory.createFont("src/main/resources/data/H2GTRM.TTF", PdfEncodings.IDENTITY_H, true);
			fontVerdana = PdfFontFactory.createFont("src/main/resources/data/VERDANA.TTF", PdfEncodings.IDENTITY_H, true);
		} catch (IOException e) {
			logger.error("Cannot create pdf font:"+ e.getMessage());
			e.printStackTrace();
		}
    	
    }
    
	public void build(String dest, String excelFile, int sheetNo, DataFilter filter) {
		List<PurchaseOrder> orders = null;
		Document document = null; 
		PurchaseOrder order = null;
		
		try {
			document =  createDocument(dest);
			orders = orderExtractor.extract(excelFile, sheetNo, filter);
			
			for (int i=0, len=orders.size(); i < len; i++) {
				order = orders.get(i);
				renderOrder(document, order);
			}
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e.getCause());
			e.printStackTrace();
		} finally {
			if ( document != null ) { document.close(); }
		}
		
	}//:
	
	private void renderOrder(Document document, PurchaseOrder order) {
		Table slipContainer = createPageContainer(10, 10);
		
		Table leftWrapper  = createBoxTable();
		Table rightWrapper = createBoxTable();
		
		renderCompany(leftWrapper);
		renderShipTo(leftWrapper, order);
		renderOrderItem(leftWrapper, order);
		renderOrderSummary(leftWrapper, order);
		renderCustomerService(rightWrapper);
		renderNotice(rightWrapper, order);
		renderMemo(rightWrapper, order);
		
		
		slipContainer.getCell(0, 0).add(leftWrapper);
		slipContainer.getCell(0, 1).add(rightWrapper);
        
		document.add(slipContainer);
	}//:
	
	private void renderNotice(Table parent, PurchaseOrder order) {
		Table container = new Table(new float[]{1f});
		container.setWidthPercent(100)
	     		 .setHorizontalAlignment(HorizontalAlignment.RIGHT)
	     		 .setMargins(0f, 0f, 0f, 0f);
		
		Table labelTable = new Table(new float[]{1f});
		labelTable.setWidthPercent(100)
				  .setBorder(Border.NO_BORDER)
				  .setMarginBottom(0f)
		 		  .setHorizontalAlignment(HorizontalAlignment.CENTER)
		 		  .setMargins(0f, 0f, 0f, 0f);
		
		labelTable.addCell(createDefaultCell(Config.getString("slip.field.notice")));
		
		Table contentTable = new Table(new float[]{1f});
		contentTable.setWidthPercent(100)
		 		    .setHorizontalAlignment(HorizontalAlignment.CENTER)
		 		    .setMarginTop(0f)
		 		    .setPaddingTop(0f)
		 		    .setBorder(Border.NO_BORDER)
		 		    .setMargins(0f, 0f, 0f, 0f);
		
		List<String> notices = order.getNotices();
		if (notices != null && notices.size() > 0) {
			for (String each : notices) {
				contentTable.addCell(createDefaultCell(each));
			}
		}
		
		container.addCell(new Cell().setBorder(Border.NO_BORDER).setHeight(10f).add(labelTable));
		container.addCell(new Cell().setBorder(new SolidBorder(0.3f)).setHeight(100f).add(contentTable));
		
		Cell pcell = new Cell();
		pcell.setBorder(Border.NO_BORDER);
		pcell.add(container);
		parent.addCell(pcell);
	}
	
	
	private void renderMemo(Table parent, PurchaseOrder order) {
		Table container = new Table(new float[]{1f});
		container.setWidthPercent(100)
	     		 .setHorizontalAlignment(HorizontalAlignment.RIGHT)
	     		 .setMargins(0f, 0f, 0f, 0f);
		
		Table labelTable = new Table(new float[]{1f});
		labelTable.setWidthPercent(100)
				  .setBorder(Border.NO_BORDER)
				  .setMarginBottom(0f)
		 		  .setHorizontalAlignment(HorizontalAlignment.CENTER)
		 		  .setMargins(0f, 0f, 0f, 0f);
		
		labelTable.addCell(createDefaultCell(Config.getString("slip.field.memo")));
		
		Table contentTable = new Table(new float[]{1f});
		contentTable.setWidthPercent(100)
		 		    .setHorizontalAlignment(HorizontalAlignment.CENTER)
		 		    .setMarginTop(0f)
		 		    .setPaddingTop(0f)
		 		    .setBorder(Border.NO_BORDER)
		 		    .setMargins(0f, 0f, 0f, 0f);
		
		List<String> memo = order.getMessages();
		if (memo != null && memo.size() > 0) {
			for (String each : memo) {
				contentTable.addCell(createDefaultCell(each));
			}
		}
		
		container.addCell(new Cell().setBorder(Border.NO_BORDER).setHeight(10f).add(labelTable));
		container.addCell(new Cell().setBorder(new SolidBorder(0.3f)).setHeight(60f).add(contentTable));
		
		Cell pcell = new Cell();
		pcell.setBorder(Border.NO_BORDER);
		pcell.add(container);
		parent.addCell(pcell);
	}
	
	private void renderCustomerService(Table parent) {
		Table container = new Table(new float[]{1f});
		container.setWidthPercent(100)
	     		 .setHorizontalAlignment(HorizontalAlignment.RIGHT)
	     		 .setMargins(0f, 0f, 0f, 0f);
		
		
		container.addCell((new Cell()
				.setBorder(Border.NO_BORDER)
				.setPaddings(0,  0,  0, 0)
				.setMargins(0, 0, 0, 0)
				.setHeight(10f)
				.add(new Paragraph(Config.getString("slip.right.company"))
						.setBold()
						.setFont(fontKor)
						.setFontSize(FONT_SIZE))));
		
		container.addCell(createDefaultCell(Config.getString("slip.company.cs.phone")));
		container.addCell(createDefaultCell(Config.getString("freight_forwarder.phone")));
		
		Cell pcell = new Cell();
		pcell.setBorder(Border.NO_BORDER);
		pcell.add(container);
		parent.addCell(pcell);
	}//:
	
	
	private Cell createDefaultCell(String value) {
		return createDefaultCell(value, 0, 0, 0, 0);
	}
	
	private Cell createDefaultCell(String value, float paddingTop, float paddingright, float paddingBottom, float paddingLeft) {
		return new Cell()
				.setBorder(Border.NO_BORDER)
				.setPaddings(paddingTop,  paddingright, paddingBottom, paddingLeft)
				.setMargins(0, 0, 0, 0)
				.setHeight(10f)
				.add(new Paragraph(value))
						.setFont(fontKor)
						.setFontSize(FONT_SIZE);
	}
	
	

	private void renderOrderSummary(Table parent, PurchaseOrder order) {
		Table container = new Table(new float[]{1.5f, 1f});
		container.setWidthPercent(100)
	     		 .setHorizontalAlignment(HorizontalAlignment.RIGHT)
	     		 .setMargins(0f, 0f, 0f, 0f);
		
		Table footerTable = createFooterTable(); 
		Table summaryTable = createSummaryTable(order);
		
		container.addCell(new Cell().setBorder(Border.NO_BORDER).add(footerTable));
		container.addCell(new Cell().setBorder(Border.NO_BORDER).add(summaryTable));
		
		Cell pcell = new Cell();
		pcell.setBorder(Border.NO_BORDER);
		pcell.add(container);
		parent.addCell(pcell);
	}//:

	private Table createSummaryTable(PurchaseOrder order) {
		List<String> orderSummary = makeOrderSummary(order);
		
		Table table = new Table(new float[]{1.5f, 1f});
		table.setWidthPercent(80)
		     .setBorder(Border.NO_BORDER)
		     .setHorizontalAlignment(HorizontalAlignment.RIGHT)
		     .setMargins(0f, 15f, 0f, 0f);
		
		for (String each : orderSummary) {
			table.addCell(new Cell().setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.RIGHT).setTextAlignment(TextAlignment.RIGHT)
					.setPaddings(0f, 0f, 0f, 0f)
					.setMargins(0f, 0f, 0f, 0f)
					.setHeight(10f)
					.add(new Paragraph(each).setHorizontalAlignment(HorizontalAlignment.RIGHT).setTextAlignment(TextAlignment.RIGHT).setFontSize(FONT_SIZE).setFont(fontKor)));
		}
		
		return table;
	}

	private Table createFooterTable() {
		Table table = new Table(new float[]{1f});
		table.setWidthPercent(100)
 		     .setBorder(Border.NO_BORDER)
 		     .setHorizontalAlignment(HorizontalAlignment.LEFT)
 		     .setMargins(0f, 0f, 0f, 5f);

		table.addCell(new Cell()
				.setHeight(10f)
				.setBorder(Border.NO_BORDER)
				.setPaddings(0f, 0f,  0f,  0f)
				.setMargins(0f, 0f,  0f,  0f)
				.add(new Paragraph(Config.getString("slip.company.greeting")).setPaddings(0, 0, 0, 0).setFont(fontKor).setFontSize(FONT_SIZE)));
		
		table.addCell(new Cell()
				.setHeight(10f)
				.setPaddings(0f, 0f,  0f,  0f)
				.setMargins(0f, 0f,  0f,  0f)
				.setBorder(Border.NO_BORDER)
				.add(new Paragraph(Config.getString("slip.company.url"))
						.setPaddings(0, 0, 0, 0)
						.setVerticalAlignment(VerticalAlignment.TOP)
						.setFont(fontKor)
						.setFontSize(FONT_SIZE)));
		
		return table;
	}

	private void renderOrderItem(Table parent, PurchaseOrder order) {
		Table table = new Table(new float[]{2.5f, 0.4f, 0.4f, 0.6f, 0.6f});
		table.setWidthPercent(98)
		     .setBorder(Border.NO_BORDER)
		     .setBorderTop(new SolidBorder(0.3f))
		     .setBorderBottom(new SolidBorder(0.3f))
		     .setHorizontalAlignment(HorizontalAlignment.CENTER)
		     .setMargin(0f);
		
		table.setMarginTop(8f);
		
		renderOrderItemHeader(table);
		renderLineItems(table, order);
		
		Cell pcell = new Cell();
		pcell.setBorder(Border.NO_BORDER);
		pcell.add(table);
		parent.addCell(pcell);
	}
	
	/**
	 * Render each item information. product name, oder quantity, etc... 
	 * Fill blank rows if line item amount is less than 10
	 * @param table
	 * @param order
	 */
	private void renderLineItems(Table table, PurchaseOrder order) {
		List<OrderItem> items = order.getOrderItems();
		List<String> tableData = new ArrayList<String>();
		DecimalFormat df = new DecimalFormat("#,###");
	
		int rowsPerPage = 10;
		int rowNum = 0;
		
		for (OrderItem item : items) {
			table.addCell(createLineItemCell(item.getProdcutName(), TextAlignment.LEFT));
			table.addCell(createLineItemCell(String.valueOf(item.getOrderQuantity()), TextAlignment.RIGHT));
			table.addCell(createLineItemCell(String.valueOf(item.getShipQuantity()), TextAlignment.RIGHT));
			table.addCell(createLineItemCell(df.format(item.getPrice()), TextAlignment.RIGHT));
			table.addCell(createLineItemCell(df.format(item.getPrice().multiply(new BigDecimal(item.getOrderQuantity()))), TextAlignment.RIGHT));

			rowNum++;
		}
		
		if (rowNum < rowsPerPage) {
			for (int i=0; i < rowsPerPage-rowNum; i++) {
				for (int j=0; j < 5; j++) {
					tableData.add("");
				}
			}
			
			processCellValues(table, tableData.toArray(new String[0]), fontKor, FONT_SIZE, false);
		}
		
		
	}//:
	
	
	private Cell createLineItemCell(String value, TextAlignment alignment) {
		
		Cell cell = new Cell().setBorder(Border.NO_BORDER)
				              .setTextAlignment(alignment)
				              .setHeight(10f)
				              .setMargins(0f, 0f, 0f, 0f);
		if (alignment == TextAlignment.LEFT) {
			cell = cell.setPaddings(0f,  0f,  0f,  0.3f);
		} else {
			cell = cell.setPaddings(0f,  15f,  0f,  0);
		}
		
		cell = cell.add(new Paragraph(value)
				        .setTextAlignment(alignment)
				        .setFontSize(FONT_SIZE)
				        .setFont(fontKor));
		
		return cell;
	}
	
	/**
	 * Render table header of line item.
	 * 
	 * @param table
	 */
	private void renderOrderItemHeader(Table table) {
		String[] header = new String[] {
				Config.getString("slip.item.title"),
				Config.getString("slip.item.order_qty"),
				Config.getString("slip.item.ship_qty"),
				Config.getString("slip.item.unit_price"),
				Config.getString("slip.item.ext_price")
			};
			
			processCellValues(table, header, fontKor, FONT_SIZE, true, TextAlignment.CENTER);
	}
	
	/**
	 * Render order summary
	 * 
	 * @param parent
	 * @param order
	 */
	private List<String> makeOrderSummary(PurchaseOrder order) {
		List<OrderItem> items = order.getOrderItems();
		List<String> tableData = new ArrayList<String>();
		DecimalFormat df = new DecimalFormat("#,###");
	
		BigDecimal total = new BigDecimal(0);
		BigDecimal lineExtension = new BigDecimal(0);
		BigDecimal grandTotal = new BigDecimal(0);
		BigDecimal discount = new BigDecimal(0);
		BigDecimal freight = new BigDecimal(0);
		
		for (OrderItem item : items) {
			lineExtension = item.getPrice().multiply(new BigDecimal(item.getOrderQuantity()));
			total = total.add(lineExtension);
			freight = freight.add(item.getShipCost());
			discount = discount.add(item.getDiscount());
		}
		
		grandTotal = grandTotal.add(total).add(freight).subtract(discount);
		
		tableData.add(Config.getString("slip.field.total"));
		tableData.add(df.format(total));
		tableData.add(Config.getString("slip.field.freight"));
		tableData.add(freight.intValue() == 0 ? "" : "(+)" + df.format(freight));
		tableData.add(Config.getString("slip.field.discount"));
		tableData.add(discount.intValue() == 0 ? "" : "(-)" + df.format(discount));
		tableData.add(Config.getString("slip.field.grand_total"));
		tableData.add(df.format(grandTotal));
		
		return tableData;
	}//:

	

//	public void process(Table table, String line, PdfFont font, boolean isHeader) {
//        StringTokenizer tokenizer = new StringTokenizer(line, ";");
//        while (tokenizer.hasMoreTokens()) {
//            if (isHeader) {
//                table.addHeaderCell(new Cell().setHorizontalAlignment(HorizontalAlignment.CENTER).
//                		add(new Paragraph(tokenizer.nextToken()).setHorizontalAlignment(HorizontalAlignment.CENTER).setFontSize(6).setFont(font)));
//            } else {
//                table.addCell(new Cell().setHorizontalAlignment(HorizontalAlignment.CENTER).add(new Paragraph(tokenizer.nextToken()).setHorizontalAlignment(HorizontalAlignment.CENTER).setFont(font)));
//            }
//        }
//    }
	
	private void renderShipTo(Table parent, PurchaseOrder order) {
		Table table = new Table(new float[]{3.5f, 1.5f, 1.5f});
		table.setWidthPercent(100)
		     .setBorder(Border.NO_BORDER)
		     .setMargins(0f, 0f, 0f, 5f)
             .setHorizontalAlignment(HorizontalAlignment.LEFT);
      	
		List<String> tableData = new ArrayList<String>(6);
		
		tableData.add(Config.getString("slip.field.shipTo") + order.getShipToName() + " " + Config.getString("slip.field.shipTo.postfix"));
		tableData.add(Config.getString("slip.field.order_date") + order.getOrderDate().replaceAll("-", "/"));
		tableData.add(Config.getString("slip.field.ship_date") + order.getShipDate().replaceAll("-",  "/"));
		tableData.add(order.getShipToAddress());
		tableData.add(Config.getString("siip.field.forwader") + Config.getString("freight_forwarder"));
		tableData.add(Config.getString("slip.field.tracking") + nvl(order.getTrackingNo()));
		tableData.add(Config.getString("slip.field.order_number") + order.getOrderNnumber());
		tableData.add(Config.getString("slip.field.channel") + channelMap.get(nvl(order.getChannel(), DEFAULT_CHANNEL)));
		
		processCellValues(table, tableData.toArray(new String[0]), fontKor, FONT_SIZE, false, TextAlignment.LEFT);
		
		Cell pcell = new Cell();
		pcell.setBorder(Border.NO_BORDER);
		pcell.setMargin(0f);
		pcell.setPadding(0f);
		pcell.add(table);
		parent.addCell(pcell);
	}
	
	private void renderCompany(Table parent) {
		Table table= new Table(new float[] { 1 });
		table.setWidthPercent(100).setMargin(0).setBorder(Border.NO_BORDER);
		
		table.addCell(new Cell()
						.setBorder(Border.NO_BORDER)
						.setPadding(0).add(getCompanyParagraph()));
		 
	    Cell cell = new Cell();
	    cell.setBorder(Border.NO_BORDER);
	    cell.add(table);
	    parent.addCell(cell);
	}
	
	private Paragraph getCompanyParagraph() {
		
		if (headerCompany == null) {
			Text logo = new Text(Config.getString("slip.company.name")).setFont(fontVerdana);
	        logo.setBold();
	        
	        Text logo2 = new Text("\n"+Config.getString("slip.company.desc")).setFont(fontVerdana);
	        logo2.setItalic().setBold();
	        
	        Paragraph ph = new Paragraph();
	        ph.setFontSize(8);
	        ph.setFontColor(Color.LIGHT_GRAY);
	        ph.add(logo).add(logo2);
	        
	        return ph;
		} else {
			return headerCompany;
		}
		
	}
	
	
	private Table createBoxTable() {
		Table table = new Table(new float[]{ 1});
		table.setWidthPercent(100);
		return table;
	}

	public Document createDocument(String dest) throws FileNotFoundException {
		PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
		PageSize ps = PageSize.A4;
		
        // Initialize document
        Document document = new Document(pdf, ps);
        document.setMargins(0, 20, 0, 20);
        
        return document;
	}
	
	private Table createPageContainer(int marginTop, int marginBottom) {
		Table table = new Table(new float[]{4f, 1});
		table.setWidthPercent(100)
			 .setMarginBottom(marginTop)
			 .setMarginTop(marginBottom)
             .setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        Cell leftCell = new Cell();
        Cell rightCell = new Cell();
        
        leftCell.setBorder(Border.NO_BORDER);
        rightCell.setBorder(Border.NO_BORDER);
        leftCell.setHeight(pageHeight);
        rightCell.setHeight(pageHeight);
        
        table.addCell(leftCell);
        table.addCell(rightCell);
        
		return table;
	}
	
	
	public void processCellValues(Table table, String[] lineData, PdfFont font, float fontSize, boolean isHeader) {
		processCellValues(table, lineData, font, fontSize, isHeader, TextAlignment.LEFT);
	}
	
	
		
	public void processCellValues(Table table, 
				  String[] lineData, 
				  PdfFont font, 
				  float fontSize, 
				  boolean isHeader, 
				  TextAlignment textAlignment) {
	
		for (String each : lineData) {
			if (isHeader) {
				table.addHeaderCell(new Cell().setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(0.3f)).setTextAlignment(textAlignment)
					 .setPaddings(0f, 0f, 0f, 0f)
				     .setMargins(0f, 0f, 0f, 0f)
				     .setHeight(12f)
				     .add(new Paragraph(each).setTextAlignment(textAlignment).setFontSize(fontSize).setFont(font)));
			} else {
				table.addCell(new Cell().setBorder(Border.NO_BORDER).setTextAlignment(textAlignment)
						.setPaddings(0f, 0f, 0f, 0f)
						.setMargins(0f, 0f, 0f, 0f)
						.setHeight(10f)
						.add(new Paragraph(each).setTextAlignment(textAlignment).setFontSize(fontSize).setFont(font)));
			}
		}
	}
	
	
	
	public String nvl(Object obj) {
		return obj == null ? "" : obj.toString();
	}
	
	public String nvl(Object obj, String defaultValue) {
		return obj == null ? defaultValue : obj.toString();
	}
}
