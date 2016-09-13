package packingslip.test;

import org.junit.Test;

import com.francosmith.dist.slip.SlipBuilder;
import com.francosmith.dist.slip.TriplePageBuilder;
import com.francosmith.util.Config;
import com.francosmith.util.excel.ColumnDataFilter;
import com.francosmith.util.excel.DataFilter;

public class PackSlipTest {
	
	public static final String FILE_PATH = "src/test/resources/프랑코스미스-송장양식-sample.xlsx";
	
	@Test
	public void testPrintPackingSlip() {
		SlipBuilder builder = new TriplePageBuilder();
		
		String date = "2016-09-08";
		
		String dist = "src/test/resources/slip_"+date + ".pdf";
		DataFilter filter = new ColumnDataFilter(Config.getInt("filter_date_column_index"), date);
		
		builder.build(dist, FILE_PATH, -1, filter);
	}
}
