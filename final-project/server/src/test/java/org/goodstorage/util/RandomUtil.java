package org.goodstorage.util;

import org.goodstorage.domain.Good;
import org.goodstorage.domain.Group;
import org.goodstorage.domain.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;

public final class RandomUtil {

    private static final Random random = new Random();

    private RandomUtil() {
    }


    public static String randomString(int length) {
        int leftLimit = 97;
        int rightLimit = 122;

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static Group radndomGroup() {
        return Group.builder()
                .id(UUID.randomUUID().toString())
                .name(randomString(10))
                .description(randomString(100))
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public static Good randomGood(Group group) {
        return Good.builder()
                .id(UUID.randomUUID().toString())
                .name(randomString(10))
                .description(randomString(100))
                .producer(randomString(10))
                .price(randomBigDecimal(
                        BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(10000.00).setScale(2, RoundingMode.HALF_UP)
                ))
                .quantity(random.nextInt(1000))
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .group(group)
                .build();
    }

    public static Good randomGood() {
        return Good.builder()
                .id(UUID.randomUUID().toString())
                .name(randomString(10))
                .description(randomString(100))
                .producer(randomString(10))
                .price(randomBigDecimal(
                        BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(10000.00).setScale(2, RoundingMode.HALF_UP)
                ))
                .quantity(random.nextInt(1000))
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .group(radndomGroup())
                .build();
    }

    public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max) {
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    public static User radnomUser() {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .fullName(randomString(15))
                .username(randomString(10))
                .password(randomString(8))
                .role(randomString(4))
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

}
