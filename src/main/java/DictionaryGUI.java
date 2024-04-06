import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

public class DictionaryGUI {

	private JFrame frame;
	private JTextField searchTextField;
	private JButton searchButton;
	private JTextArea resultTextArea;
	private Dictionary dictionary;
	private JLabel imageLabel;

	public DictionaryGUI() {
		frame = new JFrame("Anime Dictionary");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 700); // Set initial frame size

		// Initialize components
		JPanel mainPanel = new JPanel(new BorderLayout());
		searchTextField = new JTextField(20);
		searchButton = new JButton("Search");
		resultTextArea = new JTextArea(25, 50);
		resultTextArea.setEditable(false);

		// Create a JScrollPane for the resultTextArea with desired size
		JScrollPane scrollPane = new JScrollPane(resultTextArea);
		scrollPane.setPreferredSize(new Dimension(700, 500)); // Adjust as needed

		// Add components to the main panel
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel("Enter Anime Name:"));
		inputPanel.add(searchTextField);
		inputPanel.add(searchButton);

		// Add input and scroll pane to the main panel
		mainPanel.add(inputPanel, BorderLayout.NORTH);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		// Add main panel to the frame
		frame.add(mainPanel);

		// Add image label
		imageLabel = new JLabel();

		// Initialize Dictionary instance
		dictionary = new Dictionary();

		// Add key listener to searchTextField
		searchTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Call the search action when "Enter" key is pressed in searchTextField
				performSearch();
			}
		});

		// Add action listener to the search button
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Call the search action when search button is clicked
				performSearch();
			}
		});
	}

	public void displayGUI() {
		frame.setVisible(true);
	}

	private void displaySearchResults(List<JSONObject> searchResults) {
		StringBuilder sb = new StringBuilder();

		for (JSONObject animeInfo : searchResults) {
			sb.append("Title: ").append(animeInfo.optString("title", "N/A")).append("\n");

			// Append synopsis if available with line breaks for word wrapping
			String synopsis = animeInfo.optString("synopsis", "Synopsis not available");
			synopsis = insertLineBreaks(synopsis, 60); // Adjust line width as needed
			sb.append("Synopsis:\n").append(synopsis).append("\n\n");

			// Append aired date if available
			String airedDate = "Aired date not available";
			if (animeInfo.has("aired") && !animeInfo.isNull("aired")) {
				JSONObject aired = animeInfo.getJSONObject("aired");
				if (aired.has("from") && !aired.isNull("from")) {
					airedDate = aired.getString("from").split("T")[0]; // Extract date part only
				}
			}
			sb.append("Aired Date: ").append(airedDate).append("\n");

			// Append status if available
			String status = animeInfo.optString("status", "Status not available");
			sb.append("Status: ").append(status).append("\n");

			sb.append("-----------------------------------\n"); // Separator between search results

			// Display image of anime series using small_image_url
			if (animeInfo.has("small_image_url")) {
				String imageUrl = animeInfo.getString("small_image_url");
				displayImage(imageUrl);
			}

		}

		// Set text and scroll to the top of the text area
		resultTextArea.setText(sb.toString());
		resultTextArea.setCaretPosition(0); // Scroll to the beginning of the text
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

	private void performSearch() {
		// Get the anime name from the text field
		String animeName = searchTextField.getText().trim();
		if (!animeName.isEmpty()) {
			// Call Dictionary to search anime by name
			List<JSONObject> searchResults = dictionary.searchAnimeByName(animeName);
			if (!searchResults.isEmpty()) {
				// Display search results with images
				displaySearchResults(searchResults);
			} else {
				resultTextArea.setText("Anime not found or error occurred.");
			}
		} else {
			resultTextArea.setText("Please enter an anime name.");
		}
	}

	private void displayImage(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			ImageIcon icon = new ImageIcon(ImageIO.read(url));
			imageLabel.setIcon(icon); // Set the image icon on imageLabel
		} catch (IOException e) {
			System.err.println("Error loading image from URL: " + imageUrl);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			DictionaryGUI gui = new DictionaryGUI();
			gui.displayGUI();
		});
	}
}
