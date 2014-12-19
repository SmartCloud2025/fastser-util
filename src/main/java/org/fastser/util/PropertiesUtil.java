/**
 * Copyright (c ) 2010 Founder International Co. Ltd & JetStream
 * 
 */
package org.fastser.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author WeiJun Ye
 * @version 1.0
 */
public class PropertiesUtil {
	
	private static final Logger LOG = Logger.getLogger(PropertiesUtil.class);
	
	private static final String CONFIG_FILENAME = "/system.properties";
	
	public static Map<String, String> loadPropertiesToMap(){
		return loadPropertiesToMap(CONFIG_FILENAME);
	}
	
	public static Map<String, String> loadPropertiesToMap(String filePath){
		Map<String, String> temp=new HashMap<String, String>();
		if(StringUtils.isEmpty(filePath)){
			filePath = CONFIG_FILENAME;
		}
		try {
			Properties properties = new Properties();
			InputStream inputStream = PropertiesUtil.class.getResourceAsStream(filePath);
			properties.load(inputStream);
			inputStream.close();
			for (Enumeration<?> e = properties.propertyNames();e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = properties.getProperty(key);
				temp.put(key, value);
			}
			properties = null;
		} catch (IOException e) {
			LOG.debug(e.getMessage(),e);
		}
		return temp;
	}
	
	public static Map<String, String> loadPropertiesToMap(InputStream inputStream){
		Map<String, String> temp=new HashMap<String, String>();
		if(null == inputStream){
			return temp;
		}
		try {
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			for (Enumeration<?> e = properties.propertyNames();e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = properties.getProperty(key);
				temp.put(key, value);
			}
			properties = null;
		} catch (IOException e) {
			LOG.debug(e.getMessage(),e);
		}
		return temp;
	}
	
	
	public static String getPropertisValue(String filePath,String key) {
		Properties properties = new Properties();
		properties = getPropertis(filePath);
		return properties.getProperty(key);
	}
	
	public static String getPropertisValue(InputStream inputStream, String key) {
		Properties properties = new Properties();
		properties = getPropertis(inputStream);
		return properties.getProperty(key);
	}
	
	public static String getPropertisValue(String key) {
		Properties properties = new Properties();
		properties = getPropertis(CONFIG_FILENAME);
		return properties.getProperty(key);
	}
	
	
	public static Properties getPropertis(String filePath){
		if(StringUtils.isEmpty(filePath)){
			return null;
		}
		InputStream inputStream = PropertiesUtil.class.getResourceAsStream(filePath);
		return getPropertis(inputStream);
	}
	
	public static Properties getPropertis(InputStream inputStream){
		if(null == inputStream){
			return null;
		}
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			LOG.debug(e.getMessage(),e);
		}
		return properties;
	}
}