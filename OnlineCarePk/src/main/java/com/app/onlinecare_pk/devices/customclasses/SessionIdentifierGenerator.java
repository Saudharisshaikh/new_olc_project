package com.app.onlinecare_pk.devices.customclasses;

/**
 * Created by aftab on 06/08/2016.
 */

import java.security.SecureRandom;
import java.math.BigInteger;

public final class SessionIdentifierGenerator {
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }
}