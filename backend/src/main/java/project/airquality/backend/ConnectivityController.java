package project.airquality.backend;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;


@CrossOrigin(origins = "*")
@RestController
public class ConnectivityController {
	
	@RequestMapping("/connectionTest")
	public String getWindData(){
	    	return "{connection : \"1\"}";
	}
	
}
