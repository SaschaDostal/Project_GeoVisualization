package project.airquality.backend;

import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "*")
@RestController
public class PrecipitationController {
	
	// example uri: http://localhost:8080/precipitation?tstamp=202111041210
	@RequestMapping("/precipitation")
	public String getAllPMStations(@RequestParam String tstamp) throws SQLException {
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
	    String query = "SELECT * FROM precipitation WHERE tstamp LIKE '" + tstamp + "'";
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(query);
	    JSONObject json = new  JSONObject();
	    JSONArray ja = new JSONArray();
	    while (rs.next()) {
	    	JSONObject jo = new JSONObject();
	    	jo.put("tstamp", rs.getString("tstamp"));
	    	jo.put("duration", rs.getInt("duration"));
			jo.put("mm", rs.getFloat("mm"));
			jo.put("raining", rs.getInt("raining"));
			ja.put(jo);
	    }
	    json.put("precipitation", ja);
	    con.close();
		return json.toString();
	}
	
}
