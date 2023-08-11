package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.request.ActivityRequest;
import br.com.gomining.schoolsimulator.model.response.ActivityResponse;
import br.com.gomining.schoolsimulator.service.impl.ActivityServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static br.com.gomining.schoolsimulator.model.mapper.ActivityMapper.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/activities")
@AllArgsConstructor
@Tag(name = "Activity Controller")
public class ActivityController {
    private final ActivityServiceImpl activityService;

    @GetMapping
    @Operation(summary = "Returns a list of activities")
    public List<ActivityResponse> getAllActivities() {
        return toListResponse(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Returns a single activity")
    public ActivityResponse getActivityById(@PathVariable String id) {
        return toResponse(activityService.getActivityById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Creates a new activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ActivityResponse createActivity(@RequestBody @Valid ActivityRequest activity) {
        return toResponse(activityService.createActivity(toEntity(activity)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates an existing activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ActivityResponse updateActivity(@PathVariable String id, @RequestBody @Valid ActivityRequest activity) {
        return toResponse(activityService.updateActivity(id, toEntity(activity)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Deletes an existing activity")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteActivity(@PathVariable String id) {
        activityService.deleteActivity(id);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Deletes all activities")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllActivities() {
        activityService.deleteAllActivities();
    }
}
