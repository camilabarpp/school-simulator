package br.com.gomining.schoolsimulator.repository;

import br.com.gomining.schoolsimulator.model.entity.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends MongoRepository<Grade, String> {
}
