package br.com.gomining.schoolsimulator.repository;

import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
}
