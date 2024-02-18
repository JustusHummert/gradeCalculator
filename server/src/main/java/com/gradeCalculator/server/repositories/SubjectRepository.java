package com.gradeCalculator.server.repositories;

import com.gradeCalculator.server.Entities.SubjectEntity;
import org.springframework.data.repository.CrudRepository;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Integer> {
    Iterable<SubjectEntity> findByName(String name);
}
