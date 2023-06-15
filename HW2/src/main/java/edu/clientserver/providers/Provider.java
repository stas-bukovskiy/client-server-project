package edu.clientserver.providers;

public interface Provider<T> {
    T provide();
    boolean validate(T t);
}
