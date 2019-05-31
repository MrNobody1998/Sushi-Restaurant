package comp1206.sushi.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
public class Configuration {
	private ArrayList<String> content = new ArrayList<String>();
	
	public Configuration() {
		content.clear();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("data.txt"));
			String line = reader.readLine();
			while (line != null) {
				content.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public Configuration(String filename) {
		content.clear();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			while (line != null) {
				content.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
		
	public ArrayList<String> getContent() {
		return content;
	}
}