package edu.clientserver.util;

import edu.clientserver.domain.Good;
import edu.clientserver.domain.Group;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public final class RandomUtil {

    private final static Random random = new Random();

    private RandomUtil() {
    }

    public static String randomString(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static Good radnomGood(Group group) {
        return Good.builder()
                .name(randomString(10))
                .quantity(random.nextInt(0, 1000))
                .price(randomBigDecimal(
                        new BigDecimal("1.00").setScale(2, RoundingMode.HALF_UP),
                        new BigDecimal("1000.00").setScale(2, RoundingMode.HALF_UP)
                ))
                .group(group)
                .build();
    }

    public static Group randomGroup() {
        return Group.builder()
                .name(randomString(10))
                .build();
    }


    public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max) {
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.setScale(2, RoundingMode.HALF_UP);
    }


}
