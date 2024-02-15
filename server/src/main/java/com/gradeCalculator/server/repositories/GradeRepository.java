package com.gradeCalculator.server.repositories;

import com.gradeCalculator.server.Entities.GradeEntity;
import org.springframework.data.repository.CrudRepository;


public interface GradeRepository extends CrudRepository<GradeEntity, Integer> {
}
