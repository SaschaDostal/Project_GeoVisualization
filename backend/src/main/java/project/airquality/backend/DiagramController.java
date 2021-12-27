package project.airquality.backend;

import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "*")
@RestController
public class DiagramController {

	@RequestMapping("/DiagramLinePM/{id}")
	public String getPMStationDiagramm(@PathVariable(value = "id") int id) throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		Float lastPM10 = 0.0f;
		Float lastPM25 = 0.0f;
		for (int day = 1; day <= 9; day++) {
			for (int hour = 0; hour < 24; hour++) {
				JSONObject jo = new JSONObject();
				// Format PM: 2021-01-01T00:0 Format Pr/Wi: 20210101000
				String timestampPM = "2021-01-" + String.format("%02d", day) + "T" + String.format("%02d", hour);
				jo.put("TSTAMP", timestampPM + "0");

				String query = "SELECT P1, P2 FROM PM_VALUES WHERE ID=" + id + " AND TIME_STAMP LIKE '" + timestampPM
						+ "%'";
				try {
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery(query);
					rs.next();
					lastPM10 = rs.getFloat("P1");
					lastPM25 = rs.getFloat("P2");

				} catch (SQLException e) {
					System.out.println("No PM data found for time stamp: " + timestampPM);
				}

				jo.put("P1", lastPM10);
				jo.put("P2", lastPM25);

				ja.put(jo);

			}
		}
		json.put("diagrammLinePM", ja);
		con.close();
		return json.toString();
	}

	@RequestMapping("/DiagramLine")
	public String getAllDiagramm() throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		for (int day = 1; day <= 9; day++) {
			for (int hour = 0; hour < 24; hour++) {
				JSONObject jo = new JSONObject();
				// Format PM: 2021-01-01T00:0 Format Pr/Wi: 20210101000
				String timestampPM = "2021-01-" + String.format("%02d", day) + "T" + String.format("%02d", hour);
				String timestampPrWi = "202101" + String.format("%02d", day) + String.format("%02d", hour) + "00";

				jo.put("TSTAMP", timestampPM + "00");

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
		json.put("diagrammLine", ja);
		con.close();
		try (FileWriter file = new FileWriter("json_cache/diagram_line_cached.json")) {
            file.write(json.toString()); 
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return json.toString();
	}
	
	@RequestMapping("/DiagramLineCached")
	public String getAllDiagrammCached() throws SQLException {
		try(BufferedReader reader = new BufferedReader(new FileReader("json_cache/diagram_line_cached.json")))
        {
			return reader.readLine();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return "{}";
        } catch (IOException e2) {
			e2.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping("/DiagramWindDirection")
	public String getWindDiagramm() throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
		JSONObject json = new JSONObject();
		JSONArray ja = new JSONArray();
		
		// NORTH

		ArrayList<Float> pm10North = new ArrayList<Float>();
		ArrayList<Float> pm25North = new ArrayList<Float>();
		String queryNorth = "SELECT TSTAMP FROM WIND WHERE SPEED>=1.0 AND (DEGREE>337 OR DEGREE<23) AND TSTAMP LIKE '2021010%'";
		Statement stmtNorth = con.createStatement();
		ResultSet rsNorth = stmtNorth.executeQuery(queryNorth);

		while (rsNorth.next()) {
			String tstamp = rsNorth.getString("TSTAMP");
			String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
					+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);
			String queryPM = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%'";
			Statement stmtPM = con.createStatement();
			ResultSet rsPM = stmtPM.executeQuery(queryPM);
			if (rsPM.next()) {
				pm10North.add(rsPM.getFloat("AVG(P1)"));
				pm25North.add(rsPM.getFloat("AVG(P2)"));
			}
		}
		JSONObject jsonNorth = new JSONObject();
		jsonNorth.put("WindDirection", "North");
		jsonNorth.put("P1", pm10North.stream().mapToDouble(a -> a).average().getAsDouble());
		jsonNorth.put("P2", pm25North.stream().mapToDouble(a -> a).average().getAsDouble());
		ja.put(jsonNorth);

		// NORTHEAST

		ArrayList<Float> pm10NorthEast = new ArrayList<Float>();
		ArrayList<Float> pm25NorthEast = new ArrayList<Float>();
		String queryNorthEast = "SELECT TSTAMP FROM WIND WHERE SPEED>=1.0 AND (DEGREE>292 AND DEGREE<338) AND TSTAMP LIKE '2021010%'";
		Statement stmtNorthEast = con.createStatement();
		ResultSet rsNorthEast = stmtNorthEast.executeQuery(queryNorthEast);

		while (rsNorthEast.next()) {
			String tstamp = rsNorthEast.getString("TSTAMP");
			String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
					+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);
			String queryPM = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%'";
			Statement stmtPM = con.createStatement();
			ResultSet rsPM = stmtPM.executeQuery(queryPM);
			if (rsPM.next()) {
				pm10NorthEast.add(rsPM.getFloat("AVG(P1)"));
				pm25NorthEast.add(rsPM.getFloat("AVG(P2)"));
			}
		}
		JSONObject jsonNorthEast = new JSONObject();
		jsonNorthEast.put("WindDirection", "NorthEast");
		jsonNorthEast.put("P1", pm10NorthEast.stream().mapToDouble(a -> a).average().getAsDouble());
		jsonNorthEast.put("P2", pm25NorthEast.stream().mapToDouble(a -> a).average().getAsDouble());
		ja.put(jsonNorthEast);

		// EAST

		ArrayList<Float> pm10East = new ArrayList<Float>();
		ArrayList<Float> pm25East = new ArrayList<Float>();
		String queryEast = "SELECT TSTAMP FROM WIND WHERE SPEED>=1.0 AND (DEGREE>247 AND DEGREE<293) AND TSTAMP LIKE '2021010%'";
		Statement stmtEast = con.createStatement();
		ResultSet rsEast = stmtEast.executeQuery(queryEast);

		while (rsEast.next()) {
			String tstamp = rsEast.getString("TSTAMP");
			String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
					+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);
			String queryPM = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%'";
			Statement stmtPM = con.createStatement();
			ResultSet rsPM = stmtPM.executeQuery(queryPM);
			if (rsPM.next()) {
				pm10East.add(rsPM.getFloat("AVG(P1)"));
				pm25East.add(rsPM.getFloat("AVG(P2)"));
			}
		}
		JSONObject jsonEast = new JSONObject();
		jsonEast.put("WindDirection", "East");
		jsonEast.put("P1", pm10East.stream().mapToDouble(a -> a).average().getAsDouble());
		jsonEast.put("P2", pm25East.stream().mapToDouble(a -> a).average().getAsDouble());
		ja.put(jsonEast);

		// SOUTHEAST

		ArrayList<Float> pm10SouthEast = new ArrayList<Float>();
		ArrayList<Float> pm25SouthEast = new ArrayList<Float>();
		String querySouthEast = "SELECT TSTAMP FROM WIND WHERE SPEED>=1.0 AND (DEGREE>202 AND DEGREE<248) AND TSTAMP LIKE '2021010%'";
		Statement stmtSouthEast = con.createStatement();
		ResultSet rsSouthEast = stmtSouthEast.executeQuery(querySouthEast);

		while (rsSouthEast.next()) {
			String tstamp = rsSouthEast.getString("TSTAMP");
			String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
					+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);
			String queryPM = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%'";
			Statement stmtPM = con.createStatement();
			ResultSet rsPM = stmtPM.executeQuery(queryPM);
			if (rsPM.next()) {
				pm10SouthEast.add(rsPM.getFloat("AVG(P1)"));
				pm25SouthEast.add(rsPM.getFloat("AVG(P2)"));
			}
		}
		JSONObject jsonSouthEast = new JSONObject();
		jsonSouthEast.put("WindDirection", "SouthEast");
		jsonSouthEast.put("P1", pm10SouthEast.stream().mapToDouble(a -> a).average().getAsDouble());
		jsonSouthEast.put("P2", pm25SouthEast.stream().mapToDouble(a -> a).average().getAsDouble());
		ja.put(jsonSouthEast);

		// SOUTH

		ArrayList<Float> pm10South = new ArrayList<Float>();
		ArrayList<Float> pm25South = new ArrayList<Float>();
		String querySouth = "SELECT TSTAMP FROM WIND WHERE SPEED>=1.0 AND (DEGREE>157 AND DEGREE<203) AND TSTAMP LIKE '2021010%'";
		Statement stmtSouth = con.createStatement();
		ResultSet rsSouth = stmtSouth.executeQuery(querySouth);

		while (rsSouth.next()) {
			String tstamp = rsSouth.getString("TSTAMP");
			String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
					+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);
			String queryPM = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%'";
			Statement stmtPM = con.createStatement();
			ResultSet rsPM = stmtPM.executeQuery(queryPM);
			if (rsPM.next()) {
				pm10South.add(rsPM.getFloat("AVG(P1)"));
				pm25South.add(rsPM.getFloat("AVG(P2)"));
			}
		}
		JSONObject jsonSouth = new JSONObject();
		jsonSouth.put("WindDirection", "South");
		jsonSouth.put("P1", pm10South.stream().mapToDouble(a -> a).average().getAsDouble());
		jsonSouth.put("P2", pm25South.stream().mapToDouble(a -> a).average().getAsDouble());
		ja.put(jsonSouth);

		// SOUTHWEST

		ArrayList<Float> pm10SouthWest = new ArrayList<Float>();
		ArrayList<Float> pm25SouthWest = new ArrayList<Float>();
		String querySouthWest = "SELECT TSTAMP FROM WIND WHERE SPEED>=1.0 AND (DEGREE>112 AND DEGREE<158) AND TSTAMP LIKE '2021010%'";
		Statement stmtSouthWest = con.createStatement();
		ResultSet rsSouthWest = stmtSouthWest.executeQuery(querySouthWest);

		while (rsSouthWest.next()) {
			String tstamp = rsSouthWest.getString("TSTAMP");
			String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
					+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);
			String queryPM = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%'";
			Statement stmtPM = con.createStatement();
			ResultSet rsPM = stmtPM.executeQuery(queryPM);
			if (rsPM.next()) {
				pm10SouthWest.add(rsPM.getFloat("AVG(P1)"));
				pm25SouthWest.add(rsPM.getFloat("AVG(P2)"));
			}
		}
		JSONObject jsonSouthWest = new JSONObject();
		jsonSouthWest.put("WindDirection", "SouthWest");
		jsonSouthWest.put("P1", pm10SouthWest.stream().mapToDouble(a -> a).average().getAsDouble());
		jsonSouthWest.put("P2", pm25SouthWest.stream().mapToDouble(a -> a).average().getAsDouble());
		ja.put(jsonSouthWest);

		// WEST

		ArrayList<Float> pm10West = new ArrayList<Float>();
		ArrayList<Float> pm25West = new ArrayList<Float>();
		String queryWest = "SELECT TSTAMP FROM WIND WHERE SPEED>=1.0 AND (DEGREE>67 AND DEGREE<113) AND TSTAMP LIKE '2021010%'";
		Statement stmtWest = con.createStatement();
		ResultSet rsWest = stmtWest.executeQuery(queryWest);

		while (rsWest.next()) {
			String tstamp = rsWest.getString("TSTAMP");
			String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
					+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);
			String queryPM = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%'";
			Statement stmtPM = con.createStatement();
			ResultSet rsPM = stmtPM.executeQuery(queryPM);
			if (rsPM.next()) {
				pm10West.add(rsPM.getFloat("AVG(P1)"));
				pm25West.add(rsPM.getFloat("AVG(P2)"));
			}
		}
		JSONObject jsonWest = new JSONObject();
		jsonWest.put("WindDirection", "West");
		jsonWest.put("P1", pm10West.stream().mapToDouble(a -> a).average().getAsDouble());
		jsonWest.put("P2", pm25West.stream().mapToDouble(a -> a).average().getAsDouble());
		ja.put(jsonWest);

		// NORTHWEST

		ArrayList<Float> pm10NorthWest = new ArrayList<Float>();
		ArrayList<Float> pm25NorthWest = new ArrayList<Float>();
		String queryNorthWest = "SELECT TSTAMP FROM WIND WHERE SPEED>=1.0 AND (DEGREE>22 AND DEGREE<68) AND TSTAMP LIKE '2021010%'";
		Statement stmtNorthWest = con.createStatement();
		ResultSet rsNorthWest = stmtNorthWest.executeQuery(queryNorthWest);

		while (rsNorthWest.next()) {
			String tstamp = rsNorthWest.getString("TSTAMP");
			String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
					+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);
			String queryPM = "SELECT AVG(P1), AVG(P2) FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%'";
			Statement stmtPM = con.createStatement();
			ResultSet rsPM = stmtPM.executeQuery(queryPM);
			if (rsPM.next()) {
				pm10NorthWest.add(rsPM.getFloat("AVG(P1)"));
				pm25NorthWest.add(rsPM.getFloat("AVG(P2)"));
			}
		}
		JSONObject jsonNorthWest = new JSONObject();
		jsonNorthWest.put("WindDirection", "NorthWest");
		jsonNorthWest.put("P1", pm10NorthWest.stream().mapToDouble(a -> a).average().getAsDouble());
		jsonNorthWest.put("P2", pm25NorthWest.stream().mapToDouble(a -> a).average().getAsDouble());
		ja.put(jsonNorthWest);

		json.put("diagramWindDirection", ja);
		con.close();
		try (FileWriter file = new FileWriter("json_cache/diagram_wind_cached.json")) {
            file.write(json.toString()); 
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return json.toString();
	}

	@RequestMapping("/DiagramWindDirectionCached")
	public String getWindCached() throws SQLException {
		try(BufferedReader reader = new BufferedReader(new FileReader("json_cache/diagram_wind_cached.json")))
        {
			return reader.readLine();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return "{}";
        } catch (IOException e2) {
			e2.printStackTrace();
			return "{}";
		}
	}
	
	@RequestMapping("/dataSingleSensor")
	public String getDataSingleSensor(@RequestParam int id, @RequestParam String tstamp) throws SQLException {
		Connection con = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
		JSONObject jo = new JSONObject();
		String tstampPM = tstamp.substring(0, 4) + "-" + tstamp.substring(4, 6) + "-" + tstamp.substring(6, 8) + "T"
				+ tstamp.substring(8, 10) + ":" + tstamp.substring(10, 11);

		String queryPM = "SELECT P1, P2 FROM PM_VALUES WHERE TIME_STAMP LIKE '" + tstampPM + "%' AND ID=" + id;
		Statement stmtPM = con.createStatement();
		ResultSet rsPM = stmtPM.executeQuery(queryPM);
		if (rsPM.next()) {
			jo.put("P1", rsPM.getFloat("P1"));
			jo.put("P2", rsPM.getFloat("P2"));
		} else {
			jo.put("P1", "Keine Daten vorhanden");
			jo.put("P2", "Keine Daten vorhanden");
		}

		String queryW = "SELECT SPEED, DEGREE FROM WIND WHERE TSTAMP LIKE '" + tstamp + "'";
		Statement stmtW = con.createStatement();
		ResultSet rsW = stmtW.executeQuery(queryW);
		rsW.next();
		jo.put("SPEED", rsW.getFloat("SPEED"));
		jo.put("DEGREE", rsW.getInt("DEGREE"));

		String queryP = "SELECT DURATION, MM FROM PRECIPITATION WHERE TSTAMP LIKE '" + tstamp + "'";
		Statement stmtP = con.createStatement();
		ResultSet rsP = stmtP.executeQuery(queryP);
		rsP.next();
		jo.put("MM", rsP.getFloat("MM"));
		jo.put("DURATION", rsP.getInt("DURATION"));

		con.close();
		return jo.toString();
	}

}
