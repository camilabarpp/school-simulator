package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.common.exception.ActivityAlreadyAddedException;
import br.com.gomining.schoolsimulator.common.exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
import br.com.gomining.schoolsimulator.model.entity.student.Student;
import br.com.gomining.schoolsimulator.repository.StudentRepository;
import br.com.gomining.schoolsimulator.service.impl.ActivityServiceImpl;
import br.com.gomining.schoolsimulator.service.impl.GradeServiceImpl;
import br.com.gomining.schoolsimulator.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.gomining.schoolsimulator.common.exception.util.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class StudentServiceTest {

    @InjectMocks
    private StudentServiceImpl studentService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ActivityServiceImpl activityService;

    @Mock
    private GradeServiceImpl gradeService;

    @Test
    @DisplayName("Test getting all students")
    void getAllStudents() {
        List<Student> expectedStudents = listOfStudents;

        when(studentRepository.findAll()).thenReturn(expectedStudents);

        List<Student> result = studentService.getAllStudents();

        assertEquals(expectedStudents, result);
    }

    @Test
    @DisplayName("Test getting student by id")
    void getStudentById() {
        String id = "1";

        Student expectedStudent = listOfStudents.get(0);

        when(studentRepository.findById(expectedStudent.getId())).thenReturn(Optional.of(expectedStudent));

        Student result = studentService.getStudentById(id);

        assertEquals(expectedStudent, result);
    }

    @Test
    @DisplayName("Should throw exception when student is not found")
    void shouldThrowExceptionWhenStudentIsNotFound() {
        String id = "1";

        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ApiNotFoundException.class, () -> studentService.getStudentById(id));

        assertEquals(STUDENT_NOT_FOUND_BY_ID + id, exception.getMessage());
    }

    @Test
    @DisplayName("Test creating student")
    void createStudent() {
        Student expectedStudent = listOfStudents.get(0);

        when(studentRepository.save(expectedStudent)).thenReturn(expectedStudent);

        Student result = studentService.createStudent(expectedStudent);

        assertEquals(expectedStudent, result);
        assertNotNull(result.getRegistrationDate());
        assertEquals(result.getRegistrationDate(), LocalDate.now().toString());
    }

    @Test
    @DisplayName("Test updating student")
    void updateStudent() {
        String id = "1";

        Student expectedStudent = listOfStudents.get(0);

        when(studentRepository.findById(id)).thenReturn(Optional.of(expectedStudent));
        when(studentRepository.save(expectedStudent)).thenReturn(expectedStudent);

        Student result = studentService.updateStudent(id, expectedStudent);

        assertEquals(expectedStudent, result);
        assertNotNull(result.getLastUpdateDate());
        assertEquals(result.getLastUpdateDate(), LocalDate.now().toString());
    }

    @Test
    @DisplayName("Should throw exception when updating an invalid student")
    void shouldThrowExceptionWhenUpdatingAnInvalidStudent() {
        String id = "1";

        Student expectedStudent = listOfStudents.get(0);

        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ApiNotFoundException.class, () -> studentService.updateStudent(id, expectedStudent));

        assertEquals(STUDENT_NOT_FOUND_BY_ID + id, exception.getMessage());
    }

    @Test
    @DisplayName("Test deleting student")
    void deleteStudent() {
        String id = "1";

        Student expectedStudent = listOfStudents.get(0);

        when(studentRepository.save(expectedStudent)).thenReturn(expectedStudent);
        when(studentRepository.findById(id)).thenReturn(Optional.of(expectedStudent));

        studentService.deleteStudent(id);

        verify(studentRepository).delete(expectedStudent);
    }

    @Test
    @DisplayName("Should throw exception when deleting an invalid student")
    void shouldThrowExceptionWhenDeletingAnInvalidStudent() {
        String id = "1";

        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ApiNotFoundException.class, () -> studentService.deleteStudent(id));

        assertEquals(STUDENT_NOT_FOUND_BY_ID + id, exception.getMessage());
    }

    @Test
    @DisplayName("Test deleting all students")
    void deleteAllStudents() {
        studentService.deleteAllStudents();

        verify(studentRepository).deleteAll();
    }

    @Test
    @DisplayName("Test adding activity")
    void testAddActivity_Success() {
        // Arrange
        String studentId = "1";
        String activityId = "A1";
        Student student = listOfStudents.get(0);
        student.setRegistrationDate(LocalDate.now().toString());
        Activity actualActivity = activity;
        actualActivity.setRegistrationDate(LocalDate.now().toString());

        when(activityService.getActivityById(activityId)).thenReturn(actualActivity);
        when(studentRepository.save(student)).thenReturn(student);

        // Configurar o comportamento esperado para o getStudentById
//        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // Act
        Student result = studentService.addActivity(studentId, activityId);

        // Assert
        assertTrue(result.getActivities().contains(actualActivity));
    }

    @Test
    @DisplayName("Test adding activity to non-existent student")
    void testAddActivity_StudentNotFound() {
        String studentId = "1";
        String activityId = "A1";
        Student student = listOfStudents.get(0);
        student.setRegistrationDate(LocalDate.now().toString());
        Activity actualActivity = activity;
        actualActivity.setRegistrationDate(LocalDate.now().toString());

        when(activityService.getActivityById(activityId)).thenReturn(actualActivity);

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        var exception = assertThrows(ApiNotFoundException.class, () -> studentService.addActivity(studentId, activityId));

        assertEquals(STUDENT_NOT_FOUND_BY_ID + studentId, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when adding activity already added")
    void shouldThrowExceptionWhenAddingActivityAlreadyAdded() {
        String studentId = "1";
        String activityId = "A1";
        Student student = listOfStudents.get(0);
        student.setRegistrationDate(LocalDate.now().toString());
        student.setActivities(getActivities());
        Activity actualActivity = activity;
        actualActivity.setRegistrationDate(LocalDate.now().toString());

        when(activityService.getActivityById(activityId)).thenReturn(actualActivity);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        var exception = assertThrows(ActivityAlreadyAddedException.class, () -> studentService.addActivity(studentId, activityId));

        assertEquals(ACTIVITY_ALREADY_ADDED, exception.getMessage());
    }

    @Test
    @DisplayName("Test adding grade to activity")
    void testAddGrade_Success() {
        // Arrange
        String studentId = "1";
        String activityId = "1";
        Grade gradeToAdd = listOfGrades.get(0);

        Student student = listOfStudents.get(0);
        student.setActivities(new ArrayList<>());

        Activity actualActivity = activity;
        actualActivity.setGrade(new ArrayList<>());

        student.getActivities().add(actualActivity);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(gradeService.createGrade(gradeToAdd)).thenReturn(gradeToAdd);

        // Act
        Student result = studentService.addGrade(studentId, activityId, gradeToAdd);

        // Assert
        assertTrue(result.getActivities().get(0).getGrade().contains(gradeToAdd));
    }

    @Test
    @DisplayName("Test adding grade to non-existent activity")
    void testAddGrade_ActivityNotFound() {
        String studentId = "1";
        String activityId = "A1";
        Grade gradeToAdd = listOfGrades.get(0);

        Student student = listOfStudents.get(0);
        student.setActivities(new ArrayList<>());

        Activity actualActivity = activity;
        actualActivity.setGrade(new ArrayList<>());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(gradeService.createGrade(gradeToAdd)).thenReturn(gradeToAdd);

        var exception = assertThrows(ApiNotFoundException.class,
                () -> studentService.addGrade(studentId, activityId, gradeToAdd));

        assertEquals(ACTIVITY_NOT_FUND_WITH_ID + activityId, exception.getMessage());
    }

    @Test
    @DisplayName("Test calculating student average based on all activities")
    void testCalculateStudentAverageBasedOnAllActivities() {
        String studentId = "1";

        Student student = new Student();
        student.setId(studentId);
        List<Activity> activities = new ArrayList<>();
        Activity activity1 = new Activity();
        activity1.setId("A1");
        activities.add(activity1);
        student.setActivities(activities);

        Grade grade1 = listOfGrades.get(0);
        grade1.setGradeValue(9.5);
        List<Grade> grades = new ArrayList<>();
        grades.add(grade1);
        activity1.setGrade(grades);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        Double average = studentService.calculateStudentAverageBasedOnAllActivity(studentId);

        double expectedAverage = grade1.getGradeValue();
        assertEquals(expectedAverage, average, 0.001);
    }

    @Test
    @DisplayName("Test calculating student average based on all activities")
    void testCalculateActivityAverage() {
        // Arrange
        String studentId = "1";
        Student student = new Student();
        student.setId(studentId);
        List<Activity> activities = new ArrayList<>();
        Activity activity1 = new Activity();
        List<Grade> grades1 = new ArrayList<>();
        Grade grade1 = new Grade();
        grade1.setGradeValue(9.5);
        grades1.add(grade1);
        activity1.setGrade(grades1);
        activities.add(activity1);
        Activity activity2 = new Activity();
        List<Grade> grades2 = new ArrayList<>();
        Grade grade2 = new Grade();
        grade2.setGradeValue(8.0);
        grades2.add(grade2);
        activity2.setGrade(grades2);
        activities.add(activity2);
        student.setActivities(activities);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // Act
        double average = studentService.calculateStudentAverageBasedOnAllActivity(studentId);

        // Assert
        double expectedAverage = (grade1.getGradeValue() + grade2.getGradeValue()) / 2;
        assertEquals(expectedAverage, average, 0.001);
    }

    @Test
    @DisplayName("Test calculating activity average with null or empty grades")
    void testCalculateActivityAverage_NullOrEmptyGrades() {
        String studentId = "1";
        Student student = new Student();
        student.setId(studentId);

        Activity activity = this.activity;

        List<Activity> activities = new ArrayList<>();

        activities.add(activity);

        student.setActivities(activities);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        double average = studentService.calculateStudentAverageBasedOnAllActivity(studentId);

        assertEquals(0.0, average);
    }

    @Test
    @DisplayName("Should throws exception when student dont have activities")
    void shouldThrowsExceptionWhenStudentDontHaveActivities() {
        String studentId = "1";
        Student student = new Student();
        student.setId(studentId);
        student.setActivities(new ArrayList<>());

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        var exception = assertThrows(ApiNotFoundException.class, () -> studentService.calculateStudentAverageBasedOnAllActivity(studentId));

        assertEquals(STUDENT_WITHOUT_ACTIVITIES, exception.getMessage());
    }

    @Test
    @DisplayName("Test calculating student average in activity")
    void testCalculateStudentAverageInActivity() {
        // Arrange
        String studentId = "1";
        String activityId = "1";
        Student student = listOfStudents.get(0);
        Activity activity = this.activity;

        List<Activity> activities = new ArrayList<>();
        activities.add(activity);

        student.setActivities(activities);
        activity.setGrade(listOfGrades);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(activityService.getActivityById(activityId)).thenReturn(activity);

        // Act
        double average = studentService.calculateStudentAverageInActivity(studentId, activityId);

        // Assert
        assertEquals(85.0, average, 0.001);
    }

    @Test
    @DisplayName("Test calculating student average in activity with no student grades")
    void testCalculateStudentAverageInActivity_NoStudentGrades() {
        // Arrange
        String studentId = "1";
        String activityId = "A1";
        Student student = new Student();
        student.setId(studentId);

        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setGrade(null);


        List<Activity> activities = new ArrayList<>();
        Activity studentActivity = new Activity();
        studentActivity.setId(activityId);
        activities.add(studentActivity);

        student.setActivities(activities);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(activityService.getActivityById(activityId)).thenReturn(activity);

        var exception = assertThrows(ApiNotFoundException.class,
                () -> studentService.calculateStudentAverageInActivity(studentId, activityId));

        assertEquals(STUDENT_WITHOUT_GRADES_IN_ACTIVITY, exception.getMessage());
    }

    @Test
    @DisplayName("Test calculating overall average for activity")
    void calculateOverallAverageForActivity() {
        String activityId = "1";
        Activity activity = this.activity;
        List<Student> students = listOfStudents;
        students.get(0).setActivities(getActivities());
        students.get(1).setActivities(getActivities());
        activity.setGrade(listOfGrades);

        when(studentService.getAllStudents()).thenReturn(listOfStudents);
        when(activityService.getActivityById(activityId)).thenReturn(activity);

        double average = studentService.calculateOverallAverageForActivity(activityId);

        assertEquals(85.0, average, 0.001);
    }

    @Test
    @DisplayName("Should throws exception when try to calculate overall average for activity with no user")
    void shouldThrowsExceptionWhenTryToCalculateOverallAverageForActivityWithNoUser() {
        String activityId = "1";
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setGrade(null);

        when(activityService.getActivityById(activityId)).thenReturn(activity);

        var exception = assertThrows(ApiNotFoundException.class, () -> studentService.calculateOverallAverageForActivity(activityId));

        assertEquals(NO_REGISTERED_STUDENTS, exception.getMessage());
    }

    @Test
    @DisplayName("Test calculating student activity average with no grades")
    void testCalculateStudentActivityAverage_NoGrades() {
        String activityId = "A1";
        Activity activity = new Activity();
        activity.setId(activityId);

        List<Student> students = listOfStudents;
        students.get(0).setActivities(getActivities());
        students.get(1).setActivities(getActivities());
        activity.setGrade(listOfGrades);

        when(studentService.getAllStudents()).thenReturn(students);
        when(activityService.getActivityById(activityId)).thenReturn(activity);

        // Act
        Double result = studentService.calculateOverallAverageForActivity(activityId);

        // Assert
        assertEquals(0.0, result, 0.0001);
    }


    @Test
    @DisplayName("Test getting student by CPF, email, or phone - student found by CPF")
    void testGetStudentByCpfEmailOrPhone_StudentFoundByCpf() {
        // Arrange
        String identifier = "12345678900"; // CPF of an existing student
        Student expectedStudent = new Student();
        expectedStudent.setId("1");
        when(studentRepository.findByCpfOrEmailOrTelephone(identifier, identifier, identifier)).thenReturn(expectedStudent);

        // Act
        Student result = studentService.getStudentByCpfEmailOrPhone(identifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedStudent.getId(), result.getId());
    }

    @Test
    @DisplayName("Test getting student by CPF, email, or phone - student found by email")
    void testGetStudentByCpfEmailOrPhone_StudentFoundByEmail() {
        // Arrange
        String identifier = "test@example.com"; // Email of an existing student
        Student expectedStudent = new Student();
        expectedStudent.setId("2");
        when(studentRepository.findByCpfOrEmailOrTelephone(identifier, identifier, identifier)).thenReturn(expectedStudent);

        // Act
        Student result = studentService.getStudentByCpfEmailOrPhone(identifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedStudent.getId(), result.getId());
    }

    @Test
    @DisplayName("Test getting student by CPF, email, or phone - student found by phone")
    void testGetStudentByCpfEmailOrPhone_StudentFoundByPhone() {
        // Arrange
        String identifier = "5551234567"; // Phone of an existing student
        Student expectedStudent = new Student();
        expectedStudent.setId("3");
        when(studentRepository.findByCpfOrEmailOrTelephone(identifier, identifier, identifier)).thenReturn(expectedStudent);

        // Act
        Student result = studentService.getStudentByCpfEmailOrPhone(identifier);

        // Assert
        assertNotNull(result);
        assertEquals(expectedStudent.getId(), result.getId());
    }

    @Test
    @DisplayName("Test getting student by CPF, email, or phone - student not found")
    void testGetStudentByCpfEmailOrPhone_StudentNotFound() {
        String identifier = "nonexistent"; // Identifier that does not match any student
        when(studentRepository.findByCpfOrEmailOrTelephone(identifier, identifier, identifier)).thenReturn(null);

        var exception = assertThrows(ApiNotFoundException.class, () -> studentService.getStudentByCpfEmailOrPhone(identifier));
        assertEquals(STUDENT_NOT_FOUND_BY_CPF_EMAIL_PHONE + identifier, exception.getMessage());
    }

    @Test
    @DisplayName("Test getting student grades by CPF, email, or phone - student has grades")
    void testGetStudentGradesByCpfEmailOrPhone_StudentHasGrades() {
        String identifier = "12345678900";
        Student student = new Student();
        List<Activity> activities = new ArrayList<>();
        Activity activity1 = new Activity();
        activity1.setId("1");
        List<Grade> grades1 = listOfGrades;
        activity1.setGrade(grades1);
        activities.add(activity1);
        student.setActivities(activities);

        when(studentRepository.findByCpfOrEmailOrTelephone(identifier, identifier, identifier)).thenReturn(student);

        List<Grade> result = studentService.getStudentGradesByCpfEmailOrPhone(identifier);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test getting student grades by CPF, email, or phone - student has no grades")
    void testGetStudentGradesByCpfEmailOrPhone_StudentHasNoGrades() {
        String identifier = "test@example.com"; // Email of an existing student with no grades
        Student student = new Student();
        List<Activity> activities = new ArrayList<>();
        student.setActivities(activities);

        when(studentRepository.findByCpfOrEmailOrTelephone(identifier, identifier, identifier)).thenReturn(student);

        List<Grade> result = studentService.getStudentGradesByCpfEmailOrPhone(identifier);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test getting student grades by CPF, email, or phone - student not found")
    void testGetStudentGradesByCpfEmailOrPhone_StudentNotFound() {
        String identifier = "nonexistent";
        when(studentRepository.findByCpfOrEmailOrTelephone(identifier, identifier, identifier)).thenReturn(null);

        var exception = assertThrows(ApiNotFoundException.class, () -> studentService.getStudentGradesByCpfEmailOrPhone(identifier));

        assertEquals(STUDENT_NOT_FOUND_BY_CPF_EMAIL_PHONE + identifier, exception.getMessage());
    }

    private List<Activity> getActivities() {
        List<Activity> activities = new ArrayList<>();
        activities.add(this.activity);
        return activities;
    }

    List<Student> listOfStudents = List.of(
            new Student("1", "João", "12345678901", "joao@mail.com", "123456789", getActivities()),
            new Student("2", "Maria", "12345678902", "maria@mail.com", "123456780")
    );

    Activity activity = new Activity("1", "Activity 1", "Description 1", this.listOfGrades);

    List<Grade> listOfGrades = List.of(
            new Grade("1", 90.0),
            new Grade("2", 80.0)
    );
}