package project.airquality.backend;

import org.springframework.web.bind.annotation.RestController;

import project.airquality.backend.repository.PMRepository;

import java.sql.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "*")
@RestController
public class ParticulateMatterController {
	
	@Autowired
	private PMRepository pmrepo;
	@RequestMapping("/PMStations/{id}")
	public String getPMStation(@PathVariable(value="id") int id) {
		JSONObject jo = new JSONObject();
		jo.put("id", id);
		jo.put("lon", pmrepo.findById((long) id).get().getLon());
		jo.put("lat", pmrepo.findById((long) id).get().getLat());
		
		return jo.toString();
	}
	
	@RequestMapping("/PMStations")
	public String getAllPMStations() throws SQLException {
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
	    String query = "SELECT * FROM PM_SENSORS";
	    Statement stmt = con.createStatement();
	    JSONObject json = new JSONObject();
	    ResultSet rs = stmt.executeQuery(query);
	    JSONArray ja = new JSONArray();
	    while (rs.next()) {
	    	JSONObject jo = new JSONObject();
	    	jo.put("id", rs.getInt("id"));
	    	jo.put("lon", rs.getFloat("lon"));
			jo.put("lat", rs.getFloat("lat"));
			ja.put(jo);
	    }
	    json.put("pmstations", ja);
	    con.close();
	    
		return json.toString();
	}
	
	//2021-01-01T00:01:00 http://localhost:8080/PMData?tstamp=2021-01-01T00:
	@RequestMapping("/PMData")
	public String getPMStation(@RequestParam String tstamp) throws SQLException {
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
	    String query = "SELECT * FROM PM_SENSORS";
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(query);
	    
	    JSONObject json = new  JSONObject();
	    JSONArray ja = new JSONArray();
	    
	    while (rs.next()) {
	    	JSONObject jo = new JSONObject();
	    	try {
	    		String query2 = "SELECT * FROM PM_VALUES, PM_SENSORS WHERE PM_VALUES.ID=PM_SENSORS.ID AND TIME_STAMP LIKE '" + tstamp + "%' AND PM_VALUES.ID = " + rs.getInt("id");
	    		Statement stmt2 = con.createStatement();
	    		ResultSet rs2 = stmt2.executeQuery(query2);
	    		rs2.next();
	    		jo.put("ID", rs2.getString("ID"));
	    		jo.put("P1", rs2.getFloat("P1"));
	    		jo.put("P2", rs2.getFloat("P2"));
	    		jo.put("LAT", rs2.getFloat("LAT"));
	    		jo.put("LON", rs2.getFloat("LON"));
	    		ja.put(jo);
	    	} catch (Exception e) {
	    		System.out.println("No entry found for: " + rs.getInt("id") + " - " + tstamp);
	    	}
	    }
	    json.put("sensors", ja);
	    con.close();
		return json.toString();
	}
	
}
