package br.com.gomining.schoolsimulator.model.entity.grade;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@ApiModel
@Document(collection = "Grade")
public class Grade {
    @Id
    @Schema(description = "The database generated grade ID")
    private String id;
    @Schema(description = "The grade value")
    private Double gradeValue;
}
