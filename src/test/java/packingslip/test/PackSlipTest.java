package packingslip.test;

import org.junit.Test;

import com.francosmith.dist.slip.SlipBuilder;
import com.francosmith.dist.slip.TriplePageBuilder;
import com.francosmith.util.Config;
import com.francosmith.util.excel.ColumnDataFilter;
import com.francosmith.util.excel.DataFilter;

public class PackSlipTest {
	
	public static final String FILE_PATH = "src/test/resources/2016-11-17.xlsx";
	
	@Test
	public void testPrintPackingSlip() {
		SlipBuilder builder = new TriplePageBuilder();
		
		String date = "2017-01-16";
		
		String dist = "src/test/resources/slip_"+date + ".pdf";
		DataFilter filter = new ColumnDataFilter(Config.getInt("filter_date_column_index"), date);
		
		builder.build(dist, FILE_PATH, -1, filter);
	}
}
