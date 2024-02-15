package com.gradeCalculator.server.SessionManagement;

import com.gradeCalculator.server.Entities.UserEntity;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @Test
    void getInstance() {
        SessionManager first = SessionManager.getInstance();
        SessionManager second = SessionManager.getInstance();
        assertEquals(first, second, "The Instance should always be the same");
    }

    @Test
    void addSession() {
        UserEntity user = new UserEntity();
        String key = SessionManager.getInstance().addSession(user);
        assertEquals(SessionManager.getInstance().getSession(key), user, "Saved user should be accessible.");
    }

    @Test
    void getSession() {
        UserEntity user = new UserEntity();
        String key = SessionManager.getInstance().addSession(user);
        assertEquals(SessionManager.getInstance().getSession(key), user, "Saved user should be accessible.");
        assertNull(SessionManager.getInstance().getSession("test"), "there should be no Instance for a random string");
    }
}