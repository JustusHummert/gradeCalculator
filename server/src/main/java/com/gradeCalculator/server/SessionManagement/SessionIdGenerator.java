package com.gradeCalculator.server.SessionManagement;

import java.security.SecureRandom;

public class SessionIdGenerator extends SecureRandom {
    private static final int sessionIdLength = 32;

    //All english letters and digits
    //a total of 62 chars
    private static final char[] lettersAndNumbers = {
            '1','2','3','4','5','6','7','8','9','0',
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };

    //Creates a random char from numbers and english letters
    private char nextChar(){
        return lettersAndNumbers[next(6)%62];
    }

    //Creates a random String with a given length
    public String nextString(int length){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<length; i++)
            builder.append(nextChar());
        return builder.toString();
    }

    //Creates the next SessionId
    public String nextSessionId(){
        return nextString(sessionIdLength);
    }

}
