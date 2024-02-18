package com.gradeCalculator.server.repositories;
import org.springframework.data.repository.CrudRepository;

import com.gradeCalculator.server.Entities.ModuleEntity;

public interface ModuleRepository extends CrudRepository<ModuleEntity, Integer>{
    Iterable<ModuleEntity> findByName(String name);
}
