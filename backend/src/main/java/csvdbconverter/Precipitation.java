package csvdbconverter;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Scanner;  

public class Precipitation {

	public static void main(String[] args) throws FileNotFoundException, SQLException {
		
		List<List<String>> records = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("data/niederschlag_zehn_min_rr_20200612_20211213_04931.csv"));) {
		    while (scanner.hasNextLine()) {
		        records.add(getRecordFromLine(scanner.nextLine()));
		    }
		}
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
		for(List<String> line : records) {
			String timestamp = line.get(1);
			int duration = Integer.parseInt(line.get(3));
			float mm = Float.parseFloat(line.get(4));
			int raining = Integer.parseInt(line.get(5));
			
		    String query = "insert into PRECIPITATION (TSTAMP, DURATION, MM, RAINING) values (" + timestamp + ", " + duration + ", " + mm + ", " + raining + ")";
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
