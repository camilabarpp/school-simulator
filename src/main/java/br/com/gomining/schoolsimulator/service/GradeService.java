package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.model.entity.grade.Grade;

import java.util.List;

public interface GradeService {
    List<Grade> getAllGrades();
    Grade getGradeById(String id);
    Grade createGrade(Grade grade);
    Grade updateGrade(String id, Grade grade);
    void deleteGrade(String id);
    void deleteAllGrades();
}
