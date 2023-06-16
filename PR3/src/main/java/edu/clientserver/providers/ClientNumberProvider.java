package edu.clientserver.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientNumberProvider implements Provider<Byte> {

    private static final byte INITIAL_VALUE = 0;
    private static ClientNumberProvider instance;

    private final List<Byte> clientNumbers;
    private AtomicInteger index;


    private ClientNumberProvider() {
        clientNumbers = Collections.synchronizedList(new ArrayList<>());
        index = new AtomicInteger(INITIAL_VALUE);
    }


    public static ClientNumberProvider get() {
        if (instance == null)
            instance = new ClientNumberProvider();
        return instance;
    }

    @Override
    public synchronized Byte provide() {
        clientNumbers.add((byte) index.get());
        return (byte) index.getAndIncrement();
    }

    @Override
    public synchronized boolean validate(Byte clientNumber) {
        return clientNumbers.contains(clientNumber);
    }

}
