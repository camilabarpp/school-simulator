package br.com.gomining.schoolsimulator.repository;

import br.com.gomining.schoolsimulator.model.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    UserDetails findByUsername(String username);
}
