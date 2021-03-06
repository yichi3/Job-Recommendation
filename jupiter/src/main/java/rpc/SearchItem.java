package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import external.GitHubClient;

import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// class: check session
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

//		// class 1: Learn Get
//		JSONArray array = new JSONArray();
//		array.put(new JSONObject().put("username", "abcd"));
//		array.put(new JSONObject().put("username", "1234"));
//		RpcHelper.writeJsonArray(response, array);
		
//		if (request.getParameter("username") != null) {
//			JSONObject obj = new JSONObject();
//			String username = request.getParameter("username");
//			obj.put("username", username);
//			writer.print(obj);
//		}
		
		// class 2: Send HTTP GET request
		String userId = request.getParameter("user_id");
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));

		GitHubClient client = new GitHubClient();
		// class 3: we have a List<Item> of items that stores the JSON info
		List<Item> items = client.search(lat, lon, null);
		
		MySQLConnection connection = new MySQLConnection();
		Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId);
		connection.close();
		JSONArray array = new JSONArray();
		for (Item item : items) {
			JSONObject obj = item.toJSONObject();
			obj.put("favorite", favoriteItemIds.contains(item.getItemId()));
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
