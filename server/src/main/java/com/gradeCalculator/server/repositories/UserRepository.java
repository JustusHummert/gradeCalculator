package com.gradeCalculator.server.repositories;

import com.gradeCalculator.server.Entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {
}
