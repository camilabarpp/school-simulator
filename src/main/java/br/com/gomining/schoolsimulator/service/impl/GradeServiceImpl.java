package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.Exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.Activity;
import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.model.entity.Student;
import br.com.gomining.schoolsimulator.repository.GradeRepository;
import br.com.gomining.schoolsimulator.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private StudentServiceImpl studentServiceImpl;
    private ActivityServiceImpl activityServiceImpl;

    @Autowired
    public GradeServiceImpl(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }
    @Override
    public List<Grade> getAllGrades() {
        return this.gradeRepository.findAll();
    }

    @Override
    public Grade getGradeById(String id) {
        return this.gradeRepository.findById(id).orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));
    }

    @Override
    public Grade createGrade(Grade grade) {
        return this.gradeRepository.save(grade);
    }

    @Override
    public Grade updateGrade(String id, Grade updatedGrade) {
        return this.gradeRepository.findById(id).map(existingGrade -> {
            existingGrade.setGradeValue(updatedGrade.getGradeValue());
            return this.gradeRepository.save(existingGrade);
        }).orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));
    }

    @Override
    public void deleteGrade(String id) {
        Grade existingGrade = this.gradeRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));

        this.gradeRepository.delete(existingGrade);
    }

    @Override
    public void deleteAllGrades() {
        gradeRepository.deleteAll();
    }
}
