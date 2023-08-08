package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.Exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.repository.GradeRepository;
import br.com.gomining.schoolsimulator.service.GradeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return this.gradeRepository.findById(id).orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));
    }

    @Override
    public Grade createGrade(Grade grade) {
        return this.gradeRepository.save(grade);
    }

    @Override
    public Grade updateGrade(String id, Grade updatedGrade) {
        Grade existingGrade = gradeRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));

        existingGrade.setStudent(updatedGrade.getStudent());
        existingGrade.setActivity(updatedGrade.getActivity());
        existingGrade.setGradeValue(updatedGrade.getGradeValue());

        return gradeRepository.save(existingGrade);
    }

    @Override
    public void deleteGrade(String id) {
        Grade existingGrade = this.gradeRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Nota não encontrada com o ID: " + id));

        this.gradeRepository.delete(existingGrade);
    }

    @Override
    public double getAverageGradeForStudentInActivity(String studentId, String activityId) {
//        List<Grade> grades = this.gradeRepository.getAllGradesForStudentInActivity(studentId, activityId);
//        double sumGrades = grades.stream().mapToDouble(Grade::getGradeValue).sum();
//        return grades.isEmpty() ? 0 : sumGrades / grades.size();
        return 0;
    }

    @Override
    public double getAverageGradeForStudent(String studentId) {
//        List<Grade> grades = this.gradeRepository.getAllGradesForStudent(studentId);
//        double sumGrades = grades.stream().mapToDouble(Grade::getGradeValue).sum();
//        return grades.isEmpty() ? 0 : sumGrades / grades.size();
        return 0;
    }

    @Override
    public double getAverageGradeForActivity(String activityId) {
//        List<Grade> grades = this.gradeRepository.getAllGradesForActivity(activityId);
//        double sumGrades = grades.stream().mapToDouble(Grade::getGradeValue).sum();
//        return grades.isEmpty() ? 0 : sumGrades / grades.size();
        return 0;
    }

    @Override
    public List<Grade> getAllGradesForStudent(String studentId) {
//        return this.gradeRepository.getAllGradesForStudent(studentId);
        return null;
    }
}
