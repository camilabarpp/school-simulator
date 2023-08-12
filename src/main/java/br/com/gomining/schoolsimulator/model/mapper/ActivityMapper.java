package br.com.gomining.schoolsimulator.model.mapper;

import br.com.gomining.schoolsimulator.model.entity.activity.Activity;
import br.com.gomining.schoolsimulator.model.request.ActivityRequest;
import br.com.gomining.schoolsimulator.model.response.ActivityResponse;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ActivityMapper {
    public static Activity toEntity(ActivityRequest activityRequest) {
        return Activity.builder()
                .title(activityRequest.getTitle())
                .questionStatement(activityRequest.getQuestionStatement())
                .registrationDate(activityRequest.getRegistrationDate())
                .lastUpdateDate(activityRequest.getLastUpdateDate())
                .grade(activityRequest.getGrade())
                .build();
    }

    public static ActivityRequest toRequest(Activity activity) {
        return ActivityRequest.builder()
                .title(activity.getTitle())
                .questionStatement(activity.getQuestionStatement())
                .registrationDate(activity.getRegistrationDate())
                .lastUpdateDate(activity.getLastUpdateDate())
                .grade(activity.getGrade())
                .build();
    }

    public static ActivityResponse toResponse(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .questionStatement(activity.getQuestionStatement())
                .registrationDate(activity.getRegistrationDate())
                .lastUpdateDate(activity.getLastUpdateDate())
//                .grade(activity.getGrade())
                .build();
    }

    public static List<ActivityResponse> toListResponse(List<Activity> activities) {
        return activities.stream()
                .map(ActivityMapper::toResponse)
                .collect(Collectors.toList());
    }
}
