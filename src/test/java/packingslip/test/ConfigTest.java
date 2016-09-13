package packingslip.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.francosmith.util.Config;

public class ConfigTest {

	@Test
	public void testString() {
		assertEquals("yyyy-MM-dd", Config.getString("simple_date_format"));
	}

	@Test
	public void testStringArray() {
		String[] expected = { "프랑코", "네이버페이", "11번가", "옥션", "지마켓", "인터파크", "이오텍", "기타" };
		String[] result = Config.getStringArray("shopping_channels");
		
		System.out.println("shopping_channels:"+Config.getString("shopping_channels"));
		assertArrayEquals(expected, result);
		
	}
	
}
