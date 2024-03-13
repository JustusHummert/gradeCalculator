package com.gradeCalculator.repositories;

import com.gradeCalculator.Entities.SubjectEntity;
import org.springframework.data.repository.CrudRepository;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Integer> {
    Iterable<SubjectEntity> findByName(String name);
}
