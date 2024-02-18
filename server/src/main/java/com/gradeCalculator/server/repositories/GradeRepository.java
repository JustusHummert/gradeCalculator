package com.gradeCalculator.server.repositories;

import com.gradeCalculator.server.Entities.GradeEntity;
import com.gradeCalculator.server.Entities.ModuleEntity;
import com.gradeCalculator.server.Entities.UserEntity;
import org.springframework.data.repository.CrudRepository;


public interface GradeRepository extends CrudRepository<GradeEntity, Integer> {
    Iterable<GradeEntity> findByUser(UserEntity user);
    Iterable<GradeEntity> findByModule(ModuleEntity module);
}
