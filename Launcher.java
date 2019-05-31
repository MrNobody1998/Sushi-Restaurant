package comp1206.sushi;

/**
 * YOU ARE NOT ALLOWED TO MODIFY THIS CLASS IN ANY WAY
 * 
 * Any initialisation and changes you need to make can be done in the constructor for the Server or Client
 *
 */
public class Launcher {

	public static void main(String[] argv) {	
		if(argv.length > 0 && argv[0].equals("client")) {
			System.out.println("Running client");
			ClientApplication.main(argv);
		} else {
			System.out.println("Running server");
			ServerApplication.main(argv);
		}
	}
}
