package com.gradeCalculator.services;

import com.gradeCalculator.Entities.SubjectEntity;
import com.gradeCalculator.Entities.UserEntity;
import com.gradeCalculator.services.exceptions.Forbidden;
import jakarta.transaction.Transactional;

/**
 * Service for subject operations
 */
public interface SubjectService {
/**
     * Create a new subject
     * @param name The name of the subject
     * @param user The user that wants to create the subject
     * @return The new subject
     */
    public SubjectEntity createSubject(String name, UserEntity user);

    /**
     * Get the subject with the given id
     * @param subjectId The id of the subject
     * @param user The user that wants to get the subject
     * @return The subject with the given id
     */
    public SubjectEntity getSubject(int subjectId, UserEntity user) throws Forbidden;

    /**
     * Delete the subject
     * @param subject The subject to delete
     */
    public void deleteSubject(SubjectEntity subject);

    /**
     * Get the average grade of the subject
     * @param subject The subject to get the average grade of
     * @return The average grade of the subject
     */
    public double averageGrade(SubjectEntity subject);

    /**
     * Get the best possible grade of the subject
     * @param subject The subject to get the best possible grade of
     * @return The best possible grade of the subject
     */
    public double bestPossibleGrade(SubjectEntity subject);

    /**
     * Get the worst possible grade of the subject
     * @param subject The subject to get the worst possible grade of
     * @return The worst possible grade of the subject
     */
    public double worstPossibleGrade(SubjectEntity subject);
}
