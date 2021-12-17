package csvdbconverter;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Scanner;  

public class Wind {

	public static void main(String[] args) throws FileNotFoundException, SQLException {
		
		List<List<String>> records = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File("data/wind_zehn_min_ff_20200612_20211213_04931.csv"));) {
		    while (scanner.hasNextLine()) {
		        records.add(getRecordFromLine(scanner.nextLine()));
		    }
		}
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
		for(List<String> line : records) {
			String timestamp = line.get(1);
			float speed = Float.parseFloat(line.get(3));
			int degree = Integer.parseInt(line.get(4));
			
		    String query = "insert into WIND (TSTAMP, SPEED, DEGREE) values (" + timestamp + ", " + speed + ", " + degree + ")";
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
