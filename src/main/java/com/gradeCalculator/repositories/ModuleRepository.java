package com.gradeCalculator.repositories;
import org.springframework.data.repository.CrudRepository;

import com.gradeCalculator.entities.ModuleEntity;

public interface ModuleRepository extends CrudRepository<ModuleEntity, Integer>{
    Iterable<ModuleEntity> findByName(String name);
}
