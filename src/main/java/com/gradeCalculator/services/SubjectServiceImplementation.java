package com.gradeCalculator.services;

import com.gradeCalculator.entities.ModuleEntity;
import com.gradeCalculator.entities.SubjectEntity;
import com.gradeCalculator.entities.UserEntity;
import com.gradeCalculator.repositories.SubjectRepository;
import com.gradeCalculator.services.exceptions.Forbidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for subject operations
 */
@Service
public class SubjectServiceImplementation implements SubjectService{

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ModuleService moduleService;

    /**
     * Create a new subject
     * @param name The name of the subject
     * @param user The user that wants to create the subject
     * @return The new subject
     */
    @Override
    public SubjectEntity createSubject(String name, UserEntity user) {
        SubjectEntity subject = new SubjectEntity(name, user);
        return subjectRepository.save(subject);
    }

    /**
     * Get the subject with the given id
     * Throw an exception if the subject does not exist
     * Throw an exception if the user does not have access to the subject
     * @param subjectId The id of the subject
     * @param user      The user that wants to get the subject
     * @return The subject with the given id
     */
    @Override
    public SubjectEntity getSubject(int subjectId, UserEntity user) throws Forbidden {
        SubjectEntity subject = subjectRepository.findById(subjectId).orElseThrow(Forbidden::new);
        if(!subject.getUser().equals(user))
            throw new Forbidden();
        return subject;
    }

    /**
     * Delete the subject and all modules in it
     * @param subject The subject to delete
     */
    @Override
    public void deleteSubject(SubjectEntity subject) {
        if(subject.getModules()!=null && !subject.getModules().isEmpty())
            for(ModuleEntity module : subject.getModules()){
                moduleService.delete(module);
            }
        subjectRepository.delete(subject);
    }

    /**
     * Get the average grade of the subject
     * @param subject The subject to get the average grade of
     * @return The average grade of the subject
     */
    @Override
    public double averageGrade(SubjectEntity subject) {
        return totalGrade(subject)/totalGradingFactor(subject);
    }

    /**
     * Get the best possible grade of the subject
     * @param subject The subject to get the best possible grade of
     * @return The best possible grade of the subject
     */
    @Override
    public double bestPossibleGrade(SubjectEntity subject) {
        return totalGrade(subject)+(1-totalGradingFactor(subject));
    }

    /**
     * Get the worst possible grade of the subject
     * @param subject The subject to get the worst possible grade of
     * @return The worst possible grade of the subject
     */
    @Override
    public double worstPossibleGrade(SubjectEntity subject) {
        return totalGrade(subject)+(1-totalGradingFactor(subject))*4;
    }

    /**
     * grades added together multiplied by grading factor
     * @param subject The subject to get the total grade of
     * @return The total grade of the subject
     */
    private double totalGrade(SubjectEntity subject){
        return subjectRepository.totalGrade(subject);
    }

    /**
     * grading factor added together
     * @param subject The subject to get the total grading factor of
     * @return The total grading factor of the subject
     */
    private double totalGradingFactor(SubjectEntity subject){
        return subjectRepository.totalGradingFactor(subject);
    }
}
