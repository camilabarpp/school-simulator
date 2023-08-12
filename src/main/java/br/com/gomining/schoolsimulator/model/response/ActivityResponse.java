package br.com.gomining.schoolsimulator.model.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ActivityResponse {
    private String id;
    private String title;
    private String questionStatement;
    private String registrationDate;
    private String lastUpdateDate;
}
