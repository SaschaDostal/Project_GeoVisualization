package csvdbconverter;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Scanner;  

public class PMSensors {

	public static void main(String[] args) throws FileNotFoundException, SQLException {
		
		List<List<String>> records = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("data/PM_Sensor_Location.csv"));) {
		    while (scanner.hasNextLine()) {
		        records.add(getRecordFromLine(scanner.nextLine()));
		    }
		}
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
		for(List<String> line : records) {
			int id = Integer.parseInt(line.get(0));
			float lon = Float.parseFloat(line.get(2));
			float lat = Float.parseFloat(line.get(1));
			
		    String query = "insert into PM_SENSORS (ID, LON, LAT) values (" + id + ", " + lon + ", " + lat  + ")";
		    Statement stmt = con.createStatement();
		    stmt.execute(query);
		}
		con.close();
	}
	
	private static List<String> getRecordFromLine(String line) {
	    List<String> values = new ArrayList<String>();
	    try (Scanner rowScanner = new Scanner(line)) {
	        rowScanner.useDelimiter(";");
	        while (rowScanner.hasNext()) {
	            values.add(rowScanner.next());
	        }
	    }
	    return values;
	}

}
