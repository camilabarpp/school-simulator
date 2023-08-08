package br.com.gomining.schoolsimulator.model.mapper;

import br.com.gomining.schoolsimulator.model.entity.Activity;
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
                .description(activityRequest.getDescription())
                .registrationDate(activityRequest.getRegistrationDate())
                .lastUpdateDate(activityRequest.getLastUpdateDate())
                .build();
    }

    public static ActivityResponse toResponse(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .registrationDate(activity.getRegistrationDate())
                .lastUpdateDate(activity.getLastUpdateDate())
                .build();
    }

    public static List<ActivityResponse> toListResponse(List<Activity> activities) {
        return activities.stream()
                .map(ActivityMapper::toResponse)
                .collect(Collectors.toList());
    }
}
