package com.gradeCalculator.server.repositories;

import com.gradeCalculator.server.Entities.ModuleInSubjectEntity;
import com.gradeCalculator.server.Entities.SubjectEntity;
import org.springframework.data.repository.CrudRepository;

public interface ModuleInSubjectRepository extends CrudRepository<ModuleInSubjectEntity, Integer> {
    Iterable<ModuleInSubjectEntity> findBySubject(SubjectEntity subject);
}
