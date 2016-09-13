package com.francosmith.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Config {

	public static final Configuration config;
	
	static {
		Parameters params = new Parameters();
		
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = 
				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
							.setFileName("config.properties")
							.setListDelimiterHandler(new DefaultListDelimiterHandler(','))
							.setEncoding("utf-8")
						);
				
		Configuration c = null;
		
		try {
			c = builder.getConfiguration();
			
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		config = c;
		
	}
	
	public static String getString(String key) {
		return config.getString(key);
	}
	
	public static int getInt(String key) {
		return config.getInt(key);
	}
	
	public static String[] getStringArray(String key) {
		return config.getStringArray(key);
	}
	
	
	
}
