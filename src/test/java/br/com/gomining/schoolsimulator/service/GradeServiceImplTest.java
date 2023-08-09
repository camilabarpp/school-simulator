package br.com.gomining.schoolsimulator.service;

import org.junit.jupiter.api.Test;
import br.com.gomining.schoolsimulator.model.entity.Grade;
import br.com.gomining.schoolsimulator.repository.GradeRepository;
import br.com.gomining.schoolsimulator.service.impl.GradeServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class GradeServiceImplTest {

    @InjectMocks
    private GradeServiceImpl gradeService;

    @Mock
    private GradeRepository gradeRepository;

    @Test
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
    void findAllGrades() {
        List<Grade> expected = List.of(new Grade("1", 0.0), new Grade("2", 0.0));

        when(gradeRepository.findAll()).thenReturn(expected);

        List<Grade> result = gradeService.getAllGrades();

        assertEquals(expected, result);
        assertEquals(2, result.size());
        verify(gradeRepository).findAll();
    }
}
