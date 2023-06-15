package edu.clientserver.providers;

import java.util.ArrayList;
import java.util.List;

public class ClientNumberProvider implements Provider<Byte> {

    private static final byte INITIAL_VALUE = 0;
    private static ClientNumberProvider instance;

    private final List<Byte> clientNumbers;
    private byte index;


    private ClientNumberProvider() {
        clientNumbers = new ArrayList<>();
        index = INITIAL_VALUE;
    }


    public static ClientNumberProvider get() {
        if (instance == null)
            instance = new ClientNumberProvider();
        return instance;
    }

    @Override
    public Byte provide() {
        clientNumbers.add(index);
        return index++;
    }

    @Override
    public boolean validate(Byte clientNumber) {
        return clientNumbers.contains(clientNumber);
    }

}
