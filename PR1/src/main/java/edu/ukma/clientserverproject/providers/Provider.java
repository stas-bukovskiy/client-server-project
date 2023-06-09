package edu.ukma.clientserverproject.providers;

public interface Provider<T> {
    T provide();
    boolean validate(T t);
}
