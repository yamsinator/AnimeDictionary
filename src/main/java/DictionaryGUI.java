import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
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

import org.json.JSONObject;

public class DictionaryGUI {

	private JFrame frame;
	private JTextField nameTextField;
	private JButton searchButton;
	private JTextArea resultTextArea;
	private JComboBox<String> searchTypeComboBox; // Dropdown menu for search types
	private Dictionary dictionary;

	public DictionaryGUI() {
		
		frame = new JFrame("Anime Dictionary");
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

		// Initialize Dictionary instance
		dictionary = new Dictionary();

		// Add action listener to the search button
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get the anime name from the text field
				String searchTerm = nameTextField.getText().trim();
				if (!searchTerm.isEmpty()) {
					// Determine the selected search type
					String selectedType = (String) searchTypeComboBox.getSelectedItem();

					// Call Dictionary to search based on selected type
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
						// Display search results
						displaySearchResults(searchResults);
					} else {
						resultTextArea.setText("No results found for " + selectedType + ": " + searchTerm);
					}
				} else {
					resultTextArea.setText("Please enter an anime name.");
				}
			}
		});
	}

	public void displayGUI() {
		frame.setVisible(true);
	}

	private void displaySearchResults(List<JSONObject> searchResults) {
		StringBuilder sb = new StringBuilder();

		for (JSONObject item : searchResults) {
			sb.append("Title: ").append(item.optString("title", "N/A")).append("\n");
			// Append other details as needed
			sb.append("-----------------------------------\n");
		}

		// Set text and scroll to the top of the text area
		resultTextArea.setText(sb.toString());
		resultTextArea.setCaretPosition(0); // Scroll to the beginning of the text
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			DictionaryGUI gui = new DictionaryGUI();
			gui.displayGUI();
		});
	}
}
