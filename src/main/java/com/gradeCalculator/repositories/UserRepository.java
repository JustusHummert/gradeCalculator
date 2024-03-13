package com.gradeCalculator.repositories;

import com.gradeCalculator.Entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, String> {
}
