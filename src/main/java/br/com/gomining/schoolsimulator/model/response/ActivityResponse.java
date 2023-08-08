package br.com.gomining.schoolsimulator.model.response;

import br.com.gomining.schoolsimulator.model.entity.Grade;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ActivityResponse {
    private String id;
    private String title;
    private String questionStatement;
    private String registrationDate;
    private String lastUpdateDate;
//    private List<Grade> grade;
}
