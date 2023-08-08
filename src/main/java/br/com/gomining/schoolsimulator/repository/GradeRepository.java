package br.com.gomining.schoolsimulator.repository;

import br.com.gomining.schoolsimulator.model.entity.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends MongoRepository<Grade, String> {
//    List<Grade> getAllGradesForStudentInActivity(String studentId, String activityId);
//
//    List<Grade> getAllGradesForStudent(String studentId);
//
//    List<Grade> getAllGradesForActivity(String activityId);
}
