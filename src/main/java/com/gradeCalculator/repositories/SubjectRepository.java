package com.gradeCalculator.repositories;

import com.gradeCalculator.entities.SubjectEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Integer> {
    Iterable<SubjectEntity> findByName(String name);

    @Query("SELECT SUM(m.grade * m.gradingFactor) FROM ModuleEntity m WHERE m.subject = :subject")
    Double totalGrade(SubjectEntity subject);

    @Query("SELECT SUM(m.gradingFactor) FROM ModuleEntity m WHERE m.subject = :subject")
    Double totalGradingFactor(SubjectEntity subject);
}
