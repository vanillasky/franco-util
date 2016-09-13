package packingslip.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.francosmith.dist.model.PurchaseOrder;
import com.francosmith.dist.slip.OrderExctrator;

public class OrderExtractorTest {

	public static final String FILE_PATH = "src/test/resources/프랑코스미스-송장양식-sample.xlsx";
	
	private OrderExctrator extractor;
	
	@Before
	public void init() {
		extractor = new OrderExctrator();
	}
	
	
	
	@Test
	public void testExtractOrderData() throws IOException  {
		List<PurchaseOrder> orders = extractor.extract(FILE_PATH, "2016-09-08");
		for (PurchaseOrder each : orders) {
			System.out.println(each);
		}
		assertEquals(22, orders.size());
	}
	

}
