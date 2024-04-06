import javax.swing.SwingUtilities;

public class ApplicationLauncher {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			// Display the DictionaryGUI
			DictionaryGUI gui = new DictionaryGUI();
			gui.displayGUI(); // This method displays the GUI
		});
	}
}