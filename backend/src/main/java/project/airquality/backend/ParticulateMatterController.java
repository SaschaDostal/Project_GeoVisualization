package project.airquality.backend;

import org.springframework.web.bind.annotation.RestController;

import project.airquality.backend.repository.PMRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ParticulateMatterController {
	
	@Autowired
	private PMRepository pmrepo;
	
	@RequestMapping("/")
	public String getTest(@RequestParam int id) {
		return "ID: " + id + ", lon: " + pmrepo.findById((long) id);
	}
}
