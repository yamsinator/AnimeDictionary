
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

public class DictionaryGUI {

	private JFrame frame;
	private JTextField nameTextField;
	private JButton searchButton;
	private JTextArea resultTextArea;
	private JComboBox<String> searchTypeComboBox; // Dropdown menu for search types
	private Dictionary dictionary;

	public DictionaryGUI() {
		frame = new JFrame("Anime dictionary");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 700); // Set initial frame size

		// Initialize components
		JPanel mainPanel = new JPanel(new BorderLayout());

		// Dropdown menu for search types
		searchTypeComboBox = new JComboBox<>(new String[] { "Anime", "Manga", "Characters" });
		searchTypeComboBox.setSelectedIndex(0); // Default selection is "Anime"

		nameTextField = new JTextField(20);
		searchButton = new JButton("Search");
		resultTextArea = new JTextArea(30, 50);
		resultTextArea.setEditable(false);

		// Create a JScrollPane for the resultTextArea with desired size
		JScrollPane scrollPane = new JScrollPane(resultTextArea);
		scrollPane.setPreferredSize(new Dimension(700, 500)); // Adjust as needed

		// Add components to the main panel
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel("Search Type:"));
		inputPanel.add(searchTypeComboBox); // Add dropdown menu
		inputPanel.add(new JLabel("Enter Search:"));
		inputPanel.add(nameTextField);
		inputPanel.add(searchButton);

		// Add input and scroll pane to the main panel
		mainPanel.add(inputPanel, BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Add main panel to the frame
		frame.add(mainPanel);

		// Initialize dictionary instance
		dictionary = new Dictionary();

		// Add action listener to the search button
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get the search term from the text field
				String searchTerm = nameTextField.getText().trim();
				if (!searchTerm.isEmpty()) {
					// Determine the selected search type
					String selectedType = (String) searchTypeComboBox.getSelectedItem();

					// Call dictionary to search based on selected type
					List<JSONObject> searchResults;
					switch (selectedType) {
					case "Anime":
						searchResults = dictionary.searchAnimeByName(searchTerm);
						break;
					case "Manga":
						searchResults = dictionary.searchMangaByName(searchTerm);
						break;
					case "Characters":
						searchResults = dictionary.searchCharactersByName(searchTerm);
						break;
					default:
						searchResults = Collections.emptyList(); // Default case (shouldn't happen)
						break;
					}

					if (!searchResults.isEmpty()) {
						// Display search results based on search type
						displaySearchResults(searchResults, selectedType);
					} else {
						resultTextArea.setText("No results found for " + selectedType + ": " + searchTerm);
					}
				} else {
					resultTextArea.setText("Please enter a search term.");
				}
			}
		});
	}

	public void displayGUI() {
		frame.setVisible(true);
	}

	private void displaySearchResults(List<JSONObject> searchResults, String searchType) {
		StringBuilder sb = new StringBuilder();

		for (JSONObject item : searchResults) {
			// Customize display based on search type
			switch (searchType) {
			case "Anime":
				sb.append(displayAnimeInfo(item));
				break;
			case "Manga":
				sb.append(displayMangaInfo(item));
				break;
			case "Characters":
				sb.append(displayCharacterInfo(item));
				break;
			default:
				sb.append("Invalid search type");
				break;
			}
		}

		// Set text and scroll to the top of the text area
		resultTextArea.setText(sb.toString());
		resultTextArea.setCaretPosition(0); // Scroll to the beginning of the text
	}

	private String displayAnimeInfo(JSONObject animeInfo) {
		StringBuilder sb = new StringBuilder();

		// Titles (English, Japanese)
		sb.append("Title: ").append(animeInfo.optString("title", "N/A")).append("\n");
		sb.append("Japanese Title: ").append(animeInfo.optString("title_japanese", "N/A")).append("\n");
		sb.append("\n");

		// Synopsis
		String synopsis = animeInfo.optString("synopsis", "Synopsis not available");
		synopsis = insertLineBreaks(synopsis, 70); // Adjust line width as needed
		sb.append("Synopsis:\n").append(synopsis).append("\n\n");

		// Type of series (TV, Manga, Movie)
		sb.append("Type: ").append(animeInfo.optString("type", "N/A")).append("\n");

		// Episodes
		sb.append("Episode(s): ").append(animeInfo.optString("episodes", "N/A")).append("\n");

		// Airing dates if available
		String airedDate = "?";
		String airEnd = "?";

		if (animeInfo.has("aired") && !animeInfo.isNull("aired")) {
			JSONObject aired = animeInfo.getJSONObject("aired");
			if (aired.has("from") && !aired.isNull("from")) {
				airedDate = formatDate(aired.getString("from"));

				if (aired.has("to") && !aired.isNull("to")) {
					airEnd = formatDate(aired.getString("to"));
				}

				sb.append("Aired: ").append(airedDate).append(" to ").append(airEnd).append("\n");
				// Extract month from aired date
				int airedMonth = getMonthFromDate(aired.getString("from"));
				String season = getSeasonFromMonth(airedMonth);

				// Append season and year to Premiered
				sb.append("Premiered: ").append(season).append(" ").append(formatYear(aired.getString("from")))
						.append("\n");
			}
		}

		// Studios
		if (animeInfo.has("studios") && !animeInfo.isNull("studios")) {
			JSONArray studiosArray = animeInfo.getJSONArray("studios");
			sb.append("Studios: ");
			for (int i = 0; i < studiosArray.length(); i++) {
				JSONObject studioObj = studiosArray.getJSONObject(i);
				String studioName = studioObj.optString("name", "Unknown Studio");
				sb.append(studioName);
				if (i < studiosArray.length() - 1) {
					sb.append(", ");
				}
			}
			sb.append("\n");
		} else {
			sb.append("Studios: Studios not available\n");
		}

		// Genres
		if (animeInfo.has("genres") && !animeInfo.isNull("genres")) {
			JSONArray genresArray = animeInfo.getJSONArray("genres");
			sb.append("Genres: ");
			for (int i = 0; i < genresArray.length(); i++) {
				JSONObject genreObj = genresArray.getJSONObject(i);
				String genreName = genreObj.optString("name", "Unknown Genre");
				sb.append(genreName);
				if (i < genresArray.length() - 1) {
					sb.append(", ");
				}
			}
			sb.append("\n");
		} else {
			sb.append("Genres: Genres not available\n");
		}

		// Rating (G, PG, PG-13, R-17+, R+, Rx
		sb.append("Rating: ").append(animeInfo.optString("rating", "N/A")).append("\n");

		// Append separator at the end of each anime info block
		sb.append("---------------------------------------------\n");
		return sb.toString();
	}

	private String displayMangaInfo(JSONObject mangaInfo) {
		StringBuilder sb = new StringBuilder();

		// Title
		sb.append("Title: ").append(mangaInfo.optString("title", "N/A")).append("\n");
		sb.append("\n");

		// Synopsis
		String synopsis = mangaInfo.optString("synopsis", "Synopsis not available");
		synopsis = insertLineBreaks(synopsis, 60); // Adjust line width as needed
		sb.append("Synopsis:\n").append(synopsis).append("\n\n");

		// Type
		sb.append("Type: ").append(mangaInfo.optString("type", "N/A")).append("\n");

		// Volumes
		int volumes = mangaInfo.optInt("volumes");
		sb.append("Volumes: ").append(volumes).append("\n");

		// Chapters
		int chapters = mangaInfo.optInt("chapters");
		sb.append("Chapters: ").append(chapters).append("\n");

		// Status
		String status = mangaInfo.optString("status", "Status not available");
		sb.append("Status: ").append(status).append("\n");

		// Publishing dates if available
		String publishedDate = "?";
		String endPublish = "?";

		if (mangaInfo.has("published") && !mangaInfo.isNull("published")) {
			JSONObject publish = mangaInfo.getJSONObject("published");
			if (publish.has("from") && !publish.isNull("from")) {
				publishedDate = formatDate(publish.getString("from"));

				if (publish.has("to") && !publish.isNull("to")) {
					endPublish = formatDate(publish.getString("to"));
				}
				sb.append("Published: ").append(publishedDate).append(" to ").append(endPublish).append("\n");
			}
		}

		// Genres
		if (mangaInfo.has("genres") && !mangaInfo.isNull("genres")) {
			JSONArray genresArray = mangaInfo.getJSONArray("genres");
			sb.append("Genres: ");

			for (int i = 0; i < genresArray.length(); i++) {
				JSONObject genreObj = genresArray.getJSONObject(i);
				String genreName = genreObj.optString("name", "Unknown Genre");
				sb.append(genreName);
				if (i < genresArray.length() - 1) {
					sb.append(", ");
				}
			}
			sb.append("\n");
		} else {
			sb.append("Genres: Genres not available\n");
		}
		// Theme
		if (mangaInfo.has("themes") && !mangaInfo.isNull("themes")) {
			JSONArray themesArray = mangaInfo.getJSONArray("themes");
			sb.append("Themes: ");

			for (int i = 0; i < themesArray.length(); i++) {
				JSONObject genreObj = themesArray.getJSONObject(i);
				String themeName = genreObj.optString("name", "Unknown Theme");
				sb.append(themeName);
				if (i < themesArray.length() - 1) {
					sb.append(", ");
				}
			}
			sb.append("\n");
		} else {
			sb.append("Themes: Themes not available\n");
		}

		// Authors
		if (mangaInfo.has("authors") && !mangaInfo.isNull("authors")) {
			JSONArray authorArray = mangaInfo.getJSONArray("authors");
			sb.append("Authors: ");

			for (int i = 0; i < authorArray.length(); i++) {
				JSONObject genreObj = authorArray.getJSONObject(i);
				String authorName = genreObj.optString("name", "Unknown Author");
				sb.append(authorName);
				if (i < authorArray.length() - 1) {
					sb.append(", ");
				}
			}
			sb.append("\n");
		} else {
			sb.append("Authors: Authors not available\n");
		}

		// Append separator at the end of manga info block
		sb.append("---------------------------------------------\n");

		return sb.toString();
	}

	private String displayCharacterInfo(JSONObject characterInfo) {

		StringBuilder sb = new StringBuilder();

		// Character Name
		sb.append("Name: ").append(characterInfo.optString("name", "N/A")).append("\n");
		sb.append("\n");

		sb.append("Japanese Name: ").append(characterInfo.optString("name_kanji", "N/A")).append("\n");
		sb.append("\n");

		// Nicknames
		if (characterInfo.has("nicknames") && !characterInfo.isNull("nicknames")) {
			JSONArray nickNameArray = characterInfo.getJSONArray("nicknames");
			if (nickNameArray.length() > 0) {
				sb.append("Nicknames: ");
				for (int i = 0; i < nickNameArray.length(); i++) {
					String nickname = nickNameArray.optString(i, "Unknown Nickname");
					sb.append(nickname);
					if (i < nickNameArray.length() - 1) {
						sb.append(", ");
					}
				}
				sb.append("\n");
			} else {
				sb.append("Nicknames: Nicknames not available\n");
			}
		} else {
			sb.append("Nicknames: Nicknames not available\n");
		}

		// Append separator at the end of character info block
		sb.append("---------------------------------------------\n");
		return sb.toString();
	}

	private String formatDate(String dateString) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy");

			Date date = inputFormat.parse(dateString);
			return outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return dateString; // Return as it is if parsing fails
		}
	}

	private String formatYear(String premierYear) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");

			Date year = inputFormat.parse(premierYear);
			return outputFormat.format(year);

		} catch (ParseException e) {
			e.printStackTrace();
			return premierYear; // Return as it is if parsing fails
		}
	}

	private int getMonthFromDate(String dateString) {
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date date = inputFormat.parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.MONTH) + 1; // Month is zero-based in Calendar
		} catch (ParseException e) {
			e.printStackTrace();
			return 0; // Return 0 if parsing fails
		}
	}

	private String getSeasonFromMonth(int month) {
		switch (month) {
		case 12:
		case 1:
		case 2:
			return "Winter";
		case 3:
		case 4:
		case 5:
			return "Spring";
		case 6:
		case 7:
		case 8:
			return "Summer";
		case 9:
		case 10:
		case 11:
			return "Fall";
		default:
			return "Season not available";
		}
	}

	private String insertLineBreaks(String text, int maxLineLength) {

		StringBuilder sb = new StringBuilder();
		int currentIndex = 0;

		while (currentIndex < text.length()) {
			// Determine the end index for the current line
			int endIndex = currentIndex + maxLineLength;
			if (endIndex >= text.length()) {
				endIndex = text.length(); // Reached the end of the text
			} else {
				// Check if the current line ends within a word
				while (endIndex > currentIndex && !Character.isWhitespace(text.charAt(endIndex - 1))) {
					endIndex--; // Move back to find a word boundary
				}
				// If endIndex is still at currentIndex, no whitespace found; break at max
				// length
				if (endIndex == currentIndex) {
					endIndex = currentIndex + maxLineLength;
				}
			}

			// Append the current line to StringBuilder
			sb.append(text.substring(currentIndex, endIndex)).append("\n");

			// Move to the next line
			currentIndex = endIndex;
			// Skip leading whitespace characters on the next line
			while (currentIndex < text.length() && Character.isWhitespace(text.charAt(currentIndex))) {
				currentIndex++;
			}
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			DictionaryGUI gui = new DictionaryGUI();
			gui.displayGUI();
		});
	}
}
