package com.gradeCalculator.services;

import com.gradeCalculator.Entities.UserEntity;
import com.gradeCalculator.services.exceptions.LoginFailed;
import com.gradeCalculator.services.exceptions.UsernameTaken;

/**
 * Service for user operations
 */
public interface UserService {

    /**
     * Create a new user
     * @param username The username of the new user
     * @param password The password of the new user
     * @return The new user
     */
    public UserEntity createUser(String username, String password) throws UsernameTaken;

    /**
     * Get the user with the given username
     * @param username The username of the user
     * @return The user with the given username
     */
    public UserEntity getUser(String username) throws LoginFailed;

    /**
     * Get the user with the given username and password
     * @param username The username of the user
     * @param password The password of the user
     * @return The user with the given username and password
     */
    public UserEntity getUser(String username, String password) throws LoginFailed;
}
