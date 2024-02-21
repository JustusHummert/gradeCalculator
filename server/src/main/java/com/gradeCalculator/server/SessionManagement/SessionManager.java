package com.gradeCalculator.server.SessionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SessionManager {
    //1 hour = 216000 ms
    private static final int sessionTime = 216000;
    private static SessionManager instance;
    private final SessionIdGenerator generator;
    private final Map<String, String> sessions;
    private final Timer timer;

    private SessionManager(){
        generator = new SessionIdGenerator();
        sessions = new HashMap<>();
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

    public String addSession(String username){
        String key = generator.nextSessionId();
        sessions.put(key, username);
        createSessionTimer(key);
        return key;
    }

    public String getSession(String key){
        return sessions.get(key);
    }




}
