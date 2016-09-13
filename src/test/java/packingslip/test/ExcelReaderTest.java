package packingslip.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.francosmith.util.Config;
import com.francosmith.util.excel.ColumnDataFilter;
import com.francosmith.util.excel.ExcelReader;

public class ExcelReaderTest {
	public static final String FILE_PATH = "src/test/resources/프랑코스미스-송장양식-sample.xlsx";
	
	@Test
	public void testReadExcelFile() throws IOException {
		ExcelReader reader = new ExcelReader();
		JSONObject result = reader.read(FILE_PATH, ExcelReader.LAST_SHEET);
		
		JSONArray records = result.getJSONArray("result");
		
		for (int i=0, len=records.length(); i < len; i++) {
			System.out.println(records.get(i));
		}
		
		assertEquals(178, records.length());
	}
	
	@Test
	public void testExcelRowFiltering() throws IOException {
		ExcelReader reader = new ExcelReader();
		JSONObject result = reader.filter(FILE_PATH, ExcelReader.LAST_SHEET, new ColumnDataFilter(Config.getInt("filter_date_column_index"), "2016-09-08"));
		JSONArray records = result.getJSONArray("result");
		
		for (int i=0, len=records.length(); i < len; i++) {
			System.out.println(records.get(i));
		}
		
		assertEquals(31, records.length());
	}

}
