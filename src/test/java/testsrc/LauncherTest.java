package testsrc;

import javax.swing.SwingUtilities;

public class LauncherTest {

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			// Display the DictionaryGUI
			GUITEST gui = new GUITEST();
			gui.displayGUI(); // This method displays the GUI
		});
	}
}
