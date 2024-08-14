package com.gradeCalculator.services;

import com.gradeCalculator.entities.UserEntity;
import com.gradeCalculator.services.exceptions.LoginFailed;
import com.gradeCalculator.services.exceptions.UsernameTaken;
import jakarta.servlet.http.HttpSession;

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
    UserEntity createUser(String username, String password) throws UsernameTaken;

    /**
     * Get the user with the given username
     * @param username The username of the user
     * @return The user with the given username
     */
    UserEntity getUser(String username) throws LoginFailed;

    /**
     * Get the user with the given username and password
     * @param username The username of the user
     * @param password The password of the user
     * @return The user with the given username and password
     */
    UserEntity getUser(String username, String password) throws LoginFailed;

    /**
     * Get the active user
     * @param session The session to get the user from
     * @return The active user
     */
    UserEntity getActiveUser(HttpSession session) throws LoginFailed;
}
