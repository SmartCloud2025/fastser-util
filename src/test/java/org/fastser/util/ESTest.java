package org.fastser.util;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.fastser.util.es.ElasticSearchFactory;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import junit.framework.TestCase;

public class ESTest extends TestCase{
	
	 //https://github.com/searchbox-io/Jest/tree/master/jest
	 public void testApp(){
		 
		 String query = "{\"query\": { \"match_all\": {} }}";
		 
		 SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		 searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));

		 

		Search search = new Search.Builder(query)
		                .addIndex("serverfilm")
		                .addIndex("889")
		                .build();

		try {
			SearchResult result = ElasticSearchFactory.getJestClient().execute(search);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	 }
	
	

}
