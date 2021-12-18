package project.airquality.backend;

import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "*")
@RestController
public class WindController {
	
	// example uri: http://localhost:8080/windVector?tstamp=202111041210
	@RequestMapping("/windVector")
	public String getWindData(@RequestParam String tstamp) throws SQLException {
		Connection con = DriverManager.
	            getConnection("jdbc:h2:file:./database", "sa", "");
	    String query = "SELECT * FROM wind WHERE tstamp LIKE '" + tstamp + "'";
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(query);
	    
	    if (rs.next()) {
	    	int degree = Integer.parseInt(rs.getString("degree"));
	    	degree += 90;
	    	if(degree > 359) degree -= 360;
	    	float speed = Float.parseFloat(rs.getString("speed"));
	    
	    	float x = speed * (float) Math.cos((double) degree * Math.PI /180.0);
	    	float y = speed * (float) Math.sin((double) degree * Math.PI /180.0);
	    
	    	JSONObject json = new  JSONObject();
	    	json.put("x", x);
	    	json.put("y", y);
	    	return json.toString();
	    } else {
	    	return "No Data";
	    }
	}
	
}
