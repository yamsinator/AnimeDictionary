import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DictionaryGUI extends JFrame {

	private JTextField searchField;
	private JLabel resultImage;
	private JLabel loadingLabel;

	public DictionaryGUI() {
		super("Anime Dictionary");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(700, 800);
		setLocationRelativeTo(null);
		setLayout(null); // Using null layout to manually position components
		setResizable(true);

		addGUIComponents();
	}

	private void addGUIComponents() {

		// Create search field
		searchField = new JTextField();
		searchField.setBounds(15, 15, 320, 45);
		searchField.setFont(new Font("Helvetica", Font.PLAIN, 24));
		add(searchField);

		// Create search button
		JButton searchButton = new JButton("Search");
		searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		searchButton.setBounds(350, 15, 80, 45);
		add(searchButton);

		// Create loading label
		loadingLabel = new JLabel("Loading...");
		loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loadingLabel.setBounds(0, 70, 450, 36);
		loadingLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
		loadingLabel.setForeground(Color.BLACK);
		loadingLabel.setVisible(false);
		add(loadingLabel);

		// Create result image label
		resultImage = new JLabel(loadImage("src/main/resources/appImages/111305.jpg"));
		resultImage.setBounds(50, 120, 350, 480);
		add(resultImage);

		// Create

		// Add action listener to search button
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String searchTerm = searchField.getText().trim();
				if (!searchTerm.isEmpty()) {
					try {
						// Simulate loading (replace with actual API call)
						loadingLabel.setVisible(true);
						displayAnimeImage(searchTerm);
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(DictionaryGUI.this,
								"Error fetching anime image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					} finally {
						loadingLabel.setVisible(false);
					}
				} else {
					JOptionPane.showMessageDialog(DictionaryGUI.this, "Please enter an anime title.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private void displayAnimeImage(String searchTerm) throws IOException {
		// Replace this URL with your actual image API endpoint or file URL
		String imageUrl = "https://example.com/animeimages/" + searchTerm + ".jpg";
		URL url = new URL(imageUrl);
		ImageIcon imageIcon = new ImageIcon(ImageIO.read(url));
		resultImage.setIcon(imageIcon);
	}

	// Used to load images in our GUI components.
	private ImageIcon loadImage(String fileName) {
		try {
			// Read the image file from the root directory of the JAR.
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/" + fileName));
			// Return an ImageIcon so that our component can render it.
			return new ImageIcon(image);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not find resource: " + fileName);
			return null;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DictionaryGUI app = new DictionaryGUI();
				app.setVisible(true);
			}
		});
	}
}
