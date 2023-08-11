package br.com.gomining.schoolsimulator.controller;

import br.com.gomining.schoolsimulator.model.request.ActivityRequest;
import br.com.gomining.schoolsimulator.model.response.ActivityResponse;
import br.com.gomining.schoolsimulator.service.impl.ActivityServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "Activity")
public class ActivityController {
    private final ActivityServiceImpl activityService;

    @GetMapping
    @ApiOperation(value = "Returns a list of activities")
    public List<ActivityResponse> getAllActivities() {
        return toListResponse(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Returns a single activity")
    public ActivityResponse getActivityById(@PathVariable String id) {
        return toResponse(activityService.getActivityById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation(value = "Creates a new activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ActivityResponse createActivity(@RequestBody @Valid ActivityRequest activity) {
        return toResponse(activityService.createActivity(toEntity(activity)));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates an existing activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ActivityResponse updateActivity(@PathVariable String id, @RequestBody @Valid ActivityRequest activity) {
        return toResponse(activityService.updateActivity(id, toEntity(activity)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation(value = "Deletes an existing activity")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteActivity(@PathVariable String id) {
        activityService.deleteActivity(id);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @ApiOperation(value = "Deletes all activities")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllActivities() {
        activityService.deleteAllActivities();
    }
}
