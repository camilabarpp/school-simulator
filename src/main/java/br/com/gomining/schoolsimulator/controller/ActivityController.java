package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.request.ActivityRequest;
import br.com.gomining.schoolsimulator.model.response.ActivityResponse;
import br.com.gomining.schoolsimulator.service.impl.ActivityServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static br.com.gomining.schoolsimulator.model.mapper.ActivityMapper.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/activities")
@AllArgsConstructor
public class ActivityController {
    private final ActivityServiceImpl activityService;

    @GetMapping
    public List<ActivityResponse> getAllActivities() {
        return toListResponse(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    public ActivityResponse getActivityById(@PathVariable String id) {
        return toResponse(activityService.getActivityById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ActivityResponse createActivity(@RequestBody @Valid ActivityRequest activity) {
        return toResponse(activityService.createActivity(toEntity(activity)));
    }

    @PutMapping("/{id}")
    public ActivityResponse updateActivity(@PathVariable String id, @RequestBody @Valid ActivityRequest activity) {
        return toResponse(activityService.updateActivity(id, toEntity(activity)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteActivity(@PathVariable String id) {
        activityService.deleteActivity(id);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteAllActivities() {
        activityService.deleteAllActivities();
    }
}
