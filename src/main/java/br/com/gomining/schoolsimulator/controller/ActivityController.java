package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.request.ActivityRequest;
import br.com.gomining.schoolsimulator.model.response.ActivityResponse;
import br.com.gomining.schoolsimulator.service.ActivityService;
import br.com.gomining.schoolsimulator.service.impl.ActivityServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.gomining.schoolsimulator.model.mapper.ActivityMapper.*;

@RestController
@RequestMapping("/activities")
@AllArgsConstructor
public class ActivityController {
    private final ActivityServiceImpl activityService;

    @GetMapping
    public List<ActivityResponse> getAllActivities() {
        return toListResponse(this.activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    public ActivityResponse getActivityById(@PathVariable String id) {
        return toResponse(this.activityService.getActivityById(id));
    }

    @PostMapping
    public ActivityResponse createActivity(@RequestBody ActivityRequest activity) {
        return toResponse(this.activityService.createActivity(toEntity(activity)));
    }

    @PutMapping("/{id}")
    public ActivityResponse updateActivity(@PathVariable String id, @RequestBody ActivityRequest activity) {
        return toResponse(this.activityService.updateActivity(id, toEntity(activity)));
    }

    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable String id) {
        this.activityService.deleteActivity(id);
    }
}
