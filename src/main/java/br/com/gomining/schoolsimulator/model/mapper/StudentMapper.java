package br.com.gomining.schoolsimulator.model.mapper;

import br.com.gomining.schoolsimulator.model.entity.Student;
import br.com.gomining.schoolsimulator.model.request.StudentRequest;
import br.com.gomining.schoolsimulator.model.response.StudentResponse;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class StudentMapper {
    public static Student toEntity(StudentRequest studentRequest) {
        return Student.builder()
                .fullName(studentRequest.getFullName())
                .cpf(studentRequest.getCpf())
                .email(studentRequest.getEmail())
                .telephone(studentRequest.getTelephone())
                .registrationDate(studentRequest.getRegistrationDate())
                .lastUpdateDate(studentRequest.getLastUpdateDate())
                .build();
    }

    public static StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .cpf(student.getCpf())
                .email(student.getEmail())
                .telephone(student.getTelephone())
                .registrationDate(student.getRegistrationDate())
                .lastUpdateDate(student.getLastUpdateDate())
                .activities(student.getActivities())
                .build();
    }

    public static List<StudentResponse> toListResponse(List<Student> students) {
        return students.stream()
                .map(StudentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
