package com.gradeCalculator.services;

import com.gradeCalculator.entities.UserEntity;
import com.gradeCalculator.repositories.UserRepository;
import com.gradeCalculator.services.exceptions.LoginFailed;
import com.gradeCalculator.services.exceptions.UsernameTaken;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * Service for user operations
 */
@Service
public class UserServiceImplementation implements UserService{
    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new user
     * check if the user already exists
     * Encrypt the password
     * @param username The username of the new user
     * @param password The password of the new user
     * @return The new user
     */
    @Override
    public UserEntity createUser(String username, String password) throws UsernameTaken {
        if(userRepository.existsById(username))
            throw new UsernameTaken();
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        return userRepository.save(user);
    }

    /**
     * Get the user with the given username
     * Throws an exception if the user does not exist
     * @param username The username of the user
     * @return The user with the given username
     */
    @Override
    public UserEntity getUser(String username) throws LoginFailed {
        return userRepository.findById(username).orElseThrow(LoginFailed::new);
    }

    /**
     * Get the user with the given username and password
     * Throws an exception if the password is incorrect
     * @param username The username of the user
     * @param password The password of the user
     * @return The user with the given username and password
     */
    @Override
    public UserEntity getUser(String username, String password) throws LoginFailed {
        UserEntity user = getUser(username);
        if(!BCrypt.checkpw(password, user.getPassword()))
            throw new LoginFailed();
        return user;
    }

    /**
     * Get the active user
     * Throws an exception if the user is not logged in
     * @param session The session to get the user from
     * @return The active user
     */
    @Override
    public UserEntity getActiveUser(HttpSession session) throws LoginFailed {
        Object usernameObject = session.getAttribute("username");
        if(usernameObject == null)
            throw new LoginFailed();
        String username =  usernameObject.toString();
        return getUser(username);
    }
}
