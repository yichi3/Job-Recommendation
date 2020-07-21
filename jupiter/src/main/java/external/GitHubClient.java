package external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";
	
	// we need to achieve search function
	public List<Item> search(double lat, double lon, String keyword) {
		// edge case check
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		// provide HTTP client
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			// HTTP GET request here
			CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
			// check HTTP response status
			if (response.getStatusLine().getStatusCode() != 200) {
				return new ArrayList<Item>();
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return new ArrayList<Item>();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuilder responseBody = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				responseBody.append(line);
			}
			JSONArray array = new JSONArray(responseBody.toString());
			return getItemList(array);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<Item>();
	}
	
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}
	
	private List<Item> getItemList(JSONArray array) {
		List<Item> itemList = new ArrayList<>();
		
		// batch process all keywords
		List<String> descriptionList = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
			
			// check corner case
			if (description.equals("") || description.equals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			} else {
				descriptionList.add(description);
			}
		}
		
		// in case monkeylearn in down
		// List<List<String>> keywords = MonkeyLearnClient.extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));
		
		for (int i = 0; i < array.length(); ++i) {
			JSONObject object = array.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			
			builder.setItemId(getStringFieldOrEmpty(object, "id"));
			builder.setName(getStringFieldOrEmpty(object, "title"));
			builder.setAddress(getStringFieldOrEmpty(object, "location"));
			builder.setUrl(getStringFieldOrEmpty(object, "url"));
			builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
			// for the case if monkeylearn is not working
			builder.setKeywords(new HashSet<String>());
			// builder.setKeywords(new HashSet<String>(keywords.get(i)));
			Item item = builder.build();
			itemList.add(item);
		}
		
		return itemList;
	}
	
	

}
