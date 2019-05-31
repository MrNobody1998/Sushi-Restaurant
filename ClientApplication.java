package comp1206.sushi;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import comp1206.sushi.client.Client;
import comp1206.sushi.client.ClientWindow;

/**
 * YOU ARE NOT ALLOWED TO MODIFY THIS CLASS IN ANY WAY
 * 
 * Any initialisation and changes you need to make can be done in the constructor for the Client
 *
 */
public class ClientApplication {
	public static void main(String[] argv) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			e.printStackTrace(); 
		}

		Client client = new Client();
		
		SwingUtilities.invokeLater(() -> new ClientWindow(client));
	}
}
 