package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.common.exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import br.com.gomining.schoolsimulator.model.entity.grade.Grade;
import br.com.gomining.schoolsimulator.repository.ActivityRepository;
import br.com.gomining.schoolsimulator.service.impl.ActivityServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ActivityServiceTest {

    @InjectMocks
    private ActivityServiceImpl activityService;

    @Mock
    private ActivityRepository activityRepository;

    @Test
    @DisplayName("Test getting all activities")
    void testGetAllActivities() {
        // Arrange
        List<Activity> expectedActivities = listOfActivities;

        when(activityRepository.findAll()).thenReturn(expectedActivities);

        // Act
        List<Activity> result = activityService.getAllActivities();

        // Assert
        assertEquals(expectedActivities, result);
    }

    @Test
    @DisplayName("Test getting activity by id")
    void testGetActivityById() {
        String id = "1";

        // Arrange
        Activity expectedActivity = listOfActivities.get(0);

        when(activityRepository.findById(expectedActivity.getId())).thenReturn(Optional.of(expectedActivity));

        // Act
        Activity result = activityService.getActivityById(id);

        // Assert
        assertEquals(expectedActivity, result);
    }

    @Test
    @DisplayName("Should throw exception when getting an invalid activity by id")
    void testGetInvalidActivityById() {
        String id = "1";

        // Arrange
        when(activityRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ApiNotFoundException.class, () -> activityService.getActivityById(id));
    }

    @Test
    @DisplayName("Test creating activity")
    void testCreateActivity() {
        // Arrange
        Activity expectedActivity = listOfActivities.get(0);

        when(activityRepository.save(expectedActivity)).thenReturn(expectedActivity);

        // Act
        Activity result = activityService.createActivity(expectedActivity);

        // Assert
        assertNotNull(result.getRegistrationDate());
        assertEquals(expectedActivity, result);
    }

    @Test
    @DisplayName("Test updating activity")
    void testUpdateActivity() {
        String id = "1";

        // Arrange
        Activity expectedActivity = listOfActivities.get(0);

        when(activityRepository.findById(id)).thenReturn(Optional.of(expectedActivity));
        when(activityRepository.save(expectedActivity)).thenReturn(expectedActivity);

        // Act
        Activity result = activityService.updateActivity(id, expectedActivity);

        // Assert
        assertNotNull(result.getLastUpdateDate());
        assertEquals(expectedActivity, result);
    }

    @Test
    @DisplayName("Should throw exception when updating an invalid activity")
    void testUpdateInvalidActivity() {
        String id = "1";

        // Arrange
        Activity expectedActivity = listOfActivities.get(0);

        when(activityRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ApiNotFoundException.class, () -> activityService.updateActivity(id, expectedActivity));
    }

    @Test
    @DisplayName("Test deleting activity")
    void testDeleteActivity() {
        String id = "1";

        // Arrange
        Activity expectedActivity = listOfActivities.get(0);

        when(activityRepository.save(expectedActivity)).thenReturn(expectedActivity);
        when(activityRepository.findById(id)).thenReturn(Optional.of(expectedActivity));

        // Act
        activityService.deleteActivity(id);

        // Assert
        verify(activityRepository).delete(expectedActivity);
    }

    @Test
    @DisplayName("Should throw exception when deleting an invalid activity")
    void testDeleteInvalidActivity() {
        String id = "1";

        // Arrange
        when(activityRepository.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ApiNotFoundException.class, () -> activityService.deleteActivity(id));
    }

    @Test
    @DisplayName("Test deleting all activities")
    void testDeleteAllActivities() {
        // Act
        activityService.deleteAllActivities();

        // Assert
        verify(activityRepository).deleteAll();
    }

    List<Activity> listOfActivities = List.of(
            new Activity("1", "Activity 1", "Description 1", this.listOfGrades),
            new Activity("2", "Activity 2", "Description 2", this.listOfGrades)
    );

    List<Grade> listOfGrades = List.of(
            new Grade("1", 0.0),
            new Grade("2", 0.0)
    );
}
