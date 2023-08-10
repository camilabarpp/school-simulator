package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.common.exception.ApiNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
import br.com.gomining.schoolsimulator.repository.GradeRepository;
import br.com.gomining.schoolsimulator.service.impl.GradeServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GradeServiceImplTest {

    @InjectMocks
    private GradeServiceImpl gradeService;

    @Mock
    private GradeRepository gradeRepository;

    @Test
    @DisplayName("Test if the grade is found by id")
    void testGetGradeById_ExistingId_ReturnsGrade() {
        // Arrange
        String existingId = "existingId";
        Grade grade = new Grade(existingId, 0.0);

        when(gradeRepository.findById(existingId)).thenReturn(Optional.of(grade));

        // Act
        Grade result = gradeService.getGradeById(existingId);

        // Assert
        assertEquals(grade, result);
        verify(gradeRepository).findById(existingId);
    }

    @Test
    @DisplayName("Test if the grade is not found by id")
    void findAllGrades() {
        List<Grade> expected = List.of(new Grade("1", 0.0), new Grade("2", 0.0));

        when(gradeRepository.findAll()).thenReturn(expected);

        List<Grade> result = gradeService.getAllGrades();

        assertEquals(expected, result);
        assertEquals(2, result.size());
        verify(gradeRepository).findAll();
    }

    @Test
    @DisplayName("Test if the grade is saved")
    void testSaveGrade() {
        // Arrange
        Grade grade = new Grade("1", 0.0);

        when(gradeRepository.save(grade)).thenReturn(grade);

        // Act
        Grade result = gradeService.createGrade(grade);

        // Assert
        assertEquals(grade, result);
        verify(gradeRepository).save(grade);
    }

    @Test
    @DisplayName("Test if the grade is updated")
    void testUpdateGrade() {
        // Arrange
        String gradeId = "1";
        Grade existingGrade = new Grade(gradeId, 0.0);
        Grade updatedGrade = new Grade(gradeId, 10.0);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existingGrade));
        when(gradeRepository.save(updatedGrade)).thenReturn(updatedGrade);

        // Act
        Grade result = gradeService.updateGrade(gradeId, updatedGrade);

        // Assert
        assertEquals(updatedGrade, result);
        verify(gradeRepository).save(updatedGrade);
    }

    @Test
    @DisplayName("Test deleting an existing grade")
    void testDeleteExistingGrade() {
        // Arrange
        String gradeId = "1";
        Grade existingGrade = new Grade(gradeId, 10.0);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existingGrade));

        // Act
        gradeService.deleteGrade(gradeId);

        // Assert
        verify(gradeRepository).delete(existingGrade);
    }

    @Test
    @DisplayName("Test deleting a non-existent grade")
    void testDeleteNonExistentGrade() {
        // Arrange
        String gradeId = "1";

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ApiNotFoundException.class, () -> gradeService.deleteGrade(gradeId));
    }

    @Test
    @DisplayName("Test deleting all grades")
    void testDeleteAllGrades() {
        // Act
        gradeService.deleteAllGrades();

        // Assert
        verify(gradeRepository).deleteAll();
    }
}
