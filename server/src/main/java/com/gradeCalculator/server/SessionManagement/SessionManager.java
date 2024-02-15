package com.gradeCalculator.server.SessionManagement;

import com.gradeCalculator.server.Entities.UserEntity;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SessionManager {
    private static final int sessionTime = 216000;
    private static SessionManager instance;
    private final SessionIdGenerator generator;
    private final Map<String, UserEntity> sessions;
    private final Timer timer;

    private SessionManager(){
        generator = new SessionIdGenerator();
        sessions = new HashMap<String, UserEntity>();
        timer = new Timer();
    }

    public static SessionManager getInstance() {
        if (instance == null){
            instance = new SessionManager();
        }
        return instance;
    }

    //deletes Session after certain time period
    private void createSessionTimer(String key){
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        sessions.remove(key);
                        cancel();
                    }
                },
                sessionTime
        );
    }

    public String addSession(UserEntity user){
        String key = generator.nextSessionId();
        sessions.put(key, user);
        createSessionTimer(key);
        return key;
    }

    public UserEntity getSession(String key){
        return sessions.get(key);
    }




}
