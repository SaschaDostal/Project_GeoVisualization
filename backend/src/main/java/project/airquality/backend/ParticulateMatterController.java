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
	
	@RequestMapping("/set")
	public String setTest(@RequestParam int id) throws SQLException {
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
	    String query = "insert into PM_SENSORS values (" + id + ", " + id + ".2, " + id + ".3)";
	    Statement stmt = con.createStatement();
	    stmt.execute(query);
		con.close();
		
		return "ID: " + id + ", lon: " + pmrepo.findById((long) id).get().getLon();
	}
	
	@RequestMapping("/PMStations")
	public String getAllPMStations() throws SQLException {
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
	    String query = "SELECT * FROM PM_SENSORS";
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(query);
	    JSONArray ja = new JSONArray();
	    while (rs.next()) {
	    	JSONObject jo = new JSONObject();
	    	jo.put("id", rs.getInt("id"));
	    	jo.put("lon", rs.getFloat("lon"));
			jo.put("lat", rs.getFloat("lat"));
			ja.put(jo);
	    }
	    
	    con.close();
	    
		return ja.toString();
	}
	
}
