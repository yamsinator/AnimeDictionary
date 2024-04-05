import javax.swing.SwingUtilities;

public class AnimeDictionaryLauncher {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Display weather app
				new DictionaryGUI().setVisible(true);

				// Testing functions
//				System.out.println(WeatherApp.getLocationData("Tokyo"));

//				System.out.println(WeatherApp.getCurrentTime());
			}
		});
	}
}