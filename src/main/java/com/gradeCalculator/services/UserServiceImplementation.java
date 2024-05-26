package com.gradeCalculator.services;

import com.gradeCalculator.Entities.UserEntity;
import com.gradeCalculator.repositories.UserRepository;
import com.gradeCalculator.services.exceptions.LoginFailed;
import com.gradeCalculator.services.exceptions.UsernameTaken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

public class UserServiceImplementation implements UserService{
    @Autowired private UserRepository userRepository;

    /**
     * Create a new user
     * check if the user already exists
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
        user.setPassword(password);
        userRepository.save(user);
        return user;
    }

    /**
     * Get the user with the given username
     * Throws an exception if the user does not exist
     * @param username The username of the user
     * @return The user with the given username
     */
    @Override
    public UserEntity getUser(String username) throws LoginFailed {
        Optional<UserEntity> user = userRepository.findById(username);
        if(user.isEmpty())
            throw new LoginFailed();
        return user.get();
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
}
