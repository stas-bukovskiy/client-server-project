package edu.ukma.clientserverproject.providers;

import java.util.ArrayList;
import java.util.List;

public class UserIdProvider implements Provider<Integer> {

    private static final byte INITIAL_VALUE = 0;
    private static UserIdProvider instance;

    private final List<Integer> userIds;
    private int index;


    private UserIdProvider() {
        userIds = new ArrayList<>();
        index = INITIAL_VALUE;
    }


    public static UserIdProvider get() {
        if (instance == null)
            instance = new UserIdProvider();
        return instance;
    }

    @Override
    public Integer provide() {
        userIds.add(index);
        return index++;
    }

    @Override
    public boolean validate(Integer userId) {
        return userIds.contains(userId);
    }

}
