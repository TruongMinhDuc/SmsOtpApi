package com.example.smsDemo.Repositories;

import com.example.smsDemo.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByPhoneNumber(String phone);
}
