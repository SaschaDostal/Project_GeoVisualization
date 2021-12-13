package project.airquality.backend;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import project.airquality.backend.repository.PMRepository;

@SpringBootApplication
public class BackendApplication {
	
	@Autowired
	private PMRepository pmrepo;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
	
	@PostConstruct
	private void postInit() {
		System.out.println(pmrepo.findAll());
	}

}
