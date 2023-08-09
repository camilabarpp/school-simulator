package br.com.gomining.schoolsimulator.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityResponse {
    private String id;
    private String title;
    private String questionStatement;
    private String registrationDate;
    private String lastUpdateDate;
}
