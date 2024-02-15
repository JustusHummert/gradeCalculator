package com.gradeCalculator.server.SessionManagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionIdGeneratorTest {

    @Test
    void nextString() {
        SessionIdGenerator generator = new SessionIdGenerator();
        String test = generator.nextString(10);
        assertEquals(10,test.length(), "The test String should have the given length");
    }

    @Test
    void nextSessionId() {
        SessionIdGenerator generator = new SessionIdGenerator();
        String test = generator.nextSessionId();
        assertEquals(32,test.length(), "The test String should have the given length");
        String another = generator.nextSessionId();
        assertNotEquals(test, another, "The chance of two SessionIds being the same should be 62^32, so this test should pass.");
    }
}