package edu.clientserver.util;

import java.security.SecureRandom;
import java.util.Random;

public final class RandomUtil {

    private static final Random random = new SecureRandom();

    private RandomUtil() {}

    public static String randomString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
