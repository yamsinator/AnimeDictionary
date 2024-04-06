package testsrc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class APITEST {

	private static final String API_BASE_URL = "https://api.jikan.moe/v4/";

	public List<JSONObject> searchAnimeByName(String animeName) {
		return performSearch("anime", animeName);
	}

	public List<JSONObject> searchMangaByName(String mangaName) {
		return performSearch("manga", mangaName);
	}

	public List<JSONObject> searchCharactersByName(String charName) {
		return performSearch("characters", charName);
	}

	public List<JSONObject> performSearch(String type, String searchQuery) {

		List<JSONObject> resultsList = new ArrayList<>();

		try {
			// Construct URL for anime search
			String urlString = API_BASE_URL + type + "?q=" + searchQuery.replace(" ", "%20");
			URL url = new URL(urlString);

			// Open connection
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			// Check response code
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				throw new IOException("Unexpected HTTP response code: " + responseCode);
			}

			// Read response into a JSON object
			Scanner scanner = new Scanner(url.openStream());
			StringBuilder response = new StringBuilder();
			while (scanner.hasNext()) {
				response.append(scanner.nextLine());
			}
			scanner.close();

			// Parse JSON response
			JSONObject jsonResponse = new JSONObject(response.toString());

			// Get results array
			JSONArray results = jsonResponse.getJSONArray("data");
			for (int i = 0; i < results.length(); i++) {
				JSONObject animeInfo = results.getJSONObject(i);

				// Retrieve image URL and add to animeInfo
				if (animeInfo.has("small_image_url")) {
					String imageUrl = animeInfo.getString("small_image_url");
					animeInfo.put("small_image_url", imageUrl); // Update small_image_url field
				}

				resultsList.add(animeInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultsList;
	}
}
