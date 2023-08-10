package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
import br.com.gomining.schoolsimulator.repository.GradeRepository;
import br.com.gomining.schoolsimulator.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.gomining.schoolsimulator.common.exception.util.ErrorMessage.GRADE_NOT_FOUND_WITH_ID;

@Service
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

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
        return this.gradeRepository.findById(id).orElseThrow(() -> new ApiNotFoundException(GRADE_NOT_FOUND_WITH_ID + id));
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
        }).orElseThrow(() -> new ApiNotFoundException(GRADE_NOT_FOUND_WITH_ID + id));
    }

    @Override
    public void deleteGrade(String id) {
        Grade existingGrade = this.gradeRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException(GRADE_NOT_FOUND_WITH_ID + id));

        this.gradeRepository.delete(existingGrade);
    }

    @Override
    public void deleteAllGrades() {
        gradeRepository.deleteAll();
    }
}
