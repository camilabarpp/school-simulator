package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
import br.com.gomining.schoolsimulator.model.entity.student.Student;
import br.com.gomining.schoolsimulator.model.mapper.StudentMapper;
import br.com.gomining.schoolsimulator.model.request.StudentRequest;
import br.com.gomining.schoolsimulator.model.response.StudentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.gomining.schoolsimulator.model.mapper.StudentMapper.toResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.2.8")
class StudentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mongoTemplate.remove(new Query(), Student.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return a list of students")
    void shouldReturnAListOfStudents() throws Exception {
        List<Student> students = this.students;

        mongoTemplate.insertAll(students);

        MvcResult result = mvc.perform(get("/students")
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        Student[] studentsResponse = mapper.readValue(json, Student[].class);

        List<Student> studentsInDatabase = mongoTemplate.findAll(Student.class);

        assertThat(studentsResponse).containsExactlyInAnyOrderElementsOf(studentsInDatabase);
    }

    @Test
    @DisplayName("Should return a single student")
    void shouldReturnASingleStudent() throws Exception {
        Student student = this.student;
        student.setId("65f1zFA2E6kM64d6b322143fe");
        String id = student.getId();

        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(get("/students/{id}", id)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        Student studentResponse = mapper.readValue(json, Student.class);

        assertEquals(student, studentResponse);

        Student studentInDatabase = mongoTemplate.findById(id, Student.class);

        assertEquals(student, studentInDatabase);

        assertEquals(json, result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Should throw an ApiNotFoundException when the student is not found")
    void shouldThrowAnExceptionWhenTheStudentIsNotFound() throws Exception {
        String id = "65f1zFA2E6kM64d6b322143fe";

        MvcResult result = mvc.perform(get("/students/{id}", id)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Estudante nÃ£o encontrado com o ID: " + id));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should create a student when user is admin")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateAStudentWhenUserIsAdmin() throws Exception {
        StudentRequest studentRequest = StudentMapper.toRequest(this.student);

        MvcResult result = mvc.perform(post("/students")
                .contentType("application/json")
                .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        StudentResponse studentResponse = mapper.readValue(json, StudentResponse.class);

        Student studentInDatabase = mongoTemplate.findById(studentResponse.getId(), Student.class);

        assertNotNull(studentInDatabase);
        assertThat(studentResponse.getId()).isEqualTo(studentInDatabase.getId());
    }

    @DisplayName("Should create a student when user is teacher")
    @WithMockUser(roles = "TEACHER")
    void shouldCreateAStudentWhenUserIsATeacher() throws Exception {
        StudentRequest studentRequest = StudentMapper.toRequest(this.student);

        MvcResult result = mvc.perform(post("/students")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        StudentResponse studentResponse = mapper.readValue(json, StudentResponse.class);

        Student studentInDatabase = mongoTemplate.findById(studentResponse.getId(), Student.class);

        assertNotNull(studentInDatabase);
        assertThat(studentResponse.getId()).isEqualTo(studentInDatabase.getId());
    }

    @Test
    @DisplayName("Should throw MethodArgumentNotValidException when username is null or blank")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionWhenUsernameIsNullOrBlank() throws Exception {
        studentWithInvalidData.setCpf("842.327.210-96");
        studentWithInvalidData.setEmail("student@mail.com");
        studentWithInvalidData.setTelephone("123456789");

        StudentRequest studentRequest = StudentMapper.toRequest(studentWithInvalidData);

        MvcResult result = mvc.perform(post("/students")
                .contentType("application/json")
                .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("fullName is mandatory"));
        assertTrue(json.contains("BAD_REQUEST"));
        assertTrue(json.contains("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("Should throw an ApiForbiddenException when user is not admin")
    @WithMockUser(roles = "STUDENT")
    void shouldThrowAnExceptionWhenUserIsNotAdmin() throws Exception {
        StudentRequest studentRequest = StudentMapper.toRequest(this.student);

        MvcResult result = mvc.perform(post("/students")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Access is denied"));
        assertTrue(json.contains("FORBIDDEN"));
        assertTrue(json.contains("AccessDeniedException"));
    }

    @Test
    @DisplayName("Should throw MethodArgumentNotValidException when email is null or blank")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionWhenEmailIsNullOrBlank() throws Exception {
        studentWithInvalidData.setFullName("Student 1");
        studentWithInvalidData.setCpf("842.327.210-96");
        studentWithInvalidData.setTelephone("123456789");

        StudentRequest studentRequest = StudentMapper.toRequest(studentWithInvalidData);

        MvcResult result = mvc.perform(post("/students")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("email is mandatory"));
        assertTrue(json.contains("BAD_REQUEST"));
        assertTrue(json.contains("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("Should throw MethodArgumentNotValidException when cpf is null or blank")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionWhenCpfIsNullOrBlank() throws Exception {
        studentWithInvalidData.setFullName("Student 1");
        studentWithInvalidData.setEmail("student@mail.com");
        studentWithInvalidData.setTelephone("123456789");

        StudentRequest studentRequest = StudentMapper.toRequest(studentWithInvalidData);

        MvcResult result = mvc.perform(post("/students")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("cpf is mandatory"));
        assertTrue(json.contains("BAD_REQUEST"));
        assertTrue(json.contains("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("Should throw MethodArgumentNotValidException when telephone is null or blank")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionWhenTelephoneIsNullOrBlank() throws Exception {
        studentWithInvalidData.setFullName("Student 1");
        studentWithInvalidData.setCpf("842.327.210-96");
        studentWithInvalidData.setEmail("student@mail.com");

        StudentRequest studentRequest = StudentMapper.toRequest(studentWithInvalidData);

        MvcResult result = mvc.perform(post("/students")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("telephone is mandatory"));
        assertTrue(json.contains("BAD_REQUEST"));
        assertTrue(json.contains("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("Should throw exception when email is invalid")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionWhenEmailIsInvalid() throws Exception {
        studentWithInvalidData.setFullName("Student 1");
        studentWithInvalidData.setCpf("842.327.210-96");
        studentWithInvalidData.setEmail("studentmail.com");
        studentWithInvalidData.setTelephone("123456789");

        StudentRequest studentRequest = StudentMapper.toRequest(studentWithInvalidData);

        MvcResult result = mvc.perform(post("/students")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("invalid e-mail"));
        assertTrue(json.contains("BAD_REQUEST"));
        assertTrue(json.contains("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("Should throw exception when cpf is invalid")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionWhenCpfIsInvalid() throws Exception {
        studentWithInvalidData.setFullName("Student 1");
        studentWithInvalidData.setCpf("123456789");
        studentWithInvalidData.setEmail("student@mail.com");
        studentWithInvalidData.setTelephone("123456789");

        StudentRequest studentRequest = StudentMapper.toRequest(studentWithInvalidData);

        MvcResult result = mvc.perform(post("/students")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("invalid CPF"));
        assertTrue(json.contains("BAD_REQUEST"));
        assertTrue(json.contains("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("Should update student if user is an admin")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateStudentWithSuccess() throws Exception {
        Student student = this.student;
        student.setId("nYcBS3hIPLFZ64d698b67e30f");
        String id = student.getId();

        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(student)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        StudentResponse studentResponse = mapper.readValue(json, StudentResponse.class);

        Student studentInDatabase = mongoTemplate.findById(studentResponse.getId(), Student.class);

        assertNotNull(studentInDatabase);
        assertNotNull(studentInDatabase.getLastUpdateDate());
        assertEquals(studentResponse, toResponse(studentInDatabase));
    }

    @Test
    @DisplayName("Should update a student if user is teacher")
    @WithMockUser(roles = "TEACHER")
    void shouldUpdateStudentIfUserIsTeacher() throws Exception {
        Student student = this.student;
        student.setId("CCnTjk8bpaCI64d6b7bc718cc");
        String id = student.getId();

        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(student)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        StudentResponse studentResponse = mapper.readValue(json, StudentResponse.class);

        Student studentInDatabase = mongoTemplate.findById(studentResponse.getId(), Student.class);

        assertNotNull(studentInDatabase);
        assertNotNull(studentInDatabase.getLastUpdateDate());
        assertEquals(studentResponse, toResponse(studentInDatabase));
    }

    @Test
    @DisplayName("Should deny access when try to update if user is not an admin or teacher")
    @WithMockUser(roles = "STUDENT")
    void shouldDenyAccessWhenUserIsNotAdminOrTeacher() throws Exception {
        Student student = this.student;
        student.setId("OAcYfXyWPT4064d6dfb475bb5");
        String id = student.getId();

        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(student)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Access is denied"));
        assertTrue(json.contains("FORBIDDEN"));
        assertTrue(json.contains("AccessDeniedException"));
    }

    @Test
    @DisplayName("Should throw exception when try to update a student not found")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionWhenTryToUpdateAStudentNotFound() throws Exception {
        Student student = this.student;
        student.setId("4a9i6O7pGxXB64d6dd7b01d5d");
        String id = student.getId();

        MvcResult result = mvc.perform(put("/students/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(student)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Estudante nÃ£o encontrado com o ID: " + id));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should delete a student if user is an admin")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteStudentWithSuccessIfUserIsAnAdmin() throws Exception {
        Student student = this.student;
        student.setId("enYOOCuTZECt64d6e0abb1241");
        String id = student.getId();

        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(delete("/students/{id}", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        Student studentInDatabase = mongoTemplate.findById(id, Student.class);

        assertNull(studentInDatabase);
    }

    @Test
    @DisplayName("Should deny access when try to delete a student if user is a teacher")
    @WithMockUser(roles = "TEACHER")
    void shouldDenyAccessToDeleteWhenUserIsATeacher() throws Exception {
        Student student = this.student;
        student.setId("WXVgQnMeudaP64d6e279b7357");
        String id = student.getId();

        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(delete("/students/{id}", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Access is denied"));
        assertTrue(json.contains("FORBIDDEN"));
        assertTrue(json.contains("AccessDeniedException"));
    }

    @Test
    @DisplayName("Should deny access when try to delete a student if user is a student")
    @WithMockUser(roles = "STUDENT")
    void shouldDenyAccessToDeleteWhenUserIsAStudent() throws Exception {
        Student student = this.student;
        student.setId("KFDOW32vluBZ64d6e27fd9946");
        String id = student.getId();

        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(delete("/students/{id}", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Access is denied"));
        assertTrue(json.contains("FORBIDDEN"));
        assertTrue(json.contains("AccessDeniedException"));
    }

    @Test
    @DisplayName("Should throw exception when try to delete a student not found")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowExceptionWhenTryToDeleteAStudentNotFound() throws Exception {
        Student student = this.student;
        student.setId("fLdiwMnj2xQc64d6e2bc44c7e");
        String id = student.getId();

        MvcResult result = mvc.perform(delete("/students/{id}", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Estudante nÃ£o encontrado com o ID: " + id));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should delete all students if user is an admin")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteAllStudentsWithSuccessIfUserIsAnAdmin() throws Exception {
        mongoTemplate.insertAll(students);

        MvcResult result = mvc.perform(delete("/students")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        List<Student> studentsInDatabase = mongoTemplate.findAll(Student.class);

        assertTrue(studentsInDatabase.isEmpty());
    }

    @Test
    @DisplayName("Should deny access when try to delete all students if user is a teacher")
    @WithMockUser(roles = "TEACHER")
    void shouldDenyAccessToDeleteAllWhenUserIsATeacher() throws Exception {
        mongoTemplate.insertAll(students);

        MvcResult result = mvc.perform(delete("/students")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Access is denied"));
        assertTrue(json.contains("FORBIDDEN"));
        assertTrue(json.contains("AccessDeniedException"));
    }

    @Test
    @DisplayName("Should deny access when try to delete all students if user is a student")
    @WithMockUser(roles = "STUDENT")
    void shouldDenyAccessToDeleteAllWhenUserIsAStudent() throws Exception {
        mongoTemplate.insertAll(students);

        MvcResult result = mvc.perform(delete("/students")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Access is denied"));
        assertTrue(json.contains("FORBIDDEN"));
        assertTrue(json.contains("AccessDeniedException"));
    }

    @Test
    @DisplayName("Should add an activity to a student if user is an admin")
    @WithMockUser(roles = "ADMIN")
    void shouldAddActivityToStudentWithSuccessIfUserIsAnAdmin() throws Exception {
        Student student = this.student;
        student.setId("4XxwlLpgOlfe64d6e401007c5");
        student.setActivities(new ArrayList<>());
        String id = student.getId();
        String activityId = "GYeWhlj6IQfp64d6ef01dca0b";

        mongoTemplate.insert(student);

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        mongoTemplate.insert(activity);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}", id, activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activity)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Student studentInDatabase = mongoTemplate.findById(id, Student.class);

        assertNotNull(studentInDatabase);
        assertEquals(1, studentInDatabase.getActivities().size());
        assertEquals(activity.getTitle(), studentInDatabase.getActivities().get(0).getTitle());
        assertEquals(activity.getQuestionStatement(), studentInDatabase.getActivities().get(0).getQuestionStatement());
    }

    @Test
    @DisplayName("Should add an activity to a student if user is a teacher")
    @WithMockUser(roles = "TEACHER")
    void shouldAddActivityToStudentWithSuccessIfUserIsATeacher() throws Exception {
        Student student = this.student;
        student.setId("4PevquveFpbB64d6ee0012858");
        student.setActivities(new ArrayList<>());
        String id = student.getId();
        String activityId = "TQ9HiFlQNnJu64d6ee0589030";

        mongoTemplate.insert(student);

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        mongoTemplate.insert(activity);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}", id, activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activity)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Student studentInDatabase = mongoTemplate.findById(id, Student.class);

        assertNotNull(studentInDatabase);
        assertEquals(1, studentInDatabase.getActivities().size());
        assertEquals(activity.getTitle(), studentInDatabase.getActivities().get(0).getTitle());
        assertEquals(activity.getQuestionStatement(), studentInDatabase.getActivities().get(0).getQuestionStatement());
    }

    @Test
    @DisplayName("Should deny access when try to add an activity to a student if user is a student")
    @WithMockUser(roles = "STUDENT")
    void shouldDenyAccessWhenToTryToAddAnActivityToAStudentIfUserIsAStudent() throws Exception {
        Student student = this.student;
        student.setId("dTAUurUvqmrx64d6eef15e65e");
        student.setActivities(new ArrayList<>());
        String id = student.getId();
        String activityId = "9f5pgzswmVZZ64d6ef11422b8";

        mongoTemplate.insert(student);

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        mongoTemplate.insert(activity);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}", id, activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activity)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Access is denied"));
        assertTrue(json.contains("FORBIDDEN"));
        assertTrue(json.contains("AccessDeniedException"));
    }

    @Test
    @DisplayName("Should throw ApiNotFoundException when trying add an activity not found to a student")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowsApiNotFoundException_WhenTryingToAppAActivityNotFound() throws Exception {
        Student student = this.student;
        student.setId("HMnYIvuy2nCw64d6eee40a626");
        String id = student.getId();
        String activityId = "v6oxFbcj9YCN64d6ef1fd7975";

        mongoTemplate.insert(student);

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}", id, activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activity)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Atividade nÃ£o encontrada com o ID:"));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should throw ApiNotFoundException when trying add an activity already added to a student")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowsApiNotFoundExceptionWhenTryingToAddAnActivityAlreadyAddedToAStudent() throws Exception {
        Student student = this.student;
        student.setId("eC9xpH61bKUm64d6ec0cee075");
        String id = student.getId();
        String activityId = "k5rrlw4YmevV64d6ec1f510de";

        Activity activity = Activity.builder()
                .id("k5rrlw4YmevV64d6ec1f510de")
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        student.setActivities(List.of(activity));
        mongoTemplate.save(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}", id, activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activity)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Atividade jÃ¡ adicionada ao estudante"));
        assertTrue(json.contains("BAD_REQUEST"));
        assertTrue(json.contains("ActivityAlreadyAddedException"));
    }

    @Test
    @DisplayName("Should add a grade to a student if user is a teacher")
    @WithMockUser(roles = "TEACHER")
    void shouldAddAGradeToAStudentIfUserIsATeacher() throws Exception {
        Student student = this.student;
        student.setId("00029yae1cFF64d7032cf2e42");

        String id = student.getId();
        String activityId = "s2X6yohGNV8l64d6f011822f4";
        String gradeId = "WDuNzFsWzoAA64d703389785c";


        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        Grade grade = Grade.builder()
                .id(gradeId)
                .gradeValue(10.0)
                .build();

        activity.setGrade(List.of());
        student.setActivities(List.of(activity));

        mongoTemplate.insert(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}/grade", id, activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(grade)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        Student returnedStudent = mapper.readValue(json, Student.class);

        assertThat(returnedStudent.getActivities().get(0).getGrade().get(0).getGradeValue()).isEqualTo(grade.getGradeValue());

        Student studentInDatabase = mongoTemplate.findById(id, Student.class);

        assertNotNull(studentInDatabase);
        assertEquals(1, studentInDatabase.getActivities().get(0).getGrade().size());
        assertEquals(grade.getGradeValue(), studentInDatabase.getActivities().get(0).getGrade().get(0).getGradeValue());
    }

    @Test
    @DisplayName("Should add a grade to a student if user is an admin")
    @WithMockUser(roles = "ADMIN")
    void shouldAddAGradeToAStudentIfUserIsAnAdmin() throws Exception {
        Student student = this.student;
        student.setId("qknyows7aURx64d6f436c22a9");

        String id = student.getId();
        String activityId = "mUmAZky0q5V064d6f44f5fd83";

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        Grade grade = Grade.builder()
                .gradeValue(10.0)
                .build();

        activity.setGrade(List.of());
        student.setActivities(List.of(activity));

        mongoTemplate.insert(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}/grade", id, activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(grade)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        Student returnedStudent = mapper.readValue(json, Student.class);

        assertThat(returnedStudent.getActivities().get(0).getGrade().get(0).getGradeValue()).isEqualTo(grade.getGradeValue());

        Student studentInDatabase = mongoTemplate.findById(id, Student.class);

        assertNotNull(studentInDatabase);
        assertEquals(1, studentInDatabase.getActivities().get(0).getGrade().size());
        assertEquals(grade.getGradeValue(), studentInDatabase.getActivities().get(0).getGrade().get(0).getGradeValue());
    }

    @Test
    @DisplayName("Should deny access to add a grade to a student if user is a student")
    @WithMockUser(roles = "STUDENT")
    void shouldDenyAccessToAddAGradeToAStudentIfUserIsAStudent() throws Exception {
        Student student = this.student;
        student.setId("q87pXrlEnAjR64d6f4b9857f2");

        String id = student.getId();
        String activityId = "rz4IWveyBl6k64d6f4bf6f346";

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        Grade grade = Grade.builder()
                .gradeValue(10.0)
                .build();

        activity.setGrade(List.of());
        student.setActivities(List.of(activity));

        mongoTemplate.insert(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}/grade", id, activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(grade)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Access is denied"));
        assertTrue(json.contains("FORBIDDEN"));
        assertTrue(json.contains("AccessDeniedException"));
    }

    @Test
    @DisplayName("Should throws ApiNotFoundException when to try add a grade to a student that not exists")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowsApiNotFoundExceptionWhenTryToAddAGradeToAStudentThatNotExists() throws Exception {
        Student student = this.student;
        student.setId("Cq9RNxzthvyb64d6f62d2078a");

        String id = student.getId();
        String activityId = "eeozQyAs3WcC64d6f6370953e";

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        Grade grade = Grade.builder()
                .gradeValue(10.0)
                .build();

        activity.setGrade(List.of());
        student.setActivities(List.of(activity));

        mongoTemplate.insert(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}/grade", "q87pXrlEnAjR64d6f4b9857f3", activityId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(grade)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Estudante nÃ£o encontrado com o ID"));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should throws ApiNotFoundException when to try add a grade to an activity that not exists")
    @WithMockUser(roles = "ADMIN")
    void shouldThrowsApiNotFoundExceptionWhenToTryToAddAnActivityThatNotExists() throws Exception {
        Student student = this.student;
        student.setId("tZA4vLxQBGAc64d6f640e51de");

        String id = student.getId();
        String activityId = "TIZmEJBwP2cC64d6f647a1396";

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        Grade grade = Grade.builder()
                .gradeValue(10.0)
                .build();

        activity.setGrade(List.of());
        student.setActivities(List.of(activity));

        mongoTemplate.insert(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(put("/students/{id}/activity/{activityId}/grade", id, "rz4IWveyBl6k64d6f4bf6f347")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(grade)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Atividade nÃ£o encontrada com o ID"));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should calculates the average of a student in an activity")
    void shouldCalculatesTheAverageOfAStudentInAnActivity() throws Exception {
        Student student = this.student;
        student.setId("KQwsozIIUYKt64d6f7192a172");

        String id = student.getId();
        String activityId = "Otuzd6oJFrdy64d6f71f42301";

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        Grade grade = Grade.builder()
                .gradeValue(10.0)
                .build();

        activity.setGrade(List.of(grade));
        student.setActivities(List.of(activity));

        mongoTemplate.insert(grade);
        mongoTemplate.insert(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(get("/students/{id}/activity/{activityId}/average", id, activityId)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertEquals("10.0", json);
    }

    @Test
    @DisplayName("Should throws ApiNotFoundException when to try calculates the average of a activity without grade")
    void shouldThrowsApiNotExceptionWhenToTryCalculatesAverageOfActivityWithoutGrade() throws Exception {
        Student student = this.student;
        student.setId("2rA1dN1Mq0My64d6f88c2ce00");

        String id = student.getId();
        String activityId = "2zhERMFKKD4v64d6f8916f2ce";

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        activity.setGrade(List.of());
        student.setActivities(List.of(activity));

        mongoTemplate.insert(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(get("/students/{id}/activity/{activityId}/average", id, activityId)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("O estudante nÃ£o possui notas nessa atividade."));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));    }

    @Test
    @DisplayName("Should throws ApiNotFoundException when to try calculates the average of a activity that not exists")
    void shouldThrowsApiNotExceptionWhenToTryCalculatesAverageOfActivityThatNotExists() throws Exception {
        Student student = this.student;
        student.setId("PEtu6xPjX55G64d6f9a2370cd");

        String id = student.getId();
        String activityId = "JDnAbdUH1rtX64d6f9a98e51b";

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .build();

        activity.setGrade(List.of());
        student.setActivities(List.of(activity));

        mongoTemplate.insert(activity);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(get("/students/{id}/activity/{activityId}/average", id, "2zhERMFKKD4v64d6f8916f2cf")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Atividade nÃ£o encontrada com o ID"));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should calculates the average of a student in all activities")
    void shouldCalculatesTheAverageOfAStudentInAllActivities() throws Exception {
        Student student = this.student;
        student.setId("gSj5PNfcXXWu64d6fa3707cf8");

        String id = student.getId();
        String activityId = "YaRUKmVUhwMo64d6fa3ccc155";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of(Grade.builder().gradeValue(10.0).build()))
                .build();

        Activity activity2 = Activity.builder()
                .id("YaRUKmVUhwMo64d6fa3ccc156")
                .title("Activity 2")
                .questionStatement("Description 2")
                .grade(List.of(Grade.builder().gradeValue(8.6).build()))
                .build();

        student.setActivities(List.of(activity1, activity2));

        mongoTemplate.insert(activity1);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(get("/students/{id}/average", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertEquals("9.3", json);
    }

    @Test
    @DisplayName("Should throws ApiNotFoundException when to try calculates the average of a student without activities")
    void shouldThrowsApiNotFoundExceptionWhenToTryCalculatesTheAverageOfAStudentWithoutActivities() throws Exception {
        Student student = this.student;
        student.setId("7Ct5UPXwdoJS64d6fb8fc1369");
        student.setActivities(List.of());

        String id = student.getId();

        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(get("/students/{id}/average", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("O estudante nÃ£o possui atividades."));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should return 0.0 when to try calculates the average of a student without grade")
    void shouldReturnZeroWhenToTryCalculatesTheAverageOfAStudentWithoutGrades() throws Exception {
        Student student = this.student;
        student.setId("MXqNVKYpbuEK64d6fd6d00687");

        String id = student.getId();
        String activityId = "R179jZJgSwzL64d6fd7381f5e";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of())
                .build();

        Activity activity2 = Activity.builder()
                .id("YaRUKmVUhwMo64d6fa3ccc156")
                .title("Activity 2")
                .questionStatement("Description 2")
                .grade(List.of())
                .build();

        student.setActivities(List.of(activity1, activity2));

        mongoTemplate.insert(activity1);
        mongoTemplate.insert(student);

        MvcResult result = mvc.perform(get("/students/{id}/average", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertEquals("0.0", json);
    }

    @Test
    @DisplayName("Should calculates the average of all students in an activity")
    void shouldCalculatesTheAverageOfAllStudentsInAnActivity() throws Exception {
        Student student1 = this.student;
        student1.setId("zJr4676LXThH64d700c0899f2");

        String activityId = "bH32ANHFsCW664d700c9bb614";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of(Grade.builder().gradeValue(10.0).build()))
                .build();

        Activity activity2 = Activity.builder()
                .id("NORl9OT8TN0z64d6fedfc1a6c")
                .title("Activity 2")
                .questionStatement("Description 2")
                .grade(List.of(Grade.builder().gradeValue(8.6).build()))
                .build();

        student1.setActivities(List.of(activity1, activity2));

        mongoTemplate.insert(activity1);
        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/activity/{activityId}/average", activityId)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertEquals("10.0", json);
    }

    @Test
    @DisplayName("Should throws ApiNotFoundException when to try calculates the average of all students in an activity without students")
    void shouldThrowsApiNotFoundExceptionWhenToTryCalculatesTheAverageOfAllStudentsInAnActivityWithoutStudents() throws Exception {
        String activityId = "sKaomVYWcaqb64d700b21af4f";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of(Grade.builder().gradeValue(10.0).build()))
                .build();

        mongoTemplate.insert(activity1);

        MvcResult result = mvc.perform(get("/students/activity/{activityId}/average", activityId)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("NÃ£o hÃ¡ estudantes cadastrados."));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should return 0.0 when to try calculates the average of all students in an activity without grades")
    void shouldThrowsApiNotFoundExceptionWhenToTryCalculatesTheAverageOfAllStudentsInAnActivityWithoutGrades() throws Exception {
        Student student1 = this.student;
        student1.setId("Mlf2NYqT1lbN64d7034e608fe");

        String activityId = "Tm8KML0fRxiG64d70bf297658";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of())
                .build();

        Activity activity2 = Activity.builder()
                .id("w0FGX83vrRTm64d7035c98adb")
                .title("Activity 2")
                .questionStatement("Description 2")
                .grade(List.of())
                .build();

        student1.setActivities(List.of(activity1, activity2));

        mongoTemplate.insert(activity1);
        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/activity/{activityId}/average", activityId)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertEquals("0.0", json);
    }

    @Test
    @DisplayName("Should throws ApiNotFoundException when to try calculates the average of all students in an activity without activity")
    void shouldThrowsApiNotFoundExceptionWhenToTryCalculatesTheAverageOfAllStudentsInAnActivityWithoutActivity() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");
        String activityId = "u2qbKc96YQ1S64d6fe4c8652b";

        List<Activity> activities = new ArrayList<>();
        student1.setActivities(activities);

        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/activity/{activityId}/average", activityId)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Atividade nÃ£o encontrada com o ID:"));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should searches for a student by CPF")
    void shouldSearchesForAStudentByCPF() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");

        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/search")
                        .param("identifier", student1.getCpf())
                        .contentType("application/json"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Student 3"));
    }

    @Test
    @DisplayName("Should searches for a student by email")
    void shouldSearchesForAStudentByEmail() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");

        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/search")
                        .param("identifier", student1.getEmail())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Student 3"));
    }

    @Test
    @DisplayName("Should searches for a student by telephone")
    void shouldSearchesForAStudentByPhone() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");

        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/search")
                        .param("identifier", student1.getTelephone())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Student 3"));
    }

    @Test
    @DisplayName("Should throws ApiNotFoundException when to try searches for a student by invalid identifier")
    void shouldThrowsApiNotFoundExceptionWhenToTrySearchesForAStudentByInvalidIdentifier() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");

        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/search")
                        .param("identifier", "invalid identifier")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Estudante nÃ£o encontrado com o CPF, email ou telefone: invalid identifier"));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Searches for a student's grades by CPF")
    void shouldSearchesForAStudentGradesByCPF() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");

        String activityId = "71pfYAl6K8iz64d703559bed9";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of(Grade.builder()
                        .gradeValue(10.0)
                        .build()))
                .build();

        List<Grade> grades = new ArrayList<>();
        grades.add(Grade.builder()
                .gradeValue(10.0)
                .build());
        grades.add(Grade.builder()
                .gradeValue(8.5)
                .build());

        Activity activity2 = Activity.builder()
                .id("w0FGX83vrRTm64d7035c98adb")
                .title("Activity 2")
                .questionStatement("Description 2")
                .grade(grades)
                .build();

        student1.setActivities(List.of(activity1, activity2));

        mongoTemplate.insert(activity1);
        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/search/grades")
                        .param("identifier", student1.getCpf())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertThat(json).contains("10.0");
        assertThat(json).contains("8.5");
    }

    @Test
    @DisplayName("Searches for a student's grades by Email")
    void shouldSearchesForAStudentGradesByEmail() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");

        String activityId = "MFHpzUWkxes564d70bfcde9b4";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of(Grade.builder()
                        .gradeValue(10.0)
                        .build()))
                .build();

        List<Grade> grades = new ArrayList<>();
        grades.add(Grade.builder()
                .gradeValue(10.0)
                .build());
        grades.add(Grade.builder()
                .gradeValue(8.5)
                .build());

        Activity activity2 = Activity.builder()
                .id("w0FGX83vrRTm64d7035c98adb")
                .title("Activity 2")
                .questionStatement("Description 2")
                .grade(grades)
                .build();

        student1.setActivities(List.of(activity1, activity2));

        mongoTemplate.insert(activity1);
        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/search/grades")
                        .param("identifier", student1.getEmail())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertThat(json).contains("10.0");
        assertThat(json).contains("8.5");
    }

    @Test
    @DisplayName("Searches for a student's grades by Phone")
    void shouldSearchesForAStudentGradesByPhone() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");

        String activityId = "Kk4NsD29Q9Ii64d70c0530332";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of(Grade.builder()
                        .gradeValue(10.0)
                        .build()))
                .build();

        List<Grade> grades = new ArrayList<>();
        grades.add(Grade.builder()
                .gradeValue(10.0)
                .build());
        grades.add(Grade.builder()
                .gradeValue(8.5)
                .build());

        Activity activity2 = Activity.builder()
                .id("w0FGX83vrRTm64d7035c98adb")
                .title("Activity 2")
                .questionStatement("Description 2")
                .grade(grades)
                .build();

        student1.setActivities(List.of(activity1, activity2));

        mongoTemplate.insert(activity1);
        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/search/grades")
                        .param("identifier", student1.getTelephone())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertThat(json).contains("10.0");
        assertThat(json).contains("8.5");
    }

    @Test
    @DisplayName("Searches for a student's grades by invalid identifier")
    void shouldSearchesForAStudentGradesByInvalidIdentifier() throws Exception {
        Student student1 = this.student;
        student1.setId("emsEXCrI0gfy64d6fe3e36155");

        String activityId = "wt5qqyX5brDH64d70c0ce6ac2";

        Activity activity1 = Activity.builder()
                .id(activityId)
                .title("Activity 1")
                .questionStatement("Description 1")
                .grade(List.of(Grade.builder()
                        .gradeValue(10.0)
                        .build()))
                .build();

        List<Grade> grades = new ArrayList<>();
        grades.add(Grade.builder()
                .gradeValue(10.0)
                .build());
        grades.add(Grade.builder()
                .gradeValue(8.5)
                .build());

        Activity activity2 = Activity.builder()
                .id("w0FGX83vrRTm64d7035c98adb")
                .title("Activity 2")
                .questionStatement("Description 2")
                .grade(grades)
                .build();

        student1.setActivities(List.of(activity1, activity2));

        mongoTemplate.insert(activity1);
        mongoTemplate.insert(student1);

        MvcResult result = mvc.perform(get("/students/search/grades")
                        .param("identifier", "invalid")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Estudante nÃ£o encontrado com o CPF, email ou telefone"));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    List<Student> students = Arrays.asList(
            Student.builder()
                    .fullName("Student 1")
                    .email("student@mail.com")
                    .cpf("517.141.480-76")
                    .telephone("123456789")
                    .build(),
            Student.builder()
                    .fullName("Student 2")
                    .email("student2@mail.com")
                    .cpf("332.757.370-07")
                    .telephone("123456789")
                    .build()
    );

    Student student = Student.builder()
            .fullName("Student 3")
            .email("student@mail.com")
            .cpf("290.532.760-09")
            .telephone("123456789")
            .build();

    Student studentWithInvalidData = Student.builder()
            .build();

}