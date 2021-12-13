package project.airquality.backend.repository;

import org.springframework.data.repository.CrudRepository;

import project.airquality.backend.entity.PMStations;

public interface PMRepository extends CrudRepository<PMStations, Long>{

}
