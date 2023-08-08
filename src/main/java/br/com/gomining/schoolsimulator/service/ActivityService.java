package br.com.gomining.schoolsimulator.service;

import br.com.gomining.schoolsimulator.model.entity.Activity;

import java.util.List;

public interface ActivityService {
    List<Activity> getAllActivities();
    Activity getActivityById(String id);
    Activity createActivity(Activity activity);
    Activity updateActivity(String id, Activity activity);
    void deleteActivity(String id);
}
