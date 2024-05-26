package com.gradeCalculator.services;

import com.gradeCalculator.Entities.ModuleEntity;
import com.gradeCalculator.Entities.SubjectEntity;
import com.gradeCalculator.Entities.UserEntity;
import com.gradeCalculator.services.exceptions.Forbidden;

/**
 * Service for module operations
 */
public interface ModuleService {
    /**
     * Create a new module
     * @param name The name of the module
     * @param gradingFactor The grading factor of the module
     * @param subject The subject the module should belong to
     * @param user The user that wants to create the module
     * @return The new module
     */
    public ModuleEntity createModule(String name, double gradingFactor, SubjectEntity subject, UserEntity user) throws Forbidden;

    /**
     * Get the module with the given id
     * @param moduleId The id of the module
     * @param user The user that wants to get the module
     * @return The module with the given id
     */
    public ModuleEntity getModule(int moduleId, UserEntity user) throws Forbidden;

    /**
     * Set the grade of the module
     * @param module The module to add the grade to
     * @param grade The grade to add
     * @return The module with the grade added
     */
    public ModuleEntity setGrade(ModuleEntity module, double grade);


    /**
     * Delete the module
     * @param module The module to delete
     */
    public void deleteModule(ModuleEntity module);

    /**
     * Get the weighted grade of the module
     * @param module The module to get the weighted grade of
     * @return The weighted grade of the module
     */
    public double getWeightedGrade(ModuleEntity module);

}
