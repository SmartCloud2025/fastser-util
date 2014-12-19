package org.fastser.util.es;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.HttpClientConfig.Builder;
import io.searchbox.client.http.JestHttpClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.fastser.util.PropertiesUtil;

public class ElasticSearchFactory {
	
	private static ElasticSearchFactory instance = new ElasticSearchFactory();
	
	private JestClientFactory factory = null;
	
	private ElasticSearchFactory(){}
	
	public static JestClient getJestClient(){
		if(null == instance.factory){
			instance.factory = new JestClientFactory();
			Map<String,String> map = PropertiesUtil.loadPropertiesToMap("/system.properties");
			String urlStr = map.get("es.url.list");
			if(StringUtils.isNotEmpty(urlStr)){
				 List<String> list = Arrays.asList(urlStr.split(","));
				 List<String> urls = new ArrayList<>();
				 String port = map.get("es.port");
				 for(String url:list){
					 urls.add(url+":"+port);
				 }
				 Builder builder = new HttpClientConfig.Builder(urls);
				 builder.multiThreaded(true);
				 String maxTotalConnection = map.get("es.url.maxTotalConnection");
				 if(StringUtils.isNotEmpty(maxTotalConnection)){
					 builder.maxTotalConnection(Integer.valueOf(maxTotalConnection));
				 }else{
					 builder.maxTotalConnection(20);
				 }
				 String defaultMaxTotalConnectionPerRoute = map.get("es.url.defaultMaxTotalConnectionPerRoute");
				 if(StringUtils.isNotEmpty(defaultMaxTotalConnectionPerRoute)){
					 builder.defaultMaxTotalConnectionPerRoute(Integer.valueOf(defaultMaxTotalConnectionPerRoute));
				 }else{
					 builder.defaultMaxTotalConnectionPerRoute(10);
				 }
				 String maxConnectionPerRoute = map.get("es.url.maxConnectionPerRoute");
				 if(StringUtils.isNotEmpty(maxConnectionPerRoute)){
					 List<String> routes = Arrays.asList(maxConnectionPerRoute.split(","));
					 if(null != routes && routes.size()>0){
						 for(int i=0;i<list.size();i++){
							 String url = list.get(i);
							 HttpRoute route = new HttpRoute(new HttpHost(url));
							 builder.maxTotalConnectionPerRoute(route, Integer.valueOf(routes.get(i)));
						 }
					 }
				 }
				 HttpClientConfig httpClientConfig = builder.build();
				 instance.factory.setHttpClientConfig(httpClientConfig);
			}
		}
		return instance.factory.getObject();
	}
	
	public static JestHttpClient getJestHttpClient(){
		return (JestHttpClient)getJestClient();
	}
}
