package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
import br.com.gomining.schoolsimulator.model.mapper.GradeMapper;
import br.com.gomining.schoolsimulator.model.request.GradeRequest;
import br.com.gomining.schoolsimulator.model.response.GradeResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static br.com.gomining.schoolsimulator.model.mapper.GradeMapper.toResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.2.8")
class GradeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should return all grades")
    void getAllGrades() throws Exception {
        List<Grade> grades = List.of(grade1, grade2);
        mongoTemplate.insertAll(grades);

        MvcResult result = mvc.perform(get("/grades")
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        List<Grade> gradesResponse = mapper.readValue(json, new TypeReference<>(){});

        for (Grade grade : gradesResponse) {
            assertThat(grade.getGradeValue()).isIn(10.0, 9.0);
        }

        List<Grade> gradesInDatabase = mongoTemplate.findAll(Grade.class);
        assertThat(gradesInDatabase)
                .extracting(Grade::getGradeValue)
                .containsOnly(10.0, 9.0);

        assertEquals(json, result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Should return grade by id")
    void shouldReturnGradeById() throws Exception {
        Grade grade = grade1;
        grade.setId("64d68cec897bc");
        String id = grade.getId();

        mongoTemplate.insert(grade);

        MvcResult result = mvc.perform(get("/grades/{id}", id)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        Grade gradeResponse = mapper.readValue(json, Grade.class);
        Grade gradeInDatabase = mongoTemplate.findById(id, Grade.class);

        assertNotNull(gradeInDatabase);
        assertThat(gradeResponse.getGradeValue()).isEqualTo(10.0);
        assertEquals(gradeInDatabase.getId(), gradeResponse.getId());
        assertThat(gradeInDatabase.getGradeValue()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Should throw exception when grade not found")
    void shouldThrowExceptionWhenGradeNotFound() throws Exception {
        String id = "XYHVP6bURoKd64d68e7f49ec6";
        MvcResult result = mvc.perform(get("/grades/{id}", id)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Nota nÃ£o encontrada com o ID: " + id));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should create grade if user is a admin")
    @WithMockUser(roles = {"ADMIN"})
    void createGradeIdUserIsAAdmin() throws Exception {
        GradeRequest gradeRequest = GradeMapper.toRequest(grade1);

        MvcResult result = mvc.perform(post("/grades")
                .contentType("application/json")
                .content(mapper.writeValueAsString(gradeRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        GradeResponse gradeResponse = mapper.readValue(json, GradeResponse.class);

        Grade gradeInDatabase = mongoTemplate.findById(gradeResponse.getId(), Grade.class);

        assertNotNull(gradeInDatabase);
        assertThat(gradeResponse).isEqualTo(toResponse(gradeInDatabase));

    }

    @Test
    @DisplayName("Should deny access when user is a teacher")
    @WithMockUser(roles = {"TEACHER"})
    void createGradeIdUserIsATeacher() throws Exception {
        GradeRequest gradeRequest = GradeMapper.toRequest(grade1);

        MvcResult result = mvc.perform(post("/grades")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(gradeRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        GradeResponse gradeResponse = mapper.readValue(json, GradeResponse.class);

        Grade gradeInDatabase = mongoTemplate.findById(gradeResponse.getId(), Grade.class);

        assertNotNull(gradeInDatabase);
        assertThat(gradeResponse).isEqualTo(toResponse(gradeInDatabase));
    }

    @Test
    @DisplayName("Should throw exception when grade value is null")
    @WithMockUser(roles = {"ADMIN"})
    void shouldThrowExceptionWhenTitleIsNull() throws Exception {
        GradeRequest gradeRequest = GradeRequest.builder()
                .gradeValue(null)
                .build();

        MvcResult result = mvc.perform(post("/grades")
                .contentType("application/json")
                .content(mapper.writeValueAsString(gradeRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("gradeValue is mandatory"));
        assertTrue(json.contains("BAD_REQUEST"));
        assertTrue(json.contains("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("Should deny access when user is not admin")
    void shouldDenyAccessWhenUserIsNotAdmin() throws Exception {
        GradeRequest gradeRequest = GradeMapper.toRequest(grade1);

        mvc.perform(post("/grades")
                .contentType("application/json")
                .content(mapper.writeValueAsString(gradeRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Should update grade if user is a admin")
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateAGradeWithSuccessIfUserIsAAdmin() throws Exception {
        Grade grade = grade1;
        grade.setId("nYcBS3hIPLFZ64d698b67e30f");
        String id = grade.getId();

        mongoTemplate.insert(grade);

        GradeRequest gradeRequest = GradeMapper.toRequest(grade2);

        MvcResult result = mvc.perform(put("/grades/{id}", id)
                .contentType("application/json")
                .content(mapper.writeValueAsString(gradeRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        GradeResponse gradeResponse = mapper.readValue(json, GradeResponse.class);

        Grade gradeInDatabase = mongoTemplate.findById(id, Grade.class);

        assertNotNull(gradeInDatabase);
        assertThat(gradeResponse).isEqualTo(toResponse(gradeInDatabase));
    }

    @Test
    @DisplayName("Should update grade if user is a teacher")
    @WithMockUser(roles = {"TEACHER"})
    void shouldUpdateAGradeWithSuccessIfUserIsATeacher() throws Exception {
        Grade grade = grade1;
        grade.setId("3IARc8rhrNOS64d69aa001bb6");
        String id = grade.getId();

        mongoTemplate.insert(grade);

        GradeRequest gradeRequest = GradeMapper.toRequest(grade2);

        MvcResult result = mvc.perform(put("/grades/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(gradeRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        GradeResponse gradeResponse = mapper.readValue(json, GradeResponse.class);

        Grade gradeInDatabase = mongoTemplate.findById(id, Grade.class);

        assertNotNull(gradeInDatabase);
        assertThat(gradeResponse).isEqualTo(toResponse(gradeInDatabase));
    }

    @Test
    @DisplayName("Should throw exception when grade not found")
    @WithMockUser(roles = {"ADMIN"})
    void shouldThrowExceptionWhenGradeNotFoundOnUpdate() throws Exception {
        String id = "XYHVP6bURoKd64d68e7f49ec6";
        GradeRequest gradeRequest = GradeMapper.toRequest(grade1);

        MvcResult result = mvc.perform(put("/grades/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(gradeRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Nota nÃ£o encontrada com o ID: " + id));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should deny access when user is not admin or teacher")
    void shouldDenyAccessWhenUserIsNotAdminOrTeacherOnUpdate() throws Exception {
        Grade grade = grade1;
        grade.setId("uO2QZ0VeVHJ464d69d781c284");
        String id = grade.getId();

        mongoTemplate.insert(grade);

        GradeRequest gradeRequest = GradeMapper.toRequest(grade2);

        mvc.perform(put("/grades/{id}", id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(gradeRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Should delete grade if user is a admin")
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteAGradeWithSuccessIfUserIsAAdmin() throws Exception {
        Grade grade = grade1;
        grade.setId("AmWNB0YIanSZ64d69c38d2bd4");
        String id = grade.getId();

        mongoTemplate.insert(grade);

        mvc.perform(delete("/grades/{id}", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        Grade gradeInDatabase = mongoTemplate.findById(id, Grade.class);

        assertNull(gradeInDatabase);
    }

    @Test
    @DisplayName("Should delete grade if user is a teacher")
    @WithMockUser(roles = {"TEACHER"})
    void shouldDeleteAGradeWithSuccessIfUserIsATeacher() throws Exception {
        Grade grade = grade1;
        grade.setId("JckyfofwT4XY64d69c30db2dd");
        String id = grade.getId();

        mongoTemplate.insert(grade);

        mvc.perform(delete("/grades/{id}", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        Grade gradeInDatabase = mongoTemplate.findById(id, Grade.class);

        assertNull(gradeInDatabase);
    }

    @Test
    @DisplayName("Should deny access when user is not admin or teacher")
    void shouldDenyAccessWhenUserIsNotAdminOrTeacherOnDelete() throws Exception {
        Grade grade = grade1;
        grade.setId("ljBvGh8Xm51K64d69cb92b4bf");
        String id = grade.getId();

        mongoTemplate.insert(grade);

        mvc.perform(delete("/grades/{id}", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Should throw exception when grade not found")
    @WithMockUser(roles = {"ADMIN"})
    void shouldThrowExceptionWhenGradeNotFoundOnDelete() throws Exception {
        String id = "XYHVP6bURoKd64d68e7f49ec6";

        MvcResult result = mvc.perform(delete("/grades/{id}", id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("Nota nÃ£o encontrada com o ID: " + id));
        assertTrue(json.contains("NOT_FOUND"));
        assertTrue(json.contains("ApiNotFoundException"));
    }

    @Test
    @DisplayName("Should delete all grades if user is a admin")
    @WithMockUser(roles = {"ADMIN"})
    void deleteAllGrades() throws Exception {
        mongoTemplate.insert(grade1);
        mongoTemplate.insert(grade2);

        mvc.perform(delete("/grades")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        List<Grade> gradesInDatabase = mongoTemplate.findAll(Grade.class);

        assertTrue(gradesInDatabase.isEmpty());
    }

    @Test
    @DisplayName("Should delete all grades if user is a teacher")
    @WithMockUser(roles = {"TEACHER"})
    void deleteAllGradesIfUserIsATeacher() throws Exception {
        mongoTemplate.insert(grade1);
        mongoTemplate.insert(grade2);

        mvc.perform(delete("/grades")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        List<Grade> gradesInDatabase = mongoTemplate.findAll(Grade.class);

        assertTrue(gradesInDatabase.isEmpty());
    }

    @Test
    @DisplayName("Should deny access when user is not admin")
    void shouldDenyAccessWhenUserIsNotAdminOnDeleteAll() throws Exception {
        mvc.perform(delete("/grades")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    Grade grade1 = Grade.builder()
            .gradeValue(10.0)
            .build();

    Grade grade2 = Grade.builder()
            .gradeValue(9.0)
            .build();

}