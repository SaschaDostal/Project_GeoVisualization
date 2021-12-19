package csvdbconverter;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Scanner;  

public class PMSensorData {

	public static void main(String[] args) throws FileNotFoundException, SQLException {
		
		List<List<String>> records = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("data/PM_Sensor_Data_20210101.csv"));) {
		    while (scanner.hasNextLine()) {
		        records.add(getRecordFromLine(scanner.nextLine()));
		    }
		}
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
		for(List<String> line : records) {
			int id = Integer.parseInt(line.get(0));
			String timestamp = line.get(5);
			float p1 = Float.parseFloat(line.get(6));
			float p2 = Float.parseFloat(line.get(9));
			
		    String query = "insert into PM_VALUES (ID, TIME_STAMP, P1, P2) values (" + id + ", '" + timestamp + "', " + p1 + ", " + p2 + ")";
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
