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
		String[] expected = { "������", "���̹�����", "11����", "����", "������", "������ũ", "�̿���", "��Ÿ" };
		String[] result = Config.getStringArray("shopping_channels");
		
		System.out.println("shopping_channels:"+Config.getString("shopping_channels"));
		assertArrayEquals(expected, result);
		
	}
	
}
