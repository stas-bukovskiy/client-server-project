package edu.clientserver.pr5.jwt;

public interface TokenProvider {
    String provide(String username);
}
