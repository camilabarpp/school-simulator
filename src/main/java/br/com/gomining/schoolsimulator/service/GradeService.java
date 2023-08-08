package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.model.entity.Grade;

import java.util.List;

public interface GradeService {
    List<Grade> getAllGrades();
    Grade getGradeById(String id);
    Grade createGrade(Grade grade);
    Grade updateGrade(String id, Grade grade);
    void deleteGrade(String id);
    double getAverageGradeForStudentInActivity(String studentId, String activityId);
    double getAverageGradeForStudent(String studentId);
    double getAverageGradeForActivity(String activityId);
    List<Grade> getAllGradesForStudent(String studentId);
}
