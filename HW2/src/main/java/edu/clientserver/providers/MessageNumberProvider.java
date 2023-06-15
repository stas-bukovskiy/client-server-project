package edu.clientserver.providers;

public class MessageNumberProvider implements Provider<Long> {

    private static final long INITIAL_VALUE = 0;
    private static MessageNumberProvider instance;

    public long currantValue;

    private MessageNumberProvider() {
        this.currantValue = INITIAL_VALUE;
    }


    public static MessageNumberProvider get() {
        if (instance == null)
            instance = new MessageNumberProvider();
        return instance;
    }

    @Override
    public Long provide() {
        return currantValue++;
    }

    @Override
    public boolean validate(Long messageNumber) {
        return INITIAL_VALUE <= messageNumber && messageNumber < currantValue;
    }

}
