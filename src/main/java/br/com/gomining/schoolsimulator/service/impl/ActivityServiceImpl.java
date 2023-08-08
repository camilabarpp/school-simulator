package br.com.gomining.schoolsimulator.service.impl;

import br.com.gomining.schoolsimulator.common.Exception.ApiNotFoundException;
import br.com.gomining.schoolsimulator.model.entity.Activity;
import br.com.gomining.schoolsimulator.repository.ActivityRepository;
import br.com.gomining.schoolsimulator.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private ActivityRepository activityRepository;

    @Override
    public List<Activity> getAllActivities() {
        return this.activityRepository.findAll();
    }

    @Override
    public Activity getActivityById(String id) {
        return this.activityRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("Atividade não encontrada com o ID: " + id));
    }

    @Override
    public Activity createActivity(Activity activity) {
        return this.activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(String id, Activity updatedActivity) {
        Optional<Activity> activityOptional = activityRepository.findById(id);
        Activity existingActivity = activityOptional
                .orElseThrow(() -> new ApiNotFoundException("Atividade não encontrada com o ID: " + id));

        existingActivity.setTitle(updatedActivity.getTitle());
        existingActivity.setDescription(updatedActivity.getDescription());
        existingActivity.setRegistrationDate(updatedActivity.getRegistrationDate());
        existingActivity.setLastUpdateDate(updatedActivity.getLastUpdateDate());

        return activityRepository.save(existingActivity);
    }

    @Override
    public void deleteActivity(String id) {
        Optional<Activity> activityOptional = activityRepository.findById(id);
        Activity existingActivity = activityOptional
                .orElseThrow(() -> new ApiNotFoundException("Atividade não encontrada com o ID: " + id));

        activityRepository.delete(existingActivity);
    }
}
