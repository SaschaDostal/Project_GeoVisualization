package project.airquality.backend;

import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@CrossOrigin(origins = "*")
@RestController
public class DiagramController {
	
	@RequestMapping("/DiagramLinePM/{id}")
	public String getPMStationDiagramm(@PathVariable(value="id") int id) throws SQLException {
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		Float lastPM10 = 0.0f;
		Float lastPM25 = 0.0f;
		for(int day = 1; day <= 1; day++) {
			for(int hour = 0; hour < 24; hour++) {
				for(int min = 0; min < 6; min+=3) {
					JSONObject jo = new JSONObject();
					// Format PM: 2021-01-01T00:0 Format Pr/Wi: 20210101000
					String timestampPM = "2021-01-" + String.format("%02d", day) + "T" 
							+ String.format("%02d", hour) + ":" + min;
					jo.put("TSTAMP", timestampPM + "0");
					
					String query = "SELECT P1, P2 FROM PM_VALUES WHERE ID=" + id + " AND TIME_STAMP LIKE '" + timestampPM + "%'";
					try {
						Statement stmt = con.createStatement();
					    ResultSet rs = stmt.executeQuery(query);
					    rs.next();
					    lastPM10 = rs.getFloat("P1");
					    lastPM25 = rs.getFloat("P2");
					    
					} catch( SQLException e ) {
						System.out.println("No PM data found for time stamp: " + timestampPM);
					} 
				    
					jo.put("P1", lastPM10);
					jo.put("P2", lastPM25);
					
				    ja.put(jo);
				}
			}
		}
	    json.put("diagrammLinePM", ja);
	    con.close();
	    
		return json.toString();
	}
	
	@RequestMapping("/DiagramLine")
	public String getAllDiagramm() throws SQLException {
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		for(int day = 1; day <= 1; day++) {
			for(int hour = 0; hour < 24; hour++) {
				for(int min = 0; min < 6; min+=3) {
					JSONObject jo = new JSONObject();
					// Format PM: 2021-01-01T00:0 Format Pr/Wi: 20210101000
					String timestampPM = "2021-01-" + String.format("%02d", day) + "T" 
							+ String.format("%02d", hour) + ":" + min;
					String timestampPrWi = "202101" + String.format("%02d", day) + String.format("%02d", hour) + min + "0";
					
					jo.put("TSTAMP", timestampPM + "0");
					
					// Query for PM
					String query1 = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + timestampPM + "%'";
				    Statement stmt1 = con.createStatement();
				    ResultSet rs1 = stmt1.executeQuery(query1);
				    rs1.next();
				    jo.put("P1", rs1.getFloat("AVG(P1)"));
				    jo.put("P2", rs1.getFloat("AVG(P2)"));
				    
				    // Query for Precipitation
				    String query2 = "SELECT MM FROM PRECIPITATION WHERE TSTAMP LIKE '" + timestampPrWi + "'";
				    Statement stmt2 = con.createStatement();
				    ResultSet rs2 = stmt2.executeQuery(query2);
				    rs2.next();
				    jo.put("MM", rs2.getFloat("MM"));
				    
				    // Query for Wind
				    String query3 = "SELECT SPEED FROM WIND WHERE TSTAMP LIKE '" + timestampPrWi + "'";
				    Statement stmt3 = con.createStatement();
				    ResultSet rs3 = stmt3.executeQuery(query3);
				    rs3.next();
				    jo.put("SPEED", rs3.getFloat("SPEED"));
					
				    ja.put(jo);
				}
			}
		}
	    json.put("diagrammLine", ja);
	    con.close();
	    
		return json.toString();
	}
	
}
