package testsrc;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AnimeDictionaryTest {

	public static JSONObject getAnimeData(String animeName) {

		JSONArray animeID = getAnimeID(animeName);

		// Extract the ID data
		JSONObject id = (JSONObject) animeID.get(0);
		int animeCode = (int) id.get("id");

		// Build API request URL with the requested ID
		String urlString = "https://api.myanimelist.net/v2/anime/" + animeCode;

		try {
			// Call API and get response
			HttpURLConnection conn = fetchApiResponse(urlString);

			// check for response status
			// 200 - means that the connection was a success
			if (conn.getResponseCode() != 200) {
				System.out.println("Error: Could not connect to API");
				return null;
			}
			// Store resulting JSON data
			StringBuilder resultJSON = new StringBuilder();
			Scanner scanner = new Scanner(conn.getInputStream());
			while (scanner.hasNext()) {
				// read and store into the string builder
				resultJSON.append(scanner.nextLine());
			}

			// close scanner
			scanner.close();

			// close url connection
			conn.disconnect();
			
			// Parse through the data to get info on anime search
			JSONParser parser = new JSONParser();
			JSONObject resultJSONObj = (JSONObject)parser.parse(String.valueOf(resultJSON));
			
			// Retrieve title
			JSONObject title = (JSONObject)resultJSONObj.get("title");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		return animeName;
	}

	private static JSONArray getAnimeID(String animeName) {

		// Build API url with the anime name
		String urlString = "https://api.myanimelist.net/v2/" + animeName;

		try {

			// Call on the API and get a response
			HttpURLConnection conn = fetchApiResponse(urlString);

			// 200 means successful conneciton
			if (conn.getResponseCode() != 200) {
				System.out.println("Error: Could not connect to API");
				return null;
			} else {
				// Store the API results
				StringBuilder resultJSON = new StringBuilder();
				Scanner scanner = new Scanner(conn.getInputStream());

				// Read and store the resulting JSON data into the StringBuilder
				while (scanner.hasNext()) {
					resultJSON.append(scanner.nextLine());
				}

				// Close scanner
				scanner.close();
				// Close URL connection
				conn.disconnect();

				// Parse the JSON string into a JSON Object
				JSONParser parser = new JSONParser();
				JSONObject resultJSONObj = (JSONObject) parser.parse(String.valueOf(resultJSON));

				// Get the anime ID from the API
				JSONArray animeIdData = (JSONArray) resultJSONObj.get("id");
				return animeIdData;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static HttpURLConnection fetchApiResponse(String urlString) {
		try {
			// attempt to create connection
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// set request method to get
			conn.setRequestMethod("GET");

			// connect to our API
			conn.connect();
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
