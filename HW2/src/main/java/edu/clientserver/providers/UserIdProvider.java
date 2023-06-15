package edu.clientserver.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserIdProvider implements Provider<Integer> {

    private static final byte INITIAL_VALUE = 0;
    private static UserIdProvider instance;

    private final List<Integer> userIds;
    private final AtomicInteger index;


    private UserIdProvider() {
        userIds = Collections.synchronizedList(new ArrayList<>());
        index = new AtomicInteger(INITIAL_VALUE);
    }


    public static UserIdProvider get() {
        if (instance == null)
            instance = new UserIdProvider();
        return instance;
    }

    @Override
    public Integer provide() {
        userIds.add(index.get());
        return index.getAndIncrement();
    }

    @Override
    public boolean validate(Integer userId) {
        return userIds.contains(userId);
    }

}
