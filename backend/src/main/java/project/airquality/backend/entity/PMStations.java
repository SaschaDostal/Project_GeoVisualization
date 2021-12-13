package project.airquality.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PM_SENSORS")
public class PMStations {
	@Id
	private long id;
	private float lon;
	private float lat;
	
	@Override
	public String toString() {
		return "id: " + id + ", lon: " + lon + ", lat: " +lat;
	}
}
