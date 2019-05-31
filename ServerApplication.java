package comp1206.sushi;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import comp1206.sushi.server.Server;
import comp1206.sushi.server.ServerWindow;

/**
 * YOU ARE NOT ALLOWED TO MODIFY THIS CLASS IN ANY WAY
 * 
 * Any initialisation and changes you need to make can be done in the constructor for the Server
 *
 */
public class ServerApplication {

	public static void main(String[] argv) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
 
		Server server = new Server();
		SwingUtilities.invokeLater(() -> new ServerWindow(server));
	}
}

