package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnResponse;
import com.monkeylearn.MonkeyLearnException;

public class MonkeyLearnClient {
	
	private static final String API_KEY = "3addd27024fdf8b997e62eddbe8c8ef76d9e6535";
	
    public static void main( String[] args ) throws MonkeyLearnException {
        String[] data = {
        		"Amazon has a new openning in game development and distributed systems",
        		"Google has many openning in software developments",
        		"Elon Musk has shared a photo of the spacesuit designed by SpaceX."
        		+ "This is the second image shared of the new design and the first"
        		+ "to feature the spacesuit’s full-body look."};
        
        List<List<String>> words = extractKeywords(data);
        System.out.println( words );
    }
    
    public static List<List<String>> extractKeywords(String[] text) {
    	// corner case
    	if (text == null || text.length == 0) {
    		return new ArrayList<>();
    	}
    	MonkeyLearn ml = new MonkeyLearn(API_KEY);
        String modelId = "ex_YCya9nrn";
        
        ExtraParam[] extraParams = { new ExtraParam("max_keywords", "3") };
    	
        MonkeyLearnResponse res;
		try {
			res = ml.extractors.extract(modelId, text, extraParams);
			JSONArray resultArray = res.arrayResult;
			return getKeywords(resultArray);
		} catch (MonkeyLearnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return new ArrayList<>();
    }
    
    // write a helper method to transform JSONArray to List<List<String>
    // JSONArray contains multiple job descriptions. Each description contains at most 3 keywords
    private static List<List<String>> getKeywords(JSONArray resultArray) {
    	List<List<String>> topKeywords = new ArrayList<>();
    	for (int i = 0; i < resultArray.size(); i++) {
    		List<String> keywords = new ArrayList<>();
    		JSONArray keywordsArray = (JSONArray) resultArray.get(i);
    		for (int j = 0; j < keywordsArray.size(); j++) {
    			JSONObject object = (JSONObject) keywordsArray.get(j);
    			String keyword = (String) object.get("keyword");
    			keywords.add(keyword);
    		}
    		topKeywords.add(keywords);
    	}

		return topKeywords;
    	
    }
}