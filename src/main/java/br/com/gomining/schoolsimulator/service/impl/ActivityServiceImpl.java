package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.Activity;
import br.com.gomining.schoolsimulator.repository.ActivityRepository;
import br.com.gomining.schoolsimulator.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static br.com.gomining.schoolsimulator.common.exception.util.ErrorMessage.ACTIVITY_NOT_FUND_WITH_ID;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private ActivityRepository activityRepository;

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Override
    public Activity getActivityById(String id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException(ACTIVITY_NOT_FUND_WITH_ID + id));
    }

    @Override
    public Activity createActivity(Activity activity) {
        activity.setRegistrationDate(String.valueOf(LocalDate.now()));
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(String id, Activity updatedActivity) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setTitle(updatedActivity.getTitle());
                    activity.setQuestionStatement(updatedActivity.getQuestionStatement());
                    activity.setLastUpdateDate(LocalDate.now().toString());
                    return activityRepository.save(activity);
                })
                .orElseThrow(() -> new ApiNotFoundException(ACTIVITY_NOT_FUND_WITH_ID + id));
    }

    @Override
    public void deleteActivity(String id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException(ACTIVITY_NOT_FUND_WITH_ID + id));

        activityRepository.delete(activity);
    }

    @Override
    public void deleteAllActivities() {
        activityRepository.deleteAll();
    }
}
