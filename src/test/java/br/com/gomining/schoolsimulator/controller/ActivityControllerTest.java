package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import br.com.gomining.schoolsimulator.model.mapper.ActivityMapper;
import br.com.gomining.schoolsimulator.model.request.ActivityRequest;
import br.com.gomining.schoolsimulator.model.response.ActivityResponse;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.2.8")
class ActivityControllerTest {

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
    @DisplayName("Should return a list of activities")
    void shouldReturnAllActivities() throws Exception {
        List<Activity> activities = listOfActivities;

        mongoTemplate.insertAll(activities);

        MvcResult result = mvc.perform(get("/activities")
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        Activity[] returnedActivities = mapper.readValue(json, Activity[].class);

        for (Activity activity : returnedActivities) {
            assertThat(activity.getTitle()).isIn("Activity 1", "Activity 2");
        }

        List<Activity> activitiesInDatabase = mongoTemplate.findAll(Activity.class);
        assertThat(activitiesInDatabase)
                .extracting(Activity::getTitle)
                .containsOnly("Activity 1", "Activity 2");

        assertEquals(json, result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Should return a activity by id")
    void shouldReturnActivityById() throws Exception {
        Activity activity = activity();
        activity.setId("56d9bf92f9be48771d6fe5b1");
        String id = activity.getId();

        mongoTemplate.insert(activity);

        MvcResult result = mvc.perform(get("/activities/" + id)
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        Activity returnedActivity = mapper.readValue(json, Activity.class);

        assertThat(returnedActivity.getTitle()).isEqualTo("Activity 1");

        Activity activityInDatabase = mongoTemplate.findById(id, Activity.class);

        assertNotNull(activityInDatabase);
        assertThat(activityInDatabase.getTitle()).isEqualTo("Activity 1");
    }

    @Test
    @DisplayName("Should throw exception when activity not found")
    void shouldThrowExceptionWhenActivityNotFound() throws Exception {
        MvcResult result = mvc.perform(get("/activities/230")
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("message\":\"Atividade nÃ£o encontrada com o ID:"));
    }

    @Test
    @DisplayName("Should create a activity")
    @WithMockUser(roles = {"ADMIN", "TEACHER"})
    void shouldCreateNewActivity() throws Exception {
        ActivityRequest activityRequest = ActivityRequest.builder()
                .title("Activity 1")
                .questionStatement("Question 1")
                .build();

        MvcResult result = mvc.perform(post("/activities")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activityRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ActivityResponse createdActivity = mapper.readValue(jsonResponse, ActivityResponse.class);

        Activity activitiesInDatabase = mongoTemplate.findById(createdActivity.getId(), Activity.class);

        assertNotNull(createdActivity.getRegistrationDate());
        assertNotNull(createdActivity.getId());
        assertEquals(activityRequest.getTitle(), createdActivity.getTitle());
        assertNotNull(activitiesInDatabase);
        assertThat(activitiesInDatabase.getTitle()).isEqualTo("Activity 1");
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    @WithMockUser(roles = {"ADMIN", "TEACHER"})
    void shouldThrowExceptionWhenTitleIsNull() throws Exception {
        ActivityRequest activityRequest = ActivityRequest.builder()
                .title(null)
                .questionStatement("Question 1")
                .build();

        MvcResult result = mvc.perform(post("/activities")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activityRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("title is mandatory"));
    }

    @Test
    @DisplayName("Should throw exception when questionStatement is null")
    @WithMockUser(roles = {"ADMIN", "TEACHER"})
    void shouldThrowExceptionWhenQuestionStatementIsNull() throws Exception {
        ActivityRequest activityRequest = ActivityRequest.builder()
                .title("Activity 1")
                .questionStatement(null)
                .build();

        MvcResult result = mvc.perform(post("/activities")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activityRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("question statement is mandatory"));
    }
    @Test
    @DisplayName("Should deny access when user is not ADMIN or TEACHER")
    void shouldThrowExceptionWhenUserIsNotAdminOrTeacher() throws Exception {
        ActivityRequest activityRequest = ActivityRequest.builder()
                .title("Activity 1")
                .questionStatement("Question 1")
                .build();

        mvc.perform(post("/activities")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activityRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Should update a activity")
    @WithMockUser(roles = {"ADMIN", "TEACHER"})
    void shouldUpdateActivity() throws Exception {
        Activity activity = activity();
        activity.setId("64d27e8c66ffc02f55a603c5");
        String id = activity.getId();

        mongoTemplate.insert(activity);

        ActivityRequest activityRequest = ActivityMapper.toRequest(activity());

        MvcResult result = mvc.perform(put("/activities/" + id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activityRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ActivityResponse updatedActivity = mapper.readValue(jsonResponse, ActivityResponse.class);

        Activity activitiesInDatabase = mongoTemplate.findById(updatedActivity.getId(), Activity.class);

        assertNotNull(updatedActivity.getLastUpdateDate());
        assertNotNull(updatedActivity.getId());
        assertEquals(activityRequest.getTitle(), updatedActivity.getTitle());
        assertNotNull(activitiesInDatabase);
        assertThat(activitiesInDatabase.getTitle()).isEqualTo("Activity 1");
    }

    @Test
    @DisplayName("Should throw exception when activity not found")
    @WithMockUser(roles = {"ADMIN", "TEACHER"})
    void shouldThrowExceptionWhenActivityNotFoundOnUpdate() throws Exception {
        ActivityRequest activityRequest = ActivityRequest.builder()
                .title("Activity 2")
                .questionStatement("Question 2")
                .build();

        MvcResult result = mvc.perform(put("/activities/230")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activityRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("message\":\"Atividade nÃ£o encontrada com o ID:"));
        Activity activityInDatabase = mongoTemplate.findById("230", Activity.class);
        assertNull(activityInDatabase);
    }

    @Test
    @DisplayName("Should deny access when user is not ADMIN or TEACHER")
    void shouldThrowExceptionWhenUserIsNotAdminOrTeacherOnUpdate() throws Exception {
        ActivityRequest activityRequest = ActivityRequest.builder()
                .title("Activity 2")
                .questionStatement("Question 2")
                .build();

        mvc.perform(put("/activities/230")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(activityRequest)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Should delete a activity")
    @WithMockUser(roles = {"ADMIN", "TEACHER"})
    void shouldDeleteActivity() throws Exception {
        Activity activity = activity();
        activity.setId("64d09e8c66ffc02f87a603c5");
        String id = activity.getId();

        mongoTemplate.insert(activity);

        MvcResult result = mvc.perform(delete("/activities/" + id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        Activity activitiesInDatabase = mongoTemplate.findById(activity.getId(), Activity.class);

        assertNull(activitiesInDatabase);
        assertTrue(jsonResponse.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when activity not found to delete")
    @WithMockUser(roles = {"ADMIN", "TEACHER"})
    void shouldThrowExceptionWhenActivityNotFoundOnDelete() throws Exception {
        MvcResult result = mvc.perform(delete("/activities/230")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assertTrue(json.contains("message\":\"Atividade nÃ£o encontrada com o ID:"));
        Activity activityInDatabase = mongoTemplate.findById("230", Activity.class);
        assertNull(activityInDatabase);
    }

    @Test
    @DisplayName("Should deny access when user is not ADMIN")
    void shouldThrowExceptionWhenUserIsNotAdminOrTeacherOnDelete() throws Exception {
        mvc.perform(delete("/activities/230")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Should deny access when user is TEACHER")
    void shouldThrowExceptionWhenUserIsTeacherOnDelete() throws Exception {
        mvc.perform(delete("/activities/230")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Should delete all activities")
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteAllActivities() throws Exception {
        Activity activity = activity();
        activity.setId("64d19e8c56ffc02f87a603c5");
        String id = activity.getId();

        mongoTemplate.insert(activity);

        MvcResult result = mvc.perform(delete("/activities")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        Activity activitiesInDatabase = mongoTemplate.findById(activity.getId(), Activity.class);

        assertNull(activitiesInDatabase);
        assertTrue(jsonResponse.isEmpty());
    }

    @Test
    @DisplayName("Should deny access when user is not ADMIN")
    void shouldThrowExceptionWhenUserIsNotAdminOnDeleteAll() throws Exception {
        mvc.perform(delete("/activities")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn();
    }

    List<Activity> listOfActivities = Arrays.asList(
            activity(),
            Activity.builder()
                    .title("Activity 1")
                    .questionStatement("Question 1")
                    .build(),
            Activity.builder()
                    .title("Activity 2")
                    .questionStatement("Question 2")
                    .build());

    Activity activity() {
        return Activity.builder()
                .title("Activity 1")
                .questionStatement("Question 1")
                .build();
    }
}