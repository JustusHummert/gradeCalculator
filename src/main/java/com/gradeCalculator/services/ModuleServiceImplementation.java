package com.gradeCalculator.services;

import com.gradeCalculator.Entities.ModuleEntity;
import com.gradeCalculator.Entities.SubjectEntity;
import com.gradeCalculator.Entities.UserEntity;
import com.gradeCalculator.repositories.ModuleRepository;
import com.gradeCalculator.services.exceptions.Forbidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for module operations
 */
@Service
public class ModuleServiceImplementation implements ModuleService{

    @Autowired private ModuleRepository moduleRepository;

    /**
     * Create a new module
     * @param name          The name of the module
     * @param gradingFactor The grading factor of the module
     * @param subject       the subject the module should belong to
     * @return The new module
     */
    @Override
    public ModuleEntity createModule(String name, double gradingFactor, SubjectEntity subject) throws Forbidden {
        ModuleEntity module = new ModuleEntity(name, gradingFactor, subject);
        return moduleRepository.save(module);
    }

    /**
     * Get the module with the given id
     * Throw an exception if the module does not exist
     * Throw an exception if the user does not have access to the module
     * @param moduleId The id of the module
     * @param user     The user that wants to get the module
     * @return The module with the given id
     */
    @Override
    public ModuleEntity getModule(int moduleId, UserEntity user) throws Forbidden {
        ModuleEntity module = moduleRepository.findById(moduleId).orElseThrow(Forbidden::new);
        if(!module.getSubject().getUser().equals(user))
            throw new Forbidden();
        return module;
    }

    /**
     * Add a grade to the module
     *
     * @param module The module to add the grade to
     * @param grade  The grade to add
     * @return The module with the grade added
     */
    @Override
    public ModuleEntity setGrade(ModuleEntity module, double grade) {
        module.setGrade(grade);
        return moduleRepository.save(module);
    }

    /**
     * Delete the module
     *
     * @param module The module to delete
     */
    @Override
    public void deleteModule(ModuleEntity module) {
        moduleRepository.delete(module);
    }

    /**
     * Get the weighted grade of the module
     *
     * @param module The module to get the weighted grade of
     * @return The weighted grade of the module
     */
    @Override
    public double getWeightedGrade(ModuleEntity module) {
        return module.getGrade() * module.getGradingFactor();
    }
}
