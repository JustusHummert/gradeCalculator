package com.gradeCalculator.repositories;

import com.gradeCalculator.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {
}
