package br.com.gomining.schoolsimulator.repository;

import br.com.gomining.schoolsimulator.model.entity.student.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Student findByCpfOrEmailOrTelephone(String cpf, String email, String telephone);
}
